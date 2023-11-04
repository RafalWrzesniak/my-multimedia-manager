FROM openjdk:17-jdk-slim
RUN mvn clean install
COPY my-multimedia-manager-0.0.3.jar myapp.jar
EXPOSE 8080
CMD ["java","-jar","/myapp.jar"]