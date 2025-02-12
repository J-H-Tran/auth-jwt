package co.jht.controller;

import co.jht.model.AuthenticationResponse;
import co.jht.model.dto.LoginUserDto;
import co.jht.model.dto.RegisterUserDto;
import co.jht.model.entity.User;
import co.jht.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private AuthenticationService authService;

    public AuthenticationController(
            AuthenticationService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterUserDto dto
    ) {
        AuthenticationResponse response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Authorization", response.getToken())
                .body(response);
    }

    // TODO add endpoint for logout
    @PostMapping("/login")
    public ResponseEntity<String> authenticate(
            @RequestBody LoginUserDto request
    ) {
        final String jwt = authService.authenticate(request);

        if (jwt != null) {
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + jwt)
                    .build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

//    @PostMapping("/logout")
//    public ResponseEntity<String> logout() {
//        // implement logout
//    }
}