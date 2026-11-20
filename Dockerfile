FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon



FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar  /app/user.jar

EXPOSE 8082

CMD ["java", "-jar", "/app/user.jar"]

