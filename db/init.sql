CREATE TABLE user_table (
    id BIGSERIAL PRIMARY KEY, -- Używamy BIGSERIAL dla auto-inkrementacji w PostgreSQL
    name VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL
);