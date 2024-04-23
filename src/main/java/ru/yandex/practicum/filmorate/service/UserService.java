package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    public User addUser(User user) {
        setUserNameToLoginIfNotProvided(user);
        return userStorage.create(user);
    }

    // получение пользователя по id. бросает исключение если в хранилище нет пользователя с таким id
    public User getUserById(Long id) {
        return userStorage.get(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        setUserNameToLoginIfNotProvided(user);
        return userStorage.update(user);
    }

    public void deleteUser(long userId) {
        getUserById(userId);
        userStorage.delete(userId);
    }

    public List<User> getAllUsers() {
        return (List<User>) userStorage.getAll();
    }

    public User addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.addFriend(user, friend);
        log.debug("Пользователю id={} добавлен в друзья id={}", userId, friendId);
        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.deleteFriend(user, friend);
        log.debug("Пользователю id={} удалён из друзей id={}", userId, friendId);
        return getUserById(userId);
    }

    public List<User> getAllFriends(Long userId) {
        User user = getUserById((userId));
        return userStorage.getAllFriends(user);
    }

    // получение списка общих друзей
    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        List<User> userFriends = userStorage.getAllFriends(user);
        List<User> friendFriends = userStorage.getAllFriends(friend);
        userFriends.retainAll(friendFriends);
        return userFriends;
    }

    // если явно не указано имя пользователя присваивает значения логина
    private void setUserNameToLoginIfNotProvided(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("В имя пользователя с id={} записан логин {}", user.getId(), user.getLogin());
        }
    }
}
