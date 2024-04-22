package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.yandex.practicum.filmorate.service.FilmService.DEFAULT_TOP_COUNT;

@Repository
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("id");
            long filmGeneratedId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
            film.setId(filmGeneratedId);
            saveFilmGenres(film);
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
                        "mpa.id, mpa.name AS mpa_name, " +
                        "STRING_AGG(g.id, ', ') AS genre_id, " +
                        "STRING_AGG(g.name, ', ') AS genre_name " +
                        "FROM films AS f " +
                        "LEFT JOIN mpa_ratings AS mpa ON f.mpa_rating = mpa.id " +
                        "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                        "LEFT JOIN genres AS g ON fg.genre_id = g.id " +
                        "WHERE f.id = ? " +
                        "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating, mpa.name ";
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
        saveFilmGenres(film);

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
                        "mpa.id, mpa.name AS mpa_name, " +
                        "STRING_AGG(g.id, ', ') AS genre_id, " +
                        "STRING_AGG(g.name, ', ') AS genre_name " +
                        "FROM films AS f " +
                        "LEFT JOIN mpa_ratings AS mpa ON f.mpa_rating = mpa.id " +
                        "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                        "LEFT JOIN genres AS g ON fg.genre_id = g.id " +
                        "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating, mpa.name " +
                        "ORDER BY f.id ";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sqlQuery =
                "SELECT f.*, " +
                        "mpa.id, mpa.name AS mpa_name, " +
                        "STRING_AGG(g.id, ', ') AS genre_id, " +
                        "STRING_AGG(g.name, ', ') AS genre_name " +
                        "FROM films AS f " +
                        "LEFT JOIN mpa_ratings AS mpa ON f.mpa_rating = mpa.id " +
                        "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                        "LEFT JOIN genres AS g ON fg.genre_id = g.id " +
                        "LEFT JOIN likes AS l ON f.id = l.film_id " +
                        "GROUP BY f.id " +
                        "ORDER BY COUNT(l.film_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count > 0 ? count : DEFAULT_TOP_COUNT);
    }


    private void saveFilmGenres(Film film) {
        if (film.getGenres() != null) {
            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
            jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    film.getGenres(),
                    film.getGenres().size(),
                    ((ps, genre) -> {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genre.getId());
                    }));
        } else {
            film.setGenres(new LinkedHashSet<>());
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();

        String genreIdString = resultSet.getString("genre_id");
        String genreNameString = resultSet.getString("genre_name");

        if (genreIdString != null && genreNameString != null &&
                !genreIdString.isEmpty() && !genreNameString.isEmpty()) {

            String[] genreIds = genreIdString.split(", ");
            String[] genreNames = genreNameString.split(", ");

            for (int j = 0; j < genreIds.length; j++) {
                if (j < genreNames.length) {
                    int genreId = Integer.parseInt(genreIds[j]);
                    String genreName = genreNames[j];
                    genres.add(new Genre(genreId, genreName));
                }
            }
        }

        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new MpaRating(resultSet.getInt("mpa_rating"), resultSet.getString("mpa_name")))
                .genres(genres)
                .build();
    }
}
