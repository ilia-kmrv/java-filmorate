package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserStorage userStorage;
    UserService userService;
    User user;
    User friend;


    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);

        user = User.builder()
                .name("Leonard")
                .email("unknown@mail.com")
                .login("leo")
                .birthday(LocalDate.of(1990, 12, 25))
                .build();

        friend = User.builder()
                .name("Frida")
                .email("unknown-friend@mail.com")
                .login("frida")
                .birthday(LocalDate.of(1991, 04, 15))
                .build();

        userService.addUser(user);
        userService.addUser(friend);

    }

    @Test
    void addUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    @DisplayName("Проверка сервиса на добавление в друзья")
    void addFriendShouldThrowExceptionIfIdNotFound() {
        userService.addFriend(user.getId(), friend.getId());

        assertEquals(user.getFriends(), userService.getUserById(user.getId()).getFriends());

        user.setId(Long.MAX_VALUE);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.addFriend(user.getId(), friend.getId()));
        assertEquals( "Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка удаления из друзей")
    void deleteFriendTestShouldThrowExceptionIfIdNotFound() {
        userService.addFriend(user.getId(), friend.getId());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteFriend(user.getId(), Long.MAX_VALUE));
        assertEquals("Пользователь не найден", e.getMessage());

        userService.deleteFriend(user.getId(), friend.getId());
        assertEquals(user.getFriends().isEmpty(), friend.getFriends().isEmpty());
    }

    @Test
    @DisplayName("Проверка получения списка всех друзей")
    void getAllFriendsTest() {
        userService.addFriend(user.getId(), friend.getId());

        List<Long> expectedList = Collections.singletonList(friend.getId());

        assertEquals(expectedList, userService.getAllFriends(user.getId()), "Списки друзей не совпали");
    }

    @Test
    @DisplayName("Проверка получения пользователя по id")
    void getUserByIdTestShouldReturnUserOrThrowException() {
        assertTrue(user.equals(userService.getUserById(user.getId())), "Пользователи не совпали");

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(Long.MAX_VALUE));

        assertTrue(String.format("Пользователь с id=%d не найден", Long.MAX_VALUE).equals(e.getMessage()));
    }
}