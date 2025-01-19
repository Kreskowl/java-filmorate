package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<GenreDto> getAllGenres() {
        return genreRepository.getAll()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found"));
        return GenreMapper.mapToGenreDto(genre);
    }
}
