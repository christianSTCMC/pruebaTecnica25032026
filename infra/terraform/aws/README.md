# Terraform AWS - EC2 para API Franquicias

Este stack crea una instancia **Ubuntu 22.04** en AWS con estas reglas:

- Puerto `22` abierto solo a tu IP (`allowed_ssh_cidr`).
- Puerto `8080` abierto públicamente (`allowed_api_cidrs`).
- IP pública fija mediante **Elastic IP** (se mantiene al apagar/encender).
- No se define ninguna regla para `3306` (queda cerrado).

## Requisitos

- Terraform `>= 1.5`
- AWS CLI configurado con credenciales válidas (`aws configure` o perfil IAM)
- No se requiere crear `key pair` manual: Terraform lo genera automáticamente.

## Uso rápido

1. Entrar al directorio:

```bash
cd infra/terraform/aws
```

2. Preparar variables:

```bash
cp terraform.tfvars.example terraform.tfvars
```

3. Configurar Terraform Cloud en `versions.tf`:

- Organización: `pruebaTecnica`.
- Workspace: `WorkspacePruebaTecnica`.

4. Reemplazar tu IP pública actual:

```bash
MY_IP="$(curl -s https://checkip.amazonaws.com | tr -d '\n')"
sed -i "s|190.10.20.30/32|${MY_IP}/32|g" terraform.tfvars
```

5. Inicializar y previsualizar:

```bash
terraform init
terraform plan
```

6. Aplicar:

```bash
terraform apply
```

7. Ver salida:

```bash
terraform output
```

## Destruir recursos

```bash
terraform destroy
```

## Nota operativa

Este stack crea infraestructura base (EC2 + red de acceso).  
Luego debes conectarte por SSH a la instancia para instalar Docker y levantar tu app con `docker compose`.

## Operación diaria

Para comandos de apagar, encender, reiniciar y resetear base de datos, revisa:

- [Guía de operación Terraform AWS](../../../DOCS/comandos-operacion-terraform-aws.md)
