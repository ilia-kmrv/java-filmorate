package ru.yandex.practicum.filmorate.service;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storages;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.model.Film.FIRST_FILM_DATE;

class UserServiceTest {

    UserStorage userStorage;
    UserService userService;
    User user;
    User friend;
    EasyRandomParameters easyRandomParameters;
    EasyRandom easyRandom;


    @BeforeEach
    void setUp() {
        userStorage = Storages.getDefaultUserStorage();
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


        easyRandomParameters = new EasyRandomParameters();
        easyRandomParameters.seed(12L);
        easyRandomParameters.charset(Charset.forName("UTF-8"));
        easyRandomParameters.collectionSizeRange(1, 10);
        easyRandomParameters.dateRange(LocalDate.parse(FIRST_FILM_DATE), LocalDate.now());
        easyRandomParameters.overrideDefaultInitialization(true);

        easyRandom = new EasyRandom(easyRandomParameters);

    }

    // TODO:
//    @Test
//    @DisplayName("Проверка сервиса на добавление в друзья")
//    void addFriendShouldThrowExceptionIfIdNotFound() {
//        userService.addFriend(user.getId(), friend.getId());
//
//        assertEquals(user.getFriends(), userService.getUserById(user.getId()).getFriends());
//
//        user.setId(Long.MAX_VALUE);
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                () -> userService.addFriend(user.getId(), friend.getId()));
//        assertEquals(String.format("Пользователь с id=%d не найден", Long.MAX_VALUE), exception.getMessage());
//    }

    //TODO:
//    @Test
//    void deleteFriendTestShouldThrowExceptionIfIdNotFound() {
//        userService.addFriend(user.getId(), friend.getId());
//
//        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
//                () -> userService.deleteFriend(user.getId(), Long.MAX_VALUE));
//        assertEquals(String.format("Пользователь с id=%d не найден", Long.MAX_VALUE), e.getMessage());
//
//        userService.deleteFriend(user.getId(), friend.getId());
//        assertEquals(user.getFriends().isEmpty(), friend.getFriends().isEmpty());
//    }

    @Test
    @DisplayName("Проверка получения списка всех друзей")
    void getAllFriendsTest() {
        userService.addFriend(user.getId(), friend.getId());

        List<User> expectedList = Collections.singletonList(friend);

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

    @Test
    @DisplayName("Проверка получения списка общих друзей")
    void getCommonFriendsTestShouldReturnCorrectListOfFriends() {
        User thirdFriend = easyRandom.nextObject(User.class);
        User fourthFriend = easyRandom.nextObject(User.class);
        User fifthFriend = easyRandom.nextObject(User.class);

        userService.addUser(thirdFriend);
        userService.addUser(fourthFriend);
        userService.addUser(fifthFriend);

        // добавляем друзей первому юзеру
        userService.addFriend(user.getId(), thirdFriend.getId());
        userService.addFriend(user.getId(), fourthFriend.getId());
        userService.addFriend(user.getId(), friend.getId());

        // добавляем друзей его другу
        userService.addFriend(friend.getId(), thirdFriend.getId());
        userService.addFriend(friend.getId(), fourthFriend.getId());
        userService.addFriend(friend.getId(), fifthFriend.getId());

        List<User> expectedList = List.of(thirdFriend, fourthFriend);

        userService.getCommonFriends(user.getId(), friend.getId()).stream()
                .map(u -> u.getId())
                .forEach(System.out::println);

        assertEquals(expectedList, userService.getCommonFriends(user.getId(), friend.getId()),
                "Списки общих друзей не совпали");

    }
}