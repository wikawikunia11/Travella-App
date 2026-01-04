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
    first_user_id INTEGER NOT NULL,
    second_user_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    -- type_id INTEGER NOT NULL,

    PRIMARY KEY (first_user_id, second_user_id),
    FOREIGN KEY (first_user_id_id) REFERENCES users_table(id_user),
    FOREIGN KEY (second_user_id) REFERENCES users_table(id_user),

    CONSTRAINT check_users_order CHECK (first_user_id < second_user_id)
);

-- CREATE TABLE frindships_types (
--     id_type SERIAL PRIMARY KEY
--     name VARCHAR(25)
-- )

CREATE TABLE posts (
    id_post SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    post_date TIMESTAMP DEFAULT NOW(),
    caption VARCHAR(50),
    visit_date DATE,
    longitude DECIMAL(9, 6),
    latitude DECIMAL(9, 6),
    description_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users_table(id_user)
);

CREATE TABLE description (
    id_description SERIAL PRIMARY KEY,
    -- ........
)


CREATE TABLE medias (
    id_media SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL,
    media_file VARCHAR(250) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts (id_post)
);
