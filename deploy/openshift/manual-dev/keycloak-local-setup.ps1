$ErrorActionPreference = "Stop"

$KeycloakContainer = $env:KEYCLOAK_CONTAINER
if ([string]::IsNullOrWhiteSpace($KeycloakContainer)) {
    $KeycloakContainer = "keycloak"
}

$AdminUser = $env:KEYCLOAK_ADMIN
if ([string]::IsNullOrWhiteSpace($AdminUser)) {
    $AdminUser = "admin"
}

$AdminPassword = $env:KEYCLOAK_ADMIN_PASSWORD
if ([string]::IsNullOrWhiteSpace($AdminPassword)) {
    $AdminPassword = "admin"
}

$Realm = $env:OIDC_REALM
if ([string]::IsNullOrWhiteSpace($Realm)) {
    $Realm = "customer-microservice"
}

$PublicClientId = $env:KEYCLOAK_TEST_CLIENT_ID
if ([string]::IsNullOrWhiteSpace($PublicClientId)) {
    $PublicClientId = "postman-client"
}

$TestUsername = $env:KEYCLOAK_TEST_USERNAME
if ([string]::IsNullOrWhiteSpace($TestUsername)) {
    $TestUsername = "customer1"
}

$TestPassword = $env:KEYCLOAK_TEST_PASSWORD
if ([string]::IsNullOrWhiteSpace($TestPassword)) {
    $TestPassword = "Password123!"
}

$Kcadm = "/opt/keycloak/bin/kcadm.sh"

function Invoke-Kcadm {
    param(
        [Parameter(ValueFromRemainingArguments = $true)]
        [string[]] $Arguments
    )

    docker exec $KeycloakContainer $Kcadm @Arguments
}

function Test-Kcadm {
    param(
        [Parameter(ValueFromRemainingArguments = $true)]
        [string[]] $Arguments
    )

    docker exec $KeycloakContainer $Kcadm @Arguments *> $null
    return $LASTEXITCODE -eq 0
}

Write-Host "Logging in to Keycloak Admin CLI..."
Invoke-Kcadm config credentials `
    --server "http://localhost:8080" `
    --realm "master" `
    --user $AdminUser `
    --password $AdminPassword

Write-Host "Creating realm if missing: $Realm"
if (-not (Test-Kcadm get "realms/$Realm")) {
    Invoke-Kcadm create realms `
        -s "realm=$Realm" `
        -s "enabled=true"
} else {
    Write-Host "Realm already exists."
}

Write-Host "Creating public client if missing: $PublicClientId"
$clientExists = docker exec $KeycloakContainer $Kcadm get clients -r $Realm -q "clientId=$PublicClientId" --fields clientId --format csv --noquotes
if ([string]::IsNullOrWhiteSpace($clientExists)) {
    Invoke-Kcadm create clients `
        -r $Realm `
        -s "clientId=$PublicClientId" `
        -s "enabled=true" `
        -s "protocol=openid-connect" `
        -s "publicClient=true" `
        -s "standardFlowEnabled=true" `
        -s "directAccessGrantsEnabled=true" `
        -s 'redirectUris=["https://oauth.pstmn.io/v1/callback","http://localhost:8083/*"]' `
        -s 'webOrigins=["*"]'
} else {
    Write-Host "Client already exists."
}

Write-Host "Creating roles if missing..."
if (-not (Test-Kcadm get "roles/customer_user" -r $Realm)) {
    Invoke-Kcadm create roles `
        -r $Realm `
        -s "name=customer_user" `
        -s "description=Regular customer API user"
} else {
    Write-Host "Role customer_user already exists."
}

if (-not (Test-Kcadm get "roles/customer_admin" -r $Realm)) {
    Invoke-Kcadm create roles `
        -r $Realm `
        -s "name=customer_admin" `
        -s "description=Customer API administrator"
} else {
    Write-Host "Role customer_admin already exists."
}

Write-Host "Creating test user if missing: $TestUsername"
$userExists = docker exec $KeycloakContainer $Kcadm get users -r $Realm -q "username=$TestUsername" --fields username --format csv --noquotes
if ([string]::IsNullOrWhiteSpace($userExists)) {
    Invoke-Kcadm create users `
        -r $Realm `
        -s "username=$TestUsername" `
        -s "enabled=true" `
        -s "emailVerified=true" `
        -s "email=$TestUsername@example.com" `
        -s "firstName=Customer" `
        -s "lastName=One"
} else {
    Write-Host "User already exists."
}

Write-Host "Setting test user password..."
Invoke-Kcadm set-password `
    -r $Realm `
    --username $TestUsername `
    --new-password $TestPassword `
    --temporary=false

Write-Host "Assigning customer_user role..."
Invoke-Kcadm add-roles `
    -r $Realm `
    --uusername $TestUsername `
    --rolename "customer_user"

Write-Host "Keycloak setup completed."
Write-Host "Realm: $Realm"
Write-Host "Client: $PublicClientId"
Write-Host "User: $TestUsername"
