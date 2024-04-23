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
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long userGeneratedId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(userGeneratedId);
        log.debug("Пользователь {} c id={} успешно добавлен", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public Optional<User> get(Long id) {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id).stream().findFirst();
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    public boolean delete(Long userId) {
        if (get(userId).isPresent()) {
            String sqlQuery = "DELETE FROM users WHERE id = ?";
            return jdbcTemplate.update(sqlQuery, userId) > 0;
        }
        return false;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    public User addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO friendship (first_user_id, second_user_id, status)" +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId(), true);
        return null;
    }

    @Override
    public User deleteFriend(User user, User friend) {
        String sqlQuery = "DELETE FROM friendship WHERE first_user_id = ? AND second_user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
        return user;
    }

    @Override
    public List<User> getAllFriends(User user) {
        String sqlQuery = "SELECT u.* FROM friendship AS f " +
                "JOIN users AS u ON u.id = f.second_user_id WHERE f.first_user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, user.getId());
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}

