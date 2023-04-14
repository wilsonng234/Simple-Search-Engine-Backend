package com.github.wilsonng234.Simple.Search.Engine.Backend.repository;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.TitlePostingList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitlePostingListRepository extends MongoRepository<TitlePostingList, String> {
    Optional<TitlePostingList> findTitlePostingListByWordId(String wordId);
}
