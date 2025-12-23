# --- Etapa 1: Construcción (Build) ---
# Usamos una imagen oficial de Maven con Java 21.
# Esto evita usar el archivo 'mvnw' local que suele dar problemas de permisos.
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copiamos solo el pom.xml para descargar dependencias primero (aprovecha caché)
COPY pom.xml .
# Descargamos las librerías necesarias
RUN mvn dependency:go-offline

# 2. Copiamos el código fuente
COPY src ./src

# 3. Compilamos el proyecto (saltando tests para evitar errores de entorno)
RUN mvn clean package -DskipTests

# --- Etapa 2: Ejecución (Run) ---
# Usamos una imagen ligera de Java 21 para correr la app
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el .jar generado en la etapa anterior
# Nota: El nombre del archivo se basa en <artifactId> y <version> de tu pom.xml
COPY --from=build /app/target/formex-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto 8080 (puerto por defecto de Spring Boot)
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]