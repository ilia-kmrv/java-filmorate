package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.service.FilmService.DEFAULT_TOP_COUNT;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private long idCounter;

    private long generateId() {
        return ++idCounter;
    }

    @Override
    public Film create(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм {} с id={} успешно добавлен", film.getName(), film.getId());
        return film;
    }

    @Override
    public Optional<Film> get(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.info("Фильм {} с id={} успешно обновлён", film.getName(), film.getId());
        return film;
    }

    @Override
    public boolean delete(Long filmId) {
        films.remove(filmId);
        log.info("Фильм c id={} успешно удалён", filmId);
        return true;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(Long filmId, Long userId) {

    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

    }

    @Override
    public List<Film> getTopFilms(int count) {
        List<Film> list = getAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count > 0 ? count : DEFAULT_TOP_COUNT)
                .collect(Collectors.toList());
        return list;
    }
}
