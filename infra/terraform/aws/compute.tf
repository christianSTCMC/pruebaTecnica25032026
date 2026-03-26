# Consultamos el AMI oficial más reciente de Ubuntu 22.04 vía SSM.
data "aws_ssm_parameter" "ubuntu_2204_ami" {
  name = "/aws/service/canonical/ubuntu/server/22.04/stable/current/amd64/hvm/ebs-gp2/ami-id"
}

# Generamos una llave SSH para no depender de recursos manuales externos.
resource "tls_private_key" "ssh" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

# Publicamos la llave pública en AWS para asociarla a la instancia EC2.
resource "aws_key_pair" "generated" {
  key_name   = var.key_pair_name
  public_key = tls_private_key.ssh.public_key_openssh
}

resource "aws_instance" "api" {
  ami                         = data.aws_ssm_parameter.ubuntu_2204_ami.value
  instance_type               = var.instance_type
  subnet_id                   = local.selected_subnet_id
  key_name                    = aws_key_pair.generated.key_name
  vpc_security_group_ids      = [aws_security_group.api.id]
  associate_public_ip_address = true

  # Forzamos IMDSv2 para mejorar seguridad de metadatos.
  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "required"
  }

  # Capa mínima de hardening de almacenamiento.
  root_block_device {
    volume_size           = var.root_volume_size
    volume_type           = "gp3"
    encrypted             = true
    delete_on_termination = true
  }

  tags = {
    Name = var.instance_name
  }
}
