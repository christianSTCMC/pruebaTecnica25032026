# Ejecución Manual (sin Docker)

## Objetivo

Levantar la API en tu host local usando Java + Maven Wrapper.

## Requisitos

- `Java 21`
- `MySQL 8+`
- Terminal compatible (`bash` recomendado)

## Variables mínimas de entorno

```bash
export SERVER_PORT=8080
export FRANQUICIAS_DB_JDBC_URL='jdbc:mysql://localhost:3306/franquicias?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export FRANQUICIAS_DB_USERNAME='franquicias_user'
export FRANQUICIAS_DB_PASSWORD='franquicias_pass'
export FRANQUICIAS_FLYWAY_ENABLED=true
```

Notas:

- Si ya aplicaste SQL manualmente, usa `FRANQUICIAS_FLYWAY_ENABLED=false`.
- Si no aplicaste SQL manual, deja `FRANQUICIAS_FLYWAY_ENABLED=true`.

## Levantar API

```bash
./mvnw spring-boot:run
```

## Verificar API

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Alternativa con JAR

```bash
./mvnw clean package -DskipTests
java -jar target/api-franquicias-*.jar
```
