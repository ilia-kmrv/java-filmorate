package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("UserDbStorage")
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long userGeneratedId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(userGeneratedId);
        log.info("Пользователь {} c id={} успешно добавлен", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToUser(rs, rowNum), id).stream().findFirst();
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public boolean deleteUser(long userId) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, userId) > 0;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    public User addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friendship (user_id, friend_id, status)" +
                "VALUES (?, ?, ?)" +
                "ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sqlQuery, userId, friendId, true);
        return null;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}

