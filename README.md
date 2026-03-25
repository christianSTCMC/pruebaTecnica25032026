# API de Franquicias

## Objetivo

Construir un API REST en `Spring Boot` para administrar franquicias, sus sucursales y los productos ofertados en cada sucursal, incluyendo la consulta del producto con mayor stock por sucursal para una franquicia puntual.

## Problema de negocio

Una franquicia se compone de:

- Un `nombre`
- Un listado de `sucursales`

Una sucursal se compone de:

- Un `nombre`
- Un listado de `productos`

Un producto se compone de:

- Un `nombre`
- Una `cantidad de stock`

## Criterios de aceptación

1. El proyecto debe ser desarrollado en `Spring Boot`.
2. Exponer un endpoint para agregar una nueva franquicia.
3. Exponer un endpoint para agregar una nueva sucursal a una franquicia.
4. Exponer un endpoint para agregar un nuevo producto a una sucursal.
5. Exponer un endpoint para eliminar un producto de una sucursal.
6. Exponer un endpoint para modificar el stock de un producto.
7. Exponer un endpoint para mostrar cuál es el producto con más stock por sucursal dentro de una franquicia puntual.
8. Utilizar un sistema de persistencia como `Redis`, `MySQL`, `MongoDB` o `DynamoDB` en algún proveedor de nube.

## Propuesta técnica documentada

Para reducir ambigüedad antes de escribir código, esta documentación asume la siguiente base técnica:

- `Java 21`
- `Spring Boot 3.x`
- `Spring Web`
- `Spring Data JPA`
- `Spring Validation`
- `MySQL 8` como base de datos principal
- `springdoc-openapi` para documentación de endpoints
- `Docker Compose` para levantar dependencias locales

- `Java 21`
- `Spring Boot 3.5.x`
- `Spring WebFlux` para capa HTTP
- `Spring Data JPA` para persistencia relacional
- `Spring Validation`
- `MySQL 8`
- `Flyway` habilitado con migraciones reales
- `springdoc-openapi` para Swagger UI
- `Maven Wrapper`
- `Docker Compose` para MySQL

> Nota técnica: la entrada HTTP se mantiene en `WebFlux`, mientras la persistencia del MVP usa `JPA` para modelado relacional y restricciones de integridad en base de datos.

## Decisión de persistencia

Se propone `MySQL` como persistencia principal porque:

- El dominio es claramente relacional: una franquicia tiene muchas sucursales y una sucursal tiene muchos productos.
- La consulta de producto con mayor stock por sucursal se resuelve bien con SQL.
- Permite un camino simple desde desarrollo local hasta despliegue en nube con `AWS RDS`, `Cloud SQL` o `Azure Database for MySQL`.

## Alcance funcional inicial

El MVP documentado contempla:

- Crear franquicias
- Crear sucursales dentro de una franquicia
- Crear productos dentro de una sucursal
- Eliminar productos de una sucursal
- Actualizar stock de un producto
- Consultar el producto de mayor stock por sucursal para una franquicia dada

## Resumen de endpoints esperados

- `POST /api/v1/franquicias`
- `POST /api/v1/franquicias/{franquiciaId}/sucursales`
- `POST /api/v1/sucursales/{sucursalId}/productos`
- `DELETE /api/v1/sucursales/{sucursalId}/productos/{productoId}`
- `PATCH /api/v1/productos/{productoId}/stock`
- `GET /api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal`

## Reglas funcionales documentadas

- El `stock` no puede ser negativo.
- El `nombre` de una franquicia es obligatorio.
- El `nombre` de una sucursal es obligatorio.
- El `nombre` de un producto es obligatorio.
- Una sucursal pertenece a una sola franquicia.
- Un producto pertenece a una sola sucursal.
- Dentro de una misma sucursal no debe repetirse el nombre del producto.
- Si dos productos tienen el mismo stock máximo en una sucursal, se elige el de nombre alfabéticamente menor para garantizar respuesta determinística.

## Documentación del proyecto

La documentación para implementar el proyecto en fases quedó organizada así:

