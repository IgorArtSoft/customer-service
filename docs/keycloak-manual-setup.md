# Keycloak Manual Setup

This guide configures Keycloak for local `customer-service` development.

## Local assumptions

| Setting | Value |
|---|---|
| Keycloak Admin Console | `http://localhost:8180/admin` |
| Internal Keycloak container URL | `http://localhost:8080` |
| Realm | `customer-microservice` |
| Public test client | `postman-client` |
| Test user | `customer1` |
| Realm role | `customer_user` |
| Admin realm role | `customer_admin` |

## Option A: Configure by Admin Console

### 1. Log in

Open:

```text
http://localhost:8180/admin
```

Use the admin username/password configured for your local Keycloak container.

### 2. Create realm

1. Open the realm selector.
2. Click **Create realm**.
3. Realm name:

```text
customer-microservice
```

4. Set **Enabled** to ON.
5. Save.

### 3. Create public client for Postman/local testing

Create client:

| Field | Value |
|---|---|
| Client type | OpenID Connect |
| Client ID | `postman-client` |
| Name | `Postman Local Client` |
| Client authentication | OFF |
| Standard flow | ON |
| Direct access grants | ON for local development only |
| Valid redirect URIs | `https://oauth.pstmn.io/v1/callback` and `http://localhost:8083/*` |
| Web origins | `*` for local development only |

Notes:

- Authorization Code + PKCE is preferred for realistic OAuth testing.
- Direct Access Grants allow username/password token requests. Keep this only for local development.
- This client is public, so it has no client secret.

### 4. Create realm roles

Create these realm roles:

```text
customer_user
customer_admin
```

### 5. Create local test users

Create user `customer1`:

| Field | Value |
|---|---|
| Username | `customer1` |
| Email | `customer1@example.com` |
| First name | `Customer` |
| Last name | `One` |
| Enabled | ON |
| Email verified | ON for local development |

Set password:

| Field | Value |
|---|---|
| Password | `Password123!` |
| Temporary | OFF |

Assign role:

```text
customer_user
```

Optional second user:

| Field | Value |
|---|---|
| Username | `customer2` |
| Password | `Password123!` |
| Role | `customer_user` |

Optional admin user:

| Field | Value |
|---|---|
| Username | `customer-admin` |
| Password | `Password123!` |
| Role | `customer_admin` |

## Option B: Configure by `kcadm.sh` inside Docker

This assumes your Keycloak container is named `keycloak`.

Find container name if needed:

```powershell
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}"
```

Login to Keycloak Admin CLI:

```powershell
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh config credentials `
  --server http://localhost:8080 `
  --realm master `
  --user admin `
  --password admin
```

Create realm:

```powershell
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh create realms `
  -s realm=customer-microservice `
  -s enabled=true
```

Create public local test client:

```powershell
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh create clients `
  -r customer-microservice `
  -s clientId=postman-client `
  -s enabled=true `
  -s protocol=openid-connect `
  -s publicClient=true `
  -s standardFlowEnabled=true `
  -s directAccessGrantsEnabled=true `
  -s 'redirectUris=["https://oauth.pstmn.io/v1/callback","http://localhost:8083/*"]' `
  -s 'webOrigins=["*"]'
```

Create realm roles:

```powershell
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh create roles `
  -r customer-microservice `
  -s name=customer_user `
  -s 'description=Regular customer API user'

docker exec -it keycloak /opt/keycloak/bin/kcadm.sh create roles `
  -r customer-microservice `
  -s name=customer_admin `
  -s 'description=Customer API administrator'
```

Create user:

```powershell
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh create users `
  -r customer-microservice `
  -s username=customer1 `
  -s enabled=true `
  -s emailVerified=true `
  -s email=customer1@example.com `
  -s firstName=Customer `
  -s lastName=One
```

Set password:

```powershell
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh set-password `
  -r customer-microservice `
  --username customer1 `
  --new-password Password123! `
  --temporary=false
```

Assign role:

```powershell
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh add-roles `
  -r customer-microservice `
  --uusername customer1 `
  --rolename customer_user
```

Verify assigned roles:

```powershell
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh get-roles `
  -r customer-microservice `
  --uusername customer1
```

## Get a token from PowerShell

```powershell
$response = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8180/realms/customer-microservice/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body @{
    grant_type = "password"
    client_id  = "postman-client"
    username   = "customer1"
    password   = "Password123!"
    scope      = "openid profile email"
  }

$token = $response.access_token
$token
```

## Decode token quickly

Paste the token into jwt.io or decode locally.

PowerShell payload decode:

```powershell
$payload = $token.Split(".")[1]
$payload = $payload.Replace("-", "+").Replace("_", "/")
switch ($payload.Length % 4) {
  2 { $payload += "==" }
  3 { $payload += "=" }
}
[System.Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($payload)) | ConvertFrom-Json
```

Expected important claims:

| Claim | Expected value |
|---|---|
| `iss` | `http://localhost:8180/realms/customer-microservice` |
| `preferred_username` | `customer1` |
| `realm_access.roles` | includes `customer_user` |

## Spring Security note about roles

If the API only checks `authenticated()`, no special role mapping is required.

If you want to use annotations like:

```java
@PreAuthorize("hasRole('customer_user')")
```

or:

```java
@PreAuthorize("hasAuthority('ROLE_customer_user')")
```

then you need to map Keycloak realm roles from:

```json
realm_access.roles
```

into Spring Security authorities. Spring Security does not automatically treat Keycloak realm roles as Spring roles unless you configure a JWT authority converter.

## Common errors

### `invalid_client`

For `postman-client`, make sure:

| Setting | Expected |
|---|---|
| Client authentication | OFF |
| Direct access grants | ON if using password grant |
| Client ID | exactly `postman-client` |
| Client secret | not supplied for this public client |

### `401 Unauthorized`

The API did not receive a valid bearer token.

### `Jwt issuer mismatch`

Check that this application property:

```text
OIDC_ISSUER_URI=http://localhost:8180/realms/customer-microservice
```

matches the token's `iss` claim exactly.
