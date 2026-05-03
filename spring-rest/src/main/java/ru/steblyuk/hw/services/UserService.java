package ru.steblyuk.hw.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.steblyuk.hw.models.User;

public interface UserService {
    User getByLogin(String login);
    User getCurrentUser();
    UserDetailsService getDetailsService();
    User create(User user);
    User save(User user);
}
