package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {

    FilmController controller;
    Film firstFilm;
    Validator validator;
    FilmService filmService;
    FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;
    private MpaRatingStorage mpaRatingStorage;
    private GenreStorage genreStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        mpaRatingStorage = new MpaRatingDbStorage(jdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        filmService = new FilmService(filmStorage, genreStorage);
        controller = new FilmController(filmService);

        Optional<MpaRating> mpaRatingOptional1 = mpaRatingStorage.get(1L);

        firstFilm = Film.builder()
                .name("Film 1")
                .description("First film")
                .releaseDate(LocalDate.of(2020, 12, 01))
                .duration(110)
                .mpa(mpaRatingOptional1.get())
                .build();

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Проверка списка")
    void getAllFilmsShouldReturnNotEmptyList() {
        controller.postFilm(firstFilm);

        assertFalse(controller.getAllFilms().isEmpty(), "После добавления фильма список пустой");
        assertTrue(controller.getAllFilms().stream().anyMatch(u -> u.getId() == firstFilm.getId()));
    }

    @Test
    @DisplayName("Проверка название фильма не пустое и не null")
    void postFilmWithBlankNameShouldFailValidation() {
        firstFilm.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(firstFilm);
        assertFalse(violations.isEmpty(), "Добавлено название co значением null");

        firstFilm.setName(" ");
        violations = validator.validate(firstFilm);
        assertFalse(violations.isEmpty(), "Добавлен название c пустой строкой");
    }

    @Test
    @DisplayName("Описание фильма должно быть меньше или равно 200 символов")
    void postFilmDescriptionWithMoreThan200symbolsShouldFailValidation() {
        char[] chars = new char[200];
        Arrays.fill(chars, '1');
        String description = new String(chars);

        firstFilm.setDescription(description);
        Set<ConstraintViolation<Film>> violations = validator.validate(firstFilm);
        assertTrue(violations.isEmpty(), "Не добавилось описание co значением равно 200 символов");

        chars = new char[201];
        Arrays.fill(chars, '1');
        description = new String(chars);

        firstFilm.setDescription(description);
        violations = validator.validate(firstFilm);
        assertFalse(violations.isEmpty(), "Добавлено описание co значением равно 201 символов");
    }

    @Test
    @DisplayName("Дата рождения пользователя не сегодня и не в будущем")
    void postFilmWithReleaseDateIsBeforeFirstEverFilmDateOrNullShouldFailValidation() {
        firstFilm.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(firstFilm);
        assertFalse(violations.isEmpty(), "Добавлена дата релиза co значением null");

        firstFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        violations = validator.validate(firstFilm);
        assertTrue(violations.isEmpty(), "Не добавлена дата релиза 28.12.1895");

        firstFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        violations = validator.validate(firstFilm);
        assertFalse(violations.isEmpty(), "Добавлена дата релиза до 28.12.1895");
    }

    @Test
    @DisplayName("Проверка продолжительности - только положительное число")
    void postFilmWithNegativeDurationShouldFailValidation() {
        firstFilm.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(firstFilm);
        assertFalse(violations.isEmpty(), "Добавлена продолжительность co значением 0");

        firstFilm.setDuration(-100);
        violations = validator.validate(firstFilm);
        assertFalse(violations.isEmpty(), "Добавлено продолжительность c отрицательным значением");
    }

    @Test
    @DisplayName("Обновление фильма c несуществующим id")
    void putFilmWithWrongIdValueShouldFail() {
        firstFilm.setId(Long.MAX_VALUE);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> controller.putFilm(firstFilm));

        assertEquals(String.format("Фильм с id=%d не найден", firstFilm.getId()), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка создания фильма с несуществующим MPA")
    void putFilmWithWrongMPAShouldThrowException() {
        firstFilm.setMpa(new MpaRating(Integer.MAX_VALUE, "xxx"));

        DataAccessException exception = assertThrows(DataAccessException.class, () -> controller.postFilm(firstFilm));
        assertEquals(String.format("Жанра или MPA с указанным id нет в базе", firstFilm.getId()), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка создания фильма с несуществующим жанром")
    void putFilmWithWrongGenreShouldThrowException() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(Integer.MAX_VALUE, "no_genre"));
        firstFilm.setGenres(genres);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> controller.postFilm(firstFilm));
        assertEquals(String.format("Жанра или MPA с указанным id нет в базе", firstFilm.getId()), exception.getMessage());
    }
}