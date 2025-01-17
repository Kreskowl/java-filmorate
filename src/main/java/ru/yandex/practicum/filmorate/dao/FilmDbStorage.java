package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final Logger logger = LoggerFactory.getLogger(FilmDbStorage.class);
    private static final String FIND_ALL_QUERY = """
            SELECT
                f.id AS film_id,
                f.title,
                f.description,
                f.duration,
                f.release_date,
                r.id AS rating_id,
                r.name AS rating_name,
                GROUP_CONCAT(DISTINCT g.id || ':' || g.name) AS genres,
                GROUP_CONCAT(DISTINCT l.user_id) AS likes
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN film_likes l ON f.id = l.film_id
            GROUP BY f.id, f.title, f.description, f.duration, f.release_date, r.id, r.name;
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                f.id AS film_id,
                f.title,
                f.description,
                f.duration,
                f.release_date,
                r.id AS rating_id,
                r.name AS rating_name,
                GROUP_CONCAT(DISTINCT g.id || ':' || g.name) AS genres,
                GROUP_CONCAT(DISTINCT l.user_id) AS likes
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN film_likes l ON f.id = l.film_id
            WHERE f.id = ?
            GROUP BY f.id, f.title, f.description, f.duration, f.release_date, r.id, r.name
            """;
    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films (title, description, release_date,  duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ?
            WHERE id = ?
            """;
    private static final String FIND_MOST_POPULAR_BY_LIKES = """
            SELECT
                f.id AS film_id,
                f.title,
                f.description,
                f.duration,
                f.release_date,
                r.id AS rating_id,
                r.name AS rating_name,
                GROUP_CONCAT(DISTINCT g.id || ':' || g.name) AS genres,
                COUNT(DISTINCT l.user_id) AS likes
            FROM films f
            LEFT JOIN ratings r ON f.rating_id = r.id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN film_likes l ON f.id = l.film_id
            GROUP BY f.id, f.title, f.description, f.duration, f.release_date, r.id, r.name
            ORDER BY likes DESC
            LIMIT ?
            """;

    private static final String ADD_LIKE_QUERY = """
            INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)
            """;

    private static final String REMOVE_LIKE_QUERY = """
            DELETE FROM film_likes WHERE film_id = ? AND user_id = ?
            """;
    private final GenreRepository genreRepository;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreRepository genreRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Film> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film create(Film newFilm) {
        long id = insert(INSERT_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                Optional.ofNullable(newFilm.getRating()).map(Rating::getId).orElse(null)
        );
        newFilm.setId(id);

        saveGenres(newFilm.getGenres(), id);
        logger.info("Created film: {}", newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film updatedFilm) {
        update(UPDATE_FILM_QUERY,
                updatedFilm.getName(),
                updatedFilm.getDescription(),
                Date.valueOf(updatedFilm.getReleaseDate()),
                updatedFilm.getDuration(),
                Optional.ofNullable(updatedFilm.getRating()).map(Rating::getId).orElse(null),
                updatedFilm.getId()
        );

        saveGenres(updatedFilm.getGenres(), updatedFilm.getId());
        logger.info("Updated film with id {}", updatedFilm.getId());
        return updatedFilm;
    }

    @Override
    public void deleteById(long id) {
        if (delete("DELETE FROM films WHERE id = ?", id)) {
            logger.info("Deleted film with id {}", id);
        } else {
            logger.warn("Film with id {} not found for deletion", id);
        }
    }

    @Override
    public Optional<Film> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Film> getBestByLikes(int count) {
        return findMany(FIND_MOST_POPULAR_BY_LIKES, count);
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
        logger.info("Added like for filmId {} by userId {}", filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
        logger.info("Removed like for filmId {} by userId {}", filmId, userId);
    }

    private void saveGenres(Set<Genre> genres, long filmId) {
        genreRepository.updateGenresForFilm(filmId, genres);
        logger.info("Updated genres for filmId {}: {}", filmId, genres);
    }

}


