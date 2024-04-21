package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends Storage<User> {
    User addFriend(User user, User friend);

    User deleteFriend(User user, User friend);

    List<User> getAllFriends(User user);
}