- [Requerimientos y Arquitectura](DOCS/01-requerimientos-y-arquitectura.md)
- [Contrato Base del API](DOCS/02-contrato-api.md)
- [Plan de Implementación por Fases](DOCS/03-plan-implementacion-por-fases.md)
- [Prompts para IA por Fase](DOCS/04-prompts-ia-por-fase.md)

## Estructura actual del proyecto

```text
.
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src
│   ├── main
│   │   ├── java/com/accenture/franquicias
│   │   │   ├── ApiFranquiciasApplication.java
│   │   │   ├── compartido/
│   │   │   │   ├── application/
│   │   │   │   ├── domain/
│   │   │   │   └── infrastructure/
│   │   │   │       ├── config/OpenApiConfig.java
│   │   │   │       ├── logging/
│   │   │   │       └── persistence/EntidadAuditada.java
│   │   │   ├── franquicia/
│   │   │   │   ├── application/port/{in,out}/
│   │   │   │   ├── domain/
│   │   │   │   └── infrastructure/output/persistence/
│   │   │   │       ├── entity/FranquiciaEntity.java
│   │   │   │       └── repository/FranquiciaRepositoryJpa.java
│   │   │   ├── producto/
│   │   │   │   ├── application/port/{in,out}/
│   │   │   │   ├── domain/
│   │   │   │   └── infrastructure/output/persistence/
│   │   │   │       ├── entity/ProductoEntity.java
│   │   │   │       └── repository/ProductoRepositoryJpa.java
│   │   │   └── sucursal/
│   │   │       ├── application/port/{in,out}/
│   │   │       ├── domain/
│   │   │       └── infrastructure/output/persistence/
│   │   │           ├── entity/SucursalEntity.java
│   │   │           └── repository/SucursalRepositoryJpa.java
│   │   └── resources
│   │       ├── application.yml
│   │       ├── db/migration/V1__crear_esquema_inicial_franquicias.sql
│   │       └── logback-spring.xml
│   └── test
│       ├── java/com/accenture/franquicias/ApiFranquiciasApplicationTests.java
│       ├── java/com/accenture/franquicias/compartido/infrastructure/logging/RequestLoggingFilterIntegrationTest.java
│       ├── java/com/accenture/franquicias/producto/infrastructure/output/persistence/repository/ProductoRepositoryJpaIntegrationTest.java
│       └── resources/application.yml
└── DOCS
    ├── 01-requerimientos-y-arquitectura.md
    ├── 02-contrato-api.md
    ├── 03-plan-implementacion-por-fases.md
    └── 04-prompts-ia-por-fase.md
```

### Criterio de organización

- `compartido`: piezas transversales como configuración base.
- `franquicia`, `sucursal` y `producto`: módulos por contexto funcional.
- `domain`: reglas puras del dominio.
- `application`: casos de uso y puertos.
- `infrastructure`: adaptadores HTTP y persistencia.

## Cómo validar Fase 1

1. Levantar MySQL local:

   ```bash
   docker compose up -d
   ```

2. Ejecutar pruebas:

   ```bash
   ./mvnw test
   ```

3. Levantar la aplicación (Flyway ejecuta la migración inicial):

   ```bash
   ./mvnw spring-boot:run
   ```

4. Abrir Swagger UI:

   ```text
   http://localhost:8080/swagger-ui.html
   ```

5. Consultar OpenAPI:

   ```text
   http://localhost:8080/v3/api-docs
   ```

> Nota: para compilar necesitas un `JDK 21` completo, no solo un `JRE`.

## Estado actual


- Se mantiene `Spring Boot` con `Maven Wrapper`.
- Se habilitó persistencia relacional con `Spring Data JPA` + `MySQL`.
- Se modelaron entidades `Franquicia`, `Sucursal` y `Producto` con `UUID`.
- Se configuraron relaciones y repositorios de persistencia por módulo.
- Se agregó migración `Flyway` con restricciones de negocio:
  - `stock >= 0`
  - unicidad de producto por sucursal (`UNIQUE(sucursal_id, nombre)`)
  - llaves foráneas entre `franquicias`, `sucursales` y `productos`
- Se mantuvo `OpenAPI/Swagger` y logging base.



