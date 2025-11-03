FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean verify -Dspring.profiles.active=test -fae -Dmaven.test.failure.ignore=false

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
