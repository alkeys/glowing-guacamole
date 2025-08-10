
# --------------------------------------------------------
# ETAPA 1: COMPILAR LA APLICACIÓN CON MAVEN
# --------------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Directorio de trabajo dentro del contenedor de build
WORKDIR /app

# Copiar solo el archivo pom.xml para aprovechar cache de dependencias
COPY pom.xml .

# Descargar las dependencias necesarias
RUN mvn dependency:go-offline

# Copiar todo el código fuente del proyecto
COPY src ./src

# Compilar y empaquetar el .war
RUN mvn clean package


# --------------------------------------------------------
# ETAPA 2: CREAR LA IMAGEN FINAL CON OPEN LIBERTY
# --------------------------------------------------------
FROM icr.io/appcafe/open-liberty:full-java21-openj9-ubi-minimal

# Directorio de trabajo del servidor
WORKDIR /config

# Copiar configuración del servidor y dependencias
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/
COPY --chown=1001:0 src/main/liberty/config/postgresql-42.7.6.jar /config/

# ⚠️ Si no usás certificados, comentá esta línea
COPY --chown=1001:0 ./keystore.p12 /config/

# Copiar el archivo WAR desde la etapa de build
COPY --chown=1001:0 --from=build /app/target/innova-1.0-SNAPSHOT.war /config/dropins/

# Exponer puertos (HTTP, HTTPS, otro si lo usás)
EXPOSE 9080 9443 10000

# Comando para iniciar Open Liberty
CMD ["/opt/ol/wlp/bin/server", "run", "defaultServer"]
