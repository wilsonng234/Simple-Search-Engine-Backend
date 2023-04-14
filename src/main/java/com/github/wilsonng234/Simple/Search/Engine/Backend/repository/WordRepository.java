package com.github.wilsonng234.Simple.Search.Engine.Backend.repository;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Word;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends MongoRepository<Word, String> {
}
