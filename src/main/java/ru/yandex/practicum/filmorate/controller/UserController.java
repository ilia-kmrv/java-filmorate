package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        log.debug("Обработан POST user запрос.");
        return userService.addUser(user);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        log.debug("Обработан GET user {} запрос.", userId);
        return userService.getUserById(userId);
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        log.debug("Обработан PUT user запрос.");
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.debug("Обработан DELETE user {} запрос.", userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Обработан GET users запрос");
        return userService.getAllUsers();
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.debug("Обработан PUT user {} friends запрос.", userId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.debug("Обработан DELETE user's friend {} запрос.", userId);
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable long userId) {
        log.debug("Обработан GET user {} friends запрос.", userId);
        return userService.getAllFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable long userId, @PathVariable long friendId) {
        log.debug("Обработан GET user {} common friends запрос.", userId);
        return userService.getCommonFriends(userId, friendId);
    }

}
