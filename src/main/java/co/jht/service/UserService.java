package co.jht.service;

import co.jht.model.CustomUserDetails;
import co.jht.model.UserMapper;
import co.jht.model.dto.UserDto;
import co.jht.model.entity.User;
import co.jht.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final JwtService jwtService;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserRepository userRepository;

    public UserService(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return List.of();
        }

        String jwt = authHeader.substring(7);
        if (jwtService.isTokenBlacklisted(jwt)) {
            return List.of();
        }

        String username = jwtService.extractUsername(jwt);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if (!jwtService.isValidToken(jwt, new CustomUserDetails(user))) {
            return List.of();
        }

        List<User> users = userRepository.findAll();
        return userMapper.usersToDtoUsers(users);
    }
}