package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private long idCounter;

    private long generateId() {
        return ++idCounter;
    }

    @Override
    public User addUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Пользователь {} c id={} успешно добавлен", user.getLogin(), user.getId());

        return user;
    }

    @Override
    public boolean deleteUser(long userId) {
        users.remove(userId);
        log.info("Пользователь c id={} успешно удалён", userId);
        return false;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь {} с id={} успешно обновлён", user.getLogin(), user.getId());
        return user;
    }

    public List<User> getAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

}
