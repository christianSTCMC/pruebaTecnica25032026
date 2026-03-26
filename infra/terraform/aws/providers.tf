provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Proyecto = var.project_name
      Entorno  = var.environment
      Managed  = "terraform"
    }
  }
}
