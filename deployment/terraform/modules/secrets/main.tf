resource "aws_secretsmanager_secret" "db_credentials" {
  name                    = "${var.project_name}/${var.environment}/database"
  description             = "PostgreSQL database credentials for franchise-service in ${var.environment}"
  recovery_window_in_days = 7

  tags = {
    Name        = "${var.project_name}-${var.environment}-db-secret"
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "db_credentials_val" {
  secret_id = aws_secretsmanager_secret.db_credentials.id
  secret_string = jsonencode({
    host     = var.db_host
    port     = tostring(var.db_port)
    dbname   = var.db_name
    username = var.db_user
    password = var.db_password
  })
}
