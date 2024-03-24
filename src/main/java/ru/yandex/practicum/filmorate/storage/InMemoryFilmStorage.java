package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private long idCounter;

    private long generateId() {
        return ++idCounter;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм {} с id={} успешно добавлен", film.getName(), film.getId());
        return film;
    }

    @Override
    public void deleteFilm(long filmId) {
        if (films.containsKey(filmId)) {
            films.remove(filmId);
            log.info("Фильм c id={} успешно удалён", filmId);
        } else {
            throw new ResourceNotFoundException(String.format("Фильм с id=%d не найден.", filmId));
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм {} с id={} успешно обновлён", film.getName(), film.getId());
        } else {
            throw new ResourceNotFoundException(String.format("Фильм с id=%d не найден.", film.getId()));
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }
}
