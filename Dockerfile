# Etapa 1: build con Maven y Java 21
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

# Etapa 2: runtime con JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Tu app corre en el puerto 8082 (por tu captura de localhost:8082)
EXPOSE 8082

ENTRYPOINT ["java","-jar","/app/app.jar"]
