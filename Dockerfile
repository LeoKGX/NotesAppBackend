# Etapa 1: build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/NotesApp-0.0.1-SNAPSHOT.jar /app/NotesApp.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar /app/NotesApp.jar --server.address=0.0.0.0 --server.port=${PORT:-8080}"]
