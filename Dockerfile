FROM maven:3.8.4 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/target/my-multimedia-manager-*.jar my-multimedia-manager.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "my-multimedia-manager.jar"]
