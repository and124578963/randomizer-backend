# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17-alpine AS backend
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

FROM node:20-alpine AS frontend
WORKDIR /build
COPY frontend/randomizer-frontend/package.json frontend/randomizer-frontend/package-lock.json* ./
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi
COPY frontend/randomizer-frontend/ ./
RUN npm run build

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend /app/target/randomizer.backend-0.0.1-SNAPSHOT.jar app.jar
COPY --from=frontend /build/build /app/static
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar --spring.web.resources.static-locations=file:/app/static/"]
