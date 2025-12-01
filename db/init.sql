DROP TABLE IF EXISTS users_table;

CREATE TABLE users_table (
    id_user SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(80) NOT NULL,
    biography VARCHAR(180),
    --creation_date TIMESTAMP DEFAULT NOW(), -- not yet in backend
    profile_pic VARCHAR(250)  -- DEFAULT 'jakis url'
);