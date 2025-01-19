package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class GenreRepository extends BaseRepository<Genre> implements GenreStorage {

    private static final String GET_ALL_GENRES = """
            SELECT * FROM genres ORDER BY id ASC
            """;
    private static final String FIND_GENRE_BY_ID = """
            SELECT * FROM genres WHERE id = ?
            """;

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> getAll() {
        return jdbc.query(GET_ALL_GENRES, new GenreRowMapper());
    }


    @Override
    public Optional<Genre> findById(long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_GENRE_BY_ID, new GenreRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateGenresForFilm(long filmId, Set<Genre> newGenres) {
        List<Long> currentGenreIds = jdbc.queryForList("SELECT genre_id FROM film_genres WHERE film_id = ?", Long.class, filmId);

        newGenres = newGenres != null ? newGenres : Collections.emptySet();

        for (Long currentGenreId : currentGenreIds) {
            if (newGenres.stream().noneMatch(g -> g.getId().equals(currentGenreId))) {
                jdbc.update("DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?", filmId, currentGenreId);
            }
        }

        for (Genre newGenre : newGenres) {
            if (!genreExists(newGenre.getId())) {
                throw new ValidationException("Genre with id " + newGenre.getId() + " does not exist.");
            }
            if (!currentGenreIds.contains(newGenre.getId())) {
                jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", filmId, newGenre.getId());
            }
        }
    }

    private boolean genreExists(Long genreId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM genres WHERE id = ?",
                Integer.class,
                genreId
        );
        return count != null && count > 0;
    }

}

