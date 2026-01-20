# --- Etapa 1: Construcción (Build) ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copiamos PRIMERO el pom.xml y descargamos dependencias
# Esto permite usar la caché de Docker si el pom.xml no cambia
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copiamos el código fuente después
COPY src ./src

# 3. Compilamos el proyecto (offline, mostrando errores y saltando tests)
RUN mvn clean package -DskipTests -e

# --- Etapa 2: Ejecución (Run) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el .jar generado
# Asegúrate de que tu pom.xml tenga <version>0.0.1-SNAPSHOT</version>
COPY --from=build /app/target/formex-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]