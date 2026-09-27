FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src src
RUN mvn -B package -DskipTests
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/portfolio-1.0.0.jar app.jar
COPY content content
USER 10001
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
