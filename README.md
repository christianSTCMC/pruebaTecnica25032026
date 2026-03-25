# API de Franquicias

Servicio backend para gestionar franquicias, sucursales y productos, incluyendo la consulta del producto con mayor stock por sucursal para una franquicia específica.

## Estado actual del proyecto

Fecha de corte documentada: **25 de marzo de 2026**.

- Arquitectura backend implementada con `Spring Boot 3.5.12` y `Java 21`.
- Capa HTTP reactiva con `Spring WebFlux`.
- Persistencia relacional con `Spring Data JPA` + `MySQL`.
- Migraciones versionadas con `Flyway`.
- Contrato OpenAPI publicado con `springdoc-openapi`.
- Logging estructurado con separación por categorías (`backend-api`, `db`, `error`).
- Suite de pruebas verificada en esta fecha: **45 pruebas, 0 fallas, 0 errores**.

## Alcance funcional implementado

Se encuentran implementados los criterios funcionales del reto para gestión de franquicias, sucursales y productos:

- Crear franquicia.
- Actualizar nombre de franquicia.
- Crear sucursal en una franquicia.
- Actualizar nombre de sucursal.
- Crear producto en una sucursal.
- Actualizar nombre de producto.
- Eliminar producto de una sucursal.
- Actualizar stock de producto.
- Consultar mayor stock por sucursal para una franquicia.
- Consultar mayor stock por sucursal para una franquicia en formato de listado plano.
- Listados independientes adicionales de franquicias, sucursales y productos.

### Endpoints disponibles actualmente

- `POST /api/v1/franquicias`
- `GET /api/v1/franquicias`
- `PATCH /api/v1/franquicias/{franquiciaId}/nombre`
- `POST /api/v1/franquicias/{franquiciaId}/sucursales`
- `PATCH /api/v1/sucursales/{sucursalId}/nombre`
- `GET /api/v1/sucursales`
- `POST /api/v1/sucursales/{sucursalId}/productos`
- `GET /api/v1/productos`
- `PATCH /api/v1/productos/{productoId}/nombre`
- `DELETE /api/v1/sucursales/{sucursalId}/productos/{productoId}`
- `PATCH /api/v1/productos/{productoId}/stock`
- `GET /api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal`
- `GET /api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal/listado`

## Despliegue rápido

### Paso extra (solo si Flyway está desactivado): generar la base de datos con scripts SQL

**IMPORTANTE:** este paso manual aplica únicamente si `FRANQUICIAS_FLYWAY_ENABLED=false`.  
Si `FRANQUICIAS_FLYWAY_ENABLED=true` (valor por default), Flyway ejecuta las migraciones automáticamente al iniciar la API.

Cuando se haga manual, ejecuta las migraciones ubicadas en `src/main/resources/db/migration` respetando el orden por versión (`V1`, `V2`, ...).

```bash
ls -1 src/main/resources/db/migration/V*.sql
```

#### Aclaración clave: ¿JPA crea las tablas?

- **No.** En este proyecto JPA está configurado con `spring.jpa.hibernate.ddl-auto=validate`, por lo que **solo valida** el esquema existente y no crea tablas.
- La creación automática del esquema la hace **Flyway**, no JPA.
- Por defecto, Flyway está activo con `FRANQUICIAS_FLYWAY_ENABLED=true` (default en `application.yml` y en `.env.template` para Docker).
- En este README se prioriza ejecutarlo manualmente como primer paso para tener control explícito del proceso.

#### Aclaración clave: `.env` vs `.env.template`

- `.env.template` es la **plantilla versionada** de variables de entorno.
- `.env` es el archivo **local efectivo** que `docker compose` lee automáticamente al levantar servicios.
- `.env` no debe versionarse en git (se ignora en `.gitignore`).
- Flujo recomendado: copiar plantilla y editar valores locales.

#### Opción A: MySQL local (manual)

1. Crear base y usuario (si aún no existen):

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS franquicias; CREATE USER IF NOT EXISTS 'franquicias_user'@'%' IDENTIFIED BY 'franquicias_pass'; GRANT ALL PRIVILEGES ON franquicias.* TO 'franquicias_user'@'%'; FLUSH PRIVILEGES;"
```

2. Ejecutar scripts en orden:

```bash
mysql -h 127.0.0.1 -P 3306 -u franquicias_user -p franquicias < src/main/resources/db/migration/V1__crear_esquema_inicial_franquicias.sql
mysql -h 127.0.0.1 -P 3306 -u franquicias_user -p franquicias < src/main/resources/db/migration/V2__agregar_unicidad_nombres_franquicia_sucursal.sql
```

3. Verificar tablas creadas:

```bash
mysql -h 127.0.0.1 -P 3306 -u franquicias_user -p -D franquicias -e "SHOW TABLES;"
```

#### Opción B: MySQL en Docker (manual)

1. Crear `.env` desde la plantilla y levantar solo MySQL:

```bash
cp .env.template .env
docker compose up -d mysql
```

2. Ejecutar scripts en orden dentro del contenedor:

```bash
docker compose exec -T mysql sh -lc 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < src/main/resources/db/migration/V1__crear_esquema_inicial_franquicias.sql
docker compose exec -T mysql sh -lc 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < src/main/resources/db/migration/V2__agregar_unicidad_nombres_franquicia_sucursal.sql
```

3. Verificar tablas creadas:

```bash
docker compose exec -T mysql sh -lc 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" -e "SHOW TABLES;"'
```

### Paso 2: iniciar la API

Configuración recomendada según el flujo que elijas:

- Si ya corriste los scripts manualmente, define `FRANQUICIAS_FLYWAY_ENABLED=false` para evitar que Flyway intente recrear objetos existentes.
- Si no corriste scripts manuales, deja `FRANQUICIAS_FLYWAY_ENABLED=true` (valor por default) para que Flyway los ejecute automáticamente al iniciar la aplicación.

### Local (sin Docker)

1. Exportar variables de conexión:
   - `FRANQUICIAS_DB_JDBC_URL`
   - `FRANQUICIAS_DB_USERNAME`
   - `FRANQUICIAS_DB_PASSWORD`
   - `FRANQUICIAS_FLYWAY_ENABLED=true` (default)  
     Si ya ejecutaste los `.sql` manualmente, usa `FRANQUICIAS_FLYWAY_ENABLED=false`.
2. Ejecutar:

```bash
./mvnw spring-boot:run
```

### Con Docker

1. En `.env`, dejar esta variable según el flujo:
   - `FRANQUICIAS_FLYWAY_ENABLED=true` (default) para ejecución automática con Flyway.
   - `FRANQUICIAS_FLYWAY_ENABLED=false` si ya ejecutaste los `.sql` manualmente.
2. Levantar servicios:

```bash
docker compose up -d --build
```

3. Verificar:
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - OpenAPI: `http://localhost:8080/v3/api-docs`

