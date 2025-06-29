# Usar una imagen base con Java 17
FROM eclipse-temurin:17-jre-alpine

# Directorio donde se colocará la aplicación en el contenedor
WORKDIR /app

# Copiar el archivo jar del proyecto al directorio /app en el contenedor
COPY target/NotesApp-0.0.1-SNAPSHOT.jar /app/NotesApp.jar

# Exponer el puerto que usa la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["sh", "-c", "java -jar /app/NotesApp.jar --server.address=0.0.0.0 --server.port=${PORT:-8080}"]