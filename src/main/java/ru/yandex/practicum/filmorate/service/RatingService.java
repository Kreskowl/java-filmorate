package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RatingRepository;
import ru.yandex.practicum.filmorate.dto.film.rating.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;

    public List<RatingDto> getAllRatings() {
        return ratingRepository.getAll()
                .stream()
                .map(RatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    public RatingDto getRatingById(long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rating with id " + id + " not found"));
        return RatingMapper.mapToRatingDto(rating);
    }
}
