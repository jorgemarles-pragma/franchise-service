variable "project_name" {
  type        = string
  description = "Project name"
  default     = "franchise-service"
}

variable "environment" {
  type        = string
  description = "Environment name"
}

variable "db_host" {
  type        = string
  description = "PostgreSQL DB host"
}

variable "db_port" {
  type        = number
  description = "PostgreSQL DB port"
  default     = 5432
}

variable "db_name" {
  type        = string
  description = "PostgreSQL DB name"
}

variable "db_user" {
  type        = string
  description = "PostgreSQL master username"
}

variable "db_password" {
  type        = string
  description = "PostgreSQL master password"
  sensitive   = true
}
