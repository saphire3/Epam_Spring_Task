# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy all pom files first for dependency layer caching
COPY pom.xml .
COPY gym-crm/pom.xml gym-crm/
COPY eureka-server/pom.xml eureka-server/
COPY trainer-workload-service/pom.xml trainer-workload-service/

# Download dependencies (cached layer if poms don't change)
RUN mvn -B dependency:go-offline -pl gym-crm -am -q 2>/dev/null || true

# Copy gym-crm source and build
COPY gym-crm/src gym-crm/src
RUN mvn -B package -pl gym-crm -am -DskipTests -q

# ---- Run stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /workspace/gym-crm/target/gym-crm-1.0-SNAPSHOT.jar app.jar

# Disabled integrations by default (no external ActiveMQ or Eureka needed)
ENV EUREKA_CLIENT_ENABLED=false \
    SPRING_ACTIVEMQ_BROKER_URL="vm://localhost?broker.persistent=false"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]