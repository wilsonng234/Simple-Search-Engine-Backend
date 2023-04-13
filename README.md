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
- [ ] Implement `titlePostings` collection
    - Primary key: wordId, docId
    - foreign key() references postingLists(wordId)
- [ ] Implement `bodyPostings` collection
    - Primary key: wordId, docId
    - foreign key() references postingLists(wordId)
