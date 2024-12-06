package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Login must not be blank")
    @Pattern(regexp = "\\S+", message = "Login must not contain spaces")
    private String login;

    @PastOrPresent(message = "Birthday cannot be in the future")
    private LocalDate birthday;

    public void useLoginForEmptyName() {
        if (this.name == null || this.name.isBlank()) {
            this.name = this.login;
        }
    }
}


