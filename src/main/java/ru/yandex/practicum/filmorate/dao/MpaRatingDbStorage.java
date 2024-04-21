package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public MpaRating create(MpaRating mpaRating) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("mpa_ratings")
                .usingGeneratedKeyColumns("id");
        int mpaGeneratedId = simpleJdbcInsert.executeAndReturnKey(mpaRating.toMap()).intValue();
        mpaRating.setId(mpaGeneratedId);
        log.info("Рейтинг MPA {} c id={} успешно добавлен", mpaRating.getName(), mpaRating.getId());
        return mpaRating;
    }

    @Override
    public Optional<MpaRating> get(Long id) {
        log.info("Получение MPA рейтинга с id={}", id);
        String sqlQuery = "SELECT * FROM mpa_ratings WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpaRating, id).stream().findFirst();
    }

    @Override
    public MpaRating update(MpaRating mpaRating) {
        String sqlQuery = "UPDATE mpa_ratings SET name = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                mpaRating.getName(),
                mpaRating.getId());
        return mpaRating;
    }

    @Override
    public boolean delete(Long mpaRatingId) {
        String sqlQuery = "DELETE FROM mpa_ratings WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, mpaRatingId) > 0;
    }

    @Override
    public Collection<MpaRating> getAll() {
        log.info("Получение всех MPA рейтингов");
        String sqlQuery = "SELECT * FROM mpa_ratings";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpaRating);
    }

    private MpaRating mapRowToMpaRating(ResultSet resultSet, int i) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
