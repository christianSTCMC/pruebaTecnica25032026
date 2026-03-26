# Usamos la VPC por defecto para minimizar fricción y desplegar rápido.
data "aws_vpc" "default" {
  default = true
}

# Tomamos subredes por defecto asociadas a la VPC default.
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }

  filter {
    name   = "default-for-az"
    values = ["true"]
  }
}

locals {
  # Ordenamos IDs para que la selección sea determinística entre ejecuciones.
  selected_subnet_id = sort(data.aws_subnets.default.ids)[0]
}

resource "aws_security_group" "api" {
  name        = "${var.project_name}-${var.environment}-sg"
  description = "Reglas para API Franquicias"
  vpc_id      = data.aws_vpc.default.id

  # SSH solo desde una IP/CIDR específico, evitando exposición global.
  ingress {
    description = "SSH restringido"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.allowed_ssh_cidr]
  }

  # Puerto público de la API según requerimiento (sin abrir MySQL 3306).
  ingress {
    description = "API HTTP 8080"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = var.allowed_api_cidrs
  }

  # Egreso libre para instalación de paquetes y salida a internet.
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.instance_name}-sg"
  }
}
