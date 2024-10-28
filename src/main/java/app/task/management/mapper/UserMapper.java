package app.task.management.mapper;

import app.task.management.config.MapperConfig;
import app.task.management.dto.user.UserDto;
import app.task.management.dto.user.UserRegistrationDto;
import app.task.management.dto.user.UserUpdateDto;
import app.task.management.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    void updateUserFromDto(UserUpdateDto updateDto, @MappingTarget User user);

    User toModel(UserRegistrationDto registrationDto);
}
