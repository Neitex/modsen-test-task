FROM eclipse-temurin:17-alpine AS builder
COPY . /app
WORKDIR /app
ENV GRADLE_USER_HOME=/app/.gradle
RUN ./gradlew bootJar

FROM eclipse-temurin:17-alpine
COPY --from=builder /app/build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
