# Customer Service

Spring Boot REST microservice for managing customers. The service uses MongoDB for persistence and Keycloak/OIDC for JWT-based authentication.

## Current local defaults

| Component | Local URL / value |
|---|---|
| Customer Service | `http://localhost:8083` |
| MongoDB | `localhost:27017` |
| MongoDB database | `customerdb` |
| Keycloak Admin Console | `http://localhost:8180/admin` |
| Keycloak realm | `customer-microservice` |
| OIDC issuer URI | `http://localhost:8180/realms/customer-microservice` |
| Swagger UI | `http://localhost:8083/swagger-ui/index.html` |

## Prerequisites

Install or have available:

| Tool | Purpose |
|---|---|
| Java 25 | Run the Spring Boot application |
| Maven Wrapper | Build and run the service |
| Docker Desktop | Run MongoDB and Keycloak locally |
| MongoDB Compass or `mongosh` | Inspect collections and create manual indexes |
| Postman | Get JWT tokens and test protected APIs |
| OpenShift CLI `oc` | Deploy to OpenShift, when needed |

## Repository documentation map

| File | Purpose |
|---|---|
| `.env.example` | Example environment variables; do not store real passwords here |
| `config/application-local.example.yml` | Example Spring Boot local configuration |
| `docs/environment-configuration.md` | How local, Docker, and OpenShift configuration should be supplied |
| `docs/keycloak-manual-setup.md` | Manual Keycloak Admin Console steps and equivalent `kcadm.sh` commands |
| `docs/mongodb-manual-setup.md` | Manual MongoDB database and index setup |
| `docs/postman-testing.md` | How to get an access token and call protected APIs |
| `scripts/manual/keycloak-local-setup.ps1` | Optional local Keycloak setup script using `kcadm.sh` inside Docker |
| `scripts/manual/get-keycloak-token.ps1` | Helper script to get a local development access token |
| `scripts/manual/create-all-indexes.js` | `mongosh` script to create MongoDB indexes |
| `scripts/manual/verify-mongodb-indexes.ps1` | PowerShell helper to list indexes |

## Recommended configuration approach

Keep a single application configuration file in the service and externalize environment-specific values.

Recommended Spring Boot property names:

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

Important: use `spring.data.mongodb.uri`, not a custom `spring.mongodb.uri`, unless you added your own custom configuration class.

## Local startup sequence

### 1. Start infrastructure

From the infrastructure repository or folder:

```powershell
docker compose up -d mongodb keycloak
```

Or use your existing local-dev-env script if that is the current project standard:

```powershell
.\scripts\windows\start-all.ps1
```

### 2. Configure Keycloak realm, client, roles, and users

Use either the manual Admin Console steps:

```text
docs/keycloak-manual-setup.md
```

Or run the helper script:

```powershell
.\scripts\manual\keycloak-local-setup.ps1
```

### 3. Create MongoDB indexes

```powershell
mongosh "mongodb://admin:admin123@localhost:27017/admin" .\scripts\manual\create-all-indexes.js
```

### 4. Run customer-service locally

```powershell
$env:SERVER_PORT = "8083"
$env:MONGODB_URI = "mongodb://admin:admin123@localhost:27017/customerdb?authSource=admin"
$env:OIDC_ISSUER_URI = "http://localhost:8180/realms/customer-microservice"

.\mvnw.com clean spring-boot:run
```

### 5. Verify service startup

```powershell
Invoke-RestMethod http://localhost:8083/actuator/health
```

Expected result:

```json
{
  "status": "UP"
}
```

## Getting a local access token

For local development only, use the helper script:

```powershell
$token = .\scripts\manual\get-keycloak-token.ps1
```

Or use Postman. See:

```text
docs/postman-testing.md
```

## Example protected API call

Adjust endpoint names if your controller uses different mappings.

```powershell
$headers = @{
  Authorization = "Bearer $token"
  "Content-Type" = "application/json"
}

$body = @{
  customerId = "CUST-1001"
  firstName  = "John"
  lastName   = "Smith"
  email      = "john.smith@example.com"
  phone      = "+14165551234"
  status     = "ACTIVE"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8083/customers" `
  -Headers $headers `
  -Body $body
```

## Manual checks before committing

Run:

```powershell
git status
```

Make sure these files are not committed with real secrets:

```text
.env
.env.local
*.local.yml
deploy/openshift/dev/*secret*.local.yml
```

Recommended `.gitignore` entries:

```gitignore
.env
.env.local
*.local.yml
deploy/openshift/dev/*secret*.local.yml
logs/
target/
```

## Troubleshooting

### `401 Unauthorized`

Usually means the request has no bearer token or the token is expired.

Check:

```powershell
$token
```

Then call the endpoint with:

```powershell
Authorization: Bearer <access-token>
```

### `invalid_client` in Postman

Check the Keycloak client type:

| Flow | Client setting |
|---|---|
| Authorization Code + PKCE | Public client, Standard Flow ON |
| Password grant for local dev only | Public client, Direct Access Grants ON |
| Client credentials | Confidential client, Client Authentication ON, Service Accounts ON |

For normal user-based API testing, prefer a user token, not a service-account token.

### JWT issuer mismatch

Your application property must match the token issuer exactly:

```text
http://localhost:8180/realms/customer-microservice
```

If the token was issued using a different host, port, or realm name, Spring Security will reject it.

### MongoDB duplicate key error

A unique index is working. Check if a document already exists with the same `customerId`, `email`, `phone`, `orderId`, or `eventId`, depending on which collection is being written.

### MongoDB unauthorized during index creation

Use the admin auth source:

```powershell
mongosh "mongodb://admin:admin123@localhost:27017/admin"
```

Then switch database inside `mongosh`:

```javascript
use customerdb
```
