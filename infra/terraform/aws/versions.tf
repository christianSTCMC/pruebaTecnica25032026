terraform {
  # Restringimos versiones para mantener compatibilidad estable con el proveedor AWS.
  required_version = ">= 1.5.0, < 2.0.0"

  # Integración con Terraform Cloud usando la organización detectada.
  cloud {
    organization = "pruebaTecnica"

    workspaces {
      name = "WorkspacePruebaTecnica"
    }
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }

    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0"
    }
  }
}
