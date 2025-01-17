package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dao.RatingRepository;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreRequest;
import ru.yandex.practicum.filmorate.dto.film.rating.MpaRequest;
import ru.yandex.practicum.filmorate.dto.film.rating.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final RatingRepository ratingRepository;
    private final GenreRepository genreRepository;

    public List<FilmDto> getAllFilms() {
        return filmDbStorage.getAll().stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public FilmDto getFilmById(long id) {
        return FilmMapper.mapToFilmDto(getFilm(id));
    }

    public List<GenreDto> getAllGenres() {
        return genreRepository.getAll()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found"));
        return GenreMapper.mapToGenreDto(genre);
    }

    public List<RatingDto> getAllRatings() {
        return ratingRepository.getAll()
                .stream()
                .map(RatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    public RatingDto getRatingById(long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rating with id " + id + " not found"));
        return RatingMapper.mapToRatingDto(rating);
    }

    public FilmDto createFilm(NewFilmRequest request) {
        validateGenres(request.getGenres());
        validateRating(request.getMpa());

        Film film = FilmMapper.mapToFilm(request);
        Film createdFilm = filmDbStorage.create(film);

        FilmDto createdFilmDto = FilmMapper.mapToFilmDto(createdFilm);
        logger.info("Film created successfully: {}", createdFilmDto);

        return createdFilmDto;
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        validateGenres(request.getGenres());
        validateRating(request.getMpa());

        Film updatedFilm = getFilm(request.getId());
        FilmMapper.updateFilmFromRequest(updatedFilm, request);
        filmDbStorage.update(updatedFilm);

        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public void deleteFilm(long id) {
        filmDbStorage.deleteById(id);
    }

    public FilmDto addLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        validateUserExists(userId);

        if (!film.getLikes().add(userId)) {
            throw new ValidationException("User with id " + userId + " already liked the film with id " + filmId);
        }

        filmDbStorage.addLike(filmId, userId);
        logger.info("User with id {} liked the film with id {}", userId, filmId);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto removeLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        validateUserExists(userId);

        if (!film.getLikes().remove(userId)) {
            throw new ValidationException("User with id " + userId +
                    " did not like the film with id " + filmId + ", so it cannot be removed.");
        }

        filmDbStorage.removeLike(filmId, userId);
        logger.info("User with id {} removed like from the film with id {}", userId, filmId);
        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> getBestByLikes(int count) {
        if (count <= 0) {
            throw new ValidationException("Number of films should be greater than 0");
        }
        List<FilmDto> films = filmDbStorage.getBestByLikes(count)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
        if (films.isEmpty()) {
            logger.warn("No films found for the top {} by likes", count);
        }
        return films;
    }

    private Film getFilm(long id) {
        return filmDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Film with id " + id + " not found"));
    }

    private void validateGenres(Set<GenreRequest> genres) {
        if (genres != null && !genres.isEmpty()) {
            for (GenreRequest genre : genres) {
                if (genreRepository.findById(genre.getId()).isEmpty()) {
                    throw new ValidationException("Genre with id " + genre.getId() + " does not exist.");
                }
            }
        }
    }

    private void validateRating(MpaRequest mpa) {
        if (mpa != null && mpa.getId() != null) {
            if (ratingRepository.findById(mpa.getId()).isEmpty()) {
                throw new ValidationException("Rating with id " + mpa.getId() + " does not exist.");
            }
        }
    }

    private void validateUserExists(long userId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

}
