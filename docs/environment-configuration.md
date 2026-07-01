# Environment Configuration

This project should keep application configuration portable by using environment variables.

## Recommended Spring Boot configuration

Use this structure in `src/main/resources/application.yml`:

```yaml
server:
  port: "${SERVER_PORT:8083}"

spring:
  application:
    name: customer-service

  data:
    mongodb:
      uri: "${MONGODB_URI:mongodb://admin:admin123@localhost:27017/customerdb?authSource=admin}"
      auto-index-creation: false

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: "${OIDC_ISSUER_URI:http://localhost:8180/realms/customer-microservice}"
```

Use `spring.data.mongodb.uri` because it is the Spring Boot MongoDB auto-configuration property.

## Local PowerShell variables

Temporary variables for the current PowerShell window:

```powershell
$env:SERVER_PORT = "8083"
$env:MONGODB_URI = "mongodb://admin:admin123@localhost:27017/customerdb?authSource=admin"
$env:OIDC_ISSUER_URI = "http://localhost:8180/realms/customer-microservice"
```

Run the service:

```powershell
.\mvnw.com clean spring-boot:run
```

## Persistent Windows environment variables

Use this only if you want the values to remain after closing PowerShell:

```powershell
setx SERVER_PORT "8083"
setx MONGODB_URI "mongodb://admin:admin123@localhost:27017/customerdb?authSource=admin"
setx OIDC_ISSUER_URI "http://localhost:8180/realms/customer-microservice"
```

Open a new terminal after using `setx`.

## Local `.env.local`

Create `.env.local` from `.env.example`:

```powershell
Copy-Item .env.example .env.local
```

Edit the values manually. Do not commit `.env.local`.

Recommended `.gitignore`:

```gitignore
.env
.env.local
*.local.yml
deploy/openshift/dev/*secret*.local.yml
```

## Docker Compose environment example

For the customer-service container:

```yaml
services:
  customer-service:
    image: customer-service:local
    ports:
      - "8083:8083"
    environment:
      SERVER_PORT: "8083"
      MONGODB_URI: "mongodb://admin:admin123@mongodb:27017/customerdb?authSource=admin"
      OIDC_ISSUER_URI: "http://keycloak:8080/realms/customer-microservice"
```

Important difference:

| Runtime | MongoDB host | Keycloak issuer |
|---|---|---|
| Running service on Windows host | `localhost:27017` | `http://localhost:8180/realms/customer-microservice` |
| Running service inside Docker Compose network | `mongodb:27017` | `http://keycloak:8080/realms/customer-microservice` |

The issuer must match the token's `iss` claim exactly. If tokens are obtained from the browser/Postman using `localhost:8180`, the API should normally also use `localhost:8180` as issuer during local host-based testing.

## OpenShift environment

Create a secret for sensitive values:

```powershell
oc create secret generic customer-service-secret `
  --from-literal=MONGODB_URI="mongodb://<user>:<password>@<mongodb-service>:27017/customerdb?authSource=admin" `
  --from-literal=OIDC_ISSUER_URI="https://<keycloak-route>/realms/customer-microservice"
```

Apply environment variables to the deployment:

```powershell
oc set env deployment/customer-service --from=secret/customer-service-secret
oc set env deployment/customer-service SERVER_PORT=8080
```

Check active values:

```powershell
oc set env deployment/customer-service --list
```

Restart deployment:

```powershell
oc rollout restart deployment/customer-service
oc rollout status deployment/customer-service
```

## What should not be committed

Do not commit files containing real credentials:

```text
.env
.env.local
application-local.yml
*secret*.local.yml
```

Commit only templates with placeholders, for example:

```text
.env.example
deploy/openshift/dev/secret-templates/customer-service-secret.template.yml
```
