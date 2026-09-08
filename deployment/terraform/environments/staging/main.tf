module "networking" {
  source = "../../modules/networking"

  project_name         = var.project_name
  environment          = var.environment
  vpc_cidr             = var.vpc_cidr
  availability_zones   = var.availability_zones
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
}

module "security" {
  source = "../../modules/security"

  project_name = var.project_name
  environment  = var.environment
  vpc_id       = module.networking.vpc_id
}

module "ecr" {
  source = "../../modules/ecr"

  project_name = var.project_name
  environment  = var.environment
}

module "rds" {
  source = "../../modules/rds"

  project_name       = var.project_name
  environment        = var.environment
  subnet_ids         = module.networking.private_subnet_ids
  security_group_ids = [module.security.rds_security_group_id]
  db_name            = var.db_name
  db_user            = var.db_user
  db_password        = var.db_password
  instance_class     = var.db_instance_class
}

module "secrets" {
  source = "../../modules/secrets"

  project_name = var.project_name
  environment  = var.environment
  db_host      = module.rds.address
  db_port      = module.rds.port
  db_name      = var.db_name
  db_user      = var.db_user
  db_password  = var.db_password
}

module "alb" {
  source = "../../modules/alb"

  project_name       = var.project_name
  environment        = var.environment
  vpc_id             = module.networking.vpc_id
  public_subnet_ids  = module.networking.public_subnet_ids
  security_group_ids = [module.security.alb_security_group_id]
}

module "ecs" {
  source = "../../modules/ecs"

  project_name       = var.project_name
  environment        = var.environment
  aws_region         = var.aws_region
  private_subnet_ids = module.networking.private_subnet_ids
  security_group_ids = [module.security.ecs_security_group_id]
  target_group_arn   = module.alb.target_group_arn
  ecr_image_url      = module.ecr.repository_url
  image_tag          = var.image_tag
  secret_arn         = module.secrets.secret_arn
  cpu                = var.fargate_cpu
  memory             = var.fargate_memory
  desired_count      = var.desired_count
}
