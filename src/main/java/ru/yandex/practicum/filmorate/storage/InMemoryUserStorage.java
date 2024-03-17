package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    // TODO: choose collection as storage
    private final Map<Long, User> users = new HashMap<>();

    private long idCounter;

    private long generateId() {
        return ++idCounter;
    }

    // TODO: implement adding user to storage
    @Override
    public User addUser(User user) {
        user.setId(generateId());
        setUserNameToLoginIfNotProvided(user);
        users.put(user.getId(), user);
        log.info("Пользователь {} c id={} успешно добавлен", user.getLogin(), user.getId());

        return user;
    }

    // TODO: implement deleting user from storage
    @Override
    public void deleteUser(long userId) {
        if (users.containsKey(userId)){
            users.remove(userId);
            log.info("Пользователь {} c id={} успешно удалён", users.get(userId).getLogin(), userId);
        } else {
            throw new ResourceNotFoundException("Пользователь с таким id не найден");
        }
    }

    // TODO: implement updating a user in storagemethod
    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            setUserNameToLoginIfNotProvided(user);
            users.put(user.getId(), user);
            log.info("Пользователь {} с id={} успешно обновлён", user.getLogin(), user.getId());
        } else {
            throw new ResourceNotFoundException("Пользователь с таким id не найден");
        }
        return user;
    }

    // TODO: method to get list of users
    public List<User> getAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    // если явно не указано имя пользователя присваивает значения логина
    private void setUserNameToLoginIfNotProvided(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("В имя пользователя с id={} записан логин {}", user.getId(), user.getLogin());
        }
    }
}
