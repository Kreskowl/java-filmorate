package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(long idUserRequest, long idUserToAdd) {
        User requestToAddUser = userStorage.findById(idUserRequest);
        User userToAdd = userStorage.findById(idUserToAdd);

        if (requestToAddUser.getFriends().contains(idUserToAdd)) {
            throw new ValidationException("User with id " + idUserToAdd + " is already a friend.");
        }
        requestToAddUser.getFriends().add(idUserToAdd);
        userToAdd.getFriends().add(idUserRequest);
        logger.info("User with id {} add to friends user with id {}", idUserRequest, idUserToAdd);

        userStorage.update(userToAdd);
        return userStorage.update(requestToAddUser);
    }

    public User deleteFriend(long requestUserId, long idUserToDelete) {
        User requestToDelete = userStorage.findById(requestUserId);
        User userToDelete = userStorage.findById(idUserToDelete);

        logger.debug("Friends of user with id {} before delete: {}", requestUserId, requestToDelete.getFriends());


        requestToDelete.getFriends().remove(idUserToDelete);
        userToDelete.getFriends().remove(requestUserId);

        logger.info("User with id {} delete user with id {} from friends list", requestUserId, idUserToDelete);
        logger.debug("Friends of user with id {} after delete: {}", requestToDelete, requestToDelete.getFriends());

        userStorage.update(userToDelete);
        return userStorage.update(requestToDelete);
    }

    public Set<Long> getCommonFriends(long idRequestUser, long idUserWithCommonFriends) {
        User requestUser = userStorage.findById(idRequestUser);
        User userWithCommonFriends = userStorage.findById(idUserWithCommonFriends);

        Set<Long> commonFriends = new HashSet<>(requestUser.getFriends());
        commonFriends.retainAll(userWithCommonFriends.getFriends());

        logger.info("Found {} common friends between users with ids {} and {}",
                commonFriends.size(), idRequestUser, idUserWithCommonFriends);

        return commonFriends;
    }
}