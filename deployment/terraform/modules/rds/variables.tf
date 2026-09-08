variable "project_name" {
  type        = string
  description = "Project name"
  default     = "franchise-service"
}

variable "environment" {
  type        = string
  description = "Environment name"
}

variable "subnet_ids" {
  type        = list(string)
  description = "Private subnet IDs for DB subnet group"
}

variable "security_group_ids" {
  type        = list(string)
  description = "Security group IDs for RDS"
}

variable "db_name" {
  type        = string
  description = "Database name"
  default     = "franchise_db"
}

variable "db_user" {
  type        = string
  description = "Master username"
  default     = "franchise_app_user"
}

variable "db_password" {
  type        = string
  description = "Master password"
  sensitive   = true
}

variable "instance_class" {
  type        = string
  description = "RDS instance class"
  default     = "db.t4g.micro"
}

variable "allocated_storage" {
  type        = number
  description = "Allocated storage in GB"
  default     = 20
}

variable "multi_az" {
  type        = bool
  description = "Enable Multi-AZ deployment"
  default     = false
}
