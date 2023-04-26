# Simple Search Engine Backend

## Getting started

```
1. Set up `src/main/resources/.env` for MongoDB connection string
   Fields required: MONGO_DATABASE, MONGO_USER, MONGO_PASSWORD, MONGO_CLUSTER
   
2. Run `./mvnw spring-boot:run` to get localhost  
```

## Collections

- [X] Implement `words` collection
    - Primary key: wordId
- [X] Implement `documents` collection
    - Primary key: docId
- [X] Implement `titlePostingsList` collection
    - Primary key: wordId
- [X] Implement `bodyPostingsList` collection
    - Primary key: wordId
- [X] Implement `parentLinks` collection
    - Primary key: docId

## Crawler

- [X] Implement crawler
    - [X] Create api endpoint `api/v1/crawler`
        - Query params:
            - url: url to crawl  
              Default value: https://cse.ust.hk/
            - pages: number of pages to crawl  
              Default value: 30

## Vector Space Model

- [X] Implement term weighting formula based on `tf*idf/max(tf)`
- [X] Implement Document Similarity based on cosine similarity measure
- [X] Implement mechanism to favor matches in title
- [X] Create api endpoint `api/v1/searchEngine`
    - [X] Return top-50 query results
- [X] Implement phrase search
    - [X] Implement bi-gram terms
    - [X] Implement tri-gram terms
    - [X] Parse query to find phrase terms
 