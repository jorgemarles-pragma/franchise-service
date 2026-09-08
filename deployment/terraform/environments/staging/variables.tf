variable "aws_region" {
  description = "AWS region for deployment"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name prefix"
  type        = string
  default     = "franchise-service"
}

variable "environment" {
  description = "Target deployment environment"
  type        = string
  default     = "staging"
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "Availability zones"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

variable "public_subnet_cidrs" {
  description = "Public subnet CIDRs"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "Private subnet CIDRs"
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.20.0/24"]
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "franchise_staging"
}

variable "db_user" {
  description = "Database master username"
  type        = string
  default     = "franchise_app_user"
}

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t4g.micro"
}

variable "image_tag" {
  description = "Docker image tag to deploy to ECS"
  type        = string
  default     = "latest"
}

variable "fargate_cpu" {
  description = "Fargate CPU units"
  type        = number
  default     = 512
}

variable "fargate_memory" {
  description = "Fargate memory in MB"
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Number of ECS tasks to maintain"
  type        = number
  default     = 2
}
