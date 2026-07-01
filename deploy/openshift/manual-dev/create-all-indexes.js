// Run from repository root:
//
// mongosh "mongodb://admin:admin123@localhost:27017/admin" ./scripts/manual/create-all-indexes.js
//
// This script is intentionally safe to rerun if existing indexes have the same definitions.

print("Creating customer-service indexes...")

const customerDb = db.getSiblingDB("customerdb")

customerDb.customers.createIndex(
  { customerId: 1 },
  {
    unique: true,
    name: "ux_customers_customerId"
  }
)

customerDb.customers.createIndex(
  { email: 1 },
  {
    unique: true,
    name: "ux_customers_email",
    partialFilterExpression: { email: { $type: "string" } }
  }
)

customerDb.customers.createIndex(
  { phone: 1 },
  {
    unique: true,
    name: "ux_customers_phone",
    partialFilterExpression: { phone: { $type: "string" } }
  }
)

customerDb.customers.createIndex(
  { status: 1 },
  {
    name: "idx_customers_status"
  }
)

customerDb.customers.createIndex(
  { lastName: 1, firstName: 1 },
  {
    name: "idx_customers_name"
  }
)

printjson(customerDb.customers.getIndexes())

print("Creating optional order-service indexes...")

const orderDb = db.getSiblingDB("orderdb")

orderDb.orders.createIndex(
  { orderId: 1 },
  {
    unique: true,
    name: "ux_orders_orderId"
  }
)

orderDb.orders.createIndex(
  { customerId: 1, createdAt: -1 },
  {
    name: "idx_orders_customerId_createdAt"
  }
)

printjson(orderDb.orders.getIndexes())

print("Creating optional payment-service indexes...")

const paymentDb = db.getSiblingDB("paymentdb")

paymentDb.payments.createIndex(
  { eventId: 1 },
  {
    unique: true,
    name: "ux_payments_eventId"
  }
)

paymentDb.payments.createIndex(
  { orderId: 1 },
  {
    unique: true,
    name: "ux_payments_orderId"
  }
)

printjson(paymentDb.payments.getIndexes())

print("MongoDB index setup completed.")
