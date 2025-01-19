DELETE
FROM friendships;
DELETE
FROM film_genres;
DELETE
FROM films;
DELETE
FROM users;
DELETE
FROM ratings;
DELETE
FROM genres;
ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films
    ALTER COLUMN id RESTART WITH 1;


INSERT INTO ratings (id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO users (name, email, login, birthday)
VALUES ('John Doe', 'john.doe@example.com', 'johndoe', '1985-05-10'),
       ('Jane Smith', 'jane.smith@example.com', 'janesmith', '1990-08-15'),
       ('Alice Johnson', 'alice.johnson@example.com', 'alicej', '1995-02-20');

INSERT INTO films (title, description, duration, release_date, rating_id)
VALUES ('Inception', 'A mind-bending thriller', 148, '2010-07-16', 1),
       ('The Matrix', 'A hacker discovers a shocking truth', 136, '1999-03-31', 2);

INSERT INTO genres (id, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 3);

INSERT INTO friendships (requester_id, receiver_id, status)
VALUES (1, 2, 'CONFIRMED'),
       (1, 3, 'UNCONFIRMED');