package ru.yandex.practicum.filmorate.storage;

// утилитарный класс для изменения хранилища по умолчанию
public class Storages {

    public static UserStorage getDefaultUserStorage() {
        return new InMemoryUserStorage();
    }

    public static FilmStorage getDefaultFilmStorage() {
        return new InMemoryFilmStorage();
    }
}
