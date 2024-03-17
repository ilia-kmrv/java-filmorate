package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    // добавление пользователя
    User addUser(User user);

    // удаление пользователя по id
    void deleteUser(long userId);

    // обновление пользователя
    User updateUser(User user);

    List<User> getAllUsers();
}
