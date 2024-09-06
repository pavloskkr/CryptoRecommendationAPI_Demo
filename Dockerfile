FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/cryptorec.jar cryptorec.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "cryptorec.jar"]
