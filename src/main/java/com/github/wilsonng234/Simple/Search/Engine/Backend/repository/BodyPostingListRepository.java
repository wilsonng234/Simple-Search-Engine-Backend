package com.github.wilsonng234.Simple.Search.Engine.Backend.repository;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.BodyPostingList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BodyPostingListRepository extends MongoRepository<BodyPostingList, String> {
    Optional<BodyPostingList> findBodyPostingListByWordId(String wordId);
}
