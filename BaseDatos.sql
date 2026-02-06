-- =====================================================
-- Script de Base de Datos - Prueba Técnica Devsu
-- =====================================================
-- Autor: Sistema de Microservicios
-- Fecha: 2026-02-02
-- Descripción: Script para crear las bases de datos y tablas
--              de los microservicios ClientApp y TransactionApp
-- =====================================================

-- =====================================================
-- CREACIÓN DE BASES DE DATOS
-- =====================================================

-- Base de datos para el microservicio de Clientes
DROP DATABASE IF EXISTS devsu_clients_bd;
CREATE DATABASE devsu_clients_bd
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Spain.1252'
    LC_CTYPE = 'Spanish_Spain.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Base de datos para el microservicio de Transacciones
DROP DATABASE IF EXISTS devsu_transactions_bd;
CREATE DATABASE devsu_transactions_bd
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Spain.1252'
    LC_CTYPE = 'Spanish_Spain.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- =====================================================
-- MICROSERVICIO: ClientApp (devsu_clients_bd)
-- =====================================================

\c devsu_clients_bd;

-- Tabla: person
-- Descripción: Entidad base que contiene información personal
DROP TABLE IF EXISTS client CASCADE;
DROP TABLE IF EXISTS person CASCADE;

CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 0 AND age <= 150),
    id_number VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    CONSTRAINT chk_age_positive CHECK (age >= 0),
    CONSTRAINT chk_age_realistic CHECK (age <= 150)
);

-- Índices para person
CREATE INDEX idx_person_id_number ON person(id_number);

-- Comentarios para person
COMMENT ON TABLE person IS 'Tabla base que almacena información personal';
COMMENT ON COLUMN person.id IS 'Identificador único autogenerado';
COMMENT ON COLUMN person.name IS 'Nombre completo de la persona';
COMMENT ON COLUMN person.gender IS 'Género de la persona';
COMMENT ON COLUMN person.age IS 'Edad de la persona (0-150)';
COMMENT ON COLUMN person.id_number IS 'Número de identificación único';
COMMENT ON COLUMN person.address IS 'Dirección de residencia';
COMMENT ON COLUMN person.phone IS 'Número de teléfono';

-- Tabla: client
-- Descripción: Hereda de person y agrega información específica del cliente
CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 0 AND age <= 150),
    id_number VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    client_id VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    CONSTRAINT chk_client_age_positive CHECK (age >= 0),
    CONSTRAINT chk_client_age_realistic CHECK (age <= 150)
);

-- Índices para client
CREATE INDEX idx_client_client_id ON client(client_id);
CREATE INDEX idx_client_id_number ON client(id_number);
CREATE INDEX idx_client_state ON client(state);

-- Comentarios para client
COMMENT ON TABLE client IS 'Tabla de clientes que extiende la información de person';
COMMENT ON COLUMN client.id IS 'Identificador único autogenerado';
COMMENT ON COLUMN client.client_id IS 'Identificador único de cliente (clave de negocio)';
COMMENT ON COLUMN client.password IS 'Contraseña del cliente';
COMMENT ON COLUMN client.state IS 'Estado del cliente (activo/inactivo)';

-- =====================================================
-- MICROSERVICIO: TransactionApp (devsu_transactions_bd)
-- =====================================================

\c devsu_transactions_bd;

-- Tabla: account
-- Descripción: Almacena información de cuentas bancarias
DROP TABLE IF EXISTS movement CASCADE;
DROP TABLE IF EXISTS account CASCADE;

CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    account_type VARCHAR(255) NOT NULL,
    balance DOUBLE PRECISION NOT NULL CHECK (balance >= 0),
    state VARCHAR(255) NOT NULL,
    account_key VARCHAR(255),
    client_id VARCHAR(255) NOT NULL,
    CONSTRAINT chk_balance_positive CHECK (balance >= 0)
);

-- Índices para account
CREATE INDEX idx_account_number ON account(account_number);
CREATE INDEX idx_account_client_id ON account(client_id);
CREATE INDEX idx_account_state ON account(state);

-- Comentarios para account
COMMENT ON TABLE account IS 'Tabla de cuentas bancarias asociadas a clientes';
COMMENT ON COLUMN account.id IS 'Identificador único autogenerado';
COMMENT ON COLUMN account.account_number IS 'Número de cuenta único';
COMMENT ON COLUMN account.account_type IS 'Tipo de cuenta (Ahorros, Corriente, etc.)';
COMMENT ON COLUMN account.balance IS 'Saldo actual de la cuenta';
COMMENT ON COLUMN account.state IS 'Estado de la cuenta (activa/inactiva)';
COMMENT ON COLUMN account.account_key IS 'Clave única de la cuenta';
COMMENT ON COLUMN account.client_id IS 'Referencia al ID del cliente (sin FK por microservicios)';

