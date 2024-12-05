package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 0;

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        logger.info("Created new film: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updateFilm) {
        Film existingFilm = Optional.ofNullable(films.get(updateFilm.getId()))
                .orElseThrow(() -> new NotFoundException("Film with id " + updateFilm.getId() + " not found"));

        existingFilm.setName(updateFilm.getName());
        existingFilm.setDescription(updateFilm.getDescription());
        existingFilm.setReleaseDate(updateFilm.getReleaseDate());
        existingFilm.setDuration(updateFilm.getDuration());

        films.put(updateFilm.getId(), existingFilm);
        logger.info("Updated user with id {}: {}", existingFilm.getId(), existingFilm);
        return existingFilm;
    }

    private long getNextId() {
        return ++currentId;
    }
}
