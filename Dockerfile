FROM eclipse-temurin:17-alpine AS builder
COPY build.gradle.kts /app/
COPY settings.gradle.kts /app/
COPY gradlew /app/
COPY gradle/ /app/gradle/
WORKDIR /app
RUN ./gradlew build -x test --parallel --continue > /dev/null 2>&1 || true
COPY . /app
RUN ./gradlew bootJar --parallel

FROM eclipse-temurin:17-alpine
RUN apk --no-cache add bash curl
COPY --from=builder /app/build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]