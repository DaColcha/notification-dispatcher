# Step 1: Build JAR con Gradle Multimódulo
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :event-producer:bootJar --no-daemon

# Step 2: Image de ejecución
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/event-producer/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]