# 🏢 Franchise Management Service

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring WebFlux](https://img.shields.io/badge/Spring-WebFlux%20Functional-blue.svg)](https://docs.spring.io/spring-framework/reference/web/webflux-functional.html)
[![R2DBC PostgreSQL](https://img.shields.io/badge/R2DBC-PostgreSQL-336791.svg)](https://r2dbc.io/)
[![Resilience4j](https://img.shields.io/badge/Resilience4j-CircuitBreaker%20%7C%20Retry%20%7C%20Timeout-red.svg)](https://resilience4j.readme.io/)
[![Terraform](https://img.shields.io/badge/IaC-Terraform%20AWS-623CE4.svg)](https://www.terraform.io/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20%28Bancolombia%29-yellow.svg)](https://bancolombia.github.io/scaffold-clean-architecture/)

A **100% reactive, non-blocking REST API** designed for managing franchises, branches, and product inventories. Built using enterprise-grade software engineering standards:

- **Reactive Core (Spring WebFlux)**: Fully non-blocking HTTP layer with `RouterFunction` and `HandlerFunction` (functional endpoints, zero `@RestController`, zero `.block()`, native Project Reactor operators).
- **Hexagonal Architecture (Bancolombia Clean Architecture)**: Strict separation of concerns, framework-independent pure domain model, immutable DTOs using Java `record`, and automated architectural guardrails with ArchUnit.
- **Resilience Engineering (Resilience4j)**: Production-grade fault tolerance featuring **TimeLimiter** (3s non-blocking timeout), **Retry** with exponential backoff and jitter, and **CircuitBreaker**.
- **Infrastructure as Code (Terraform AWS)**: Fully automated cloud deployment provisioning a custom VPC, private subnets, Application Load Balancer (ALB), Amazon RDS PostgreSQL 16, AWS Secrets Manager, and AWS ECS Fargate.

---

## ☁️ Live Environment (AWS Staging)

The staging environment is provisioned and publicly accessible via the Application Load Balancer:

| Resource | Public URL |
|---|---|
| **API Base URL** | `http://franchise-service-staging-alb-639543305.us-east-1.elb.amazonaws.com` |
| **Swagger UI Documentation** | `http://franchise-service-staging-alb-639543305.us-east-1.elb.amazonaws.com/webjars/swagger-ui/index.html?url=/v3/api-docs` |
| **OpenAPI Spec (JSON)** | `http://franchise-service-staging-alb-639543305.us-east-1.elb.amazonaws.com/v3/api-docs` |
| **Health Check (Actuator)** | `http://franchise-service-staging-alb-639543305.us-east-1.elb.amazonaws.com/actuator/health` |

---

## 🚀 Functional Endpoints Matrix

| # | HTTP Method | Endpoint Path | Description | Request Body | Response Content-Type | Success Status |
|---|---|---|---|---|---|:---:|
| 1 | `POST` | `/api/franchises` | Create a new franchise | `FranchiseRequest` | `application/json` | `201 Created` |
| 2 | `GET` | `/api/franchises` | List all franchises (reactive stream) | N/A | `application/x-ndjson` | `200 OK` |
| 3 | `GET` | `/api/franchises/{franchiseId}` | Get franchise by ID | N/A | `application/json` | `200 OK` |
| 4 | `PATCH` | `/api/franchises/{franchiseId}/name` | Update franchise name | `UpdateFranchiseNameRequest` | `application/json` | `200 OK` |
| 5 | `POST` | `/api/franchises/{franchiseId}/branches` | Add branch to franchise | `BranchRequest` | `application/json` | `201 Created` |
| 6 | `GET` | `/api/franchises/{franchiseId}/branches` | List branches for franchise (stream) | N/A | `application/x-ndjson` | `200 OK` |
| 7 | `GET` | `/api/branches/{branchId}` | Get branch by ID | N/A | `application/json` | `200 OK` |
| 8 | `PATCH` | `/api/branches/{branchId}/name` | Update branch name | `UpdateBranchNameRequest` | `application/json` | `200 OK` |
| 9 | `POST` | `/api/branches/{branchId}/products` | Add product to branch | `ProductRequest` | `application/json` | `201 Created` |
| 10 | `GET` | `/api/branches/{branchId}/products` | List products for branch (stream) | N/A | `application/x-ndjson` | `200 OK` |
| 11 | `GET` | `/api/products/{productId}` | Get product by ID | N/A | `application/json` | `200 OK` |
| 12 | `PATCH` | `/api/products/{productId}/stock` | Update product stock | `UpdateProductStockRequest` | `application/json` | `200 OK` |
| 13 | `PATCH` | `/api/products/{productId}/name` | Update product name | `UpdateProductNameRequest` | `application/json` | `200 OK` |
| 14 | `DELETE` | `/api/branches/{branchId}/products/{productId}` | Delete product from branch | N/A | N/A | `204 No Content` |
| 15 | `GET` | `/api/franchises/{franchiseId}/max-stock-products` | Get highest stock product per branch (stream) | N/A | `application/x-ndjson` | `200 OK` |

> ℹ️ **Reactive Streaming Note**: All endpoints returning a reactive `Flux` stream emit items as `application/x-ndjson` (`MediaType.APPLICATION_NDJSON`), enabling low-latency, backpressure-compliant streaming directly to clients without accumulating items in memory. Single-element queries (`Mono`) produce standard `application/json`.

---

## 🏛️ Architecture Overview

The project is structured according to Hexagonal Architecture (Ports and Adapters) following the Bancolombia Clean Architecture Gradle plugin standard:

```text
com.pragma.jamarlesf
├── domain
│   ├── model              # Pure business entities, domain exceptions, and gateway interfaces (no Spring dependencies)
│   └── usecase            # Business logic orchestration and reactive workflows
├── infrastructure
│   ├── driven-adapters
│   │   └── r2dbc-postgresql # Non-blocking R2DBC persistence, entity mappers, and Resilience4j decorators
│   └── entry-points
│       └── reactive-web     # WebFlux RouterFunctions, HandlerFunctions, DTO records, and WebExceptionHandler
└── applications
    └── app-service          # Spring Boot main application, configuration beans, and dependency injection wiring
```

---

## 📮 Postman Collection

Pre-configured Postman collections and environment files for all 9 endpoints are located in the `postman/` directory:

1. Import the collection (`Franchise-API.postman_collection.json`) into Postman.
2. Import the desired environment (`Local.postman_environment.json` or `Staging.postman_environment.json`).
3. Select the active environment in Postman to automatically resolve the `{{baseUrl}}` variable.

---

## 💻 Local Setup & Execution

### Prerequisites
- **JDK 17**
- **Docker & Docker Compose**

### Option 1: Docker Compose (Recommended)
Spins up a local PostgreSQL 16 container and builds/runs the reactive application:

```bash
docker-compose up -d --build
```

### Option 2: Local Gradle Execution
Ensure PostgreSQL is running locally on port `5432` with database `franchisedb`, then execute:

```bash
./gradlew bootRun
```

---

## 🏗️ AWS Cloud Deployment (Terraform)

The cloud infrastructure is located in `deployment/terraform/`:

```text
deployment/terraform/
├── modules/
│   ├── networking/      # Custom VPC, Public/Private Subnets, NAT Gateway, Route Tables
│   ├── rds/             # PostgreSQL 16 RDS instance in isolated subnets (multi_az configurable per environment)
│   ├── alb/             # Application Load Balancer, Target Groups, Health Checks
│   ├── ecs/             # ECS Cluster, Task Definition, Fargate Service, CPU-based Autoscaling
│   ├── ecr/             # Container image repository (mutable `latest` tag for manual deploys, vulnerability scan on push)
│   ├── secrets/         # Secrets Manager entry for DB credentials
│   └── security/        # Least-privilege network security groups
└── environments/
    └── staging/         # Staging environment definition and S3 remote state configuration
```

### Deploying to Staging

```bash
cd deployment/terraform/environments/staging

# 1. Configure input variables
cp terraform.tfvars.example terraform.tfvars
# Update terraform.tfvars with your AWS configuration

# 2. Initialize remote backend (S3 with state locking)
terraform init

# 3. Plan and apply infrastructure
terraform plan -out=tfplan
terraform apply tfplan
```

---

## 🧪 Testing & Quality Assurance

- **Reactive Unit & Integration Tests**: All reactive streams are validated using `StepVerifier` without any blocking operations.
- **Architecture Validation**: Architecture rules are enforced via ArchUnit in `app-service`.
- **Run All Tests**:
  ```bash
  ./gradlew test
  ```
- **Generate JaCoCo Code Coverage Report**:
  ```bash
  ./gradlew jacocoMergedReport
  # HTML Report generated at: build/reports/jacocoMergedReport/html/index.html
  ```
