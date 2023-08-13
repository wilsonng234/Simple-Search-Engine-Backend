#!/bin/sh

echo "Waiting for MongoDB to start..."
./wait-for database:27017 

echo "Starting the server..."
./mvnw spring-boot:run