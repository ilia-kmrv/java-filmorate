package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;

public interface GenreStorage extends Storage<Genre> {

    LinkedHashSet<Genre> getGenresByFilmId(Long filmId);
}
