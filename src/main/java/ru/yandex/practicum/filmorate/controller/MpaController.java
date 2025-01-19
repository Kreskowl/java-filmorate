package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.rating.RatingDto;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final RatingService ratingService;

    @GetMapping
    public List<RatingDto> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/{id}")
    public RatingDto findRatingById(@PathVariable @Positive long id) {
        return ratingService.getRatingById(id);
    }

}
