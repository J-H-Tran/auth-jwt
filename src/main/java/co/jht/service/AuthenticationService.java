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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static co.jht.model.enums.UserRole.ROLE_ADMIN;
import static co.jht.model.enums.UserRole.ROLE_GUEST;
import static co.jht.model.enums.UserRole.ROLE_MODERATOR;
import static co.jht.model.enums.UserRole.ROLE_USER;
import static co.jht.util.AuthUtil.isUserAuthenticated;

@Service
public class AuthenticationService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserRepository userRepository;

    public AuthenticationService(
            AuthenticationManager authManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserDetailsServiceImpl userDetailsService,
            UserRepository userRepository
    ) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    public AuthenticationResponse register(RegisterUserDto dto) {
        User user = userMapper.registerUserDtoToUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(getUserRole(user.getEmail()));
        User savedUser = userRepository.save(user);

        // Authenticate user right away for smooth user experience
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        setSecurityContext(userDetails);
        return new AuthenticationResponse(
                userMapper.userToRegisterUserDto(savedUser),
                jwtService.generateToken(userDetails)
        );
    }



    public String authenticate(LoginUserDto dto) {
        User user = userMapper.loginUserDtoToUser(dto);

        if (!isUserAuthenticated(authManager, userRepository, user)) return null;

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        setSecurityContext(userDetails);
        return jwtService.generateToken(userDetails);
    }

    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            jwtService.blacklistToken(jwt);
            SecurityContextHolder.clearContext();
            return "200";
        }
        return null;
    }

    private UserRole getUserRole(String email) {
        return switch (email.substring(email.indexOf('@'))) {
            case "@tda.com" -> ROLE_ADMIN;
            case "@tdamod.com" -> ROLE_MODERATOR;
            case "@guest.com" -> ROLE_GUEST;
            default -> ROLE_USER;
        };
    }

    private void setSecurityContext(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}