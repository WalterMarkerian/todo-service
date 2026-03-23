# ETAPA 1: Compilación (Build)
# Usamos una imagen de Maven con Java 21 para construir el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom.xml y descargamos las dependencias (optimiza el cache de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y compilamos el .jar saltando los tests para ir más rápido
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Runtime)
# Usamos una imagen JRE de Java 21, mucho más ligera y segura
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiamos solo el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto interno que usa Spring Boot (8080)
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]