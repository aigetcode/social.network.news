package ru.news.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.news.dto.entry.PostEntry;
import ru.news.entity.Post;
import ru.news.exceptions.NotFoundException;
import ru.news.repository.PostRepository;
import ru.news.util.Utils;

import java.util.UUID;

@Slf4j
@Service
public record PostService(PostRepository repository) {
    private static final String POST_NOT_FOUND = "Post not found";
    private static final String POST_REQUIRED = "Post is required";

    public UUID create(Post post) {
        log.info("Create post...");
        Utils.required(post, POST_REQUIRED);

        Post saved = repository.save(post);
        log.info("Saved post id:{}", saved);
        return saved.getId();
    }

    public UUID update(Post post) {
        log.info("Update post...");
        Utils.required(post, POST_REQUIRED);

        Post saved = repository.save(post);
        log.info("Updated post id:{}", saved);
        return saved.getId();
    }

    public Page<PostEntry> getPagePosts(int pageIndex, int pageSize, Sort sort) {
        log.info(String.format("Get posts page: %s, size: %s", pageIndex, pageSize));
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        Page<Post> posts = repository.findAll(pageable);
        return new PageImpl<>(PostEntry.fromListPost(posts.getContent()), pageable, posts.getTotalElements());
    }

    public PostEntry getPostById(String postId) {
        log.info(String.format("Get post by id[%s]", postId));
        Utils.required(postId, POST_REQUIRED);

        Post user = repository.findById(UUID.fromString(postId))
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, POST_NOT_FOUND));
        return PostEntry.fromPost(user);
    }

    public void deleteUser(UUID id) {
        log.info(String.format("Deleting post by id[%s]...", id));
        repository.deleteById(id);
        log.info(String.format("Deleted post by id[%s]...", id));
    }

}
