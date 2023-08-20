FROM eclipse-temurin:17.0.6_10-jdk-jammy

RUN apt-get update && apt-get install netcat -y
RUN addgroup app && adduser --system --ingroup app app
USER app
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml wait-for ./
RUN ./mvnw dependency:resolve
COPY src ./src

EXPOSE 8080
CMD ["./mvnw", "spring-boot:run"]