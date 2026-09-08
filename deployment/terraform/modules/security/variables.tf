variable "project_name" {
  type        = string
  description = "Project name"
  default     = "franchise-service"
}

variable "environment" {
  type        = string
  description = "Environment name"
}

variable "vpc_id" {
  type        = string
  description = "VPC ID"
}

variable "app_port" {
  type        = number
  description = "Application port"
  default     = 8080
}

variable "db_port" {
  type        = number
  description = "PostgreSQL port"
  default     = 5432
}
