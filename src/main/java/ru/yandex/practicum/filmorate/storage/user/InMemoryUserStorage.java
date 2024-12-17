package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User create(User newUser) {
        newUser.setId(getNextId());
        newUser.useLoginForEmptyName();
        users.put(newUser.getId(), newUser);
        logger.info("Created new user: {}", newUser);
        return newUser;
    }

    @Override
    public User update(User updatedUser) {
        User existingUser = Optional.ofNullable(users.get(updatedUser.getId()))
                .orElseThrow(() -> new NotFoundException("User with id " + updatedUser.getId() + " not found"));

        existingUser.setName(updatedUser.getName());
        existingUser.setLogin(updatedUser.getLogin());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setBirthday(updatedUser.getBirthday());
        existingUser.useLoginForEmptyName();

        logger.info("Updated user with id {}: {}", existingUser.getId(), existingUser);
        return existingUser;
    }

    @Override
    public void deleteById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        users.values().forEach(user -> user.getFriends().remove(id));
        logger.info("Removed user with id {} from friends list of all users", id);
        users.remove(id);
        logger.info("User with id {} deleted", id);
    }

    @Override
    public User findById(long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private long getNextId() {
        return ++currentId;
    }
}
