# ---------- Build Stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Cache dependencies
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests dependency:go-offline
# Copy source and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Install a tiny init to handle PID 1 signals properly (optional but good practice)
RUN apk add --no-cache dumb-init
# Copy the fat jar
COPY --from=build /app/target/sb-ecom-0.0.1-SNAPSHOT.jar /app/app.jar
# Expose the Spring Boot port
EXPOSE 8080
# Healthcheck (adjust /actuator/health if enabled)
# HEALTHCHECK --interval=30s --timeout=3s CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENV JAVA_OPTS=""
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
