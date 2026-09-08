# ==============================================================================
# Multi-stage Dockerfile for Franchise Service (Spring WebFlux Reactive API)
# Staging & Production Ready - Compliant with Nequi Architectural Standards
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Stage (JDK 17)
# Compiles source code, downloads dependencies, and packages executable bootJar.
# Leverages Docker layer caching for Gradle dependencies.
# Note: Uses /workspace to avoid Bancolombia cleanArchitecture plugin substring bug.
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

# Copy Gradle wrapper and configuration files first for layer caching
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle main.gradle gradle.properties lombok.config ./

# Copy module-level build configurations
COPY applications/app-service/build.gradle applications/app-service/
COPY domain/model/build.gradle domain/model/
COPY domain/usecase/build.gradle domain/usecase/
COPY infrastructure/entry-points/reactive-web/build.gradle infrastructure/entry-points/reactive-web/
COPY infrastructure/driven-adapters/r2dbc-postgresql/build.gradle infrastructure/driven-adapters/r2dbc-postgresql/

# Ensure Unix line endings and execution permissions on gradlew
RUN chmod +x ./gradlew

# Pre-fetch Gradle dependencies to cache them in a dedicated layer
RUN ./gradlew dependencies --no-daemon || true

# Copy application source code
COPY applications applications
COPY domain domain
COPY infrastructure infrastructure

# Build executable Spring Boot fat JAR (tests are executed in CI/CD pipeline)
RUN ./gradlew :app-service:bootJar -x test --no-daemon

# ------------------------------------------------------------------------------
# Stage 2: Runtime Stage (JRE 17 Minimal Hardened Image)
# Strips development tooling, runs as non-root user, handles OS signals for PID 1,
# and dynamically configures JVM memory from container cgroups.
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine AS runner

# Create dedicated non-root user and group (Principle of Least Privilege)
RUN addgroup -g 10001 -S appgroup && \
    adduser -u 10001 -S appuser -G appgroup

WORKDIR /app

# Copy compiled artifact from builder stage with strict ownership
COPY --from=builder --chown=appuser:appgroup /workspace/applications/app-service/build/libs/Franchise.jar /app/app.jar

# JVM container optimization flags:
# - UseContainerSupport: respects cgroup limits (CPU/Memory) in AWS ECS Fargate
# - MaxRAMPercentage: dynamically allocates 75% container memory to heap (leaving 25% for Metaspace/OS)
# - ExitOnOutOfMemoryError: fails fast so ECS task can be replaced gracefully
# - egd=file:/dev/./urandom: avoids blocking entropy pool in cloud environments
# - user.timezone: standard Colombian timezone (Nequi)
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom \
               -Duser.timezone=America/Bogota"

# Default profile set to staging (can be overridden via ECS Task Definition)
ENV SPRING_PROFILES_ACTIVE=staging

# Switch to non-root execution user
USER appuser:appgroup

# Service port
EXPOSE 8080

# Health check using Spring Boot Actuator reactive liveness probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health/liveness || exit 1

# Exec-form ENTRYPOINT with 'exec' ensures Java process becomes PID 1 to receive SIGTERM directly
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
