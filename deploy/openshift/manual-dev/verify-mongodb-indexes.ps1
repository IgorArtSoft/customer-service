$ErrorActionPreference = "Stop"

$MongoUri = $env:MONGODB_ADMIN_URI
if ([string]::IsNullOrWhiteSpace($MongoUri)) {
    $MongoUri = "mongodb://admin:admin123@localhost:27017/admin"
}

Write-Host "Using MongoDB URI: $MongoUri"

mongosh $MongoUri --eval @'
print("customerdb.customers indexes:")
printjson(db.getSiblingDB("customerdb").customers.getIndexes())

print("orderdb.orders indexes:")
printjson(db.getSiblingDB("orderdb").orders.getIndexes())

print("paymentdb.payments indexes:")
printjson(db.getSiblingDB("paymentdb").payments.getIndexes())
'@
