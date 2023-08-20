# Simple Search Engine Backend

## Installation

SDK: Oracle OpenJDK version 17.0.6  
MongoDB: version 6.0.5  
MongoDBCompass: version 1.36.4

## Getting started

1. Create new connection using MongoDBCompass  
   Set up the Connection String URI and click the Connect button  
   By default,the Connection String URI is `mongodb://localhost:27017`

    ![MongoDBCompass new connection](imgs/MongoDBCompass.png)

2. Set up [.env](src/main/resources/.env)  
   Change the environment variables to the value:  
   `MONGO_DATABASE=simple-search-engine-db`: the name of the database
   `MONGO_DB_URL=mongodb://localhost:27017/`: the Connection String URI
   `SERVER_PORT=8080`: the port that the backend will run on

3. `./mvnw spring-boot:run` to run the backend

[Optional]

-   `.\mvnw test` to run the JUnit tests
-   `.\mvnw install jacoco:report` to generate Jacoco test coverage report

# Collections

-   [x] Implement `words` collection
-   [x] Implement `documents` collection
-   [x] Implement `postings` collection
-   [x] Implement `parentLinks` collection
-   [x] Implement `termWeights` collection
-   [x] Implement `pageRanks` collection

# Crawler

-   [x] Implement crawler
    -   [x] Create api endpoint `api/v1/crawler`
        -   Query params:
            -   url: url to crawl  
                Default value: https://cse.ust.hk/
            -   pages: number of pages to crawl  
                Default value: 30

# Vector Space Model

-   [x] Implement term weighting formula based on `tf*idf/max(tf)`
-   [x] Implement Document Similarity based on cosine similarity measure
-   [x] Implement mechanism to favor matches in title
-   [x] Create api endpoint `api/v1/searchEngine`
    -   [x] Return top-50 query results
-   [x] Implement phrase search
    -   [x] Implement bi-gram terms
    -   [x] Implement tri-gram terms
    -   [x] Parse query to find phrase terms
