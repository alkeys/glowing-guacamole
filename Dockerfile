# --------------------------------------------------------
# ETAPA 1: COMPILAR LA APLICACIÓN CON MAVEN
# --------------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Directorio de trabajo dentro del contenedor de build
WORKDIR /app

# Copiar solo el pom.xml para aprovechar la cache de dependencias
COPY pom.xml .

# Descargar dependencias necesarias y plugins (más confiable que dependency:go-offline)
RUN mvn verify -DskipTests -B || true

# Copiar todo el código fuente
COPY src ./src

# Compilar y empaquetar el WAR
RUN mvn clean package -DskipTests -B


# --------------------------------------------------------
# ETAPA 2: CREAR LA IMAGEN FINAL CON OPEN LIBERTY
# --------------------------------------------------------
FROM icr.io/appcafe/open-liberty:full-java21-openj9-ubi-minimal

# Directorio de configuración de Liberty
WORKDIR /config

# Copiar configuración del servidor y dependencias (ej: driver JDBC)
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/
COPY --chown=1001:0 src/main/liberty/config/postgresql-42.7.6.jar /config/

# ⚠️ Copiar el keystore solo si lo usás (si no, dejar comentado)
# COPY --chown=1001:0 ./keystore.p12 /config/

# Copiar el WAR desde la etapa de build (más flexible: cualquier .war generado)
COPY --chown=1001:0 --from=build /app/target/*.war /config/dropins/

# Exponer puertos (ajustar según server.xml) el puerto 1000 es para render
EXPOSE  9494 1000  9090

# Comando para iniciar Open Liberty
CMD ["/opt/ol/wlp/bin/server", "run", "defaultServer"]
