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
    public User create(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("Пользователь {} c id={} успешно добавлен", user.getLogin(), user.getId());

        return user;
    }

    public boolean delete(Long userId) {
        users.remove(userId);
        log.debug("Пользователь c id={} успешно удалён", userId);
        return false;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь {} с id={} успешно обновлён", user.getLogin(), user.getId());
        return user;
    }

    public List<User> getAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User addFriend(User user, User friend) {

        return null;
    }

    @Override
    public User deleteFriend(User user, User friendId) {
        return null;
    }

    @Override
    public List<User> getAllFriends(User user) {
        return null;
    }
}
