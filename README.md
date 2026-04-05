## Requisitos

- Java 21+
- Gradle 8.0+
- PostgreSQL 16+

## Instalación

```bash
# Instalar dependencias y construir el proyecto
./gradlew build

# Iniciar en modo desarrollo
./gradlew bootRun

# Build para producción
./gradlew build -Pprod
```

## Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto o configura en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/restobar
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
server.port=3000
```

## Tecnologías

- **Spring Boot** - Framework backend
- **Spring Data JPA** - Persistencia de datos
- **PostgreSQL** - Base de datos
- **Gradle** - Build tool
- **Java** - Lenguaje principal
