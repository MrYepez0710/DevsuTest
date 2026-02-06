# ⚡ Guía Rápida - Sistema Bancario Microservicios

> **Nota:** Para documentación completa, ver [README.md](README.md)

---

## 🚀 Inicio Rápido (5 minutos)

### Requisitos
- Docker Desktop instalado y corriendo
- Puertos libres: 5432, 5433, 5672, 6379, 8080, 8081, 15672

### Pasos

```bash
# 1. Clonar repositorio
git clone https://github.com/MrYepez0710/DevsuTest.git
cd DevsuTest/

# 2. Levantar servicios
docker-compose up -d

# 3. Esperar ~30 segundos y verificar
curl http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api/actuator/health
curl http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/actuator/health
```

**¡Listo!** Los servicios están corriendo.

---

## 📊 URLs Importantes

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **ClientApp** | http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api | Gestión de clientes |
| **TransactionApp** | http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api | Cuentas y movimientos |
| **Swagger ClientApp** | http://ec2-18-208-159-85.compute-1.amazonaws.com:8080/api/swagger-ui/index.html | Documentación API |
| **Swagger TransactionApp** | http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/swagger-ui/index.html | Documentación API |
| **RabbitMQ UI** | http://ec2-18-208-159-85.compute-1.amazonaws.com:15672 | Usuario: guest / guest |

> ⚠️ **NOTA IMPORTANTE:** Las URLs mostradas corresponden a una instancia EC2 de AWS con IP pública dinámica. En caso de reinicio de la instancia, la IP cambiará y las URLs deberán actualizarse. Para uso local, reemplazar `ec2-18-208-159-85.compute-1.amazonaws.com` por `localhost`.

---

## 🧪 Ejecutar Tests

```bash
# Tests de ClientApp
cd ClientApp
./mvnw test

# Tests de TransactionApp (unitarios + integración)
cd TransactionApp
./mvnw test
```

**Resultado esperado:**
- ClientApp: 32 tests ✅
- TransactionApp: 66 tests (63 unitarios + 3 Karate) ✅

---

## 🎯 Casos de Uso Rápidos

### 1. Crear Cliente
```bash
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
```

### 2. Crear Cuenta
```bash
curl -X POST http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "478758",
    "accountType": "AHORROS",
    "initialBalance": 2000.0,
    "state": "ACTIVA",
    "clientId": "JLEMA001"
  }'
```

### 3. Crear Movimiento
```bash
curl -X POST http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "movementDate": "2026-02-05T10:00:00",
    "movementType": "Retiro de 575",
    "amount": -575.0,
    "state": "ACTIVO"
  }'
```

### 4. Generar Reporte
```bash
curl "http://ec2-18-208-159-85.compute-1.amazonaws.com:8081/api/reportes?clientId=JLEMA001&startDate=2026-02-01T00:00:00&endDate=2026-02-28T23:59:59"
```

---

## 🏗️ Arquitectura (Simplificada)

```
┌─────────────┐         ┌─────────────┐
│  ClientApp  │◄───────►│TransactionApp│
│  (8080)     │  REST   │   (8081)     │
└──────┬──────┘         └──────┬───────┘
       │                       │
       │    ┌──────────┐       │
       └───►│ RabbitMQ │◄──────┘
            └──────────┘
                 │
            ┌────▼────┐
            │  Redis  │
            └─────────┘
```

**Comunicación:**
- **Síncrona:** REST (TransactionApp → ClientApp)
- **Asíncrona:** RabbitMQ (eventos de clientes)
- **Cache:** Redis (clientes en TransactionApp)

---

## 📦 Estructura del Proyecto

```
proyecto/
├── ClientApp/              # Microservicio de Clientes
├── TransactionApp/         # Microservicio de Transacciones
├── docker-compose.yml      # Orquestación
├── BaseDatos.sql          # Scripts de BD
├── README.md              # Documentación completa
└── QUICKSTART.md          # Esta guía
```

---

## 🛑 Detener Servicios

```bash
# Detener (mantiene datos)
docker-compose stop

# Detener y eliminar (limpieza completa)
docker-compose down -v
```

---

## 🔍 Verificar RabbitMQ y Redis

### RabbitMQ
```bash
# Acceder a UI
open http://ec2-18-208-159-85.compute-1.amazonaws.com:15672
# Usuario: guest / Contraseña: guest

# Ver logs
docker-compose logs rabbitmq
```

### Redis
```bash
# Conectar a Redis CLI
docker exec -it redis redis-cli

# Ver clientes cacheados
KEYS client:*

# Ver un cliente
GET client:JLEMA001
```

---

## ❓ Problemas Comunes

### Puerto en uso
```bash
# Ver qué usa el puerto
netstat -ano | findstr :8080

# Cambiar puerto en docker-compose.yml
ports:
  - "8082:8080"
```

### Servicios no responden
```bash
# Ver logs
docker-compose logs clientapp
docker-compose logs transactionapp

# Reiniciar servicios
docker-compose restart
```

### Tests fallan
```bash
# Asegurar que Docker está corriendo
docker-compose ps

# Ejecutar solo tests unitarios
cd TransactionApp
./mvnw test -Dtest=!IntegrationTest
```

---

## 📚 Documentación Adicional

- **[README.md](README.md)** - Documentación completa y detallada

---

## ✅ Checklist de Verificación

- [ ] Docker Desktop corriendo
- [ ] `docker-compose up -d` ejecutado
- [ ] Servicios responden en puertos 8080 y 8081
- [ ] Swagger accesible
- [ ] Tests pasan (ClientApp y TransactionApp)
- [ ] RabbitMQ UI accesible (15672)
- [ ] Redis CLI funciona

---

## 🎯 Requisitos Cumplidos

| Requisito | Estado |
|-----------|--------|
| F1: CRUD | ✅ |
| F2: Movimientos | ✅ |
| F3: Validación saldo | ✅ |
| F4: Reportes | ✅ |
| F5: Tests unitarios | ✅ 96 tests |
| F6: Tests integración | ✅ 3 escenarios Karate |
| F7: Docker | ✅ |

---

**¿Necesitas más detalles?** Ver [README.md](README.md) completo.
