package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilmById(long id) {
        return filmStorage.findById(id);
    }

    public Film createFilm(Film newFilm) {
        return filmStorage.create(newFilm);
    }

    public Film updateFilm(Film updatedFilm) {
        return filmStorage.update(updatedFilm);
    }

    public void deleteFilm(long id) {
        filmStorage.deleteById(id);
    }

    public Film addLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);

        if (film.getLikes().contains(userId)) {
            throw new ValidationException("User with id " + userId + " already liked the film with id " + filmId);
        }
        film.getLikes().add(userId);
        logger.info("Add like to film with id {}, user with id {}", filmId, userId);
        return filmStorage.update(film);
    }

    public Film removeLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);

        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("User with id " + userId +
                    " did not like the film with id " + filmId + " to remove it.");
        }
        film.getLikes().remove(userId);
        logger.info("Remove like from film with id {}, user with id {}", filmId, userId);
        return filmStorage.update(film);
    }

    public List<Film> getBestByLikes(int count) {
        Comparator<Film> comparator = Comparator.comparing(Film::getLikesAmount).reversed();
        if (count <= 0) {
            throw new ValidationException("Number of films should be greater than 0");
        }
        return filmStorage.getAll()
                .stream()
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}
