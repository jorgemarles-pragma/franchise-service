variable "project_name" {
  type        = string
  description = "Project name"
  default     = "franchise-service"
}

variable "environment" {
  type        = string
  description = "Environment name"
}

variable "aws_region" {
  type        = string
  description = "AWS region"
  default     = "us-east-1"
}

variable "private_subnet_ids" {
  type        = list(string)
  description = "Private subnet IDs where ECS tasks will run"
}

variable "security_group_ids" {
  type        = list(string)
  description = "Security group IDs for ECS tasks"
}

variable "target_group_arn" {
  type        = string
  description = "ARN of ALB target group"
}

variable "ecr_image_url" {
  type        = string
  description = "ECR image URL"
}

variable "image_tag" {
  type        = string
  description = "Image tag to deploy"
  default     = "latest"
}

variable "secret_arn" {
  type        = string
  description = "ARN of Secrets Manager secret for DB credentials"
}

variable "cpu" {
  type        = number
  description = "Fargate CPU units (256, 512, 1024)"
  default     = 512
}

variable "memory" {
  type        = number
  description = "Fargate memory in MB (512, 1024, 2048)"
  default     = 1024
}

variable "desired_count" {
  type        = number
  description = "Desired number of container instances"
  default     = 2
}

variable "app_port" {
  type        = number
  description = "Port exposed by application"
  default     = 8080
}
