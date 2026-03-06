# ===== BUILD =====
FROM gradle:9.3-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon

COPY src src
RUN gradle bootJar --no-daemon
  
  # ===== RUN =====
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
