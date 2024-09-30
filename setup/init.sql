-- Create role 'postgres' if it doesn't exist
DO
$$
BEGIN
    IF NOT EXISTS (
        SELECT FROM pg_catalog.pg_roles WHERE rolname = 'postgres'
    ) THEN
        CREATE ROLE postgres WITH LOGIN PASSWORD 'postgres';
    END IF;
END
$$;

-- Create database 'db' if it doesn't exist
DO
$$
BEGIN
    IF NOT EXISTS (
        SELECT FROM pg_database WHERE datname = 'db'
    ) THEN
        CREATE DATABASE db;
    END IF;
END
$$;

-- Grant all privileges to the 'postgres' role on the 'db' database
GRANT ALL PRIVILEGES ON DATABASE db TO postgres;

-- Create schema
CREATE SCHEMA IF NOT EXISTS eii_test;

CREATE TABLE IF NOT EXISTS eii_test.data_files (
    id serial4 NOT NULL,
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on timestamp NULL,
    file_type varchar(20) NOT NULL,
    validation_status varchar(20) NOT NULL,
    CONSTRAINT data_files_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS eii_test.data_collections (
    id serial4 NOT NULL,
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on timestamp NULL,
    file_id_orders int8 NOT NULL,
    file_id_assets int8 NOT NULL,
    file_id_inventory int8 NOT NULL,
    status varchar(20) NOT NULL,
    tag varchar(50),
    note varchar(1000),
    CONSTRAINT data_collections_pkey PRIMARY KEY (id),
    CONSTRAINT collections_orders_to_data_files
        FOREIGN KEY(file_id_orders)
        REFERENCES eii_test.data_files(id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT collections_assets_to_data_files
        FOREIGN KEY(file_id_assets)
        REFERENCES eii_test.data_files(id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT collections_inventory_to_data_files
        FOREIGN KEY(file_id_inventory)
        REFERENCES eii_test.data_files(id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);

-- Insert data into data_files
INSERT INTO eii_test.data_files(id, created_on, updated_on, file_type, validation_status) VALUES
    (1, '2023-01-01 00:00:00.000', '2023-01-01 00:05:00.000', 'orders', 'valid'),
    (2, '2023-01-02 00:00:00.000', '2023-01-02 00:05:00.000', 'orders', 'valid'),
    (3, '2023-01-03 00:00:00.000', '2023-01-03 00:05:00.000', 'orders', 'valid'),
    (4, '2023-01-04 00:00:00.000', '2023-01-04 00:05:00.000', 'orders', 'invalid'),
    (5, '2023-01-05 00:00:00.000', null, 'orders', 'not_run'),
    (6, '2023-01-01 01:00:00.000', '2023-01-01 01:05:00.000', 'assets', 'invalid'),
    (7, '2023-01-02 01:00:00.000', '2023-01-02 01:05:00.000', 'assets', 'invalid'),
    (8, '2023-01-03 01:00:00.000', '2023-01-03 01:05:00.000', 'assets', 'valid'),
    (9, '2023-01-04 01:00:00.000', '2023-01-04 01:05:00.000', 'assets', 'valid'),
    (10, '2023-01-01 00:00:00.000', '2023-01-01 00:05:00.000', 'inventory', 'valid');