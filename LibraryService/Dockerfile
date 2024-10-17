FROM eclipse-temurin:17-alpine AS builder
COPY . /app
WORKDIR /app
RUN ./gradlew bootJar

FROM eclipse-temurin:17-alpine
COPY --from=builder /app/build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
