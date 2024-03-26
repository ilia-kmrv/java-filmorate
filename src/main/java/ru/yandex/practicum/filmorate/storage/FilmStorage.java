package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    // добавление фильма
    Film addFilm(Film film);

    // удаление фильма
    void deleteFilm(long filmId);

    // обновление фильма
    Film updateFilm(Film film);

    // получение списка фильмов
    List<Film> getAllFilms();

    // получение фильма по id
    Optional<Film> getFilmById(Long id);
}
