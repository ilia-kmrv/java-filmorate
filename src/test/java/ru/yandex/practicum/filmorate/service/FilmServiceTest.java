package ru.yandex.practicum.filmorate.service;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Storages;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.model.Film.FIRST_FILM_DATE;

class FilmServiceTest {

    FilmStorage filmStorage;
    FilmService filmService;
    Film film;

    EasyRandom easyRandom;
    EasyRandomParameters easyRandomParameters;


    @BeforeEach
    void setUp() {
        filmStorage = Storages.getDefaultFilmStorage();
        filmService = new FilmService(filmStorage);

        film = Film.builder()
                .name("Test film")
                .description("Test film's description")
                .releaseDate(LocalDate.of(1995, 11, 24))
                .duration(120)
                .build();

        filmService.addFilm(film);

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
    @DisplayName("Проверка добавления лайка")
    void addLikeTestShouldNotChangeSetIfLikeAlreadyExistAndAddLikeToSetIfNotExistYet() {
        Long userId = 1L;
        filmService.addLike(film.getId(), userId);

        Set<Long> expectedSet = Collections.singleton(userId);

        assertEquals(expectedSet, film.getLikes(), "Множества лайков не совпали");

        // добавление уже существующего лайка не должно ничего изменить
        filmService.addLike(film.getId(), userId);

        assertEquals(expectedSet, film.getLikes(), "Множества лайков не совпали");
    }

    @Test
    @DisplayName("Проверка удаления лайка")
    void deleteLike() {
        Long userId = 1L;
        filmService.addLike(film.getId(), userId);

        assertFalse(film.getLikes().isEmpty(), "Множество лайков пустое");

        filmService.deleteLike(film.getId(), userId);

        assertTrue(film.getLikes().isEmpty(), "Множество лайков не пустое");
    }

    @Test
    @DisplayName("Проверка списка топ фильмов по лайкам")
    void getTopFilmsTestShouldReturnReverseOrderedFilmList() {
        int filmsCount = 6;

        filmService.deleteFilm(film.getId());

        List<Film> filmList = easyRandom.objects(Film.class, filmsCount).collect(Collectors.toList());

        List<Film> expectedList = filmList.stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .collect(Collectors.toList());

        expectedList.stream().forEach(System.out::println);

        filmList.stream().forEach(filmService::addFilm);

        filmService.getTopFilms(filmsCount).stream().forEach(System.out::println);

        assertTrue(expectedList.equals(filmService.getTopFilms(filmsCount)));

    }
}