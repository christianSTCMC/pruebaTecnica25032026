# Ejecución con Docker

## Objetivo

Levantar API + MySQL usando contenedores Docker Compose.

## Requisitos

- `Docker Engine`
- `Docker Compose` (plugin `docker compose`)

## 1) Preparar variables

```bash
cp .env.template .env
```

`.env` es el archivo local efectivo que usa `docker compose`.

## 2) Levantar servicios

```bash
docker compose up -d --build
```

## 3) Verificar estado

```bash
docker compose ps
```

## 4) Revisar logs

```bash
docker compose logs -f app
docker compose logs -f mysql
```

## 5) Validar endpoints

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 6) Detener servicios

```bash
docker compose down
```

## Reinicio limpio (elimina volúmenes)

```bash
docker compose down -v --remove-orphans
docker compose up -d --build
```
