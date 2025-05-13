FROM openjdk:24

RUN mkdir /app

COPY /target/genaifinderapi-0.0.1-SNAPSHOT.jar /app

WORKDIR /app

CMD java -jar genaifinderapi-0.0.1-SNAPSHOT.jar

EXPOSE 8080