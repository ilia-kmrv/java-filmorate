package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    public static final int DEFAULT_TOP_COUNT = 10;

    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film getFilmById(long id) {
        return filmStorage.get(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Фильм с id=%d не найден", id)));
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        return filmStorage.update(film);
    }

    public void deleteFilm(long filmId) {
        getFilmById(filmId);
        filmStorage.delete(filmId);
    }

    public List<Film> getAllFilms() {
        return (List<Film>) filmStorage.getAll();
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        filmStorage.addLike(filmId, userId);
        log.info("Лайк фильму c id={} от пользователя с id={} успешно добавлен.", filmId, userId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        filmStorage.deleteLike(filmId, userId);
        log.info("Лайк фильму c id={} от пользователя с id={} успешно удалён.", filmId, userId);
        return film;
    }

    public List<Film> getTopFilms(int filmsCount) {
        log.info("Возвращаем топ {} самых популярных фильмов", filmsCount);
        return filmStorage.getTopFilms(filmsCount);
    }
}
