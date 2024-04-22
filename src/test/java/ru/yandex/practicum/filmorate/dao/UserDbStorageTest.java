package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.model.Film.FIRST_FILM_DATE;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    EasyRandomParameters easyRandomParameters;
    EasyRandom easyRandom;

    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);

        easyRandomParameters = new EasyRandomParameters();
        easyRandomParameters.seed(12L);
        easyRandomParameters.charset(Charset.forName("UTF-8"));
        easyRandomParameters.collectionSizeRange(1, 10);
        easyRandomParameters.dateRange(LocalDate.parse(FIRST_FILM_DATE), LocalDate.now());
        easyRandomParameters.overrideDefaultInitialization(true);

        easyRandom = new EasyRandom(easyRandomParameters);
    }

    @Test
    void testGetUserById() {
        User newUser = easyRandom.nextObject(User.class);
        userStorage.create(newUser);

        Optional<User> savedUser = userStorage.get(newUser.getId());

        assertThat(savedUser.isPresent() ? savedUser.get() : easyRandom.nextObject(User.class))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    void testUpdateUser() {
        User user = userStorage.create(easyRandom.nextObject(User.class));
        User userForUpdating = easyRandom.nextObject(User.class);
        User updatedUser = userStorage.update(userForUpdating);

        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userForUpdating);
    }

    @Test
    void testDeleteUser() {
        User firstUser = userStorage.create(easyRandom.nextObject(User.class));
        User secondUser = userStorage.create(easyRandom.nextObject(User.class));

        userStorage.addFriend(firstUser, secondUser);

        assertEquals(1, userStorage.getAllFriends(firstUser).size());

        userStorage.delete(secondUser.getId());

        assertEquals(0, userStorage.getAllFriends(firstUser).size());
    }

    @Test
    void testGetAllUsers() {
        User firstUser = userStorage.create(easyRandom.nextObject(User.class));
        User secondUser = userStorage.create(easyRandom.nextObject(User.class));

        Collection<User> users = userStorage.getAll();

        assertEquals(2, users.size());
        assertTrue(users.contains(firstUser));
        assertTrue(users.contains(secondUser));
    }

    @Test
    void testGetAllFriends() {
        User firstUser = userStorage.create(easyRandom.nextObject(User.class));
        User secondUser = userStorage.create(easyRandom.nextObject(User.class));

        userStorage.addFriend(firstUser, secondUser);

        assertTrue(userStorage.getAllFriends(firstUser).contains(secondUser));
        assertFalse(userStorage.getAllFriends(secondUser).contains(firstUser));

        userStorage.addFriend(secondUser, firstUser);

        assertTrue(userStorage.getAllFriends(secondUser).contains(firstUser));
    }
}