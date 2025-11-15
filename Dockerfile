FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon



FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar  /app/usuario.jar

EXPOSE 8082

CMD ["java", "-jar", "/app/usuario.jar"]

