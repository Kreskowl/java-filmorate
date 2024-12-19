package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilmsByLikes(@RequestParam(defaultValue = "10") int count) {
        return filmService.getBestByLikes(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film newFilm) {
        return filmService.createFilm(newFilm);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) {
        return filmService.updateFilm(updatedFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long id) {
        filmService.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.removeLike(id, userId);
    }
}
