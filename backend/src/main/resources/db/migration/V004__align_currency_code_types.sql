-- V002 declared CHAR(3); align existing PostgreSQL columns that were created as VARCHAR(3).
ALTER TABLE organizations ALTER COLUMN currency_code DROP DEFAULT;
ALTER TABLE organizations ALTER COLUMN currency_code TYPE CHAR(3)
    USING currency_code::CHAR(3);
ALTER TABLE organizations ALTER COLUMN currency_code SET DEFAULT 'KES';

ALTER TABLE branches ALTER COLUMN currency_code DROP DEFAULT;
ALTER TABLE branches ALTER COLUMN currency_code TYPE CHAR(3)
    USING currency_code::CHAR(3);
ALTER TABLE branches ALTER COLUMN currency_code SET DEFAULT 'KES';
