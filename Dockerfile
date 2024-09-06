FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/cryptorec.jar cryptorec.jar
COPY src/main/resources/data /app/data
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "cryptorec.jar"]
