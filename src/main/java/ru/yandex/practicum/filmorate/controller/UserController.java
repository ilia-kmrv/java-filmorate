package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Обработан GET users запрос");
        return userService.getAllUsers();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        userService.addUser(user);
        log.info("Обработан POST user запрос.");
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        try {
            userService.updateUser(user);
            log.info("Обработан PUT user запрос.");
        } catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException(e.getMessage());
        }
        return user;
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        log.info("Обработан GET user запрос.");
        return userService.getUserById(userId);
    }

}
