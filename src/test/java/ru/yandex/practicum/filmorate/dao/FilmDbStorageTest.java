package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.model.Film.FIRST_FILM_DATE;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private MpaRatingStorage mpaRatingStorage;
    private GenreStorage genreStorage;
    EasyRandomParameters easyRandomParameters;
    EasyRandom easyRandom;
    Film firstFilm;
    Film secondFilm;

    @BeforeEach
    void setUp() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        mpaRatingStorage = new MpaRatingDbStorage(jdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);

        easyRandomParameters = new EasyRandomParameters();
        easyRandomParameters.seed(12L);
        easyRandomParameters.charset(Charset.forName("UTF-8"));
        easyRandomParameters.collectionSizeRange(1, 10);
        easyRandomParameters.dateRange(LocalDate.parse(FIRST_FILM_DATE), LocalDate.now());
        easyRandomParameters.overrideDefaultInitialization(true);

        easyRandom = new EasyRandom(easyRandomParameters);

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
                .genres(genresForFirstFilm)
                .build();

        secondFilm = Film.builder()
                .id(1L)
                .name("Film 2")
                .description("Second film")
                .releaseDate(LocalDate.of(2020, 12, 01))
                .duration(120)
                .mpa(mpaRatingOptional2.get())
                .genres(genresForSecondFilm)
                .build();
    }


    @Test
    void testGetFilmById() {
        Film newFilm = filmStorage.create(firstFilm);

        Optional<Film> savedFilm = filmStorage.get(newFilm.getId());

        assertThat(savedFilm.isPresent() ? savedFilm.get() : easyRandom.nextObject(Film.class))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    void testUpdateFilm() {
        firstFilm = filmStorage.create(firstFilm);
        Film filmForUpdating = secondFilm;
        Film updatedFilm = filmStorage.update(filmForUpdating);

        assertThat(updatedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmForUpdating);
    }

    @Test
    void testDeleteFilm() {
        filmStorage.create(firstFilm);
        filmStorage.create(secondFilm);

        assertEquals(2, filmStorage.getAll().size());

        filmStorage.delete(firstFilm.getId());

        assertEquals(1, filmStorage.getAll().size());
        assertTrue(filmStorage.getAll().contains(secondFilm));
    }

    @Test
    void testGetTopFilmsByLikes() {
        User firstUser = userStorage.create(easyRandom.nextObject(User.class));
        User secondUser = userStorage.create(easyRandom.nextObject(User.class));
        User thirdUser = userStorage.create(easyRandom.nextObject(User.class));

        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);

        filmStorage.addLike(firstFilm.getId(), firstUser.getId());
        filmStorage.addLike(secondFilm.getId(), firstUser.getId());
        filmStorage.addLike(secondFilm.getId(), secondUser.getId());

        List<Film> expectedList = List.of(secondFilm, firstFilm);
        List<Film> topFilmsList = filmStorage.getTopFilms(2);

        assertEquals(expectedList, topFilmsList);

        filmStorage.deleteLike(secondFilm.getId(), firstUser.getId());
        filmStorage.deleteLike(secondFilm.getId(), secondUser.getId());

        expectedList = List.of(firstFilm, secondFilm);
        topFilmsList = filmStorage.getTopFilms(2);

        assertEquals(expectedList, topFilmsList);

    }
}