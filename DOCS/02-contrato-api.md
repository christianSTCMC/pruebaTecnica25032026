# Contrato API

## Convenciones generales

- Base path: `/api/v1`
- Formato de intercambio: `application/json`
- Identificadores: `UUID`
- Estructura de error estándar: `ApiErrorResponse`

## Recursos y endpoints

### 1) Franquicias

#### `POST /api/v1/franquicias`

Crea una franquicia.

Request body:

```json
{
  "nombre": "Franquicia Centro"
}
```

Respuestas:

- `201 Created`: franquicia creada.
- `400 Bad Request`: nombre inválido o faltante.
- `409 Conflict`: nombre de franquicia duplicado.

Response `201`:

```json
{
  "id": "c8b894eb-6b1f-46f0-8792-82e28495b2a2",
  "nombre": "Franquicia Centro"
}
```

#### `GET /api/v1/franquicias`

Lista franquicias ordenadas por `nombre` ascendente.

Respuestas:

- `200 OK`

Response `200`:

```json
[
  {
    "id": "9b6b6f7d-f276-4b30-9275-c4e695f72af8",
    "nombre": "Franquicia Centro"
  }
]
```

#### `PATCH /api/v1/franquicias/{franquiciaId}/nombre`

Actualiza el nombre de una franquicia existente.

Path params:

- `franquiciaId` (`UUID`)

Request body:

```json
{
  "nombre": "Franquicia Centro Renombrada"
}
```

Respuestas:

- `200 OK`: nombre de franquicia actualizado.
- `400 Bad Request`: `franquiciaId` inválido o `nombre` inválido.
- `404 Not Found`: franquicia inexistente.
- `409 Conflict`: nombre de franquicia duplicado.

Response `200`:

```json
{
  "id": "9b6b6f7d-f276-4b30-9275-c4e695f72af8",
  "nombre": "Franquicia Centro Renombrada"
}
```

### 2) Sucursales

#### `POST /api/v1/franquicias/{franquiciaId}/sucursales`

Crea una sucursal asociada a la franquicia indicada.

Path params:

- `franquiciaId` (`UUID`)

Request body:

```json
{
  "nombre": "Sucursal Norte"
}
```

Respuestas:

- `201 Created`: sucursal creada.
- `400 Bad Request`: `franquiciaId` inválido o `nombre` inválido.
- `404 Not Found`: franquicia inexistente.
- `409 Conflict`: nombre de sucursal duplicado.

Response `201`:

```json
{
  "id": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8",
  "nombre": "Sucursal Norte",
  "franquiciaId": "9b6b6f7d-f276-4b30-9275-c4e695f72af8"
}
```

#### `PATCH /api/v1/sucursales/{sucursalId}/nombre`

Actualiza el nombre de una sucursal existente.

Path params:

- `sucursalId` (`UUID`)

Request body:

```json
{
  "nombre": "Sucursal Norte Renombrada"
}
```

Respuestas:

- `200 OK`: nombre de sucursal actualizado.
- `400 Bad Request`: `sucursalId` inválido o `nombre` inválido.
- `404 Not Found`: sucursal inexistente.
- `409 Conflict`: nombre de sucursal duplicado.

Response `200`:

```json
{
  "id": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8",
  "nombre": "Sucursal Norte Renombrada",
  "franquiciaId": "9b6b6f7d-f276-4b30-9275-c4e695f72af8"
}
```

#### `GET /api/v1/sucursales`

Lista sucursales ordenadas por `nombre` ascendente.

Respuestas:

- `200 OK`

Response `200`:

```json
[
  {
    "id": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8",
    "nombre": "Sucursal Norte",
    "franquiciaId": "9b6b6f7d-f276-4b30-9275-c4e695f72af8"
  }
]
```

### 3) Productos

#### `POST /api/v1/sucursales/{sucursalId}/productos`

Crea un producto en la sucursal indicada.

Path params:

- `sucursalId` (`UUID`)

Request body:

```json
{
  "nombre": "Laptop Gamer",
  "stock": 15
}
```

Respuestas:

- `201 Created`: producto creado.
- `400 Bad Request`: datos inválidos (`nombre`, `stock`, UUID inválido).
- `404 Not Found`: sucursal inexistente.
- `409 Conflict`: nombre de producto duplicado en la sucursal.

Response `201`:

```json
{
  "id": "1fb5fdb8-9d84-4c9f-bf56-a0cd0f5d4e89",
  "nombre": "Laptop Gamer",
  "stock": 15,
  "sucursalId": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8"
}
```

#### `DELETE /api/v1/sucursales/{sucursalId}/productos/{productoId}`

Elimina un producto validando pertenencia a la sucursal indicada.

Path params:

- `sucursalId` (`UUID`)
- `productoId` (`UUID`)

