package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
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
        userStorage.deleteUser(userId);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователю id={} добавлен в друзья id={}", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
            getUserById(userId).getFriends().remove(friendId);
            getUserById(friendId).getFriends().remove(userId);
            log.info("Пользователю id={} удалён из друзей id={}", userId, friendId);
    }

    public List<Long> getAllFriends(Long userId) {
            return getUserById(userId).getFriends().stream().collect(Collectors.toList());
    }

    // TODO: add get common friends of two users
}
