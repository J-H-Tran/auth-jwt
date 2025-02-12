package co.jht.service;

import co.jht.model.AuthenticationResponse;
import co.jht.model.CustomUserDetails;
import co.jht.model.UserMapper;
import co.jht.model.dto.LoginUserDto;
import co.jht.model.dto.RegisterUserDto;
import co.jht.model.entity.User;
import co.jht.model.enums.UserRole;
import co.jht.repository.UserRepository;
import co.jht.service.impl.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static co.jht.model.enums.UserRole.ADMIN;
import static co.jht.model.enums.UserRole.GUEST;
import static co.jht.model.enums.UserRole.MODERATOR;
import static co.jht.model.enums.UserRole.USER;

@Service
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public AuthenticationService(AuthenticationManager authManager,
                                 UserDetailsServiceImpl userDetailsService,
                                 UserRepository userRepository,
                                 JwtService jwtService, PasswordEncoder passwordEncoder
    ) {
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationResponse register(RegisterUserDto dto) {
        User user = userMapper.registerUserDtoToUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(getUserRole(user.getEmail()));

        User savedUser = userRepository.save(user);

        // Authenticate user right away for smooth user experience
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        setSecurityContext(userDetails, user.getPassword());
        return new AuthenticationResponse(
                userMapper.userToRegisterUserDto(savedUser),
                jwtService.generateToken(userDetails));
    }



    public String authenticate(LoginUserDto dto) {
        User user = userMapper.loginUserDtoToUser(dto);
        User savedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        if (savedUser == null) {
            return null;
        }
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
                )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Error occurred: " + e.getMessage());
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        setSecurityContext(userDetails, user.getPassword());
        return jwtService.generateToken(userDetails);
    }

    private UserRole getUserRole(String email) {
        return switch (email.substring(email.indexOf('@'))) {
            case "@tda.com" -> ADMIN;
            case "@tdamod.com" -> MODERATOR;
            case "@guest.com" -> GUEST;
            default -> USER;
        };
    }

    private void setSecurityContext(UserDetails userDetails, String password) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, password, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}