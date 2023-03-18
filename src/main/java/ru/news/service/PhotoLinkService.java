package ru.news.service;

import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import ru.news.dto.entry.PhotoLinkEntry;

import java.util.List;

public interface PhotoLinkService {
    List<Long> create(String postId, List<MultipartFile> files);

    List<PhotoLinkEntry> getAllPhotosByPostId(String postId, Sort sort);

    void delete(Long id);
}
