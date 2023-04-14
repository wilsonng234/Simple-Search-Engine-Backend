package com.github.wilsonng234.Simple.Search.Engine.Backend.repository;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.BodyPostingList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BodyPostingListRepository extends MongoRepository<BodyPostingList, String> {
    Optional<BodyPostingList> findBodyPostingListByWordId(String wordId);
}
