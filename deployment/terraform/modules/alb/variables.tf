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

variable "public_subnet_ids" {
  type        = list(string)
  description = "Public subnet IDs for the ALB"
}

variable "security_group_ids" {
  type        = list(string)
  description = "Security group IDs for the ALB"
}

variable "app_port" {
  type        = number
  description = "Target port for container"
  default     = 8080
}

variable "health_check_path" {
  type        = string
  description = "Path for health check"
  default     = "/actuator/health"
}

variable "enable_access_logs" {
  type        = bool
  description = "Whether to enable access logs for the ALB"
  default     = false
}

variable "access_logs_bucket" {
  type        = string
  description = "The S3 bucket name to store ALB access logs"
  default     = "franchise-service-alb-logs"
}
