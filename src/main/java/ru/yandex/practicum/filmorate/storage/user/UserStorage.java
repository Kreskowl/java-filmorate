package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User create(User newUser);

    User update(User updatedUser);

    void deleteById(long id);

    User findById(long id);
}
