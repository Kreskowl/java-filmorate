package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotations.AfterLowerBoundDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Long id;
    @JsonIgnore
    private Set<Long> likes = new HashSet<>();

    @NotBlank(message = "Name must not be blank")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @Past(message = "Release date must be in the past")
    @AfterLowerBoundDate
    private LocalDate releaseDate;

    @Positive(message = "Duration must be a positive number")
    private long duration;

    public int getLikesAmount() {
        return likes.size();
    }
}
