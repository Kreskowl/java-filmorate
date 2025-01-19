package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("title"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getTimestamp("release_date").toLocalDateTime().toLocalDate());
        film.setDuration(resultSet.getLong("duration"));

        film.setRating(mapRating(resultSet));

        film.setGenres(mapGenres(resultSet));

        // Лайки
        film.setLikes(mapLikes(resultSet));

        return film;
    }

    private Rating mapRating(ResultSet resultSet) throws SQLException {
        if (resultSet.getString("rating_name") == null) {
            return null;
        }
        return new Rating(resultSet.getLong("rating_id"), resultSet.getString("rating_name"));
    }

    private Set<Genre> mapGenres(ResultSet resultSet) throws SQLException {
        String genresConcat = resultSet.getString("genres");
        if (genresConcat == null || genresConcat.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(genresConcat.split(","))
                .map(genre -> {
                    String[] parts = genre.split(":");
                    long id = Long.parseLong(parts[0]);
                    String name = parts[1];
                    return new Genre(id, name);
                })
                .collect(Collectors.toSet());
    }

    private Set<Long> mapLikes(ResultSet resultSet) throws SQLException {
        String likesConcat = resultSet.getString("likes");
        if (likesConcat == null || likesConcat.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(likesConcat.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toCollection(HashSet::new));
    }
}