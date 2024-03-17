package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController controller;
    User user;
    Validator validator;
    UserService userService;
    UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        controller = new UserController(userService);

        user = User.builder()
                .name("Leonard")
                .email("unknown@mail.com")
                .login("leo")
                .birthday(LocalDate.of(1990, 12, 25))
                .build();

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Проверка списка")
    void getAllUsersShouldReturnNotEmptyList() {
        controller.postUser(user);

        assertFalse(controller.getAllUsers().isEmpty(), "После добавления пользователя список пустой");
        assertTrue(controller.getAllUsers().stream().anyMatch(u -> u.getId() == 1), "id не равен 1");
    }


    @Test
    @DisplayName("Проверка email пользователя")
    void postUserWithBlankOrIncorrectEmailShouldFailValidation() {
        user.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлен email co значением null");

        user.setEmail("whatever");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлен email c некорректным форматом");

        user.setEmail("");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлен email c пустой строкой");
    }

    @Test
    @DisplayName("Проверка логин пользователя не пустой и не содержит пробелы")
    void postUserWithBlankOrWhitespacedLoginShouldFailValidation() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлен login co значением null");

        user.setLogin(" what ever ");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлен login содержащий пробелы");

        user.setLogin("");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлен login c пустой строкой");
    }

    @Test
    @DisplayName("Дата рождения пользователя не сегодня и не в будущем")
    void postUserWithBirthdayInTheFutureOrPresentShouldFailValidation() {
        user.setBirthday(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлен дата рождения co значением null");

        user.setBirthday(LocalDate.now());
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлена дата рождения сегодня");

        user.setBirthday(LocalDate.now().plusDays(1));
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Добавлена дата рождения в будущем");
    }

    @Test
    @DisplayName("Имя пользователя должно изменятся на логин если имя пустое")
    void postUserWithBlankNameShouldSetNameToLoginValue() {
        user.setName(null);
        controller.postUser(user);

        assertEquals(user.getLogin(), user.getName(), "Имя (null) не заменилось на логин");

        user.setName(" ");
        controller.postUser(user);
        assertEquals(user.getLogin(), user.getName(), "Имя (пустое) не заменилось на логин");

    }

    @Test
    @DisplayName("Обновление пользователя c несуществующим id")
    void putUserWithWrongIdValueShouldFail() {
        user.setId(Long.MAX_VALUE);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> controller.putUser(user));

        assertEquals("Пользователь с таким id не найден", exception.getMessage());
    }

}