package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final Logger logger = LoggerFactory.getLogger(UserDbStorage.class);

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = """
            INSERT INTO users (name, email, login, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE users
            SET name = ?, email = ?, login = ?, birthday = ?
            WHERE id = ?
            """;
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT receiver_id FROM friendships WHERE requester_id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendships (requester_id, receiver_id, status) VALUES (?, ?, 'UNCONFIRMED')";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friendships WHERE requester_id = ? AND receiver_id = ?";
    private static final String GET_FRIEND_IDS = "SELECT receiver_id FROM friendships WHERE requester_id = ?";
    private static final String UPDATE_FRIENDSHIP_STATUS_QUERY = """
            UPDATE friendships
            SET status = 'CONFIRMED'
            WHERE requester_id = ? AND receiver_id = ?
            """;
    private static final String CHECK_FRIENDSHIP_EXISTS_QUERY = """
            SELECT COUNT(*) FROM friendships
            WHERE requester_id = ? AND receiver_id = ?
            """;

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public List<User> getAll() {
        List<User> users = findMany(FIND_ALL_QUERY);
        loadAllFriends(users);

        return users;
    }

    public Set<Long> getFriendIds(long userId) {
        List<Long> friendIds = jdbc.queryForList(GET_FRIEND_IDS, Long.class, userId);
        return new HashSet<>(friendIds);
    }

    @Override
    public User create(User newUser) {
        if (isLoginExists(newUser.getLogin())) {
            throw new ValidationException("Login '" + newUser.getLogin() + "' is already in use.");
        }
        long id = insert(
                INSERT_QUERY,
                newUser.getName(),
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getBirthday() != null ? Date.valueOf(newUser.getBirthday()) : null
        );

        newUser.setId(id);

        logger.info("User with ID {} was successfully created.", id);
        return newUser;
    }

    @Override
    public User update(User updatedUser) {
        update(
                UPDATE_QUERY,
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                Date.valueOf(updatedUser.getBirthday()),
                updatedUser.getId()
        );
        return updatedUser;
    }

    @Override
    public void deleteById(long id) {
        jdbc.update("DELETE FROM friendships WHERE requester_id = ? OR receiver_id = ?", id, id);

        delete(DELETE_USER_QUERY, id);
    }


    @Override
    public Optional<User> findById(long id) {
        Optional<User> user = findOne(FIND_BY_ID_QUERY, id);
        user.ifPresent(this::loadFriends);
        return user;
    }

    public void sendFriendRequest(long userId, long friendId) {
        jdbc.update(ADD_FRIEND_QUERY, userId, friendId);
        logger.info("User with id {} send friendship request to user with id {}", userId, friendId);
    }

    public void cancelFriendRequest(long userId, long friendId) {
        jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId);
        logger.info("User with id {} removed friend with id {}", userId, friendId);
    }


    private void loadFriends(User user) {
        List<Long> friends = jdbc.queryForList(FIND_FRIENDS_QUERY, Long.class, user.getId());
        user.setFriends(new HashSet<>(friends));
    }

    public void confirmFriendship(long requesterId, long receiverId) {
        int rowsUpdated = jdbc.update(UPDATE_FRIENDSHIP_STATUS_QUERY, requesterId, receiverId);
        if (rowsUpdated == 0) {
            throw new NotFoundException("Friendship request not found for requester_id: " + requesterId + " and receiver_id: " + receiverId);
        }
        logger.info("Friendship confirmed between requester_id: {} and receiver_id: {}", requesterId, receiverId);
    }

    public boolean isFriendshipExists(long requesterId, long receiverId) {
        Integer count = jdbc.queryForObject(CHECK_FRIENDSHIP_EXISTS_QUERY, Integer.class, requesterId, receiverId);
        return count != null && count > 0;
    }

    public boolean isLoginExists(String login) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE login = ?", Integer.class, login);
        return count != null && count > 0;
    }

    private void loadAllFriends(List<User> users) {
        Map<Long, Set<Long>> userFriendsMap = new HashMap<>();
        jdbc.query("SELECT requester_id, receiver_id FROM friendships", rs -> {
            long requesterId = rs.getLong("requester_id");
            long receiverId = rs.getLong("receiver_id");
            userFriendsMap.computeIfAbsent(requesterId, k -> new HashSet<>()).add(receiverId);
        });
        for (User user : users) {
            user.setFriends(userFriendsMap.getOrDefault(user.getId(), Collections.emptySet()));
        }
    }
}
