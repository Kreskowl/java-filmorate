package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @GetMapping
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {
        newUser.setId(getNextId());
        newUser.useLoginForEmptyName();
        users.put(newUser.getId(), newUser);
        logger.info("Created new user: {}", newUser);
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updateUser) {
        User existingUser = Optional.ofNullable(users.get(updateUser.getId()))
                .orElseThrow(() -> new NotFoundException("User with id " + updateUser.getId() + " not found"));

        existingUser.setName(updateUser.getName());
        existingUser.setLogin(updateUser.getLogin());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setBirthday(updateUser.getBirthday());

        existingUser.useLoginForEmptyName();

        users.put(updateUser.getId(), existingUser);
        logger.info("Updated user with id {}: {}", existingUser.getId(), existingUser);
        return existingUser;
    }

    private long getNextId() {
        return ++currentId;
    }
}
