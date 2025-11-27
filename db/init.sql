CREATE TABLE users_table (
    id_user SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(80) NOT NULL,
    biography VARCHAR(180),
    creation_date TIMESTAMP DEFAULT NOW(),
    profile_pic VARCHAR(250)  -- DEFAULT 'jakis url'
);

CREATE TABLE friendships (
    following_id INTEGER NOT NULL,
    follower_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users_table(id_user),
    FOREIGN KEY (following_id) REFERENCES users_table(id_user)
);

CREATE TABLE posts (
    id_post SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    caption VARCHAR(50),
    description TEXT,
    longitude DECIMAL(9, 6),
    latitude DECIMAL(9, 6),
    post_date TIMESTAMP DEFAULT NOW(),
    id_country INTEGER,
    visit_date DATE,
    FOREIGN KEY (id_country) REFERENCES countries(id_country),
    FOREIGN KEY (user_id) REFERENCES users(id_user)
);

CREATE TABLE medias (
    id_media SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL,
    media_file VARCHAR(250) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts (id_post)
);

CREATE TABLE countries (
    id_country SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE
);

--addresses????

--likes

--comments