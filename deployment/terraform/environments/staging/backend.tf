# S3 Remote State Backend with DynamoDB State Locking
# Pre-requisites in AWS account:
# 1. S3 bucket: 'nequi-reto-terraform-state' (versioning enabled, server-side encryption enabled)
# 2. DynamoDB table: 'terraform-state-lock' (partition key: LockID [String])
terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket       = "nequi-reto-terraform-state"
    key          = "staging/franchise-service/terraform.tfstate"
    region       = "us-east-1"
    use_lockfile = true
    encrypt      = true
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}
