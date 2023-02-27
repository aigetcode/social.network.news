package ru.news.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.news.dto.entry.PhotoLinkEntry;
import ru.news.entity.PhotoLink;
import ru.news.entity.Post;
import ru.news.exceptions.NotFoundException;
import ru.news.repository.PhotoLinkRepository;
import ru.news.repository.PhotoPostS3Repository;
import ru.news.repository.PostRepository;
import ru.news.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public record PhotoLinkService(PhotoLinkRepository repository,
                               PostRepository postRepository,
                               PhotoPostS3Repository photoPostS3Repository) {
    private static final String PHOTO_LINK_PREFIX = "/minio/post-photo-bucket/";

    public void create(String postId, List<MultipartFile> files) {
        log.info("Create photos...");
        Utils.required(postId, "Post id is required");

        Post post = postRepository.findById(UUID.fromString(postId))
                .orElseThrow(() -> new IllegalStateException("Post not found"));

        for (MultipartFile multipartFile : files) {
            try {
                String filename = multipartFile.getOriginalFilename();
                Path tempFile = Files.createTempFile("temp",
                        "." + FilenameUtils.getExtension(filename));
                InputStream fileInputStream = multipartFile.getInputStream();

                File file = tempFile.toFile();
                try (FileOutputStream output = new FileOutputStream(file)) {
                    IOUtils.copy(fileInputStream, output);
                }
                try (FileInputStream inputStream = new FileInputStream(file)) {
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
                repository.save(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log.info("Created photos by post id:{}", postId);
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
