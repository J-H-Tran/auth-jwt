package co.jht.model;

import co.jht.model.dto.LoginUserDto;
import co.jht.model.dto.RegisterUserDto;
import co.jht.model.dto.UserDto;
import co.jht.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    LoginUserDto userToLoginUserDto(User user);
    RegisterUserDto userToRegisterUserDto(User user);
    List<UserDto> usersToDtoUsers(List<User> users);
    User loginUserDtoToUser(LoginUserDto dto);
    User registerUserDtoToUser(RegisterUserDto dto);
}