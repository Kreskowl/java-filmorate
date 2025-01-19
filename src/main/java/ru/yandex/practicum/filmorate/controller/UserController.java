package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest newUserRequest) {
        return userService.createUser(newUserRequest);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(updateUserRequest);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addToFriendsList(@PathVariable long id, @PathVariable long friendId) {
        userService.sendFriendshipRequest(id, friendId);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public void confirmFriendship(@PathVariable long id, @PathVariable long friendId) {
        userService.approveFriendshipRequest(friendId, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFromFriendsList(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
    }
}