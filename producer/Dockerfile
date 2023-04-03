FROM maven:3.6-jdk-11 AS builder

COPY pom.xml /app/
WORKDIR /app
RUN mvn dependency:go-offline

COPY src /app/src
RUN mkdir -p /app/target
RUN mvn package -DskipTests


FROM openjdk:11-jre-slim
COPY --from=builder /app/target/kafka-mtls-producer-1.0-SNAPSHOT.jar /usr/src/kafka-mtls-producer.jar

CMD ["java", "-jar", "/usr/src/kafka-mtls-producer.jar"]

