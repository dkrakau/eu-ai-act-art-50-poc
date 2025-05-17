FROM openjdk:24-jdk-slim
ARG JAR_FILE=target/genaifinderapi-0.0.1-SNAPSHOT.jar
RUN mkdir /app
COPY ${JAR_FILE} app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]