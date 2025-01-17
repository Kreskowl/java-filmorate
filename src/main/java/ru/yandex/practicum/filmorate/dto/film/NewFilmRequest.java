package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.AfterLowerBoundDate;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreRequest;
import ru.yandex.practicum.filmorate.dto.film.rating.MpaRequest;

import java.time.LocalDate;
import java.util.Set;

@Data
public class NewFilmRequest {
    @NotBlank(message = "Name must not be blank")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @Past(message = "Release date must be in the past")
    @AfterLowerBoundDate
    private LocalDate releaseDate;

    @Positive(message = "Duration must be a positive number")
    private long duration;

    private MpaRequest mpa;
    private Set<GenreRequest> genres;
}