package com.github.wilsonng234.simplesearchengine.backend.repository;

import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentLinkRepository extends MongoRepository<ParentLink, String> {
    Optional<ParentLink> findParentLinkByUrl(String url);
}
