package co.jht.controller;

import co.jht.model.LoginRequestDto;
import co.jht.service.CustomUserDetailsService;
import co.jht.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private AuthenticationManager authManager;
    private JwtService jwtService;
    private CustomUserDetailsService userDetailsService;

    public AuthenticationController(
            AuthenticationManager authManager,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(
            @RequestBody LoginRequestDto request
    ) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getUsername());

        final String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwt)
                .build();
    }
}