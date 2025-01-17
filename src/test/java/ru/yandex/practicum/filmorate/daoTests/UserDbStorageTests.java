package ru.yandex.practicum.filmorate.daoTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class, UserDbStorage.class, UserRowMapper.class})
@Sql(scripts = {"/test_schema.sql", "/test_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @Test
    public void testCreateUser() {
        User newUser = new User();
        newUser.setName("Test");
        newUser.setEmail("test@example.com");
        newUser.setLogin("test");
        newUser.setBirthday(LocalDate.of(1995, 5, 2));

        User createdUser = userStorage.create(newUser);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("Test");
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.findById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(1);
                    assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
                });
    }

    @Test
    public void testUpdateUser() {
        User userToUpdate = userStorage.findById(1)
                .orElseThrow(() -> new AssertionError("User not found"));

        userToUpdate.setName("Updated Name");
        userToUpdate.setEmail("updated.email@example.com");

        userStorage.update(userToUpdate);

        Optional<User> updatedUser = userStorage.findById(1);
        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getName()).isEqualTo("Updated Name");
                    assertThat(user.getEmail()).isEqualTo("updated.email@example.com");
                });
    }

    @Test
    public void testDeleteUser() {
        userStorage.deleteById(1);

        Optional<User> deletedUser = userStorage.findById(1);
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    public void testAddAndRemoveFriend() {
        userStorage.sendFriendRequest(2, 3);

        Set<Long> friends = userStorage.getFriendIds(2);
        assertThat(friends).contains(3L);

        userStorage.cancelFriendRequest(2, 3);

        Set<Long> updatedFriends = userStorage.getFriendIds(2);
        assertThat(updatedFriends).doesNotContain(3L);
    }

    @Test
    public void testOneWayFriendship() {
        Set<Long> friends = userStorage.getFriendIds(1);
        assertThat(friends).contains(2L);

        Set<Long> noFriends = userStorage.getFriendIds(2);
        assertThat(noFriends).doesNotContain(1L);
    }

    @Test
    public void testConfirmFriendship() {
        userStorage.sendFriendRequest(2, 3);
        userStorage.confirmFriendship(2, 3);


        boolean isConfirmed = userStorage.isFriendshipExists(2, 3);
        assertThat(isConfirmed).isTrue();
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userStorage.getAll();

        assertThat(users).isNotEmpty();
        assertThat(users).hasSize(3);
        assertThat(users)
                .extracting(User::getEmail)
                .contains("john.doe@example.com", "jane.smith@example.com", "alice.johnson@example.com");
    }

    @Test
    public void testFindNonExistentUser() {
        Optional<User> user = userStorage.findById(999);
        assertThat(user).isNotPresent();
    }

    @Test
    public void testAddDuplicateFriendship() {

        assertThatThrownBy(() -> userStorage.sendFriendRequest(1, 2))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("Unique index or primary key violation");
    }

}
