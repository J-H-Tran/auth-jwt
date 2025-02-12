package co.jht.util;

import co.jht.model.entity.User;
import co.jht.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthUtil {
    private static final Logger logger = LoggerFactory.getLogger(AuthUtil.class);

    public static boolean isUserAuthenticated(
            AuthenticationManager authManager,
            UserRepository userRepository,
            User user
    ) {
        User savedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        if (savedUser == null) {
            logger.info("User not found, unable to authenticate the user");
            return false;
        }
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            logger.error("Error occurred trying to authenticate the user");
            throw new RuntimeException("Error occurred: " + e.getMessage());
        }
        return true;
    }
}