## Ejecutar pruebas

Las pruebas usan H2 en memoria en entorno de test (`src/test/resources/application.yml`), por lo que no requieren MySQL local para correr.

```bash
./mvnw test
```

Reportes generados por Maven Surefire:

- `target/surefire-reports/`

## Probar API con Postman

### Colecciones disponibles

- `postman/api-franquicias-seed-10-10-20.postman_collection.json`
- `postman/api-franquicias.postman_collection.json`

### Uso recomendado en Postman Desktop

1. Levantar la API en `http://localhost:8080`.
2. Importar ambas colecciones.
3. En cada colección, validar variable `baseUrl=http://localhost:8080`.
4. Ejecutar primero la colección de seed en este orden de carpetas:
   - `01 - Seed Franquicias (10)`
   - `02 - Seed Sucursales (10)`
   - `03 - Seed Productos (20)`
   - `04 - Actualizar Nombres (Nuevos Servicios)` (opcional)
5. Luego usar la colección funcional `api-franquicias.postman_collection.json`.

> Nota: en la colección funcional los IDs (`franquiciaId`, `sucursalId`, `productoId`) traen valores de ejemplo; reemplázalos por IDs reales creados en tu ejecución.

### Ejecución opcional por CLI con Newman

```bash
newman run postman/api-franquicias-seed-10-10-20.postman_collection.json --env-var baseUrl=http://localhost:8080
newman run postman/api-franquicias.postman_collection.json --env-var baseUrl=http://localhost:8080 --env-var franquiciaId=<UUID_REAL> --env-var sucursalId=<UUID_REAL> --env-var productoId=<UUID_REAL>
```

## Arquitectura implementada

El proyecto está organizado por contexto funcional (`franquicia`, `sucursal`, `producto`, `compartido`) y por capas tipo Clean Architecture:

- `domain`: núcleo del dominio por contexto.
- `application`: casos de uso y DTOs de aplicación.
- `infrastructure/entrypoint/rest`: controladores HTTP y DTOs públicos.
- `infrastructure/output/persistence`: entidades y repositorios JPA.

Decisión técnica relevante:

- La entrada HTTP es reactiva (`Mono` en controladores y servicios).
- Como JPA es bloqueante, las operaciones de persistencia se encapsulan en `Schedulers.boundedElastic()` para evitar bloquear el event loop de WebFlux.

## Reglas funcionales y de consistencia

- `nombre` es obligatorio para franquicia, sucursal y producto.
- `stock` debe ser mayor o igual a `0`.
- Un producto pertenece a una única sucursal.
- Una sucursal pertenece a una única franquicia.
- No se permiten franquicias con nombre duplicado.
- No se permiten sucursales con nombre duplicado.
- No se permiten productos duplicados por `(sucursal_id, nombre)`.
- En empate de stock máximo por sucursal, se selecciona el producto con nombre alfabéticamente menor.
- En la consulta agregada solo se listan sucursales que tienen productos.

Estas reglas están reforzadas en validaciones de entrada, lógica de aplicación y restricciones de base de datos.

## Manejo de errores y observabilidad

- Formato homogéneo de error (`ApiErrorResponse`) para respuestas `4xx/5xx`.
- Mapeo de negocio principal:
  - `400`: validación/entrada inválida.
  - `404`: recurso no encontrado.
  - `409`: conflictos de negocio.
  - `500`: error interno no controlado.
- `RequestLoggingFilter` propaga/genera `X-Request-Id` y registra método, ruta, estado y latencia.

## Documentación técnica detallada

- [Requerimientos y arquitectura](DOCS/01-requerimientos-y-arquitectura.md)
- [Contrato API](DOCS/02-contrato-api.md)
- [Despliegue local y Docker](DOCS/05-despliegue-local-y-docker.md)
- [Estado técnico y calidad](DOCS/estado-actual-y-calidad.md)

## Artefactos del proyecto

- Colección Postman funcional: `postman/api-franquicias.postman_collection.json`
- Colección Postman para carga de datos: `postman/api-franquicias-seed-10-10-20.postman_collection.json`
- Migración base de esquema: `src/main/resources/db/migration/V1__crear_esquema_inicial_franquicias.sql`

## Brechas conocidas del estado actual

- La persistencia productiva en proveedor cloud no está desplegada en este repositorio; se documenta la estrategia objetivo.
- No se incluye pipeline CI/CD en el estado actual.
