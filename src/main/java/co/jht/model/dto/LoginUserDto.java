package co.jht.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LoginUserDto {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
}