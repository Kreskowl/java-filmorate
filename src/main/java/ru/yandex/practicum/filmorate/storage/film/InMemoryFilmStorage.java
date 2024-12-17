package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 0;

    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }

    @Override
    public Film findById(long id) {
        logger.info("Find film with id {}", id);
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("Film with id " + id + " not found"));
    }

    @Override
    public Film create(Film newFilm) {
        logger.debug("Received new film: {}", newFilm);
        newFilm.setId(getNextId());
        logger.debug("Generated ID: {}", newFilm.getId());
        films.put(newFilm.getId(), newFilm);
        logger.debug("Film stored in storage: {}", films.get(newFilm.getId()));
        return newFilm;
    }

    @Override
    public Film update(Film updatedFilm) {
        Film existingFilm = Optional.ofNullable(films.get(updatedFilm.getId()))
                .orElseThrow(() -> new NotFoundException("Film with id " + updatedFilm.getId() + " not found"));

        existingFilm.setName(updatedFilm.getName());
        existingFilm.setDescription(updatedFilm.getDescription());
        existingFilm.setReleaseDate(updatedFilm.getReleaseDate());
        existingFilm.setDuration(updatedFilm.getDuration());

        logger.info("Updated user with id {}: {}", existingFilm.getId(), existingFilm);
        return existingFilm;
    }

    @Override
    public void deleteById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Film with id " + id + " not found");
        }
        logger.info("Film with id {} deleted", id);
        films.remove(id);
    }

    private long getNextId() {
        return ++currentId;
    }
}
