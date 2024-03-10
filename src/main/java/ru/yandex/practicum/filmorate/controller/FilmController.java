package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter;

    // генератор id
    private long generateId() {
        return ++idCounter;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Обработан POST запрос. Фильм {} с id={} успешно добавлен", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Обработан PUT-запрос. Фильм {} с id={} успешно обновлён", film.getName(), film.getId());
            } else {
                throw new ResourceNotFoundException("Фильм с таким id не найден");
            }
            return film;
    }

}
