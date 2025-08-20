CREATE SCHEMA IF NOT EXISTS card_schema;
CREATE SCHEMA IF NOT EXISTS customer_schema;
CREATE TABLE IF NOT EXISTS customer_schema.outbox (
    id UUID PRIMARY KEY
);