# Postman Testing

This guide explains how to get a Keycloak access token and test protected `customer-service` endpoints.

## Local assumptions

| Setting | Value |
|---|---|
| Keycloak realm | `customer-microservice` |
| Token endpoint | `http://localhost:8180/realms/customer-microservice/protocol/openid-connect/token` |
| Client ID | `postman-client` |
| Username | `customer1` |
| Password | `Password123!` |
| Customer Service | `http://localhost:8083` |

## Option A: Password grant for local development only

Use only in local development.

Postman request:

| Field | Value |
|---|---|
| Method | `POST` |
| URL | `http://localhost:8180/realms/customer-microservice/protocol/openid-connect/token` |
| Body type | `x-www-form-urlencoded` |

Body fields:

| Key | Value |
|---|---|
| `grant_type` | `password` |
| `client_id` | `postman-client` |
| `username` | `customer1` |
| `password` | `Password123!` |
| `scope` | `openid profile email` |

Copy `access_token` from the response.

## Option B: Authorization Code + PKCE

In Postman Authorization tab:

| Field | Value |
|---|---|
| Type | OAuth 2.0 |
| Grant Type | Authorization Code with PKCE |
| Auth URL | `http://localhost:8180/realms/customer-microservice/protocol/openid-connect/auth` |
| Access Token URL | `http://localhost:8180/realms/customer-microservice/protocol/openid-connect/token` |
| Client ID | `postman-client` |
| Client Secret | empty |
| Code Challenge Method | `S256` |
| Scope | `openid profile email` |
| Callback URL | `https://oauth.pstmn.io/v1/callback` |

The Keycloak client must include this Valid Redirect URI:

```text
https://oauth.pstmn.io/v1/callback
```

## Call protected endpoint

Add header:

```text
Authorization: Bearer <access_token>
```

Example request:

| Field | Value |
|---|---|
| Method | `POST` |
| URL | `http://localhost:8083/customers` |
| Header | `Content-Type: application/json` |

Body:

```json
{
  "customerId": "CUST-1001",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "phone": "+14165551234",
  "status": "ACTIVE"
}
```

## PowerShell equivalent

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

## Test pagination

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8083/customers?page=0&size=20" `
  -Headers @{ Authorization = "Bearer $token" }
```

## Test validation

Example invalid request:

```json
{
  "customerId": "",
  "firstName": "",
  "lastName": "",
  "email": "bad-email",
  "phone": "123",
  "status": "UNKNOWN"
}
```

Expected result should be HTTP `400 Bad Request` with field-level validation errors if the controller uses Jakarta Bean Validation and `@RestControllerAdvice`.

## Troubleshooting

### `401 Unauthorized`

Check:

- Token exists.
- Header starts with `Bearer `.
- Token is not expired.
- Token `iss` claim equals `OIDC_ISSUER_URI`.

### `403 Forbidden`

Authentication succeeded, but the authenticated user does not have the required role/authority.

### `invalid_client`

For `postman-client`:

- Do not send a client secret.
- Client Authentication must be OFF.
- Client ID must match exactly.
- Direct Access Grants must be ON if using password grant.

### `invalid_grant`

Check username/password, user enabled status, and whether password is temporary.
