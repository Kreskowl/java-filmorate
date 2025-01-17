package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    private static final Logger logger = LoggerFactory.getLogger(BaseRepository.class);
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            logger.info("Successfully found entity with query: {}", query);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Entity not found for query: {}", query);
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        List<T> results = jdbc.query(query, mapper, params);
        logger.info("Retrieved {} entities for query: {}", results.size(), query);
        return results;
    }

    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbc.update(query, id);
        if (rowsDeleted > 0) {
            logger.info("Successfully deleted entity with id {} using query: {}", id, query);
            return true;
        } else {
            logger.warn("No entity found with id {} for deletion query: {}", id, query);
            return false;
        }
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated > 0) {
            logger.info("Successfully updated entity with query: {}", query);
        } else {
            logger.error("Failed to update entity with query: {}", query);
            throw new RepositoryException("Failed to update entity, possibly not found.");
        }
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            logger.error("Failed to generate ID for insert query: {}", query);
            throw new RepositoryException("Failed to insert entity, ID was not generated.");
        }

        long id = key.longValue();
        logger.info("Successfully inserted entity with generated id {} using query: {}", id, query);
        return id;
    }
}

