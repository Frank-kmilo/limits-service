# limits-service
Prueba #2 Tumipay

## Descripción

**Limits Service** es un microservicio desarrollado en **Java 21** con **Spring Boot 3** que permite:

1. **Crear configuraciones de límites de transacciones** para clientes (diario, mensual y por transacción).
2. **Autorizar operaciones** según los límites configurados, considerando acumulados diarios y mensuales.
3. **Consultar autorizaciones** previamente procesadas.

El servicio sigue **arquitectura hexagonal**, separando claramente **dominio**, **aplicación**, **infraestructura** y **REST API**.

Además, el proyecto está preparado para:
- **Swagger/OpenAPI** para documentación interactiva.
- **Postman** para pruebas manuales de endpoints.
- **CI/CD** con **Jenkins** (pipeline definido).
- **Calidad de código y tests unitarios** con JUnit 5 y Mockito.

---

## Tecnologías utilizadas

- Java 21
- Spring Boot 3.5.11
- PostgreSQL
- Maven
- Lombok
- JUnit 5 + Mockito
- Swagger/OpenAPI
- Jenkins (CI/CD)

---

# Compilar
mvn clean install

# Ejecutar aplicación
mvn spring-boot:run

# Ejecutar tests
mvn test
---

## Endpoints

### 1. Crear límite (`POST /v1/limits`)

**Request:**

{
  "customer_id": "123",
  "currency_code": "USD",
  "country_code": "US",
  "daily_limit_amount": 10000,
  "monthly_limit_amount": 300000,
  "transaction_limit_amount": 5000
}

## Response
{
"response_code": "000",
"response_message": "Limit configuration created",
"data": {
"limit_id": "36749ed7-13f0-42b0-8c80-450a0b609082",
"status": "CREATED",
"currency_code": "USD",
"country_code": "US",
"daily_limit_amount": 10000,
"monthly_limit_amount": 300000,
"transaction_limit_amount": 5000
}
}

### 2 Autorizar operación (`POST /v1/authorizations`)
{
"customer_id": "123",
"client_operation_id": "op-001",
"amount": 1500,
"currency_code": "USD",
"country_code": "US",
"operation_timestamp": "2026-02-20T19:02:19Z"
}

## Response (aprobada)
{
"response_code": "000",
"response_message": "Operation approved",
"data": {
"status": "APPROVED",
"reason": "Operation approved",
"amount": 1500,
"processed_at": "2026-02-20T19:02:19Z"
}
}

## Response (rechazada por límite transacción):
{
"response_code": "001",
"response_message": "Transaction exceeds limit",
"data": {
"status": "REJECTED",
"reason": "Transaction exceeds limit",
"amount": 6000,
"processed_at": "2026-02-20T19:05:00Z"
}
}

## Response (rechazada por límite diario):
{
"response_code": "002",
"response_message": "Daily limit exceeded",
"data": {
"status": "REJECTED",
"reason": "Daily limit exceeded",
"amount": 5000,
"processed_at": "2026-02-20T19:10:00Z"
}
}

##  3. Consultar autorización (GET /v1/authorizations/{authorization_id})
{
"response_code": "000",
"response_message": "Operation approved",
"data": {
"status": "APPROVED",
"reason": "Operation approved",
"amount": 1500,
"processed_at": "2026-02-20T19:02:19Z"
}
}

## Swagger disponible en: http://localhost:8080/swagger-ui.html