[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/alkeys/glowing-guacamole)
# Servidor de Desarrollo de Liberty
Este proyecto utiliza el plugin de Maven para Open Liberty, que permite ejecutar un servidor de desarrollo de Liberty. 
El servidor se ejecuta en modo de desarrollo, lo que significa que los cambios en el código fuente se reflejarán automáticamente
sin necesidad de reiniciar el servidor.

# Requisitos
- Java 21 
- Maven 3.8.6 o superior
- Open Liberty
- Docker (opcional, para ejecutar el servidor en un contenedor) 
- base de datos PostgreSQL (opcional, para la persistencia de datos) 

# Instrucciones de Uso y Configuración
Para ejecutar el servidor de desarrollo de Liberty, asegúrate de tener Java ,base de datos y maven  instalados en tu sistema.
Luego, puedes utilizar el siguiente comando para iniciar el servidor:

```bash
./mvnw clean install
```
Para ejecutar el servidor de desarrollo de Liberty, puedes utilizar el siguiente comando:

```bash
./mvnw io.openliberty.tools:liberty-maven-plugin:dev
```

# Configuración de la Base de Datos se recomienda utilizar PostgreSQL en docker para mayor facilidad de uso. en local
Para configurar la base de datos PostgreSQL, puedes utilizar el siguiente comando para ejecutar un contenedor de Docker:
credenciales de acceso a la base de datos:
- **Base de datos**: innovaTechDB
- **Usuario**: admin
- **Contraseña**: admin
- Puerto: 5432
- Para Crear el contenedor de PostgreSQL, asegúrate de tener Docker instalado y ejecuta el siguiente comando:
```bash
docker run --name innovatechDB -e POSTGRES_DB=innovaTech -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -p 5432:5432 -d postgres
```
#para correr el contenedor de PostgreSQL, asegúrate de tener Docker instalado y ejecuta el siguiente comando:
```bash
docker start innovatechDB
```


#Generación del Keystore esto sirve para generar un certificado SSL para el servidor de desarrollo de Liberty.
```bash
keytool -genkeypair -alias appkey \
-keyalg RSA -keysize 2048 -validity 365 \
-keystore innova-prod.p12 -storetype PKCS12 \
-storepass secret -keypass secret \
-dname "CN=localhost, OU=dev, O=empresa, L=ciudad, ST=estado, C=US"
```

#Generación del Keystore con SAN (Subject Alternative Name) esto sirve para generar un certificado SSL para el servidor de desarrollo de Liberty. 
```bash
keytool -genkeypair -alias appkey \
-keyalg RSA -keysize 2048 -validity 365 \
-keystore innova-prod.p12 -storetype PKCS12 \
-storepass SuperPass123 -keypass SuperPass123 \
-dname "CN=localhost, OU=dev, O=empresa, L=ciudad, ST=estado, C=US" \
-ext "SAN=DNS:localhost,IP:127.0.0.1"
```


# Creación de contendor de Docker para el Servidor de Desarrollo de Liberty 
Para crear un contenedor de Docker para el servidor de desarrollo de Liberty, puedes utilizar el siguiente comando:


```bash
docker build -t innovatechdb .
```

# Ejecución del Contenedor de Docker
Para ejecutar el contenedor de Docker que has creado, utiliza el siguiente comando:

```bash 
docker run --name too_app -p 1000:1000 -p 9443:9443 innovatechdb
```
# para parar el contenedor de Docker, puedes utilizar el siguiente comando:

```bash
docker stop too_app
```


# para ocupar docker compose
recuerda que debes tener instalado Docker Compose en tu sistema. 
y modificar el servidor de desarrollo de Liberty para que utilice la base de datos PostgreSQL.
serverName="db"
```bash
docker-compose up --build
```
# Detener el Contenedor de Docker
Para detener el contenedor de Docker, puedes utilizar el siguiente comando:

```bash 
docker-compose down
```

# para poner las variables de entorno en linux/mac con el archivo .env
```bash
    export $(cat .env | xargs)
```

# para windows con el archivo .env
```bash
Get-Content .env | ForEach-Object { $parts = $_ -split '='; set-item env:$($parts[0]) $parts[1] }
```



