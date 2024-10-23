package app.task.management.service;

import app.task.management.dto.user.UserDto;
import app.task.management.dto.user.UserRegistrationDto;
import app.task.management.dto.user.UserRoleUpdateDto;
import app.task.management.dto.user.UserUpdateDto;
import app.task.management.exceptions.RegistrationException;

public interface UserService {
    UserDto getUserInfo(Long id);

    UserDto updateUserInfo(Long id, UserUpdateDto updateDto);

    void updateUserRole(Long id, UserRoleUpdateDto updateDto);

    UserDto register(UserRegistrationDto registrationDto)
            throws RegistrationException;
}
