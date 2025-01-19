package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.useLoginForEmptyName();
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getFriends()
        );
    }

    public static User updateUserFromRequest(User user, UpdateUserRequest request) {
        Optional.ofNullable(request.getEmail()).ifPresentOrElse(
                user::setEmail,
                () -> user.setEmail(user.getEmail())
        );
        Optional.ofNullable(request.getLogin()).ifPresent(user::setLogin);
        Optional.ofNullable(request.getName()).ifPresent(user::setName);
        Optional.ofNullable(request.getBirthday()).ifPresent(user::setBirthday);

        return user;
    }
}
