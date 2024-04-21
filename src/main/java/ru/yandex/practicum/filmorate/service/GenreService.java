package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre addGenre(Genre genre) {
        return genreStorage.create(genre);
    }

    public Genre getGenre(Long id) {
        return genreStorage.get(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Жанр с id=%d не найден", id)));
    }

    public Genre updateGenre(Genre genre) {
        getGenre((long) genre.getId());
        return genreStorage.update(genre);
    }

    public void deleteGenre(long genreId) {
        getGenre(genreId);
        genreStorage.delete(genreId);
    }

    public List<Genre> getAllGenres() {
        return (List<Genre>) genreStorage.getAll();
    }
}