Respuestas:

- `204 No Content`: eliminado.
- `400 Bad Request`: UUID inválido.
- `404 Not Found`: sucursal o producto inexistente.
- `409 Conflict`: el producto existe, pero no pertenece a la sucursal indicada.

#### `PATCH /api/v1/productos/{productoId}/nombre`

Actualiza el nombre de un producto.

Path params:

- `productoId` (`UUID`)

Request body:

```json
{
  "nombre": "Laptop Gamer Pro"
}
```

Respuestas:

- `200 OK`: nombre actualizado.
- `400 Bad Request`: nombre inválido o UUID inválido.
- `404 Not Found`: producto inexistente.
- `409 Conflict`: nombre de producto duplicado en la misma sucursal.

Response `200`:

```json
{
  "id": "1fb5fdb8-9d84-4c9f-bf56-a0cd0f5d4e89",
  "nombre": "Laptop Gamer Pro",
  "stock": 30,
  "sucursalId": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8"
}
```

#### `PATCH /api/v1/productos/{productoId}/stock`

Actualiza el stock de un producto.

Path params:

- `productoId` (`UUID`)

Request body:

```json
{
  "stock": 30
}
```

Respuestas:

- `200 OK`: stock actualizado.
- `400 Bad Request`: stock inválido o UUID inválido.
- `404 Not Found`: producto inexistente.

Response `200`:

```json
{
  "id": "1fb5fdb8-9d84-4c9f-bf56-a0cd0f5d4e89",
  "nombre": "Laptop Gamer",
  "stock": 30,
  "sucursalId": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8"
}
```

#### `GET /api/v1/productos`

Lista productos ordenados por `nombre` ascendente.

Respuestas:

- `200 OK`

Response `200`:

```json
[
  {
    "id": "1fb5fdb8-9d84-4c9f-bf56-a0cd0f5d4e89",
    "nombre": "Laptop Gamer",
    "stock": 30,
    "sucursalId": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8"
  }
]
```

### 4) Consulta agregada

#### `GET /api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal`

Retorna un producto por sucursal (el de mayor stock) para la franquicia indicada.

Path params:

- `franquiciaId` (`UUID`)

Respuestas:

- `200 OK`
- `400 Bad Request`: UUID inválido.
- `404 Not Found`: franquicia inexistente.

Response `200`:

```json
{
  "franquiciaId": "9b6b6f7d-f276-4b30-9275-c4e695f72af8",
  "franquiciaNombre": "Franquicia Centro",
  "sucursales": [
    {
      "sucursalId": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8",
      "sucursalNombre": "Sucursal Norte",
      "productos": [
        {
          "productoId": "1fb5fdb8-9d84-4c9f-bf56-a0cd0f5d4e89",
          "productoNombre": "Laptop Gamer",
          "stock": 30
        }
      ]
    }
  ]
}
```

Notas del contrato:

- El arreglo `sucursales` se ordena por nombre de sucursal ascendente.
- En empate de stock, se elige el producto con nombre alfabéticamente menor.
- Las sucursales sin productos aparecen con `productos: []`.

#### `GET /api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal/listado`

Retorna la misma consulta agregada, pero en formato de listado plano de productos, incluyendo la sucursal a la que pertenece cada producto.

Path params:

- `franquiciaId` (`UUID`)

Respuestas:

- `200 OK`
- `400 Bad Request`: UUID inválido.
- `404 Not Found`: franquicia inexistente.

Response `200`:

```json
{
  "franquiciaId": "9b6b6f7d-f276-4b30-9275-c4e695f72af8",
  "franquiciaNombre": "Franquicia Centro",
  "productos": [
    {
      "sucursalId": "65906ceb-9b5f-481f-82f7-7f52ad7be4f8",
      "sucursalNombre": "Sucursal Norte",
      "productoId": "1fb5fdb8-9d84-4c9f-bf56-a0cd0f5d4e89",
      "productoNombre": "Laptop Gamer",
      "stock": 30
    }
  ]
}
```

Notas del contrato:

- El arreglo `productos` se ordena por nombre de sucursal ascendente.
- En empate de stock, se elige el producto con nombre alfabéticamente menor.
- Las sucursales sin productos no aparecen en esta respuesta.

## Estructura de error estándar

`ApiErrorResponse`:

```json
{
  "timestamp": "2026-03-25T20:32:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "El stock debe ser mayor o igual a 0",
  "path": "/api/v1/productos/1fb5fdb8-9d84-4c9f-bf56-a0cd0f5d4e89/stock"
}
```

Campos:

- `timestamp`: fecha/hora UTC del error.
- `status`: código HTTP.
- `error`: texto estándar HTTP.
- `message`: detalle de validación o negocio.
- `path`: ruta solicitada.
