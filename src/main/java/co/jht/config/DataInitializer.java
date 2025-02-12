package co.jht.config;

import co.jht.model.entity.User;
import co.jht.model.enums.UserRole;
import co.jht.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static co.jht.model.enums.UserRole.ROLE_ADMIN;
import static co.jht.model.enums.UserRole.ROLE_USER;

@Configuration
public class DataInitializer {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public DataInitializer(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            createUser("admin", "admin", "test@admin.com", ROLE_ADMIN);
            createUser("user", "user", "test@user.com", ROLE_USER);
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