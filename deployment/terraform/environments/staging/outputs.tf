output "alb_dns_name" {
  description = "Public DNS name of the Application Load Balancer"
  value       = module.alb.alb_dns_name
}

output "api_base_url" {
  description = "Base URL for the Franchise Service API"
  value       = "http://${module.alb.alb_dns_name}"
}

output "swagger_ui_url" {
  description = "URL for the Swagger UI documentation"
  value       = "http://${module.alb.alb_dns_name}/webjars/swagger-ui/index.html?url=/v3/api-docs"
}

output "ecr_repository_url" {
  description = "ECR Repository URL for pushing Docker images"
  value       = module.ecr.repository_url
}

output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint (internal to VPC)"
  value       = module.rds.endpoint
}

output "secrets_manager_arn" {
  description = "ARN of Secrets Manager database secret"
  value       = module.secrets.secret_arn
}

output "ecs_service_name" {
  description = "Name of the deployed ECS service"
  value       = module.ecs.service_name
}
