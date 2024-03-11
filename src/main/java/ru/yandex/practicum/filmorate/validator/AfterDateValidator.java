package ru.yandex.practicum.filmorate.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// валидатор для аннотации @AfterDate
public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {

    private LocalDate date;

    public void initialize(AfterDate annotation) {
        date = LocalDate.parse(annotation.value(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        boolean valid = true;
        if (value != null) {
            if (!value.isAfter(date) && !value.isEqual(date)) {
                valid = false;
            }
        }
        return valid;
    }
}
