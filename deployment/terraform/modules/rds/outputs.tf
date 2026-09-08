output "endpoint" {
  description = "Connection endpoint for the RDS instance"
  value       = aws_db_instance.postgres.endpoint
}

output "address" {
  description = "Hostname of the RDS instance"
  value       = aws_db_instance.postgres.address
}

output "port" {
  description = "Port of the RDS instance"
  value       = aws_db_instance.postgres.port
}

output "db_name" {
  description = "Database name"
  value       = aws_db_instance.postgres.db_name
}
