package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import javax.script.ScriptException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingStorage mpaRatingStorage;
    private final GenreStorage genreStorage;

    @Override
    public Film create(Film film) {
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("id");
            long filmGeneratedId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
            film.setId(filmGeneratedId);
            putGenres(film);
            log.info("Фильм {} c id={} успешно добавлен", film.getName(), film.getId());
        } catch (DataAccessException e) {
            log.warn("Ошибка получения жанра или MPA");
            throw new DataRetrievalFailureException("Ресурса с указанным id нет в базе");
        }
        return film;
    }


    @Override
    public Optional<Film> get(Long id) {
        String sqlQuery =
                "SELECT f.*, " +
                        "mpa.id, mpa.name AS mpa_name " +
                        "FROM films AS f " +
                        "JOIN mpa_ratings AS mpa ON f.mpa_rating = mpa.id " +
                        "WHERE f.id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id).stream().findFirst();
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating = ?" +
                " WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        putGenres(film);

        return film;
    }

    @Override
    public boolean delete(Long filmId) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery =
                "SELECT f.*, " +
                        "mpa.id, mpa.name AS mpa_name " +
                        "FROM films AS f " +
                        "JOIN mpa_ratings AS mpa ON f.mpa_rating = mpa.id ";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public void addLike(Film film, User user) {

    }

    @Override
    public void deleteLike(Film film, User user) {

    }

    @Override
    public List<Film> getTopFilms(int count) {
        return null;
    }


    private void putGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                film.getGenres(),
                film.getGenres().size(),
                ((ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                }));
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new MpaRating(resultSet.getInt("mpa_rating"), resultSet.getString("mpa_name")))
                .build();
    }
}
