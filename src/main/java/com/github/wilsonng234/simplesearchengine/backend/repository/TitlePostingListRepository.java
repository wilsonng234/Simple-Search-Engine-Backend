package com.github.wilsonng234.simplesearchengine.backend.repository;

import com.github.wilsonng234.simplesearchengine.backend.model.TitlePostingList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitlePostingListRepository extends MongoRepository<TitlePostingList, String> {
    Optional<TitlePostingList> findTitlePostingListByWordId(String wordId);
}