# API de Franquicias

Servicio backend para gestionar franquicias, sucursales y productos, incluyendo la consulta del producto con mayor stock por sucursal para una franquicia específica.

## Guías de ejecución (inicio rápido)

- [Comandos SQL](DOCS/comandos-sql.md)
- [Ejecución manual (sin Docker)](DOCS/ejecucion-manual.md)
- [Ejecución con Docker](DOCS/ejecucion-docker.md)
- [Infraestructura AWS con Terraform](infra/terraform/aws/README.md)
- [Operación Terraform AWS (start/stop/reboot/reset DB)](DOCS/comandos-operacion-terraform-aws.md)

## Estado actual del proyecto

Fecha de corte documentada: **25 de marzo de 2026**.

### Estado técnico de la API

- Arquitectura backend implementada con `Spring Boot 3.5.12` y `Java 21`.
- Capa HTTP reactiva con `Spring WebFlux`.
- Persistencia relacional con `Spring Data JPA` + `MySQL`.
- Migraciones versionadas con `Flyway`.
- Contrato OpenAPI publicado con `springdoc-openapi`.
- Logging estructurado con separación por categorías (`backend-api`, `db`, `error`).
- Suite de pruebas verificada: **45 pruebas, 0 fallas, 0 errores**.

### Estado de ejecución y despliegue

- Ejecución local sin Docker documentada y validada ([guía](DOCS/ejecucion-manual.md)).
- Ejecución local con `Docker Compose` implementada para `app + mysql` ([guía](DOCS/ejecucion-docker.md)).
- Infraestructura base en AWS aprovisionable con Terraform (`EC2 Ubuntu 22.04 + Elastic IP + Security Group`) en [`infra/terraform/aws`](infra/terraform/aws/README.md).
- Operación de ambiente AWS documentada (start/stop/reboot de EC2 y reset de MySQL en Docker) en [guía operativa](DOCS/comandos-operacion-terraform-aws.md).
- En el stack Terraform, MySQL no se expone por `3306` en el Security Group; la exposición pública está enfocada al puerto `8080` de la API.

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

Los comandos de ejecución se separaron en documentos dedicados:

- [Comandos SQL](DOCS/comandos-sql.md)
- [Ejecución manual (sin Docker)](DOCS/ejecucion-manual.md)
- [Ejecución con Docker](DOCS/ejecucion-docker.md)
- [Infraestructura AWS con Terraform](infra/terraform/aws/README.md)
- [Operación Terraform AWS (start/stop/reboot/reset DB)](DOCS/comandos-operacion-terraform-aws.md)

## Ejecutar pruebas

Las pruebas usan H2 en memoria en entorno de test (`src/test/resources/application.yml`), por lo que no requieren MySQL local para correr.

```bash
./mvnw test
```

Reportes generados por Maven Surefire:

- `target/surefire-reports/`

## Probar API con Postman

### Colecciones disponibles

- `postman/APIFranquicias-SemillaDB2-10-200.postman_collection.json`
- `postman/api-franquicias.postman_collection.json`

### Uso recomendado en Postman Desktop

1. Levantar la API en `http://localhost:8080`.
2. Importar ambas colecciones.
3. En cada colección, validar variable `baseUrl=http://localhost:8080`.
4. Ejecutar primero la colección de seed en este orden de carpetas:
   - `01 - Seed Franquicias (2)`
   - `02 - Seed Sucursales (10)`
   - `03 - Seed Productos (200)`
   - `04 - Actualizar Nombres (Nuevos Servicios)` (opcional)
5. Luego usar la colección funcional `api-franquicias.postman_collection.json`.

> Nota: en la colección funcional los IDs (`franquiciaId`, `sucursalId`, `productoId`) traen valores de ejemplo; reemplázalos por IDs reales creados en tu ejecución.

### Ejecución opcional por CLI con Newman

```bash
newman run postman/APIFranquicias-SemillaDB2-10-200.postman_collection.json --env-var baseUrl=http://localhost:8080
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
- Operaciones de persistencia se encapsulan en `Schedulers.boundedElastic()` para evitar bloquear el event loop de WebFlux.

## Reglas funcionales y de consistencia

- `nombre` es obligatorio para franquicia, sucursal y producto.
- `stock` debe ser mayor o igual a `0`.
- Un producto pertenece a una única sucursal.
- Una sucursal pertenece a una única franquicia.
- No se permiten franquicias con nombre duplicado.
- No se permiten sucursales con nombre duplicado.
- No se permiten productos duplicados por `(sucursal_id, nombre)`.
- En empate de stock máximo por sucursal, se selecciona el producto con nombre alfabéticamente menor.
- En la consulta agregada anidada se listan todas las sucursales de la franquicia; si una sucursal no tiene productos, se retorna con `productos: []`.
- En la consulta agregada en formato plano se listan productos ganadores por sucursal (solo sucursales con producto).

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
- [Comandos SQL](DOCS/comandos-sql.md)
- [Ejecución manual (sin Docker)](DOCS/ejecucion-manual.md)
- [Ejecución con Docker](DOCS/ejecucion-docker.md)
- [Infraestructura AWS con Terraform](infra/terraform/aws/README.md)
- [Operación Terraform AWS (start/stop/reboot/reset DB)](DOCS/comandos-operacion-terraform-aws.md)
- [Despliegue local y Docker](DOCS/05-despliegue-local-y-docker.md)
- [Estado técnico y calidad](DOCS/estado-actual-y-calidad.md)

## Artefactos del proyecto

- Colección Postman funcional: `postman/api-franquicias.postman_collection.json`
- Colección Postman para carga de datos: `postman/APIFranquicias-SemillaDB2-10-200.postman_collection.json`
- Migración base de esquema: `src/main/resources/db/migration/V1__crear_esquema_inicial_franquicias.sql`

## Brechas conocidas del estado actual

- Este repositorio corresponde a una **prueba técnica** y no a una configuración productiva endurecida.
- Existe despliegue cloud base con Terraform (`infra/terraform/aws`) para ambiente de prueba (`EC2 + MySQL en Docker`).
- La persistencia de nivel productivo aún está pendiente (por ejemplo `RDS/Aurora`, `Multi-AZ`, backups administrados y failover).
- No se incluye pipeline CI/CD en el estado actual.
- Seguridad de acceso pendiente: la API no implementa autenticación/autorización robusta (por ejemplo `OAuth2`/`JWT`) ni control por roles/permisos (`RBAC`).
- Seguridad de transporte pendiente: no se configura `HTTPS/TLS` de extremo a extremo en este setup local.
- Base de datos expuesta para entorno local: en `docker-compose` se publica `MySQL` en puerto host (`3306`) para facilitar pruebas; en producción debe restringirse/red privada.
- Gestión de secretos pendiente: credenciales en `.env`/`.env.template` con valores de ejemplo; para producción se requiere secret manager y rotación.
- Protección operativa pendiente: faltan controles de seguridad como `rate limiting`, políticas CORS estrictas por entorno y protección adicional de abuso.
- Falta estrategia formal de seguridad aplicada de punta a punta (escaneo de dependencias, hardening de contenedores e integración de controles en CI/CD).
