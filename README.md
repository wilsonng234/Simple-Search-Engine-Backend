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

## Crawler

- [ ] Implement crawler
    - [ ] Create api endpoint `api/v1/crawler`
        - Query params:
            - url: url to crawl  
              Default value: https://cse.ust.hk/
            - pages: number of pages to crawl  
              Default value: 30
