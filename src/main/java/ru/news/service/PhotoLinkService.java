package ru.news.service;

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
import ru.news.repository.PostRepository;
import ru.news.util.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public record PhotoLinkService(PhotoLinkRepository repository,
                               PostRepository postRepository,
                               StorageService storageService) {
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
                try (FileOutputStream output = new FileOutputStream(tempFile.toFile())) {
                    IOUtils.copy(fileInputStream, output);
                }
                storageService.putImageFile(filename, tempFile.toFile());

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

    public List<PhotoLinkEntry> getAllPhotosByPostId(String postId, Sort sort) {
        log.info(String.format("Get all photos by post: %s", postId));

        List<PhotoLink> postPhotos = repository.findAll(sort);
        return postPhotos.stream()
                .map(PhotoLinkEntry::fromPhoto)
                .toList();
    }

    public void deletePhotoLink(Long id) {
        log.info(String.format("Deleting post photo by id[%s]...", id));

        try {
            var photoLink = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Not found photo by id"));
            storageService.removeFile(photoLink.getFileKey());
            repository.deleteById(id);
            log.info(String.format("Deleted post photo by id[%s]...", id));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
