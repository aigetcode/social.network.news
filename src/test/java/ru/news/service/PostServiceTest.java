package ru.news.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.news.entity.Post;
import ru.news.exceptions.NotFoundException;
import ru.news.repository.PostRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldSuccessCreatePost() {
        var expectedUuid = UUID.randomUUID();
        var postInput = Post.builder()
                .title("test title")
                .description("description post")
                .userUuid("userId")
                .build();
        var postReturned = postInput.toBuilder()
                        .id(expectedUuid)
                        .version(0)
                        .build();
        doReturn(postReturned).when(postRepository).save(postInput);

        UUID uuid = postService.create(postInput);
        assertEquals(expectedUuid, uuid);
    }

    @Test
    void shouldErrorCreatePost() {
        assertThrows(IllegalArgumentException.class, () -> postService.create(null));
    }

    @Test
    void shouldSuccessUpdatePost() {
        var expectedUuid = UUID.randomUUID();
        var postInput = Post.builder()
                .title("test title")
                .description("description post")
                .userUuid("userId")
                .build();
        var postReturned = postInput.toBuilder()
                .id(expectedUuid)
                .version(0)
                .build();
        doReturn(postReturned).when(postRepository).save(postInput);

        UUID uuid = postService.update(postInput);
        assertEquals(expectedUuid, uuid);
    }

    @Test
    void shouldErrorUpdatePost() {
        assertThrows(IllegalArgumentException.class, () -> postService.update(null));
    }

    @Test
    void shouldSuccessGetPagePost() {
        int pageIndex = 0;
        int pageSize = 10;
        var post = Post.builder()
                .id(UUID.randomUUID())
                .build();
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id"));
        List<Post> posts = Arrays.asList(post, post);

        doReturn(new PageImpl<>(posts, pageable, posts.size()))
                .when(postRepository).findAll(any(Pageable.class));

        var returnedPosts = postService.getPagePosts(pageIndex, pageSize, Sort.by("id"));
        assertEquals(2L, returnedPosts.getTotalElements());
    }

    @Test
    void shouldErrorGetPostById() {
        String uuid = UUID.randomUUID().toString();
        assertThrows(IllegalArgumentException.class, () -> postService.getPostById(null));
        assertThrows(NotFoundException.class, () -> postService.getPostById(uuid));
    }

    @Test
    void shouldSuccessGetPostById() {
        String uuid = UUID.randomUUID().toString();
        Post post = Post.builder()
                .id(UUID.fromString(uuid))
                .title("test")
                .build();
        doReturn(Optional.of(post)).when(postRepository).findById(UUID.fromString(uuid));

        var entry = postService.getPostById(uuid);
        assertEquals(uuid, entry.getId().toString());
    }

}
