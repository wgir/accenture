FROM eclipse-temurin:21-jre
COPY target/franchise-api-0.0.1.jar franchise-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/franchise-api.jar"]
