package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public Genre postGenre(@Valid @RequestBody Genre genre) {
        log.info("Обработан POST genre запрос.");
        return genreService.addGenre(genre);
    }

    @GetMapping("/{genreId}")
    public Genre getGenre(@PathVariable long genreId) {
        log.info("Обработан GET genre {} запрос.", genreId);
        return genreService.getGenre(genreId);
    }

    @PutMapping
    public Genre putGenre(@Valid @RequestBody Genre genre) {
        log.info("Обработан PUT genre запрос.");
        return genreService.updateGenre(genre);
    }

    @DeleteMapping("/{genreId}")
    public void deleteGenre(@PathVariable long genreId) {
        log.info("Обработан DELETE genre {} запрос.", genreId);
        genreService.deleteGenre(genreId);
    }

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Обработан GET genres запрос");
        return genreService.getAllGenres();
    }
}
