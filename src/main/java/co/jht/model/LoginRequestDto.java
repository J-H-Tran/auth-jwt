package co.jht.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
}