package co.jht.model;

import co.jht.model.dto.LoginUserDto;
import co.jht.model.dto.RegisterUserDto;
import co.jht.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    LoginUserDto userToLoginUserDto(User user);
    RegisterUserDto userToRegisterUserDto(User user);
    User loginUserDtoToUser(LoginUserDto dto);
    User registerUserDtoToUser(RegisterUserDto dto);
}