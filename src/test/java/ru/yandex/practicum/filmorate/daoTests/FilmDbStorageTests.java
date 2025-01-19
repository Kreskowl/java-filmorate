package ru.yandex.practicum.filmorate.daoTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreRepository.class, GenreRowMapper.class})
@Sql(scripts = {"/test_schema.sql", "/test_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    private final FilmDbStorage filmStorage;

    @Test
    public void testCreateFilm() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("Test Film");
        request.setDescription("Test Description");
        request.setDuration(120);
        request.setReleaseDate(LocalDate.of(2025, 1, 1));

        Film film = FilmMapper.mapToFilm(request);
        filmStorage.create(film);

        assertThat(film).isNotNull();
        assertThat(film.getGenres()).isEmpty();
    }

    @Test
    public void testCreateFilmWithoutGenresAndRating() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("Film without Genres and Rating");
        request.setDescription("Test film with no genres and no rating.");
        request.setDuration(100);
        request.setReleaseDate(LocalDate.of(2023, 1, 1));

        Film film = FilmMapper.mapToFilm(request);
        filmStorage.create(film);

        assertThat(film).isNotNull();
        assertThat(film.getGenres()).isEmpty();
        assertThat(film.getRating()).isNull();
    }

    @Test
    public void testUpdateFilm() {
        Film existingFilm = filmStorage.findById(1L).orElseThrow();
        existingFilm.setName("Updated Movie");
        existingFilm.setDescription("Updated description");

        existingFilm.setGenres(Set.of(new Genre(3L, "Мультфильм")));
        existingFilm.setRating(new Rating(2L, "PG"));

        Film updatedFilm = filmStorage.update(existingFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo("Updated Movie");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated description");

        assertThat(updatedFilm.getGenres()).containsExactlyInAnyOrder(new Genre(3L, "Мультфильм"));

        assertThat(updatedFilm.getRating()).isNotNull();
        assertThat(updatedFilm.getRating().getId()).isEqualTo(2L);
        assertThat(updatedFilm.getRating().getName()).isEqualTo("PG");
    }

    @Test
    public void testDeleteFilm() {
        filmStorage.deleteById(1L);

        Optional<Film> deletedFilm = filmStorage.findById(1L);
        assertThat(deletedFilm).isNotPresent();
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.findById(1L);

        assertThat(filmOptional).isPresent();
        Film film = filmOptional.get();

        assertThat(film.getName()).isEqualTo("Inception");

        assertThat(film.getGenres()).containsExactlyInAnyOrder(
                new Genre(1L, "Комедия"),
                new Genre(2L, "Драма")
        );

        assertThat(film.getRating()).isNotNull();
        assertThat(film.getRating().getId()).isEqualTo(1L);
        assertThat(film.getRating().getName()).isEqualTo("G");
    }

    @Test
    public void testGetAllFilms() {
        List<Film> films = filmStorage.getAll();

        assertThat(films).isNotEmpty();
        assertThat(films).hasSize(2); // Зависит от ваших тестовых данных
    }

    @Test
    public void testAddLike() {
        filmStorage.addLike(1L, 2L);

        Film film = filmStorage.findById(1L).orElseThrow();
        assertThat(film.getLikes()).contains(2L);
    }

    @Test
    public void testRemoveLike() {
        filmStorage.addLike(1L, 2L);
        filmStorage.removeLike(1L, 2L);

        Film film = filmStorage.findById(1L).orElseThrow();
        assertThat(film.getLikes()).doesNotContain(2L);
    }

    @Test
    public void testGetBestByLikes() {
        filmStorage.addLike(1L, 2L);
        filmStorage.addLike(1L, 3L);
        filmStorage.addLike(2L, 2L);

        List<Film> topFilms = filmStorage.getBestByLikes(1);

        assertThat(topFilms).hasSize(1);
        assertThat(topFilms.get(0).getId()).isEqualTo(1L);
    }

    @Test
    public void testUpdateFilmGenres() {
        Film film = filmStorage.findById(1L).orElseThrow();

        film.setGenres(Set.of(new Genre(3L, "Action"), new Genre(4L, "Horror")));
        filmStorage.update(film);

        Film updatedFilm = filmStorage.findById(1L).orElseThrow();
        assertThat(updatedFilm.getGenres()).containsExactlyInAnyOrder(
                new Genre(3L, "Мультфильм"),
                new Genre(4L, "Триллер")
        );

        film.setGenres(Collections.emptySet());
        filmStorage.update(film);

        Film filmWithoutGenres = filmStorage.findById(1L).orElseThrow();
        assertThat(filmWithoutGenres.getGenres()).isEmpty();
    }


}
