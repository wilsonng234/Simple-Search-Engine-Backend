package com.github.wilsonng234.Simple.Search.Engine.Backend.repository;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {
}
