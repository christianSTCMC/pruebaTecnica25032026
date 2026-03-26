# Reserva una IP pública estática para que no cambie entre stop/start.
resource "aws_eip" "api" {
  domain = "vpc"

  tags = {
    Name = "${var.instance_name}-eip"
  }
}

# Asocia la Elastic IP a la instancia de la API.
resource "aws_eip_association" "api" {
  allocation_id = aws_eip.api.id
  instance_id   = aws_instance.api.id
}
