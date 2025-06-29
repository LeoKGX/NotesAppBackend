# NotesApp Backend

Este proyecto es el backend de una aplicación de notas, desarrollada con Java 17 y Spring Boot. Permite a los usuarios registrarse, iniciar sesión, crear notas con etiquetas, archivarlas y filtrarlas. Se utilizó una arquitectura RESTful segura con JWT y un enfoque moderno basado en DTOs y mappers.

---

## Características principales

- Registro e inicio de sesión con JWT
- CRUD de notas personales
- Gestión de etiquetas (tags)
- Archivado y desarchivado de notas
- Filtro por etiquetas
- API RESTful documentada
- Deploy Dockerizado para Render
- Seguridad con Spring Security + JWT
- Estructura por capas + DTO + Mapper

---

## Tecnologías utilizadas

| Herramienta | Descripción |
|-------------|-------------|
| Java 17     | Lenguaje principal |
| Spring Boot | Framework de backend |
| Spring Security | Autenticación y autorización |
| JWT         | Tokens de acceso seguros |
| Spring Data JPA | Persistencia en base de datos |
| Hibernate   | ORM |
| MySQL       | Base de datos |
| Docker      | Contenerización |
| Render      | Hosting gratuito |
| Lombok      | Reducción de boilerplate |
| Maven       | Gestión de dependencias |

---

## Estructura del proyecto
src/main/java
├── controller/
├── service/
├── repository/
├── model/
├── dto/
├── mapper/
└── config/ (Security)

resources/
└── application.properties

---

## 🔐 Seguridad

Este backend cuenta con un sistema de autenticación y autorización JWT robusto:

- Registro de usuario (`/register`)
- Login (`/login`) devuelve un JWT
- Protección de rutas por token
- CORS configurado para frontend específico
- BCrypt para hashear contraseñas

---

## 🛠️ Endpoints principales

### 🧑‍💻 Autenticación

| Método | Ruta         | Descripción           |
|--------|--------------|------------------------|
| POST   | `/register`  | Registra un usuario    |
| POST   | `/login`     | Inicia sesión y devuelve JWT |

### 📝 Notas

| Método | Ruta             | Descripción                     |
|--------|------------------|----------------------------------|
| POST   | `/notes`         | Crea una nota                   |
| GET    | `/notes`         | Lista todas las notas del usuario |
| PUT    | `/notes/{id}`    | Edita una nota existente        |
| DELETE | `/notes/{id}`    | Elimina una nota                |
| PUT    | `/notes/archive/{id}` | Archiva una nota          |
| PUT    | `/notes/unarchive/{id}` | Desarchiva una nota     |

### 🏷️ Tags

| Método | Ruta                        | Descripción                        |
|--------|-----------------------------|-------------------------------------|
| GET    | `/notes/filter?tag=nombre` | Filtra notas por etiqueta           |
| PUT    | `/notes/{id}/remove-tag?tag=nombre` | Quita una etiqueta de una nota |

---

## 🩺 Health Check

Health check accesible para monitorización de Render u otros servicios:

| GET | /notes/health |

Devuelve `200 OK` si el servicio está operativo.

---

## 🐳 Docker y Deploy

### Dockerfile

``dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/NotesApp-0.0.1-SNAPSHOT.jar /app/NotesApp.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar /app/NotesApp.jar --server.address=0.0.0.0 --server.port=${PORT:-8080}"]

## Deploy en Render
Subí tu proyecto a GitHub

Creá un nuevo servicio en Render

Seleccioná "Web Service" y conectá tu repo

En "Environment" elegí Docker

Listo

----
## Ejemplo de JSON

{
  "title": "Mi primera nota",
  "content": "Esto es una nota de prueba",
  "archived": false,
  "tags": [
    { "name": "personal" },
    { "name": "urgente" }
  ]
}
