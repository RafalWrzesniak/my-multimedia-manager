FROM maven:3.9-amazoncorretto-25 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM amazoncorretto:25-headless
WORKDIR /app
COPY --from=build /app/target/my-multimedia-manager-*.jar my-multimedia-manager.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "my-multimedia-manager.jar"]
