# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy all pom files first for dependency layer caching
COPY pom.xml .
COPY gym-crm/pom.xml gym-crm/
COPY eureka-server/pom.xml eureka-server/
COPY trainer-workload-service/pom.xml trainer-workload-service/

# Download dependencies (cached layer if poms don't change)
RUN mvn -B dependency:go-offline -pl eureka-server -am -q 2>/dev/null || true

# Copy eureka-server source and build
COPY eureka-server/src eureka-server/src
RUN mvn -B package -pl eureka-server -am -DskipTests -q

# ---- Run stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /workspace/eureka-server/target/eureka-server-1.0-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]