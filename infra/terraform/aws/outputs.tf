output "instance_id" {
  description = "ID de la instancia EC2 creada."
  value       = aws_instance.api.id
}

output "instance_public_ip" {
  description = "IP pública fija (Elastic IP) para acceso a la API."
  value       = aws_eip.api.public_ip
}

output "instance_public_dns" {
  description = "DNS público de la instancia."
  value       = aws_instance.api.public_dns
}

output "security_group_id" {
  description = "ID del Security Group aplicado a la instancia."
  value       = aws_security_group.api.id
}

output "key_pair_name" {
  description = "Nombre del key pair generado por Terraform."
  value       = aws_key_pair.generated.key_name
}

output "api_base_url" {
  description = "URL base esperada de la API desplegada en la instancia."
  value       = format("http://%s:8080", aws_eip.api.public_ip)
}

output "ssh_command" {
  description = "Comando de referencia para conexión SSH."
  value       = format("ssh -i <ruta_llave_privada.pem> ubuntu@%s", aws_eip.api.public_ip)
}

output "ssh_private_key_pem" {
  description = "Llave privada para acceso SSH. Trátala como confidencial."
  value       = tls_private_key.ssh.private_key_pem
  sensitive   = true
}
