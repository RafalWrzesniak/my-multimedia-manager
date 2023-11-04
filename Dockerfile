FROM openjdk:17
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install
RUN mv my-multimedia-manager-*.jar my-multimedia-manager.jar
EXPOSE 8080
CMD ["java", "-jar", "my-multimedia-manager.jar"]
