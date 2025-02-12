package co.jht.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponse {
    @JsonProperty("user")
    Object user;
    @JsonProperty("token")
    String token;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(Object user, String token) {
        this.user = user;
        this.token = "Bearer " + token;
    }
}