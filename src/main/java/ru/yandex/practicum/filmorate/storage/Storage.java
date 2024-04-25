package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;

public interface Storage<T> {
    T create(T object);

    Optional<T> get(Long id);

    T update(T object);

    boolean delete(Long id);

    Collection<T> getAll();
}

