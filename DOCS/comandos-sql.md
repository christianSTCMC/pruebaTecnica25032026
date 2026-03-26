# Comandos SQL

## Cuándo usar esta guía

- Solo si vas a trabajar con `FRANQUICIAS_FLYWAY_ENABLED=false`.
- Si `FRANQUICIAS_FLYWAY_ENABLED=true`, Flyway ejecuta migraciones al iniciar la API.

## 1) Crear base y usuario (MySQL local)

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS franquicias; CREATE USER IF NOT EXISTS 'franquicias_user'@'%' IDENTIFIED BY 'franquicias_pass'; GRANT ALL PRIVILEGES ON franquicias.* TO 'franquicias_user'@'%'; FLUSH PRIVILEGES;"
```

## 2) Ejecutar migraciones SQL manuales (MySQL local)

```bash
mysql -h 127.0.0.1 -P 3306 -u franquicias_user -p franquicias < src/main/resources/db/migration/V1__crear_esquema_inicial_franquicias.sql
mysql -h 127.0.0.1 -P 3306 -u franquicias_user -p franquicias < src/main/resources/db/migration/V2__agregar_unicidad_nombres_franquicia_sucursal.sql
```

## 3) Verificar tablas (MySQL local)

```bash
mysql -h 127.0.0.1 -P 3306 -u franquicias_user -p -D franquicias -e "SHOW TABLES;"
```

## 4) Ejecutar migraciones SQL manuales en Docker

Primero levanta solo `mysql`:

```bash
cp .env.template .env
docker compose up -d mysql
```

Luego ejecuta scripts:

```bash
docker compose exec -T mysql sh -lc 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < src/main/resources/db/migration/V1__crear_esquema_inicial_franquicias.sql
docker compose exec -T mysql sh -lc 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < src/main/resources/db/migration/V2__agregar_unicidad_nombres_franquicia_sucursal.sql
```

Verificación:

```bash
docker compose exec -T mysql sh -lc 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" -e "SHOW TABLES;"'
```
