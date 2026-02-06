# 🏦 Sistema de Microservicios Bancarios - Devsu

Sistema de gestión bancaria implementado con arquitectura de microservicios usando Spring Boot, PostgreSQL, RabbitMQ y Redis.

## 📋 Tabla de Contenidos

- [Descripción General](#-descripción-general)
- [Arquitectura](#-arquitectura)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Ejecución del Proyecto](#-ejecución-del-proyecto)
- [Endpoints Disponibles](#-endpoints-disponibles)
- [Casos de Uso](#-casos-de-uso)
- [Pruebas](#-pruebas)
- [Documentación API (Swagger)](#-documentación-api-swagger)
- [Troubleshooting](#-troubleshooting)

---

## 🎯 Descripción General

Sistema bancario distribuido que gestiona clientes, cuentas bancarias y movimientos financieros mediante dos microservicios independientes que se comunican de forma asíncrona (RabbitMQ) y síncrona (REST + Redis Cache).

### Funcionalidades Principales

- ✅ **F1:** CRUD completo de Clientes, Cuentas y Movimientos
- ✅ **F2:** Registro de movimientos con actualización automática de saldos
- ✅ **F3:** Validación de saldo insuficiente
- ✅ **F4:** Reportes de estado de cuenta por rango de fechas
- ✅ **F5:** 95 pruebas unitarias con 96% de cobertura
- ✅ **F6:** 3 pruebas de integración con Karate DSL
- ✅ **F7:** Despliegue completo en Docker

---

## 🏗️ Arquitectura

### Microservicios

```
┌─────────────────────────────────────────────────────────────┐
│                     ARQUITECTURA GENERAL                     │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐              ┌──────────────────┐    │
│  │   ClientApp      │              │ TransactionApp   │    │
│  │   (Puerto 8080)  │◄────REST────►│  (Puerto 8081)   │    │
│  │                  │              │                  │    │
│  │  - Clientes      │              │  - Cuentas       │    │
│  │  - Personas      │              │  - Movimientos   │    │
│  │                  │              │  - Reportes      │    │
│  └────────┬─────────┘              └────────┬─────────┘    │
│           │                                 │              │
│           │         ┌──────────────┐        │              │
│           └────────►│   RabbitMQ   │◄───────┘              │
│                     │  (Mensajería)│                       │
│                     └──────────────┘                       │
│                                                              │
│           │                                 │              │
│           ▼                                 ▼              │
│  ┌──────────────────┐              ┌──────────────────┐    │
│  │  PostgreSQL      │              │  PostgreSQL      │    │
│  │  clientdb        │              │  transactiondb   │    │
│  └──────────────────┘              └──────────────────┘    │
│                                            │              │
│                                            ▼              │
│                                    ┌──────────────────┐    │
│                                    │     Redis        │    │
│                                    │   (Cache)        │    │
│                                    └──────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### Patrones Implementados

- **Clean Architecture:** Separación en capas (Domain, Application, Infrastructure)
- **Repository Pattern:** Abstracción de acceso a datos
- **DTO Pattern:** Transferencia de datos entre capas
- **Service Layer Pattern:** Lógica de negocio encapsulada
- **Event-Driven Architecture:** Comunicación asíncrona con RabbitMQ
- **Cache-Aside Pattern:** Redis como cache de clientes

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Data JPA**
- **Spring AMQP (RabbitMQ)**
- **Spring Data Redis**
- **Spring WebFlux (WebClient)**
- **Lombok**

### Base de Datos
- **PostgreSQL 15**

### Mensajería y Cache
- **RabbitMQ 3.12**
- **Redis 7.2**

### Contenedores
- **Docker**
- **Docker Compose**

### Documentación
- **SpringDoc OpenAPI 3 (Swagger)**

### Testing
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**
- **JaCoCo (Cobertura de código)**

---

## 📦 Requisitos Previos

### Software Necesario

1. **Docker Desktop** (versión 20.10 o superior)
   - [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop)
   - Verificar instalación: `docker --version` y `docker-compose --version`

2. **Git** (para clonar el repositorio)
   - [Descargar Git](https://git-scm.com/downloads)
   - Verificar instalación: `git --version`

3. **(Opcional) Java 17 y Maven** - Solo si deseas ejecutar sin Docker
   - [Descargar Java 17](https://adoptium.net/)
   - [Descargar Maven](https://maven.apache.org/download.cgi)

### Recursos del Sistema

- **RAM:** Mínimo 4GB disponibles (recomendado 8GB)
- **Disco:** Mínimo 5GB libres
- **Puertos disponibles:** 5432, 5433, 5672, 6379, 8080, 8081, 15672

---

## 🚀 Instalación y Configuración

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/MrYepez0710/DevsuTest.git
cd DevsuTest/
```

> ⚠️ **NOTA IMPORTANTE:** Este proyecto está desplegado en AWS EC2 con IP pública dinámica (`ec2-18-208-159-85.compute-1.amazonaws.com`). En caso de reinicio de la instancia, la IP cambiará y todas las URLs del documento deberán actualizarse. Para ejecución local, reemplazar la URL de AWS por `localhost` en todos los comandos.

### Paso 2: Estructura del Proyecto

Después de clonar, la estructura debe ser:

```
proyecto/
├── ClientApp/              # Microservicio de Clientes
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── TransactionApp/         # Microservicio de Transacciones
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml      # Orquestación de servicios
├── BaseDatos.sql          # Script de base de datos
└── README.md              # Este archivo
```

### Paso 3: Verificar Docker

```bash
# Verificar que Docker está corriendo
docker ps

# Verificar versión de Docker Compose
docker-compose --version
```

---

## ▶️ Ejecución del Proyecto

### Opción 1: Ejecución Completa con Docker (Recomendado)

Este es el método más simple y recomendado.

#### 1. Iniciar todos los servicios

```bash
# Desde la raíz del proyecto
docker-compose up -d
```

Este comando:
- ✅ Descarga todas las imágenes necesarias
- ✅ Construye los microservicios
- ✅ Crea las bases de datos
- ✅ Inicia RabbitMQ y Redis
- ✅ Levanta los microservicios
- ✅ Configura la red entre servicios

#### 2. Verificar que todos los servicios están corriendo

```bash
docker-compose ps
```

Deberías ver 6 servicios en estado "Up":
- `clientdb` (PostgreSQL)
- `transactiondb` (PostgreSQL)
- `rabbitmq` (RabbitMQ)
- `redis` (Redis)
- `clientapp` (Microservicio)
- `transactionapp` (Microservicio)

#### 3. Ver logs de los servicios

```bash
# Ver todos los logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f clientapp
docker-compose logs -f transactionapp
```

#### 4. Cargar datos iniciales

```bash
# Conectar a la base de datos de clientes
docker exec -i clientdb psql -U postgres -d devsu_clients_bd < BaseDatos.sql

# O ejecutar el script manualmente
docker exec -it clientdb psql -U postgres
\c devsu_clients_bd
-- Copiar y pegar el contenido de BaseDatos.sql
```

#### 5. Verificar que los servicios responden

```bash
# Health check ClientApp
curl http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api/actuator/health

# Health check TransactionApp
curl http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/actuator/health
```

Ambos deben responder: `{"status":"UP"}`

---

### Opción 2: Ejecución Local (Sin Docker)

Si prefieres ejecutar los microservicios localmente:

#### 1. Iniciar solo la infraestructura con Docker

```bash
# Iniciar solo PostgreSQL, RabbitMQ y Redis
docker-compose up -d clientdb transactiondb rabbitmq redis
```

#### 2. Configurar variables de entorno

**Para ClientApp:**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://ec2-18-208-159-85.compute-1.amazonaws.com:5432/devsu_clients_bd
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=system
export SPRING_RABBITMQ_HOST=ec2-18-208-159-85.compute-1.amazonaws.com
```

**Para TransactionApp:**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://ec2-18-208-159-85.compute-1.amazonaws.com:5433/devsu_transactions_bd
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=system
export SPRING_RABBITMQ_HOST=ec2-18-208-159-85.compute-1.amazonaws.com
export SPRING_REDIS_HOST=ec2-18-208-159-85.compute-1.amazonaws.com
export CLIENTAPP_URL=http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api
```

#### 3. Ejecutar los microservicios

```bash
# Terminal 1 - ClientApp
cd ClientApp
./mvnw spring-boot:run

# Terminal 2 - TransactionApp
cd TransactionApp
./mvnw spring-boot:run
```

---

## 🌐 Endpoints Disponibles

### ClientApp (Puerto 8080)

#### Clientes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/clientes` | Listar todos los clientes |
| GET | `/api/clientes/{id}` | Obtener cliente por ID |
| POST | `/api/clientes` | Crear nuevo cliente |
| PUT | `/api/clientes/{clientId}` | Actualizar cliente |
| PATCH | `/api/clientes/{id}` | Actualización parcial |
| DELETE | `/api/clientes/{id}` | Eliminar cliente (soft delete) |

**Ejemplo POST /api/clientes:**
```json
{
  "name": "José Lema",
  "gender": "Masculino",
  "age": 35,
  "idNumber": "1234567890",
  "address": "Otavalo sn y principal",
  "phone": "098254785",
  "clientId": "JLEMA001",
  "password": "1234",
  "state": "true"
}
```

---

### TransactionApp (Puerto 8081)

#### Cuentas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/cuentas` | Listar todas las cuentas |
| GET | `/api/cuentas?clientId={id}` | Filtrar por cliente |
| GET | `/api/cuentas/{id}` | Obtener cuenta por ID |
| GET | `/api/cuentas/numero/{accountNumber}` | Buscar por número de cuenta |
| POST | `/api/cuentas` | Crear nueva cuenta |
| PUT | `/api/cuentas/{id}` | Actualizar cuenta |
| PATCH | `/api/cuentas/{id}` | Actualización parcial |

**Ejemplo POST /api/cuentas:**
```json
{
  "accountNumber": "478758",
  "accountType": "AHORROS",
  "balance": 2000.0,
  "state": "ACTIVA",
  "clientId": "JLEMA001"
}
```

#### Movimientos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/movimientos` | Listar todos los movimientos |
| GET | `/api/movimientos?accountId={id}` | Filtrar por cuenta |
| GET | `/api/movimientos/{id}` | Obtener movimiento por ID |
| POST | `/api/movimientos` | Crear nuevo movimiento |
| PUT | `/api/movimientos/{id}` | Actualizar movimiento |
| PATCH | `/api/movimientos/{id}` | Actualización parcial |

**Ejemplo POST /api/movimientos:**
```json
{
  "accountId": 1,
  "movementDate": "2026-02-04T10:00:00",
  "movementType": "Retiro de 575",
  "amount": -575.0,
  "state": "ACTIVO"
}
```

#### Reportes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reportes?clientId={id}&startDate={fecha}&endDate={fecha}` | Estado de cuenta |

**Ejemplo:**
```bash
GET /api/reportes?clientId=JLEMA001&startDate=2026-02-01T00:00:00&endDate=2026-02-28T23:59:59
```

**Respuesta:**
```json
{
  "reportDate": "2026-02-05T20:30:00",
  "client": {
    "clientId": "JLEMA001",
    "clientName": "José Lema"
  },
  "accounts": [
    {
      "accountId": 1,
      "accountNumber": "478758",
      "accountType": "AHORROS",
      "clientId": "JLEMA001",
      "initialBalance": 2000.0,
      "finalBalance": 1425.0,
      "movements": [
        {
          "movementId": 1,
          "movementDate": "2026-02-04T10:00:00",
          "movementType": "Retiro de 575",
          "amount": -575.0,
          "balance": 1425.0,
          "state": "ACTIVO"
        }
      ]
    }
  ],
  "summary": {
    "totalAccounts": 1,
    "totalMovements": 1,
    "totalDeposits": 0.0,
    "totalWithdrawals": -575.0,
    "netChange": -575.0
  }
}
```

---

## 📚 Casos de Uso

### Caso 1: Crear Clientes

```bash
# José Lema
curl -X POST http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "José Lema",
    "gender": "Masculino",
    "age": 35,
    "idNumber": "1234567890",
    "address": "Otavalo sn y principal",
    "phone": "098254785",
    "clientId": "JLEMA001",
    "password": "1234",
    "state": "true"
  }'

# Marianela Montalvo
curl -X POST http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Marianela Montalvo",
    "gender": "Femenino",
    "age": 28,
    "idNumber": "0987654321",
    "address": "Amazonas y NNUU",
    "phone": "097548965",
    "clientId": "MMONTALVO001",
    "password": "5678",
    "state": "true"
  }'
```

### Caso 2: Crear Cuentas

```bash
# Cuenta de ahorros para José Lema
curl -X POST http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "478758",
    "accountType": "AHORROS",
    "balance": 2000.0,
    "state": "ACTIVA",
    "clientId": "JLEMA001"
  }'

# Cuenta corriente para Marianela Montalvo
curl -X POST http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "225487",
    "accountType": "CORRIENTE",
    "balance": 100.0,
    "state": "ACTIVA",
    "clientId": "MMONTALVO001"
  }'
```

### Caso 3: Realizar Movimientos

```bash
# Retiro de 575 de la cuenta 478758
curl -X POST http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "movementDate": "2026-02-04T10:00:00",
    "movementType": "Retiro de 575",
    "amount": -575.0,
    "state": "ACTIVO"
  }'

# Depósito de 600 en la cuenta 225487
curl -X POST http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 2,
    "movementDate": "2026-02-05T11:00:00",
    "movementType": "Deposito de 600",
    "amount": 600.0,
    "state": "ACTIVO"
  }'
```

### Caso 4: Generar Reporte

```bash
curl "http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/reportes?clientId=JLEMA001&startDate=2026-02-01T00:00:00&endDate=2026-02-28T23:59:59"
```

---

## 🧪 Pruebas

### Ejecutar Pruebas Unitarias

```bash
# ClientApp
cd ClientApp
./mvnw test

# TransactionApp
cd TransactionApp
./mvnw test
```

### Generar Reporte de Cobertura (JaCoCo)

```bash
# ClientApp
cd ClientApp
./mvnw clean test jacoco:report
# Reporte en: target/site/jacoco/index.html

# TransactionApp
cd TransactionApp
./mvnw clean test jacoco:report
# Reporte en: target/site/jacoco/index.html
```

### Estadísticas de Pruebas

- **ClientApp:** 32 tests unitarios
- **TransactionApp:** 63 tests unitarios + 3 tests integración (Karate)
- **Total de pruebas:** 98 tests
- **Cobertura Service Layer:** 96%
- **Cobertura Controller Layer:** 81%
- **Cobertura General:** ~75%

### Pruebas de Integración (Karate DSL)

El proyecto incluye **3 escenarios** de pruebas de integración end-to-end usando Karate DSL que validan el flujo completo del sistema bancario.

#### Escenarios implementados:

**Escenario 1: Flujo completo exitoso**
1. ✅ Crear un cliente en ClientApp
2. ✅ Verificar que el cliente existe
3. ✅ Crear una cuenta asociada al cliente en TransactionApp
4. ✅ Verificar que la cuenta existe
5. ✅ Realizar un depósito y verificar actualización de saldo
6. ✅ Realizar un retiro y verificar actualización de saldo
7. ✅ Listar movimientos de la cuenta

**Escenario 2: Validación de saldo insuficiente (F3)**
1. ✅ Crear cliente y cuenta con saldo bajo (100)
2. ✅ Intentar retiro mayor al saldo disponible (200)
3. ✅ Verificar error 400: "Saldo no disponible"
4. ✅ Confirmar que el saldo no cambió

**Escenario 3: Generar reporte de estado de cuenta (F4)**
1. ✅ Crear cliente y cuenta
2. ✅ Crear múltiples movimientos (depósitos y retiros)
3. ✅ Generar reporte por rango de fechas
4. ✅ Validar estructura del reporte (cliente, cuentas, movimientos, summary)
5. ✅ Verificar cálculos de saldos y totales

#### Requisitos previos

**IMPORTANTE:** Los servicios deben estar corriendo antes de ejecutar los tests de integración:

```bash
# Iniciar servicios con Docker Compose
docker-compose up -d

# Verificar que los servicios están activos
curl http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api/actuator/health
curl http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/actuator/health
```

#### Ejecutar solo tests de integración

```bash
cd TransactionApp
./mvnw test -Dtest=IntegrationTest
```

#### Ejecutar todos los tests (unitarios + integración)

```bash
cd TransactionApp
./mvnw test
```

#### Ver reporte HTML de Karate

Después de ejecutar los tests, abre el reporte en tu navegador:

```
TransactionApp/target/karate-reports/karate-summary.html
```

El reporte incluye:
- Detalles de cada paso del test
- Tiempos de ejecución
- Request/Response de cada llamada HTTP
- Logs detallados

#### Ubicación de los archivos

```
TransactionApp/src/test/java/karate/
├── karate-config.js              # Configuración global
└── integration/
    ├── integration.feature       # Test de integración
    └── IntegrationTest.java      # JUnit runner
```

---

## 📖 Documentación API (Swagger)

### Acceder a Swagger UI

Una vez que los servicios estén corriendo:

- **ClientApp:** http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api/swagger-ui/index.html
- **TransactionApp:** http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/swagger-ui/index.html

### Características de Swagger

- ✅ Documentación interactiva de todos los endpoints
- ✅ Probar endpoints directamente desde el navegador
- ✅ Ver modelos de datos (DTOs)
- ✅ Ejemplos de request/response

---

## 🔧 Troubleshooting

### Problema: Los contenedores no inician

**Solución:**
```bash
# Detener todos los contenedores
docker-compose down

# Limpiar volúmenes
docker-compose down -v

# Reconstruir e iniciar
docker-compose up -d --build
```

### Problema: Puerto ya en uso

**Solución:**
```bash
# Ver qué está usando el puerto
netstat -ano | findstr :8080

# Matar el proceso (Windows)
taskkill /PID <PID> /F

# O cambiar el puerto en docker-compose.yml
ports:
  - "8082:8080"  # Usar puerto 8082 en lugar de 8080
```

### Problema: Error de conexión a base de datos

**Solución:**
```bash
# Verificar que las bases de datos están corriendo
docker-compose ps

# Ver logs de las bases de datos
docker-compose logs clientdb
docker-compose logs transactiondb

# Reiniciar solo las bases de datos
docker-compose restart clientdb transactiondb
```

### Problema: RabbitMQ no se conecta

**Solución:**
```bash
# Verificar estado de RabbitMQ
docker-compose logs rabbitmq

# Acceder a la consola de administración
# http://ec2-18-208-159-85.compute-1.amazonaws.com:15672
# Usuario: guest
# Contraseña: guest

# Reiniciar RabbitMQ
docker-compose restart rabbitmq
```

### Problema: Redis no funciona

**Solución:**
```bash
# Verificar Redis
docker exec -it redis redis-cli ping
# Debe responder: PONG

# Ver logs
docker-compose logs redis

# Reiniciar Redis
docker-compose restart redis
```

### Problema: Swagger no carga

**Verificar:**
1. El servicio está corriendo: `docker-compose ps`
2. La URL es correcta: `http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api/swagger-ui/index.html`
3. El context path `/api` está incluido
4. Ver logs: `docker-compose logs clientapp`

---

## 🛑 Detener el Proyecto

```bash
# Detener servicios (mantiene datos)
docker-compose stop

# Detener y eliminar contenedores (mantiene volúmenes)
docker-compose down

# Detener, eliminar contenedores y volúmenes (limpieza completa)
docker-compose down -v
```

---

## 📊 Monitoreo y Administración

### RabbitMQ Management Console
- **URL:** http://ec2-18-208-159-85.compute-1.amazonaws.com:15672
- **Usuario:** guest
- **Contraseña:** guest

### PostgreSQL - ClientDB
```bash
docker exec -it clientdb psql -U postgres -d devsu_clients_bd
```

### PostgreSQL - TransactionDB
```bash
docker exec -it transactiondb psql -U postgres -d devsu_transactions_bd
```

### Redis CLI
```bash
docker exec -it redis redis-cli
```

---

## 📝 Notas Adicionales

### Arquitectura Clean Architecture

El proyecto sigue los principios de Clean Architecture:

```
src/
├── domain/              # Entidades y lógica de negocio
│   ├── model/          # Entidades JPA
│   ├── repository/     # Interfaces de repositorio
│   └── exception/      # Excepciones de dominio
├── application/         # Casos de uso
│   ├── service/        # Interfaces de servicio
│   ├── dto/            # DTOs
│   └── mapper/         # Mappers
└── infrastructure/      # Implementaciones técnicas
    ├── controller/     # REST Controllers
    ├── config/         # Configuraciones
    ├── messaging/      # RabbitMQ
    ├── cache/          # Redis
    └── client/         # REST Clients
```

### Comunicación entre Microservicios

1. **Síncrona (REST + Cache):**
   - TransactionApp → ClientApp (validar cliente)
   - Cache en Redis para mejorar rendimiento

2. **Asíncrona (RabbitMQ):**
   - ClientApp publica eventos de cliente
   - TransactionApp escucha y actualiza cache

---

## 👥 Contacto y Soporte

Para preguntas o problemas:
- Revisar la sección de [Troubleshooting](#-troubleshooting)
- Ver logs: `docker-compose logs -f`
- Verificar health checks: `/api/actuator/health`

---

## 📄 Licencia

Este proyecto fue desarrollado como parte de una prueba técnica para Devsu.
