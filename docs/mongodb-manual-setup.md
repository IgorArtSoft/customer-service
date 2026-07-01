# MongoDB Manual Setup

This guide documents manual MongoDB database and index setup for the local microservices environment.

## Local assumptions

| Service | Database | Collection |
|---|---|---|
| customer-service | `customerdb` | `customers` |
| order-service | `orderdb` | `orders` |
| payment-service | `paymentdb` | `payments` |

Connection:

```text
mongodb://admin:admin123@localhost:27017/admin
```

The application database is selected inside the script with `db.getSiblingDB(...)`.

## Connect with `mongosh`

```powershell
mongosh "mongodb://admin:admin123@localhost:27017/admin"
```

Switch to customer database:

```javascript
use customerdb
```

## Create customer-service indexes

```javascript
use customerdb

db.customers.createIndex(
  { customerId: 1 },
  {
    unique: true,
    name: "ux_customers_customerId"
  }
)

db.customers.createIndex(
  { email: 1 },
  {
    unique: true,
    name: "ux_customers_email",
    partialFilterExpression: { email: { $type: "string" } }
  }
)

db.customers.createIndex(
  { phone: 1 },
  {
    unique: true,
    name: "ux_customers_phone",
    partialFilterExpression: { phone: { $type: "string" } }
  }
)

db.customers.createIndex(
  { status: 1 },
  {
    name: "idx_customers_status"
  }
)

db.customers.createIndex(
  { lastName: 1, firstName: 1 },
  {
    name: "idx_customers_name"
  }
)

db.customers.getIndexes()
```

## Optional order-service indexes

```javascript
use orderdb

db.orders.createIndex(
  { orderId: 1 },
  {
    unique: true,
    name: "ux_orders_orderId"
  }
)

db.orders.createIndex(
  { customerId: 1, createdAt: -1 },
  {
    name: "idx_orders_customerId_createdAt"
  }
)

db.orders.getIndexes()
```

## Optional payment-service indexes

```javascript
use paymentdb

db.payments.createIndex(
  { eventId: 1 },
  {
    unique: true,
    name: "ux_payments_eventId"
  }
)

db.payments.createIndex(
  { orderId: 1 },
  {
    unique: true,
    name: "ux_payments_orderId"
  }
)

db.payments.getIndexes()
```

## Run the prepared script

From the repository root:

```powershell
mongosh "mongodb://admin:admin123@localhost:27017/admin" .\scripts\manual\create-all-indexes.js
```

Verify indexes:

```powershell
.\scripts\manual\verify-mongodb-indexes.ps1
```

## Important behavior of unique indexes

Unique indexes fail to build if existing data already violates the uniqueness rule.

Before creating a unique index on a collection that already has data, check duplicates.

Example for `customerId`:

```javascript
use customerdb

db.customers.aggregate([
  { $group: { _id: "$customerId", count: { $sum: 1 }, ids: { $push: "$_id" } } },
  { $match: { count: { $gt: 1 } } }
])
```

Example for `email`:

```javascript
use customerdb

db.customers.aggregate([
  { $match: { email: { $type: "string" } } },
  { $group: { _id: "$email", count: { $sum: 1 }, ids: { $push: "$_id" } } },
  { $match: { count: { $gt: 1 } } }
])
```

## Why use partial unique indexes for optional email/phone?

If `email` or `phone` is optional, a plain unique index can cause problems when multiple documents have a missing or null value.

This project uses partial unique indexes so uniqueness is enforced only when the field exists as a string.

## Drop and recreate an index

If the index definition must change:

```javascript
use customerdb

db.customers.dropIndex("ux_customers_email")

db.customers.createIndex(
  { email: 1 },
  {
    unique: true,
    name: "ux_customers_email",
    partialFilterExpression: { email: { $type: "string" } }
  }
)
```

## Production recommendation

For production, prefer versioned migration tooling or an application startup migration process instead of undocumented manual index changes.

For this demo project, manual commands are acceptable as long as they are documented and repeatable.
