package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmTest {
    private static final LocalDate LOWER_RELEASE_DATE_BOUND = LocalDate.of(1895, Month.DECEMBER, 28);
    private Film film;
    private Validator validator;
    private Set<ConstraintViolation<Film>> violations;

    @BeforeEach
    public void setUp() {
        film = new Film();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Should create film successfully")
    @Test
    public void shouldCreateFilmObject() {
        film.setName("Knocking on heaven`s door");
        film.setReleaseDate(LocalDate.of(1997, Month.APRIL, 2));
        film.setDescription("Comedy/Drama");
        film.setDuration(90);

        violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Validation should pass if info is valid");
    }

    @DisplayName("Should throw an error if film`s name is empty")
    @Test
    public void shouldThrowAnErrorIfNameIsEmpty() {
        film.setName("");
        film.setReleaseDate(LocalDate.of(1997, Month.APRIL, 2));
        film.setDescription("Comedy/Drama");
        film.setDuration(90);

        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation should fail if name is empty");
    }

    @DisplayName("Should create film successfully if description size 200 symbols (bound)")
    @Test
    public void shouldCreateFilmObjectIfDescriptionSizeIsValid() {
        String symbols200 = "a".repeat(200);

        film.setName("Knocking on heaven`s door");
        film.setReleaseDate(LocalDate.of(1997, Month.APRIL, 2));
        film.setDescription(symbols200);
        film.setDuration(90);

        violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Validation should pass if description is 200 symbols");
    }

    @DisplayName("Should throw an error if description size > 200 symbols")
    @Test
    public void shouldThrowAnErrorIfDescriptionSizeInvalid() {
        String symbols201 = "a".repeat(201);

        film.setName("Knocking on heaven`s door");
        film.setReleaseDate(LocalDate.of(1997, Month.APRIL, 2));
        film.setDescription(symbols201);
        film.setDuration(90);

        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation should fail if description is > 200 symbols");
    }

    @DisplayName("Should throw an error if release date 27.12.1985 (1 day before bound)")
    @Test
    public void shouldThrowAnErrorIfReleaseDateInvalid() {
        film.setName("Knocking on heaven`s door");
        film.setReleaseDate(LOWER_RELEASE_DATE_BOUND.minusDays(1));
        film.setDescription("Comedy/Drama");
        film.setDuration(90);

        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation should fail if release date is bound date");
    }

    @DisplayName("Should create film successfully if release date 28.12.1985 (bound)")
    @Test
    public void shouldCreateFilmObjectIfReleaseDateValid() {
        film.setName("Knocking on heaven`s door");
        film.setReleaseDate(LOWER_RELEASE_DATE_BOUND);
        film.setDescription("Comedy/Drama");
        film.setDuration(90);

        violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Validation should pass if release date is bound date");
    }

    @DisplayName("Should create film successfully if duration 1 (after bound)")
    @Test
    public void shouldCreateFilmObjectIfDurationIsValid() {
        film.setName("Knocking on heaven`s door");
        film.setReleaseDate(LocalDate.of(1997, Month.APRIL, 2));
        film.setDescription("Comedy/Drama");
        film.setDuration(1);

        violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Validation should pass if duration > 0");
    }

    @DisplayName("Should throw an error if duration 0")
    @Test
    public void shouldThrowAnErrorIfDurationInvalid() {
        film.setName("Knocking on heaven`s door");
        film.setReleaseDate(LocalDate.of(1997, Month.APRIL, 2));
        film.setDescription("Comedy/Drama");
        film.setDuration(0);

        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation should fail if duration 0");
    }
}
