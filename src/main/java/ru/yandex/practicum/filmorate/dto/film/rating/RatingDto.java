package ru.yandex.practicum.filmorate.dto.film.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private long id;
    private String name;
}
