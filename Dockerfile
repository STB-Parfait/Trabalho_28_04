FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/agenda-api.jar app.jar

ENV APP_PORT=8000
EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
