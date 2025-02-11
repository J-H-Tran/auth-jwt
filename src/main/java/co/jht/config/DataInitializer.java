package co.jht.config;

import co.jht.model.User;
import co.jht.model.UserRole;
import co.jht.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static co.jht.model.UserRole.ADMIN;
import static co.jht.model.UserRole.USER;

@Configuration
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            createUser("admin", "admin", "test@admin.com", ADMIN);
            createUser("user", "user", "test@user.com", USER);
        }
    }

    private void createUser(
            String username,
            String password,
            String email,
            UserRole role
    ) {
        User user = new User(username, passwordEncoder.encode(password), email, role);
        userRepository.save(user);
    }
}