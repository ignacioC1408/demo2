# Etapa 1: build con Maven y Java 21
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Compila el jar (sin tests para acelerar)
RUN mvn -B -DskipTests package

# Etapa 2: runtime con JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiá el jar compilado
COPY --from=build /app/target/*.jar /app/app.jar

# (Opcional) el EXPOSE es informativo; dejamos 8080 por convención
EXPOSE 8080

# ⚠️ Clave: bindear al puerto que Render inyecta en $PORT
# y activar el perfil 'render' si existe
CMD ["sh","-c","java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-render} -jar /app/app.jar"]
