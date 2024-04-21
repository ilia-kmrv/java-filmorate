package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre create(Genre genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genres")
                .usingGeneratedKeyColumns("id");
        int genreGeneratedId = simpleJdbcInsert.executeAndReturnKey(genre.toMap()).intValue();
        genre.setId(genreGeneratedId);
        log.info("Жанр {} c id={} успешно добавлен", genre.getName(), genre.getId());
        return genre;
    }

    @Override
    public Optional<Genre> get(Long id) {
        log.info("Получение жанра с id={}", id);
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id).stream().findFirst();
    }

    @Override
    public Genre update(Genre genre) {
        String sqlQuery = "UPDATE genres SET name = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                genre.getName(),
                genre.getId());
        return genre;
    }

    @Override
    public boolean delete(Long genreId) {
        String sqlQuery = "DELETE FROM genres WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, genreId) > 0;
    }

    @Override
    public Collection<Genre> getAll() {
        log.info("Получение всех жанров");
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
