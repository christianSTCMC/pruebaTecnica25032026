# 05 - Despliegue local y con Docker

## Objetivo

Documentar los pasos operativos para ejecutar la API en ambiente local de dos maneras:

- Sin Docker (aplicación en host + MySQL local).
- Con Docker Compose (aplicación y MySQL en contenedores).

## Requisitos previos

### Opción local (sin Docker)

- `Java 21`
- `MySQL 8+`
- `bash` o terminal compatible
- Maven no es obligatorio (se usa `./mvnw`)

### Opción con Docker

- `Docker Engine`
- `Docker Compose` (plugin `docker compose`)

## Variables de entorno relevantes

La aplicación usa estas variables (con defaults definidos en `application.yml`):

- `SERVER_PORT`
- `FRANQUICIAS_DB_JDBC_URL`
- `FRANQUICIAS_DB_USERNAME`
- `FRANQUICIAS_DB_PASSWORD`
- `FRANQUICIAS_FLYWAY_ENABLED`
- `APP_LOG_LEVEL_ROOT`
- `APP_LOG_LEVEL_SPRING`
- `APP_LOG_LEVEL_APP`
- `APP_LOG_LEVEL_DB`
- `APP_LOGS_PATH`

### Archivo de variables para Docker

- `.env.template`: plantilla versionada para compartir configuración base.
- `.env`: archivo local que usa `docker compose` en tiempo de ejecución.
- `.env` no se debe versionar en git.
- Inicializa siempre con:

```bash
cp .env.template .env
```

## Opción A: Despliegue local (sin Docker)

### 1) Preparar base de datos MySQL local

Si no existe la base/usuario, puedes crearlos con:

```sql
CREATE DATABASE IF NOT EXISTS franquicias;
CREATE USER IF NOT EXISTS 'franquicias_user'@'%' IDENTIFIED BY 'franquicias_pass';
GRANT ALL PRIVILEGES ON franquicias.* TO 'franquicias_user'@'%';
FLUSH PRIVILEGES;
```

### 2) Exportar variables en terminal

```bash
export SERVER_PORT=8080
export FRANQUICIAS_DB_JDBC_URL='jdbc:mysql://localhost:3306/franquicias?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export FRANQUICIAS_DB_USERNAME='franquicias_user'
export FRANQUICIAS_DB_PASSWORD='franquicias_pass'
export FRANQUICIAS_FLYWAY_ENABLED=true
```

### 3) Iniciar la aplicación

```bash
./mvnw spring-boot:run
```

Alternativa con JAR:

```bash
./mvnw clean package -DskipTests
java -jar target/api-franquicias-*.jar
```

### 4) Verificar que quedó arriba

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 5) Detener

- Si se ejecutó en foreground: `Ctrl + C`

## Opción B: Despliegue con Docker Compose

### 1) Configurar variables de entorno

```bash
cp .env.template .env
```

### 2) Construir y levantar servicios

```bash
docker compose up -d --build
```

### 3) Verificar estado de contenedores

```bash
docker compose ps
```

### 4) Ver logs

```bash
docker compose logs -f app
docker compose logs -f mysql
```

### 5) Validar endpoints

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 6) Detener servicios

```bash
docker compose down
```

### 7) Limpiar volúmenes (reinicio total de datos)

```bash
docker compose down -v --remove-orphans
```

## Redespliegue recomendado en Docker

```bash
docker compose down -v --remove-orphans && docker compose up -d --build
```

## Notas operativas

- Las migraciones de esquema se ejecutan con Flyway al iniciar (`FRANQUICIAS_FLYWAY_ENABLED=true`).
- En Docker, la app usa `mysql` como host interno de base de datos.
- Los logs en Docker se persisten en el volumen `app_logs`.
