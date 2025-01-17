package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingRepository extends BaseRepository<Rating> implements RatingStorage {

    private static final String GET_ALL_RATINGS = """
            SELECT * FROM ratings ORDER BY id ASC
            """;
    private static final String FIND_RATING_BY_ID = """
            SELECT * FROM ratings WHERE id = ?
            """;

    public RatingRepository(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Rating> getAll() {
        return jdbc.query(GET_ALL_RATINGS, new RatingRowMapper());
    }

    @Override
    public Optional<Rating> findById(long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_RATING_BY_ID, new RatingRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
