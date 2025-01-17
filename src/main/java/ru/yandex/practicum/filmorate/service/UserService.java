package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDbStorage userDbStorage;

    public List<UserDto> getAllUsers() {
        return userDbStorage.getAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long id) {
        return UserMapper.mapToUserDto(getUser(id));
    }

    public UserDto createUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user = userDbStorage.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = getUser(request.getId());
        UserMapper.updateUserFromRequest(updatedUser, request);
        userDbStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void deleteUser(long id) {
        userDbStorage.deleteById(id);
    }

    public List<UserDto> getUserFriends(long id) {
        User user = getUser(id);
        Set<Long> friendIds = userDbStorage.getFriendIds(id);
        return friendIds.stream()
                .map(this::getUser)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public void sendFriendshipRequest(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            throw new ValidationException("User cannot add themselves as a friend.");
        }

        userDbStorage.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("User with id " + receiverId + " not found"));

        if (userDbStorage.isFriendshipExists(requesterId, receiverId)) {
            throw new ValidationException("Friendship request already exists.");
        }

        userDbStorage.sendFriendRequest(requesterId, receiverId);
        logger.info("User with id {} sent a friend request to user with id {}", requesterId, receiverId);
    }

    public void approveFriendshipRequest(long requesterId, long receiverId) {
        if (!userDbStorage.isFriendshipExists(requesterId, receiverId)) {
            throw new ValidationException("No friend request found from user with id " + requesterId + " to user with id " + receiverId);
        }

        userDbStorage.confirmFriendship(requesterId, receiverId);
        logger.info("User with id {} confirmed friendship request from user with id {}", receiverId, requesterId);
    }

    public void deleteFriend(long userId, long friendId) {
        getUser(userId);
        getUser(friendId);
        if (userDbStorage.isFriendshipExists(userId, friendId)) {
            userDbStorage.cancelFriendRequest(userId, friendId);
            logger.info("User with id {} removed friend with id {}", userId, friendId);
        } else {
            logger.warn("Friendship between user {} and {} not found for deletion", userId, friendId);
        }
    }

    public List<UserDto> getCommonFriends(long userId, long otherUserId) {
        Set<Long> userFriends = userDbStorage.getFriendIds(userId);
        Set<Long> otherUserFriends = userDbStorage.getFriendIds(otherUserId);

        Set<Long> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(otherUserFriends);

        return commonFriends.stream()
                .map(this::getUser)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    private User getUser(long id) {
        return userDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }
}
