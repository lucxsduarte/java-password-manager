FROM amazoncorretto:21-alpine-jdk AS builder

WORKDIR /app

COPY . .

RUN ./mvnw package -DskipTests

FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]