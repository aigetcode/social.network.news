package ru.news.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import ru.news.dto.entry.PostEntry;
import ru.news.entity.Post;

import java.util.UUID;

public interface PostService {
    UUID create(Post post);
    UUID update(Post post);
    Page<PostEntry> getPagePosts(int pageIndex, int pageSize, Sort sort);
    PostEntry getPostById(String postId);
    void deletePost(UUID id);
}
