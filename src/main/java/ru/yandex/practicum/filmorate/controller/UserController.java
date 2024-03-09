package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    private long idCounter;

    private long generateId(){
        return ++idCounter;
    }

    @GetMapping
    public Collection<User> getAllUsers(){
        return users.values();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user){
        user.setId(generateId());
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Обработан POST запрос. Пользователь {} с id={} успешно добавлен", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isBlank()){
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Обработан PUT-запрос. Пользователь {} с id={} успешно обновлён", user.getName(), user.getId());
        } else {
            throw new ResourceNotFoundException("Пользователь с таким id не найден");
        }
        return user;
    }

}
