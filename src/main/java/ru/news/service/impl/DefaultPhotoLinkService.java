package ru.news.service.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.news.dto.entry.PhotoLinkEntry;
import ru.news.entity.PhotoLink;
import ru.news.entity.Post;
import ru.news.exceptions.NotFoundException;
import ru.news.dao.PhotoLinkRepository;
import ru.news.dao.PhotoPostS3Repository;
import ru.news.dao.PostRepository;
import ru.news.service.PhotoLinkService;
import ru.news.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Primary
public record DefaultPhotoLinkService(PhotoLinkRepository repository,
                                      PostRepository postRepository,
                                      PhotoPostS3Repository photoPostS3Repository) implements PhotoLinkService {
    private static final String PHOTO_LINK_PREFIX = "/minio/post-photo-bucket/";

    public List<Long> create(String postId, List<MultipartFile> files) {
        log.info("Create photos...");
        Utils.required(postId, "Post id is required");
        Utils.required(files, "Files is required");

        Post post = postRepository.findById(UUID.fromString(postId))
                .orElseThrow(() -> new IllegalStateException("Post not found"));
        List<Long> photoIds = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            try {
                String filename = multipartFile.getOriginalFilename();
                Path tempFile = Files.createTempFile("temp",
                        "." + FilenameUtils.getExtension(filename));

                File file = tempFile.toFile();
                try (InputStream fileInputStream = multipartFile.getInputStream();
                        FileOutputStream output = new FileOutputStream(file);
                        FileInputStream inputStream = new FileInputStream(file)) {
                    IOUtils.copy(fileInputStream, output);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(file.length());
                    metadata.setContentType(Files.probeContentType(Paths.get(file.toURI())));
                    photoPostS3Repository.put(filename, inputStream, metadata);
                }

                var photo = PhotoLink.builder()
                        .link(PHOTO_LINK_PREFIX + filename)
                        .fileKey(filename)
                        .post(post)
                        .build();
                photo = repository.save(photo);
                photoIds.add(photo.getId());
            } catch (IOException exception) {
                throw new IllegalStateException("Something happen while save files for post: " + postId, exception);
            }
        }

        log.info("Created photos by post id:{}", postId);
        return photoIds;
    }

    @Deprecated(since = "1.0.0")
    public List<PhotoLinkEntry> getAllPhotosByPostId(String postId, Sort sort) {
        log.info(String.format("Get all photos by post: %s", postId));

        List<PhotoLink> postPhotos = repository.findAll(sort);
        return postPhotos.stream()
                .map(PhotoLinkEntry::fromPhoto)
                .toList();
    }

    public void delete(Long id) {
        log.info(String.format("Deleting post photo by id[%s]...", id));

        var photoLink = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Not found photo by id"));
        photoPostS3Repository.delete(photoLink.getFileKey());
        repository.deleteById(id);
        log.info(String.format("Deleted post photo by id[%s]...", id));
    }
}
