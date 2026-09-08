-- ==============================================================================
-- DDL Schema Initialization for Franchise Service (PostgreSQL / R2DBC)
-- Automatically executed by ConnectionFactoryInitializer on service startup
-- ==============================================================================

-- 1. Franchises Table
CREATE TABLE IF NOT EXISTS franchises (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- 2. Branches Table
CREATE TABLE IF NOT EXISTS branches (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    franchise_id BIGINT NOT NULL,
    CONSTRAINT fk_branches_franchise FOREIGN KEY (franchise_id) REFERENCES franchises(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_branches_franchise_id ON branches(franchise_id);

-- 3. Products Table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    branch_id BIGINT NOT NULL,
    CONSTRAINT fk_products_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_products_branch_id ON products(branch_id);
