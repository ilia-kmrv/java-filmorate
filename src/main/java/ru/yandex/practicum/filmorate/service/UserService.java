package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    public User addUser(User user) {
        setUserNameToLoginIfNotProvided(user);
        return userStorage.addUser(user);
    }

    // получение пользователя по id. бросает исключение если в хранилище нет пользователя с таким id
    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        setUserNameToLoginIfNotProvided(user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(long userId) {
        getUserById(userId);
        userStorage.deleteUser(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        log.info("Пользователю id={} добавлен в друзья id={}", userId, friendId);
        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        getUserById(userId).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(userId);
        log.info("Пользователю id={} удалён из друзей id={}", userId, friendId);
        return getUserById(userId);
    }

    public List<User> getAllFriends(Long userId) {
        return getUserById(userId).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    // получение списка общих друзей
    public List<User> getCommonFriends(Long userId, Long friendId) {
        HashSet<Long> commonFriendsIds = new HashSet<>(getUserById(userId).getFriends());

        commonFriendsIds.retainAll(getUserById(friendId).getFriends());
        return commonFriendsIds.stream().map(this::getUserById).collect(Collectors.toList());
    }

    // если явно не указано имя пользователя присваивает значения логина
    private void setUserNameToLoginIfNotProvided(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("В имя пользователя с id={} записан логин {}", user.getId(), user.getLogin());
        }
    }
}
