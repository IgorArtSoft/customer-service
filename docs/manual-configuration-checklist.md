# Manual Configuration Checklist

Use this checklist after a fresh local environment reset.

## Infrastructure

- [ ] MongoDB container is running.
- [ ] Keycloak container is running.
- [ ] Keycloak Admin Console opens at `http://localhost:8180/admin`.
- [ ] Customer service port `8083` is free.

## Keycloak

- [ ] Realm `customer-microservice` exists.
- [ ] Client `postman-client` exists.
- [ ] Client Authentication is OFF for `postman-client`.
- [ ] Standard Flow is ON.
- [ ] Direct Access Grants is ON for local development.
- [ ] Redirect URI includes `https://oauth.pstmn.io/v1/callback`.
- [ ] Realm role `customer_user` exists.
- [ ] Realm role `customer_admin` exists.
- [ ] User `customer1` exists.
- [ ] User `customer1` is enabled.
- [ ] User `customer1` has non-temporary password.
- [ ] User `customer1` has role `customer_user`.

## MongoDB

- [ ] Database `customerdb` exists or will be created on first write.
- [ ] Collection `customers` exists or will be created on first write.
- [ ] Unique index `ux_customers_customerId` exists.
- [ ] Unique partial index `ux_customers_email` exists.
- [ ] Unique partial index `ux_customers_phone` exists.
- [ ] Search index `idx_customers_status` exists.
- [ ] Search index `idx_customers_name` exists.

## Application

- [ ] `SERVER_PORT` is set to `8083`.
- [ ] `MONGODB_URI` points to `customerdb`.
- [ ] `OIDC_ISSUER_URI` equals `http://localhost:8180/realms/customer-microservice`.
- [ ] `.\mvnw.com clean spring-boot:run` starts successfully.
- [ ] `/actuator/health` returns `UP`.

## API testing

- [ ] Access token can be obtained from Keycloak.
- [ ] Access token `iss` claim matches application issuer URI.
- [ ] Protected API call succeeds with `Authorization: Bearer <token>`.
- [ ] API returns `401` when token is missing.
- [ ] Validation errors return `400`.
