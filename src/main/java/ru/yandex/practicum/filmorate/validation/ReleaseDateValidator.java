package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotations.AfterLowerBoundDate;

import java.time.LocalDate;
import java.time.Month;

public class ReleaseDateValidator implements ConstraintValidator<AfterLowerBoundDate, LocalDate> {
    private static final LocalDate LOWER_RELEASE_DATE_BOUND = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public void initialize(AfterLowerBoundDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        if (releaseDate == null) {
            return true;
        }
        return !releaseDate.isBefore(LOWER_RELEASE_DATE_BOUND);
    }
}
