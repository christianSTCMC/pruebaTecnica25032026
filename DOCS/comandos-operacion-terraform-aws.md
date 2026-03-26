# Comandos de Operación Terraform AWS

## Objetivo

Tener comandos rápidos para operar el entorno cloud desplegado con Terraform:

- apagar instancia EC2
- encender instancia EC2
- reiniciar instancia EC2
- borrar la base de datos MySQL (contenedor Docker en EC2)

## Importante

En este proyecto, la base de datos no es RDS; corre en Docker dentro de la EC2.  
La IP pública queda fija porque Terraform asigna Elastic IP.

## 0) Autenticación AWS (sin hardcodear secretos)

Ejecuta este bloque primero en tu terminal:

### Opción A: Perfil local recomendado

```bash
aws configure --profile operacion-aws
export AWS_PROFILE='operacion-aws'
export AWS_REGION='us-east-1'
export AWS_DEFAULT_REGION='us-east-1'

aws sts get-caller-identity
```

### Opción B: Variables de entorno (solo sesión actual)

```bash
export AWS_ACCESS_KEY_ID='<TU_AWS_ACCESS_KEY_ID>'
export AWS_SECRET_ACCESS_KEY='<TU_AWS_SECRET_ACCESS_KEY>'
export AWS_DEFAULT_REGION='us-east-1'
export AWS_REGION='us-east-1'
# export AWS_SESSION_TOKEN='<TU_AWS_SESSION_TOKEN>' # solo si aplica

aws sts get-caller-identity
```

Si el último comando responde JSON con `Account` y `Arn`, ya quedó autenticado.

## 1) Variables base (ejecutar después de autenticar)

```bash
cd infra/terraform/aws

export AWS_REGION="${AWS_REGION:-us-east-1}"
INSTANCE_ID="$(terraform output -raw instance_id)"
INSTANCE_IP="$(terraform output -raw instance_public_ip)"

KEY_FILE="/tmp/api-franquicias-key.pem"
terraform output -raw ssh_private_key_pem > "$KEY_FILE"
chmod 600 "$KEY_FILE"

echo "INSTANCE_ID=$INSTANCE_ID"
echo "INSTANCE_IP=$INSTANCE_IP"
```

## 2) Apagar instancia (stop)

```bash
aws ec2 stop-instances \
  --region "$AWS_REGION" \
  --instance-ids "$INSTANCE_ID"

aws ec2 wait instance-stopped \
  --region "$AWS_REGION" \
  --instance-ids "$INSTANCE_ID"
```

Comando directo (sin variables), útil para troubleshooting:

```bash
aws ec2 stop-instances --region us-east-1 --instance-ids i-082448d50776e069e
```

## 3) Encender instancia (start)

```bash
aws ec2 start-instances \
  --region "$AWS_REGION" \
  --instance-ids "$INSTANCE_ID"

aws ec2 wait instance-running \
  --region "$AWS_REGION" \
  --instance-ids "$INSTANCE_ID"

aws ec2 wait instance-status-ok \
  --region "$AWS_REGION" \
  --instance-ids "$INSTANCE_ID"
```

## 4) Reiniciar instancia (reboot)

```bash
aws ec2 reboot-instances \
  --region "$AWS_REGION" \
  --instance-ids "$INSTANCE_ID"

aws ec2 wait instance-status-ok \
  --region "$AWS_REGION" \
  --instance-ids "$INSTANCE_ID"
```

## 5) Borrar base de datos MySQL (reset)

Este comando elimina volúmenes Docker del compose (incluye `mysql_data`) y vuelve a levantar la app.

```bash
ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null \
  -i "$KEY_FILE" ubuntu@"$INSTANCE_IP" \
  'cd ~/app && sudo docker compose down -v --remove-orphans && sudo docker compose up -d --build'
```

## 6) Verificar API arriba

```bash
curl -i "http://$INSTANCE_IP:8080/v3/api-docs"
curl -i "http://$INSTANCE_IP:8080/swagger-ui.html"
```

## 7) Borrar toda la infraestructura Terraform

```bash
cd infra/terraform/aws
terraform destroy -auto-approve
```
