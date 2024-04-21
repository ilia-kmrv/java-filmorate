package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    // добавление пользователя
    User addUser(User user);

    // удаление пользователя по id
    boolean deleteUser(long userId);

    // обновление пользователя
    User updateUser(User user);

    // получение всех пользователей
    List<User> getAllUsers();

    // получение пользователя по id
    Optional<User> getUserById(Long id);

}
