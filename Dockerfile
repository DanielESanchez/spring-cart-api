FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY build/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]