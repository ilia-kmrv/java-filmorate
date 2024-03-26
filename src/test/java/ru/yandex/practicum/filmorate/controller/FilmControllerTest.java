package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController controller;
    Film film;
    Validator validator;
    FilmService filmService;
    FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
        controller = new FilmController(filmService);

        film = Film.builder()
                .name("Test film")
                .description("Test film's description")
                .releaseDate(LocalDate.of(1995, 11, 24))
                .duration(120)
                .build();

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Проверка списка")
    void getAllFilmsShouldReturnNotEmptyList() {
        controller.postFilm(film);

        assertFalse(controller.getAllFilms().isEmpty(), "После добавления пользователя список пустой");
        assertTrue(controller.getAllFilms().stream().anyMatch(u -> u.getId() == 1), "id не равен 1");
    }

    @Test
    @DisplayName("Проверка название фильма не пустое и не null")
    void postFilmWithBlankNameShouldFailValidation() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Добавлено название co значением null");

        film.setName(" ");
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Добавлен название c пустой строкой");
    }

    @Test
    @DisplayName("Описание фильма должно быть меньше или равно 200 символов")
    void postFilmDescriptionWithMoreThan200symbolsShouldFailValidation() {
        char[] chars = new char[200];
        Arrays.fill(chars, '1');
        String description = new String(chars);

        film.setDescription(description);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Не добавилось описание co значением равно 200 символов");

        chars = new char[201];
        Arrays.fill(chars, '1');
        description = new String(chars);

        film.setDescription(description);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Добавлено описание co значением равно 201 символов");
    }

    @Test
    @DisplayName("Дата рождения пользователя не сегодня и не в будущем")
    void postFilmWithReleaseDateIsBeforeFirstEverFilmDateOrNullShouldFailValidation() {
        film.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Добавлена дата релиза co значением null");

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Не добавлена дата релиза 28.12.1895");

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Добавлена дата релиза до 28.12.1895");
    }

    @Test
    @DisplayName("Проверка продолжительности - только положительное число")
    void postFilmWithNegativeDurationShouldFailValidation() {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Добавлена продолжительность co значением 0");

        film.setDuration(-100);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Добавлено продолжительность c отрицательным значением");
    }

    @Test
    @DisplayName("Обновление фильма c несуществующим id")
    void putFilmWithWrongIdValueShouldFail() {
        film.setId(Long.MAX_VALUE);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> controller.putFilm(film));

        assertEquals(String.format("Фильм с id=%d не найден", film.getId()), exception.getMessage());
    }
}