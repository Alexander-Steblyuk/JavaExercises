package ru.steblyuk.hw.services;

import ru.steblyuk.hw.models.User;

public interface UserService {
    User getByLogin(String login);
    User save(User user);
}
