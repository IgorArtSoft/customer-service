$ErrorActionPreference = "Stop"

$ProjectName = "igorart7-dev"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

$MongoDbSecretFile = Join-Path $ScriptDir "secret-templates\customer-mongodb-secret.local.yml"
$CustomerServiceMongoDbSecretFile = Join-Path $ScriptDir "secret-templates\customer-service-mongodb-secret.local.yml"

oc project $ProjectName

if (-not (Test-Path $MongoDbSecretFile)) {
    throw "Missing local secret file: $MongoDbSecretFile"
}

if (-not (Test-Path $CustomerServiceMongoDbSecretFile)) {
    throw "Missing local secret file: $CustomerServiceMongoDbSecretFile"
}

Write-Host "Applying MongoDB Secret..."
oc apply -f $MongoDbSecretFile

Write-Host "Applying customer-service MongoDB Secret..."
oc apply -f $CustomerServiceMongoDbSecretFile

Write-Host "Secrets applied."