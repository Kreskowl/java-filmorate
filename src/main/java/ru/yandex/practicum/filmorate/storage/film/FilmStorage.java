package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Film create(Film newFilm);

    Film update(Film updatedFilm);

    void deleteById(long id);

    Optional<Film> findById(long id);
}
