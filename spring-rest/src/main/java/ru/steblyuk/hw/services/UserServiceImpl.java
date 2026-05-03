package ru.steblyuk.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.steblyuk.hw.models.User;
import ru.steblyuk.hw.repositories.UserRepository;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User is not exist"));
    }

    @Override
    public User getCurrentUser() {
        var login = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByLogin(login);
    }

    @Override
    public UserDetailsService getDetailsService() {
        return this::getByLogin;
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByLogin(user.getUsername())) {
            throw new RuntimeException("User with the same login is already exist!");
        }

        return save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
