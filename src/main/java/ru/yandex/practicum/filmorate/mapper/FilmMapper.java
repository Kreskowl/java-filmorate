package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreRequest;
import ru.yandex.practicum.filmorate.dto.film.rating.MpaRequest;
import ru.yandex.practicum.filmorate.dto.film.rating.RatingDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    private static final Logger logger = LoggerFactory.getLogger(FilmMapper.class);

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setDuration(request.getDuration());
        film.setReleaseDate(request.getReleaseDate());
        film.setRating(mapRating(request.getMpa()));
        film.setGenres(mapGenres(request.getGenres()));

        logger.info("Mapped Film: {}", film);
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        logger.info("Mapping Film to FilmDto: {}", film);

        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getGenres().stream()
                        .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                        .collect(Collectors.toSet()),
                film.getRating() != null
                        ? new RatingDto(film.getRating().getId(), film.getRating().getName())
                        : null
        );
    }

    public static Film updateFilmFromRequest(Film film, UpdateFilmRequest request) {
        Optional.ofNullable(request.getName()).ifPresent(film::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(film::setDescription);
        Optional.ofNullable(request.getDuration()).ifPresent(film::setDuration);
        Optional.ofNullable(request.getReleaseDate()).ifPresent(film::setReleaseDate);

        updateRating(film, request.getMpa());
        updateGenres(film, request.getGenres());

        logger.info("Updated Film: {}", film);
        return film;
    }

    private static Rating mapRating(MpaRequest mpa) {
        return (mpa != null && mpa.getId() != null) ? new Rating(mpa.getId(), null) : null;
    }

    private static Set<Genre> mapGenres(Set<GenreRequest> genreRequests) {
        return (genreRequests != null && !genreRequests.isEmpty())
                ? genreRequests.stream()
                .map(genreRequest -> new Genre(genreRequest.getId(), null))
                .collect(Collectors.toSet())
                : Collections.emptySet();
    }

    private static void updateRating(Film film, MpaRequest mpa) {
        if (mpa != null && mpa.getId() != null) {
            Long newRatingId = mpa.getId();
            if (film.getRating() == null || !newRatingId.equals(film.getRating().getId())) {
                film.setRating(new Rating(newRatingId, null));
            }
        } else {
            film.setRating(null);
        }
    }

    private static void updateGenres(Film film, Set<GenreRequest> genreRequests) {
        if (genreRequests != null && !genreRequests.isEmpty()) {
            Set<Genre> genres = genreRequests.stream()
                    .map(genreRequest -> new Genre(genreRequest.getId(), null))
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        } else {
            film.setGenres(Collections.emptySet());
        }
    }
}