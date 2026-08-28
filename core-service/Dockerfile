FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY common/pom.xml common/
COPY core-service/pom.xml core-service/
RUN mvn dependency:go-offline -pl core-service -am
COPY common/ common/
COPY core-service/ core-service/
RUN mvn package -pl core-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/core-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
