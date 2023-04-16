package ru.news.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ru.news.entity.PhotoLink;

import java.util.List;

@Repository
public interface PhotoLinkRepository extends ListCrudRepository<PhotoLink, Long> {

    List<PhotoLink> findAll(Sort sort);

}
