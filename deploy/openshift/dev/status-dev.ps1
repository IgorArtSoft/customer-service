$ErrorActionPreference = "Stop"

$ProjectName = "igorart7-dev"
$AppName = "customer-service"
$MongoStatefulSetName = "customer-mongodb"

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

Write-Host "Using OpenShift project: $ProjectName"
Invoke-Oc project $ProjectName

Write-Host ""
Write-Host "Pods:"
Invoke-Oc get pods

Write-Host ""
Write-Host "Deployments:"
Invoke-Oc get deployment

Write-Host ""
Write-Host "StatefulSets:"
Invoke-Oc get statefulset

Write-Host ""
Write-Host "Services:"
Invoke-Oc get svc

Write-Host ""
Write-Host "Routes:"
Invoke-Oc get route

Write-Host ""
Write-Host "Persistent Volume Claims:"
Invoke-Oc get pvc

Write-Host ""
Write-Host "ConfigMap:"
Invoke-Oc get configmap customer-service-config

Write-Host ""
Write-Host "Secrets:"
Invoke-Oc get secret customer-mongodb-secret
Invoke-Oc get secret customer-service-mongodb

Write-Host ""
Write-Host "customer-service environment references:"
Invoke-Oc set env deployment/$AppName --list

Write-Host ""
Write-Host "customer-service rollout status:"
Invoke-Oc rollout status deployment/$AppName --timeout=60s

Write-Host ""
Write-Host "MongoDB rollout status:"
Invoke-Oc rollout status statefulset/$MongoStatefulSetName --timeout=60s

Write-Host ""
Write-Host "customer-service Service details:"
Invoke-Oc describe svc customer-service

Write-Host ""
Write-Host "customer-mongodb Service details:"
Invoke-Oc describe svc customer-mongodb

$routeHost = & oc get route customer-service -o jsonpath="{.spec.host}"

if ($LASTEXITCODE -eq 0 -and $routeHost) {
    $healthUrl = "https://$routeHost/actuator/health"
    $swaggerUrl = "https://$routeHost/swagger-ui/index.html"

    Write-Host ""
    Write-Host "Health URL:"
    Write-Host $healthUrl

    Write-Host ""
    Write-Host "Swagger UI:"
    Write-Host $swaggerUrl

    Write-Host ""
    Write-Host "Testing health endpoint..."

    try {
        $health = Invoke-RestMethod $healthUrl
        Write-Host "Health response:"
        $health | ConvertTo-Json -Depth 5
    }
    catch {
        Write-Host "Health check failed:"
        Write-Host $_.Exception.Message
    }
}
else {
    Write-Host ""
    Write-Host "Route host was not found."
}