variable "aws_region" {
  description = "Región AWS donde se desplegará la infraestructura."
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Nombre del proyecto para etiquetar recursos."
  type        = string
  default     = "api-franquicias"
}

variable "environment" {
  description = "Ambiente lógico para etiquetas (dev, qa, prod)."
  type        = string
  default     = "dev"
}

variable "instance_name" {
  description = "Nombre de la instancia EC2."
  type        = string
  default     = "api-franquicias-ec2"
}

variable "instance_type" {
  description = "Tipo de instancia EC2. Se recomienda t3.small o t3.medium."
  type        = string
  default     = "t3.small"

  validation {
    condition     = contains(["t3.small", "t3.medium"], var.instance_type)
    error_message = "instance_type debe ser t3.small o t3.medium."
  }
}

variable "key_pair_name" {
  description = "Nombre del key pair que Terraform creará para conexión SSH."
  type        = string
  default     = "api-franquicias-key-20260325"
}

variable "allowed_ssh_cidr" {
  description = "CIDR autorizado para SSH (ejemplo: 190.10.20.30/32)."
  type        = string
}

variable "allowed_api_cidrs" {
  description = "CIDRs autorizados al puerto 8080 de la API."
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "root_volume_size" {
  description = "Tamaño en GB del disco raíz de la instancia."
  type        = number
  default     = 20
}
