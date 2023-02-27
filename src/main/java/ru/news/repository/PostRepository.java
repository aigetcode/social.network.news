package ru.news.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.news.entity.Post;

import java.util.UUID;

@Repository
public interface PostRepository extends ListCrudRepository<Post, UUID>, PagingAndSortingRepository<Post, UUID> {
}
