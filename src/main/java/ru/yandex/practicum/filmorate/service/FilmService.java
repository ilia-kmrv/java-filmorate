package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    // TODO: add comments
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public void deleteFilm(long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId); // TODO: check should be in the controller?
        if (filmStorage.getAllFilms().contains(film)) {
            film.getLikes().add(userId);
            log.info("Лайк фильму c id={} от пользователя с id={} успешно добавлен.", filmId, userId);
        } else {
            throw new ResourceNotFoundException(String.format("Фильм с id=%d не найден.", filmId));
        }
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId); // TODO: check should be in the controller?
        if (filmStorage.getAllFilms().contains(film)) {
            film.getLikes().remove(userId);
            log.info("Лайк фильму c id={} от пользователя с id={} успешно удалён.", filmId, userId);
        } else {
            throw new ResourceNotFoundException(String.format("Фильм с id=%d не найден.", filmId));
        }
        return film;
    }

    // TODO: get 10 films with most likes
    public List<Film> getTopFilms(int filmsCount) {

        List<Film> list = filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(filmsCount > 0 ? filmsCount : 10)
                .collect(Collectors.toList());

        return list;
    }
}
