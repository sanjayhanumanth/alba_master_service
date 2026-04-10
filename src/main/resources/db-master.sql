-- ============================================================
-- alba Business Schema — Master Data Tables
-- Run after auth schema (db-init.sql from auth service)
-- ============================================================

IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = 'business')
    EXEC('CREATE SCHEMA business');
GO

-- customers
CREATE TABLE business.customers (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    customer_code    VARCHAR(50)   NOT NULL UNIQUE,
    company_name     VARCHAR(255)  NOT NULL,
    contact_person   VARCHAR(100),
    email            VARCHAR(255),
    phone            VARCHAR(20),
    address          NVARCHAR(500),
    is_active        BIT DEFAULT 1,
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       DATETIME2 DEFAULT GETDATE(),
    updated_at       DATETIME2 DEFAULT GETDATE()
);
GO

-- transport_partners
CREATE TABLE business.transport_partners (
    id                   BIGINT IDENTITY(1,1) PRIMARY KEY,
    partner_code         VARCHAR(50)  NOT NULL UNIQUE,
    partner_name         VARCHAR(255) NOT NULL,
    contact_person       VARCHAR(100),
    email                VARCHAR(255),
    phone                VARCHAR(20),
    has_portal           BIT DEFAULT 0,
    portal_url           VARCHAR(500),
    has_chep_account     BIT DEFAULT 0,
    chep_account_number  VARCHAR(100),
    is_active            BIT DEFAULT 1,
    created_by           BIGINT,
    updated_by           BIGINT,
    created_at           DATETIME2 DEFAULT GETDATE(),
    updated_at           DATETIME2 DEFAULT GETDATE()
);
GO

-- products
CREATE TABLE business.products (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_code  VARCHAR(50)   NOT NULL UNIQUE,
    product_name  VARCHAR(255)  NOT NULL,
    description   NVARCHAR(500),
    uom           VARCHAR(20),
    pack_size     DECIMAL(10,3),
    is_active     BIT DEFAULT 1,
    created_by    BIGINT,
    updated_by    BIGINT,
    created_at    DATETIME2 DEFAULT GETDATE(),
    updated_at    DATETIME2 DEFAULT GETDATE()
);
GO

-- warehouses
CREATE TABLE business.warehouses (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    warehouse_code   VARCHAR(50)  NOT NULL UNIQUE,
    warehouse_name   VARCHAR(255) NOT NULL,
    location         NVARCHAR(255),
    is_active        BIT DEFAULT 1,
    created_by       BIGINT,
    created_at       DATETIME2 DEFAULT GETDATE()
);
GO

-- vehicles
CREATE TABLE business.vehicles (
    id                    BIGINT IDENTITY(1,1) PRIMARY KEY,
    transport_partner_id  BIGINT NOT NULL REFERENCES business.transport_partners(id),
    vehicle_number        VARCHAR(50)  NOT NULL,
    vehicle_type          VARCHAR(50),
    capacity_kg           DECIMAL(10,2),
    is_active             BIT DEFAULT 1,
    created_by            BIGINT,
    updated_by            BIGINT,
    created_at            DATETIME2 DEFAULT GETDATE(),
    updated_at            DATETIME2 DEFAULT GETDATE()
);
GO

-- departments
CREATE TABLE business.departments (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    department_code   VARCHAR(50)  NOT NULL UNIQUE,
    department_name   VARCHAR(100) NOT NULL,
    description       VARCHAR(255),
    is_active         BIT DEFAULT 1,
    created_by        BIGINT,
    created_at        DATETIME2 DEFAULT GETDATE()
);
GO

-- ── Seed data ────────────────────────────────────────────────
INSERT INTO business.departments (department_code, department_name) VALUES
    ('WAREHOUSE',   'Warehouse'),
    ('ACCOUNTS',    'Accounts'),
    ('LOGISTICS',   'Logistics'),
    ('SUPERVISORS', 'Supervisors');
GO

INSERT INTO business.warehouses (warehouse_code, warehouse_name, location) VALUES
    ('WH001', 'Main Warehouse', 'Alba Edible Oils - Plant 1');
GO

INSERT INTO business.products (product_code, product_name, uom, pack_size) VALUES
    ('OIL-PALM-5L',   'Palm Oil 5L',   'CARTON', 5.000),
    ('OIL-SUNF-5L',   'Sunflower Oil 5L', 'CARTON', 5.000),
    ('OIL-CANOLA-5L', 'Canola Oil 5L', 'CARTON', 5.000);
GO

INSERT INTO business.customers (customer_code, company_name, email) VALUES
    ('CUST001', 'Demo Customer Pvt Ltd', 'demo@customer.com'),
    ('CUST002', 'Test Retail Ltd',       'procurement@testretail.com');
GO

INSERT INTO business.transport_partners
    (partner_code, partner_name, email, has_portal, has_chep_account) VALUES
    ('TP001', 'FastFreight Logistics',  'dispatch@fastfreight.com',  1, 1),
    ('TP002', 'QuickHaul Transport',    'bookings@quickhaul.com',    0, 0),
    ('TP003', 'AlbaShip Carriers',      'ops@albaship.com',          1, 0);
GO
