CREATE TABLE client
(
    id                SERIAL PRIMARY KEY,
    first_name        VARCHAR(50),
    last_name         VARCHAR(50),
    birth_date        DATE,
    registration_date TIMESTAMP,
    email             VARCHAR(255),
    is_active         BOOLEAN DEFAULT TRUE
);


CREATE TABLE account
(
    id                  SERIAL PRIMARY KEY,
    account_name        VARCHAR(30),
    currency            VARCHAR(255), -- enum будет храниться как строка
    balance             NUMERIC DEFAULT 0,
    account_kind        VARCHAR(255), -- enum будет храниться как строка
    client_id           INTEGER REFERENCES client (id) ON DELETE CASCADE,
    is_active           BOOLEAN DEFAULT TRUE,
    last_capitalization TIMESTAMP
);


CREATE TABLE operations
(
    id              SERIAL PRIMARY KEY,
    account_id      INTEGER REFERENCES account (id) ON DELETE CASCADE,
    operation_kind  VARCHAR(50), -- enum как строка (DEPOSIT, WITHDRAWAL)
    transaction_sum NUMERIC,
    operation_date  TIMESTAMP,
    currency_from   VARCHAR(50)  -- enum как строка (например: USD, EUR)
);

CREATE TABLE currency_rates
(
    char_code VARCHAR(10) PRIMARY KEY, -- Например, "USD", "EUR"
    currency  VARCHAR(50),             -- enum как строка
    value     NUMERIC                  -- курс к RUB
);


CREATE TABLE last_currency_rates_update
(
    id    SERIAL PRIMARY KEY,
    value TIMESTAMP
);

CREATE SEQUENCE account_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE client_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE operation_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE last_currency_rates_update_seq
    START WITH 1
    INCREMENT BY 50;
