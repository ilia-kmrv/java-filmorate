package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    // TODO: add comments
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    // TODO: method to delete film
    public void deleteFilm(long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    // TODO: method to update film
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }
    // TODO: method to return list of films
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }
}
