package com.github.wilsonng234.simplesearchengine.backend.repository;

import com.github.wilsonng234.simplesearchengine.backend.model.BodyPostingList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BodyPostingListRepository extends MongoRepository<BodyPostingList, String> {
    Optional<BodyPostingList> findBodyPostingListByWordId(String wordId);
}
