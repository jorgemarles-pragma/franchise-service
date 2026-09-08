# 🏢 API de Franquicias (Franchise Service)

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring WebFlux](https://img.shields.io/badge/Spring-WebFlux%20Functional-blue.svg)](https://docs.spring.io/spring-framework/reference/web/webflux-functional.html)
[![R2DBC PostgreSQL](https://img.shields.io/badge/R2DBC-PostgreSQL-336791.svg)](https://r2dbc.io/)
[![Resilience4j](https://img.shields.io/badge/Resilience4j-CircuitBreaker%20%7C%20Retry%20%7C%20Timeout-red.svg)](https://resilience4j.readme.io/)
[![Terraform](https://img.shields.io/badge/IaC-Terraform%20AWS-623CE4.svg)](https://www.terraform.io/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20%28Bancolombia%29-yellow.svg)](https://bancolombia.github.io/scaffold-clean-architecture/)

API REST **100% reactiva y no bloqueante** para la administración de franquicias, sucursales y productos. Diseñada bajo los pilares:
- **Pilar 1 (Reactivo)**: Spring WebFlux con `RouterFunction` y `HandlerFunction` (cero `@RestController`, cero `.block()`, operadores nativos de Reactor).
- **Pilar 2 (Arquitectura)**: Hexagonal con Scaffold Bancolombia (dominio puro, DTOs como `record`, inmutabilidad y pruebas ArchUnit).
- **Pilar 3 (Resiliencia)**: Resilience4j completo (**Timeout 3s** + **Retry con backoff exponencial** + **CircuitBreaker**).
- **Pilar 4 (AWS & IaC)**: Despliegue modular en Terraform (VPC, Subnets privadas, ALB, RDS PostgreSQL 16, Secrets Manager y ECS Fargate).

---

## ☁️ Entorno en Vivo (AWS Staging)

La infraestructura está provisionada y accesible públicamente a través del Application Load Balancer:

| Servicio | URL Pública |
|---|---|
| **API Base URL** | `http://franchise-service-staging-alb-639543305.us-east-1.elb.amazonaws.com` |
| **Documentación Swagger UI** | `http://franchise-service-staging-alb-639543305.us-east-1.elb.amazonaws.com/webjars/swagger-ui/index.html?url=/v3/api-docs` |
| **OpenAPI Spec (JSON)** | `http://franchise-service-staging-alb-639543305.us-east-1.elb.amazonaws.com/v3/api-docs` |
| **Health Check (Actuator)** | `http://franchise-service-staging-alb-639543305.us-east-1.elb.amazonaws.com/actuator/health` |

---

## 🚀 Matriz de Endpoints Funcionales

| # | Método | Endpoint | Descripción | Body | Respuesta Exitosa |
|---|---|---|---|---|:---:|
| 1 | `POST` | `/api/franchises` | Crear franquicia | `FranchiseRequest` | `201 Created` |
| 2 | `PATCH` | `/api/franchises/{franchiseId}/name` | Actualizar nombre franquicia | `UpdateFranchiseNameRequest` | `200 OK` |
| 3 | `POST` | `/api/franchises/{franchiseId}/branches` | Agregar sucursal | `BranchRequest` | `201 Created` |
| 4 | `PATCH` | `/api/branches/{branchId}/name` | Actualizar nombre sucursal | `UpdateBranchNameRequest` | `200 OK` |
| 5 | `POST` | `/api/branches/{branchId}/products` | Agregar producto | `ProductRequest` | `201 Created` |
| 6 | `PATCH` | `/api/products/{productId}/stock` | Modificar stock producto | `UpdateProductStockRequest` | `200 OK` |
| 7 | `PATCH` | `/api/products/{productId}/name` | Actualizar nombre producto | `UpdateProductNameRequest` | `200 OK` |
| 8 | `DELETE` | `/api/branches/{branchId}/products/{productId}` | Eliminar producto | N/A | `204 No Content` |
| 9 | `GET` | `/api/franchises/{franchiseId}/max-stock-products` | Mayor stock por sucursal | N/A | `200 OK` |

---

## 📮 Pruebas con Postman

La colección y los entornos de Postman para probar los 9 endpoints se encuentran en la carpeta `postman/` en la raíz del proyecto.
Para ejecutarlos:
1. Importa la colección y el ambiente deseado (`Local` o `Staging`) en Postman.
2. Selecciona el ambiente en el selector superior derecho de Postman para resolver la variable `{{baseUrl}}`.

---

## 💻 Ejecución Local

### Opción 1: Docker Compose (Recomendada)
Levanta la base de datos PostgreSQL 16 y la aplicación reactiva:
```bash
docker-compose up -d
```

### Opción 2: Gradle Local
Asegúrate de tener PostgreSQL corriendo localmente en el puerto `5432` y ejecuta:
```bash
./gradlew bootRun
```

---

## 🏗️ Despliegue en AWS con Terraform

La infraestructura está modularizada en `deployment/terraform/` (ver detalles en [`TERRAFORM_GUIDE.md`](./deployment/terraform/TERRAFORM_GUIDE.md)):

```bash
cd deployment/terraform/environments/staging

# 1. Configurar variables locales
cp terraform.tfvars.example terraform.tfvars

# 2. Inicializar y aplicar con State Locking en S3
terraform init
terraform plan
terraform apply
```

---

## 🧪 Pruebas y Cobertura

* **Ejecutar Pruebas (StepVerifier + ArchUnit + Unit/Integration)**:
  ```bash
  ./gradlew test
  ```
* **Reporte de Cobertura JaCoCo**:
  ```bash
  ./gradlew jacocoMergedReport
  # Reporte generado en: build/reports/jacocoMergedReport/html/index.html
  ```
