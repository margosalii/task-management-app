package app.task.management.service.impl;

import app.task.management.dto.user.UserDto;
import app.task.management.dto.user.UserRegistrationDto;
import app.task.management.dto.user.UserRoleUpdateDto;
import app.task.management.dto.user.UserUpdateDto;
import app.task.management.exceptions.RegistrationException;
import app.task.management.mapper.UserMapper;
import app.task.management.model.Role;
import app.task.management.model.RoleName;
import app.task.management.model.User;
import app.task.management.repository.RoleRepository;
import app.task.management.repository.UserRepository;
import app.task.management.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDto getUserInfo(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by ID: " + id)
        );
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserInfo(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by ID: " + id)
        );
        userMapper.updateUserFromDto(updateDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void updateUserRole(Long id, UserRoleUpdateDto updateDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by ID: " + id)
        );
        Role role = roleRepository.findByName(updateDto.getRole());
        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.add(role);
        userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto register(UserRegistrationDto registrationDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RegistrationException("User with such email already exists.");
        }
        User user = userMapper.toModel(registrationDto);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName(RoleName.ROLE_USER)));
        return userMapper.toDto(userRepository.save(user));
    }
}
