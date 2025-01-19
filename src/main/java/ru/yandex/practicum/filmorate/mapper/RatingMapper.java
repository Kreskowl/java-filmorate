package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.rating.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RatingMapper {
    public static RatingDto mapToRatingDto(Rating rating) {
        if (rating == null) {
            return null;
        }
        return new RatingDto(rating.getId(), rating.getName());
    }

    public static Rating mapToRating(RatingDto ratingDto) {
        if (ratingDto == null) {
            return null;
        }
        return new Rating(ratingDto.getId(), ratingDto.getName());
    }
}
