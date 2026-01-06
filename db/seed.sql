-- INSERT INTO friendships_types (name) VALUES ('pending_first_second')
-- INSERT INTO friendships_types (name) VALUES ('pending_second_first')
-- INSERT INTO friendships_types (name) VALUES ('friends')
-- INSERT INTO friendships_types (name) VALUES ('blocked')

-- encrypted password: pass_1
INSERT INTO users_table (username, password, name, surname, biography, profile_pic) VALUES
    ('testusr', '$2a$10$8hHWtuwXFF8eJKXKxuEY3et7Nu853HcJAAl2iaU6zA51gKmEEKSP.', 'Test', 'User', 'This is a test user from seed.sql!', NULL);
-- encrypted password: zugajka
INSERT INTO users_table (username, password, name, surname, biography, profile_pic) VALUES
    ('julciazugaj', '$2a$10$0HtZKUPXvpsL0zxefIL5yeHcGN14sKxiboo75vOChRnm1xs1uNugq', 'Julia', 'Żugaj', 'hejka', 'https://yt3.googleusercontent.com/R1VErEi424hIGuFvOFGn5TAUF3Oau8ZlelqW9a4AJZSP1QDx044GoFcLZK69pFJhMu92ADNAZA=s160-c-k-c0x00ffffff-no-rj');
-- encrypted password: papa
INSERT INTO users_table (username, password, name, surname, biography, profile_pic) VALUES
    ('pedropascal', '$2a$10$ksjaUHRGNpJV5ilQWLNc1unJ7VE.p3GTCGwq8nTq8Ws5bWfiSuViC', 'Pedro', 'Pascal', NULL, 'https://pliki.well.pl/i/06/33/88/063388_r0_940.jpg');
-- encrypted password: koperkowy
INSERT INTO users_table (username, password, name, surname, biography, profile_pic) VALUES
    ('rmaklowicz', '$2a$10$nkgBUoCMotKfW8CwALJwN.COwKyu1dbdrxfwXKGivLzFuubwc.aDm', 'Robert', 'Makłowicz', 'Podróże, historia i jedzenie. Dalmacja to mój drugi dom. Hej!', 'https://static.halowies.pl/images/2024/06/27/o_541976_1280.webp');
-- encrypted password: konin
INSERT INTO users_table (username, password, name, surname, biography, profile_pic) VALUES
    ('fagata', '$2a$10$aMaUYHStBPMm8z9NVPyyaOrwIRbPn4hydUjCJNt7mB7u4FoB.Llq6', 'Agata', 'Fąk', 'raz dwa trzy', 'https://i.scdn.co/image/ab67616100005174cf261b043f7098c8c67e0fe0');


INSERT INTO friendships (first_user_id, second_user_id) VALUES (
    (SELECT id_user FROM users_table WHERE username = 'julciazugaj'),
    (SELECT id_user FROM users_table WHERE username = 'pedropascal'));

INSERT INTO friendships (first_user_id, second_user_id) VALUES (
    (SELECT id_user FROM users_table WHERE username = 'testusr'),
    (SELECT id_user FROM users_table WHERE username = 'pedropascal'));

-- Post Roberta Makłowicza (Chorwacja)
INSERT INTO posts (user_id, caption, visit_date, longitude, latitude, description) VALUES
    (
        (SELECT id_user FROM users_table WHERE username = 'rmaklowicz'),
        'Papryka czy pomidor?',
        '2023-07-15',
        16.440193, -- Split, Chorwacja
        43.508133,
        'Dzień dobry Państwu, dzisiaj kosztujemy specjałów w Splicie.'
);

-- Post Julci Żugaj (powrót do istniejącej użytkowniczki)
INSERT INTO posts (user_id, caption, visit_date, longitude, latitude, description) VALUES
    (
        (SELECT id_user FROM users_table WHERE username = 'julciazugaj'),
        'Camp 2024 🧡',
        '2024-02-14',
        19.944981, -- Kraków/okolice
        50.064651,
        'Obóz był niesamowity, tęsknię za Wami!'
    );