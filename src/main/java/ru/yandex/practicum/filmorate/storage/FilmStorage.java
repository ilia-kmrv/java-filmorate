package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    // добавление фильма
    Film addFilm(Film film);

    // удаление фильма
    void deleteFilm(long filmId);

    // обновление фильма
    Film updateFilm(Film film);

    // получение списка фильмов
    List<Film> getAllFilms();
}
