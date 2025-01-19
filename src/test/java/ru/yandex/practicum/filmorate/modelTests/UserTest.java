package ru.yandex.practicum.filmorate.modelTests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UserTest {
    private User user;
    private Validator validator;
    private Set<ConstraintViolation<User>> violations;

    @BeforeEach
    public void setUp() {
        user = new User();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Should create user successfully")
    @Test
    public void shouldCreateUser() {
        user.setLogin("Kreskow");
        user.setEmail("test@gmail.com");
        user.setName("Kobi");
        user.setBirthday(LocalDate.of(1990, Month.DECEMBER, 12));

        violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Validation should pass if info is valid");
    }

    @DisplayName("Should throw an error when user have an empty email")
    @Test
    public void shouldThrowAnErrorWhenUserHaveEmptyEmail() {
        user.setLogin("Kreskow");
        user.setEmail(" ");

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for empty email");
    }

    @DisplayName("Should throw an error when user`s email does not contain @")
    @Test
    public void shouldThrowAnErrorIfEmailIsInvalid() {
        user.setLogin("Kreskow");
        user.setEmail("invalidgmail.com");

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for invalid email");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
    }

    @DisplayName("Should throw an error when user have an empty login")
    @Test
    public void shouldThrowAnErrorWhenUserHaveEmptyLogin() {
        user.setLogin(" ");
        user.setEmail("test@gmail.com");

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for empty login");
    }

    @DisplayName("Should throw an error when user`s login contain ' '")
    @Test
    public void shouldThrowAnErrorIfLoginIsInvalid() {
        user.setLogin("Kreskow 1 2 3");
        user.setEmail("test@gmail.com");

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for login containing spaces");
    }

    @DisplayName("Should use a login if user`s name is empty")
    @Test
    public void shouldUseLoginIfUserNameIsEmpty() {
        user.setName("");
        user.setLogin("Kreskow");
        user.useLoginForEmptyName();

        assertEquals(user.getLogin(), user.getName());
    }


    @DisplayName("Should throw an error when user`s birthday is a future date")
    @Test
    public void shouldThrowAnErrorIfUserBirthdayIsInvalid() {
        user.setBirthday(LocalDate.of(2025, Month.AUGUST, 15));

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail if birthday is in the future");
    }
}
