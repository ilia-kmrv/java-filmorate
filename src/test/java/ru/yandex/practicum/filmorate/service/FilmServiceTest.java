package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.model.Film.FIRST_FILM_DATE;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {

    private final JdbcTemplate jdbcTemplate;
    FilmStorage filmStorage;
    FilmService filmService;
    GenreStorage genreStorage;
    MpaRatingStorage mpaRatingStorage;
    Film firstFilm;

    EasyRandom easyRandom;
    EasyRandomParameters easyRandomParameters;


    @BeforeEach
    void setUp() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        mpaRatingStorage = new MpaRatingDbStorage(jdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        filmService = new FilmService(filmStorage, genreStorage);

        Optional<MpaRating> mpaRatingOptional1 = mpaRatingStorage.get(1L);
        Optional<MpaRating> mpaRatingOptional2 = mpaRatingStorage.get(2L);
        Optional<Genre> genreOptional1 = genreStorage.get(1L);
        Optional<Genre> genreOptional2 = genreStorage.get(2L);

        LinkedHashSet<Genre> genresForFirstFilm = new LinkedHashSet<>();
        genresForFirstFilm.add(genreOptional1.get());
        LinkedHashSet<Genre> genresForSecondFilm = new LinkedHashSet<>();
        genresForSecondFilm.add(genreOptional2.get());

        firstFilm = Film.builder()
                .name("Film 1")
                .description("First film")
                .releaseDate(LocalDate.of(2020, 12, 01))
                .duration(110)
                .mpa(mpaRatingOptional1.get())
                .build();

        filmService.addFilm(firstFilm);

        // параметры создания тестовых объектов
        easyRandomParameters = new EasyRandomParameters();
        easyRandomParameters.seed(12L);
        easyRandomParameters.charset(Charset.forName("UTF-8"));
        easyRandomParameters.collectionSizeRange(1, 10);
        easyRandomParameters.dateRange(LocalDate.parse(FIRST_FILM_DATE), LocalDate.now());
        easyRandomParameters.overrideDefaultInitialization(true);

        easyRandom = new EasyRandom(easyRandomParameters);

    }

    @Test
    @DisplayName("Проверка получения фильма по id")
    void getFilmByIdTestShouldReturnUserOrThrowException() {
        assertTrue(firstFilm.equals(filmService.getFilmById(firstFilm.getId())), "Фильмы не совпали");

        // проверка не существующим id
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> filmService.getFilmById(Long.MAX_VALUE));

        assertTrue(String.format("Фильм с id=%d не найден", Long.MAX_VALUE).equals(e.getMessage()));
    }
}