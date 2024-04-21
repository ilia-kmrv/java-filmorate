package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

public class FilmDbStorage implements FilmStorage {
    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public void deleteFilm(long filmId) {

    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public List<Film> getAllFilms() {
        return null;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.empty();
    }
}
