$ErrorActionPreference = "Stop"

$KeycloakBaseUrl = $env:KEYCLOAK_BASE_URL
if ([string]::IsNullOrWhiteSpace($KeycloakBaseUrl)) {
    $KeycloakBaseUrl = "http://localhost:8180"
}

$Realm = $env:OIDC_REALM
if ([string]::IsNullOrWhiteSpace($Realm)) {
    $Realm = "customer-microservice"
}

$ClientId = $env:KEYCLOAK_TEST_CLIENT_ID
if ([string]::IsNullOrWhiteSpace($ClientId)) {
    $ClientId = "postman-client"
}

$Username = $env:KEYCLOAK_TEST_USERNAME
if ([string]::IsNullOrWhiteSpace($Username)) {
    $Username = "customer1"
}

$Password = $env:KEYCLOAK_TEST_PASSWORD
if ([string]::IsNullOrWhiteSpace($Password)) {
    $Password = "Password123!"
}

$response = Invoke-RestMethod `
    -Method Post `
    -Uri "$KeycloakBaseUrl/realms/$Realm/protocol/openid-connect/token" `
    -ContentType "application/x-www-form-urlencoded" `
    -Body @{
        grant_type = "password"
        client_id  = $ClientId
        username   = $Username
        password   = $Password
        scope      = "openid profile email"
    }

$token = $response.access_token

Write-Host "Access token copied to clipboard."
$token | Set-Clipboard

# Also return token to caller:
$token
