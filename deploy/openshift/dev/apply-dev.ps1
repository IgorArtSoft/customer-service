$ErrorActionPreference = "Stop"

$ProjectName = "igorart7-dev"
$AppName = "customer-service"
$MongoStatefulSetName = "customer-mongodb"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

$ConfigMapFile = Join-Path $ScriptDir "00-customer-service-config.yml"
$MongoDbFile   = Join-Path $ScriptDir "01-mongodb.yml"
$RouteFile     = Join-Path $ScriptDir "02-customer-service-route.yml"

function Invoke-Oc {
    param(
        [Parameter(ValueFromRemainingArguments = $true)]
        [string[]] $Arguments
    )

    & oc @Arguments

    if ($LASTEXITCODE -ne 0) {
        throw "Command failed: oc $($Arguments -join ' ')"
    }
}

function Test-OcResourceExists {
    param(
        [string] $Resource
    )

    & oc get $Resource *> $null
    return $LASTEXITCODE -eq 0
}

Write-Host "Using OpenShift project: $ProjectName"
Invoke-Oc project $ProjectName

Write-Host ""
Write-Host "Checking required Secrets..."

if (-not (Test-OcResourceExists "secret/customer-mongodb-secret")) {
    throw @"
Missing required Secret: customer-mongodb-secret

Create it manually, for example:

oc create secret generic customer-mongodb-secret `
  --from-literal=MONGO_INITDB_ROOT_USERNAME=mongo_admin `
  --from-literal=MONGO_INITDB_ROOT_PASSWORD="YOUR_REAL_PASSWORD"

Do not commit real passwords to Git.
"@
}

if (-not (Test-OcResourceExists "secret/customer-service-mongodb")) {
    throw @"
Missing required Secret: customer-service-mongodb

Create it manually, for example:

oc create secret generic customer-service-mongodb `
  --from-literal=MONGODB_URI="mongodb://mongo_admin:YOUR_ENCODED_PASSWORD@customer-mongodb:27017/customerdb?authSource=admin"

Do not commit real passwords to Git.
"@
}

Write-Host "Required Secrets exist."

Write-Host ""
Write-Host "Applying customer-service ConfigMap..."
Invoke-Oc apply -f $ConfigMapFile

Write-Host ""
Write-Host "Applying MongoDB StatefulSet and Service..."
Invoke-Oc apply -f $MongoDbFile

Write-Host ""
Write-Host "Waiting for MongoDB StatefulSet rollout..."
Invoke-Oc rollout status statefulset/$MongoStatefulSetName --timeout=180s

Write-Host ""
Write-Host "Applying customer-service Route..."
Invoke-Oc apply -f $RouteFile

Write-Host ""
Write-Host "Checking customer-service Deployment..."

if (-not (Test-OcResourceExists "deployment/$AppName")) {
    throw @"
Missing deployment/$AppName

Create the application first, for example:

oc new-app registry.access.redhat.com/ubi9/openjdk-21~https://github.com/IgorArtSoft/customer-service.git `
  --name=customer-service
"@
}

Write-Host ""
Write-Host "Injecting ConfigMap into customer-service Deployment..."
Invoke-Oc set env deployment/$AppName --from=configmap/customer-service-config

Write-Host ""
Write-Host "Injecting MongoDB Secret into customer-service Deployment..."
Invoke-Oc set env deployment/$AppName --from=secret/customer-service-mongodb

Write-Host ""
Write-Host "Restarting customer-service Deployment..."
Invoke-Oc rollout restart deployment/$AppName

Write-Host ""
Write-Host "Waiting for customer-service rollout..."
Invoke-Oc rollout status deployment/$AppName --timeout=180s

Write-Host ""
Write-Host "Current OpenShift status:"
Invoke-Oc get pods
Invoke-Oc get svc
Invoke-Oc get route customer-service
Invoke-Oc get pvc

Write-Host ""
Write-Host "Deployment completed."

$routeHost = & oc get route customer-service -o jsonpath="{.spec.host}"

if ($LASTEXITCODE -eq 0 -and $routeHost) {
    Write-Host ""
    Write-Host "Health URL:"
    Write-Host "https://$routeHost/actuator/health"

    Write-Host ""
    Write-Host "Swagger UI:"
    Write-Host "https://$routeHost/swagger-ui/index.html"
}