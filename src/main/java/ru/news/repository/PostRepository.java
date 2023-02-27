package ru.news.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ru.news.entity.Post;

import java.util.UUID;

@Repository
public interface PostRepository extends ListCrudRepository<Post, UUID> {

    Page<Post> findAll(Pageable pageable);

}
