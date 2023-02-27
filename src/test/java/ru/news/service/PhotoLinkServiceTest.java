package ru.news.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.news.entity.PhotoLink;
import ru.news.entity.Post;
import ru.news.exceptions.NotFoundException;
import ru.news.repository.PhotoLinkRepository;
import ru.news.repository.PhotoPostS3Repository;
import ru.news.repository.PostRepository;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PhotoLinkServiceTest {
    private static final String PHOTO_LINK_PREFIX = "/minio/post-photo-bucket/";

    @Mock
    private PhotoLinkRepository photoLinkRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PhotoPostS3Repository photoPostS3Repository;

    @InjectMocks
    private PhotoLinkService photoLinkService;

    @Test
    void shouldSuccessCreatePhoto() {
        String postId = UUID.randomUUID().toString();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        Post post = Post.builder()
                .id(UUID.fromString(postId))
                .title("test")
                .build();
        var photo = PhotoLink.builder()
                .link(PHOTO_LINK_PREFIX + file.getOriginalFilename())
                .fileKey(file.getOriginalFilename())
                .post(post)
                .build();
        doReturn(Optional.of(post)).when(postRepository).findById(UUID.fromString(postId));
        doReturn(photo).when(photoLinkRepository).save(any(PhotoLink.class));
        doNothing().when(photoPostS3Repository)
                .put(any(String.class), any(FileInputStream.class), any(ObjectMetadata.class));

        photoLinkService.create(postId, List.of(file, file));
        verify(photoPostS3Repository, times(2))
                .put(any(String.class), any(FileInputStream.class), any(ObjectMetadata.class));
        verify(photoLinkRepository, times(2)).save(any());
    }

    @Test
    void shouldErrorCreatePhoto() {
        List<MultipartFile> list = Collections.emptyList();
        assertThrows(IllegalArgumentException.class,
                () -> photoLinkService.create(null, list));
    }

    @Test
    void shouldSuccessDeletePhoto() {
        Long photoLinkId = 12345L;
        String fileKey = "test.jpg";
        PhotoLink photoLink = PhotoLink.builder()
                .id(photoLinkId)
                .fileKey("test.jpg")
                .build();
        doReturn(Optional.of(photoLink)).when(photoLinkRepository).findById(photoLinkId);
        doNothing().when(photoPostS3Repository).delete(any(String.class));
        doNothing().when(photoLinkRepository).deleteById(any(Long.class));

        photoLinkService.delete(photoLinkId);

        verify(photoPostS3Repository).delete(fileKey);
        verify(photoLinkRepository).deleteById(photoLinkId);
    }

    @Test
    void shouldErrorDeletePhoto() {
        assertThrows(NotFoundException.class, () -> photoLinkService.delete(null));
    }

}