-- Tabla: movement
-- Descripción: Almacena los movimientos/transacciones de las cuentas
CREATE TABLE movement (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    movement_number BIGINT,
    movement_date TIMESTAMP NOT NULL,
    movement_type VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    balance DOUBLE PRECISION NOT NULL,
    state VARCHAR(255) NOT NULL,
    CONSTRAINT fk_movement_account FOREIGN KEY (account_id) 
        REFERENCES account(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

-- Índices para movement
CREATE INDEX idx_movement_account_id ON movement(account_id);
CREATE INDEX idx_movement_date ON movement(movement_date);
CREATE INDEX idx_movement_type ON movement(movement_type);
CREATE INDEX idx_movement_state ON movement(state);

-- Comentarios para movement
COMMENT ON TABLE movement IS 'Tabla de movimientos/transacciones bancarias';
COMMENT ON COLUMN movement.id IS 'Identificador único autogenerado';
COMMENT ON COLUMN movement.account_id IS 'Referencia a la cuenta asociada';
COMMENT ON COLUMN movement.movement_number IS 'Número de movimiento';
COMMENT ON COLUMN movement.movement_date IS 'Fecha y hora del movimiento';
COMMENT ON COLUMN movement.movement_type IS 'Tipo de movimiento (Depósito, Retiro, etc.)';
COMMENT ON COLUMN movement.amount IS 'Monto del movimiento (positivo o negativo)';
COMMENT ON COLUMN movement.balance IS 'Saldo resultante después del movimiento';
COMMENT ON COLUMN movement.state IS 'Estado del movimiento';

-- =====================================================
-- DATOS DE PRUEBA - MICROSERVICIO ClientApp
-- =====================================================

\c devsu_clients_bd;

-- Insertar clientes de prueba según casos de uso del documento
INSERT INTO client (name, gender, age, id_number, address, phone, client_id, password, state) VALUES
('José Lema', 'Masculino', 35, '1234567890', 'Otavalo sn y principal', '098254785', 'CLI001', '1234', 'true'),
('Marianela Montalvo', 'Femenino', 28, '0987654321', 'Amazonas y NNUU', '097548965', 'CLI002', '5678', 'true'),
('Juan Osorio', 'Masculino', 42, '1122334455', '13 de junio y equinoccial', '098874587', 'CLI003', '1245', 'true');

-- =====================================================
-- DATOS DE PRUEBA - MICROSERVICIO TransactionApp
-- =====================================================

\c devsu_transactions_bd;

-- Insertar cuentas de prueba según casos de uso del documento
INSERT INTO account (account_number, account_type, balance, state, account_key, client_id) VALUES
('478758', 'Ahorros', 2000.00, 'true', 'ACC001', 'CLI001'),
('225487', 'Corriente', 100.00, 'true', 'ACC002', 'CLI002'),
('495878', 'Ahorros', 0.00, 'true', 'ACC003', 'CLI003'),
('496825', 'Ahorros', 540.00, 'true', 'ACC004', 'CLI002'),
('585545', 'Corriente', 1000.00, 'true', 'ACC005', 'CLI001');

-- Insertar movimientos de prueba
INSERT INTO movement (account_id, movement_number, movement_date, movement_type, amount, balance, state) VALUES
(1, 1, '2022-10-02 10:00:00', 'Retiro', -575.00, 1425.00, 'true'),
(2, 1, '2022-10-02 11:00:00', 'Depósito', 600.00, 700.00, 'true'),
(3, 1, '2022-02-08 09:00:00', 'Depósito', 150.00, 150.00, 'true'),
(4, 1, '2022-02-08 14:00:00', 'Retiro', -540.00, 0.00, 'true');

-- =====================================================
-- VERIFICACIÓN DE DATOS
-- =====================================================

\c devsu_clients_bd;
SELECT 'ClientApp - Total de clientes:' as info, COUNT(*) as total FROM client;

\c devsu_transactions_bd;
SELECT 'TransactionApp - Total de cuentas:' as info, COUNT(*) as total FROM account;
SELECT 'TransactionApp - Total de movimientos:' as info, COUNT(*) as total FROM movement;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
