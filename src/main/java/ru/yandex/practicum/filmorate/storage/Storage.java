package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface Storage<T> {
    T create(T object);

    T get(Long id);

    T update(T object);

    T delete(Long id);

    Collection<T> getAll();
}

