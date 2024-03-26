package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserStorage userStorage;

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public void deleteUser(long userId) {
        getUserById(userId);
        userStorage.deleteUser(userId);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    // получение пользователя по id. бросает исключение если в хранилище нет фильма с таким id
    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    public User addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
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
}
