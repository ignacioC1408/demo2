# Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests clean package

# Run
FROM eclipse-temurin:21-jre
WORKDIR /app
# que Java respete memoria del contenedor
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
# metadata (Render inyecta su propio PORT, pero dejamos 8080 por defecto)
ENV PORT=8080
EXPOSE 8080
# copia el jar que empaqueta Spring Boot
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar
CMD ["java","-jar","/app/app.jar"]
