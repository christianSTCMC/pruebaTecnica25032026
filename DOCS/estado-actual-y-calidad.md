# Estado actual y calidad técnica

## Corte de estado

Fecha de consolidación: **25 de marzo de 2026**.

Este documento resume el estado observable del proyecto a nivel de implementación, pruebas y estabilidad técnica.

## Resumen de implementación

- API REST de franquicias/sucursales/productos implementada.
- Consulta agregada de mayor stock por sucursal implementada con desempate determinístico.
- Consulta agregada en formato de listado plano implementada para franquicia puntual.
- Endpoints de listados independientes implementados.
- Migración de esquema inicial implementada con Flyway.
- Guía de despliegue documentada para ejecución local sin Docker y con Docker Compose.
- Manejo global de errores homogéneo implementado.
- OpenAPI publicado y validado por pruebas.
- Logging por request y ciclo de vida implementado.

## Validación de calidad (resultado de pruebas)

Ejecución validada el **25 de marzo de 2026**:

- Total: `45`
- Fallas: `0`
- Errores: `0`
- Skipped: `0`

### Desglose por suite

| Suite | Tipo | Pruebas |
| --- | --- | --- |
| `ApiFranquiciasApplicationTests` | Contexto/configuración | 1 |
| `FranquiciaSucursalEndpointsIntegrationTest` | Integración HTTP | 11 |
| `FranquiciaSucursalDuplicidadNombresIntegrationTest` | Integración HTTP | 4 |
| `ProductoEndpointsIntegrationTest` | Integración HTTP | 11 |
| `ProductoActualizarNombreEndpointsIntegrationTest` | Integración HTTP | 4 |
| `FranquiciaMayorStockPorSucursalIntegrationTest` | Integración HTTP | 3 |
| `ListadosIndependientesEndpointsIntegrationTest` | Integración HTTP | 3 |
| `OpenApiDocumentationIntegrationTest` | Integración contrato OpenAPI | 1 |
| `RequestLoggingFilterIntegrationTest` | Integración observabilidad HTTP | 1 |
| `ProductoRepositoryJpaIntegrationTest` | Integración persistencia | 3 |
| `GlobalExceptionHandlerTest` | Unit test | 3 |

## Cobertura funcional validada por pruebas

- Creación de franquicia con validación de nombre.
- Restricción de duplicidad de franquicia por nombre.
- Actualización de nombre de franquicia con validación de datos y recurso.
- Creación de sucursal con control de franquicia inexistente.
- Restricción de duplicidad de sucursal por nombre.
- Creación de producto con control de sucursal inexistente.
- Actualización de nombre de sucursal con validación de datos y recurso.
- Actualización de nombre de producto con control de duplicidad por sucursal.
- Restricción de duplicidad de producto por sucursal.
- Actualización de stock con validaciones de datos y recurso.
- Eliminación de producto con validación de pertenencia a sucursal.
- Consulta de mayor stock por sucursal con desempate alfabético.
- Consulta de mayor stock por sucursal en formato plano incluyendo sucursal por producto.
- Listados independientes ordenados alfabéticamente.
- Publicación del contrato OpenAPI con esquemas esperados.
- Formato de errores homogéneo y propagación de `X-Request-Id`.

## Estado de observabilidad

- Logs de request HTTP con `requestId`, método, ruta, estado y latencia.
- Logs de ciclo de vida de aplicación (`application_ready`, `application_stopping`).
- Configuración de salida por categorías:
  - `backend-api`
  - `db`
  - `error`

## Estado de arquitectura y mantenibilidad

- Separación por contextos (`franquicia`, `sucursal`, `producto`, `compartido`).
- Separación por capas (`domain`, `application`, `infrastructure`).
- Servicios de aplicación delgados y orientados a caso de uso.
- Reglas de negocio reforzadas tanto en capa de aplicación como en persistencia.
