package app.task.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import app.task.management.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final Long ID = 1L;
    private static User user;
    private static UserDto userDto;
    private static UserRegistrationDto registrationDto;
    private static UserUpdateDto userUpdateDto;
    private static UserRoleUpdateDto roleUpdateDto;
    private static Role roleAdmin;
    private static Role roleUser;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeAll
    static void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("newuser@gmail.com");
        registrationDto.setPassword("password88");
        registrationDto.setRepeatPassword("password88");
        registrationDto.setUsername("newuser");
        registrationDto.setFirstName("FirstName");
        registrationDto.setLastName("LastName");

        roleUser = new Role();
        roleUser.setId(ID);
        roleUser.setName(RoleName.ROLE_USER);

        user = new User();
        user.setId(ID);
        user.setRoles(Set.of(roleUser));
        user.setProjects(new HashSet<>());
        user.setPassword(registrationDto.getPassword());
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());

        userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setLastName(user.getLastName());
        userDto.setFirstName(user.getFirstName());

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("Updated firstName");
        userUpdateDto.setLastName("Updated lastName");
        userUpdateDto.setUsername("Updated username");

        roleUpdateDto = new UserRoleUpdateDto();
        roleUpdateDto.setRole(RoleName.ROLE_ADMIN);

        roleAdmin = new Role();
        roleAdmin.setName(RoleName.ROLE_ADMIN);
    }

    @Test
    void getUserInfo_validId_ok() {
        when(userRepository.findById(ID)).thenReturn(Optional.ofNullable(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto userInfo = userService.getUserInfo(ID);

        assertEquals(userDto, userInfo);
    }

    @Test
    void getUserInfo_invalidId_exceptionOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserInfo(ID));
    }

    @Test
    void updateUserInfo_validRequest_ok() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto updated = userService.updateUserInfo(ID, userUpdateDto);

        assertEquals(userDto, updated);
    }

    @Test
    void updateUserInfo_invalidRequest_exceptionOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserInfo(ID, userUpdateDto));
    }

    @Test
    void register_validRequest_ok() throws RegistrationException {
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(registrationDto)).thenReturn(user);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(roleUser);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto registered = userService.register(registrationDto);

        assertEquals(userDto, registered);
    }

    @Test
    void register_invalidRequest_ExceptionOk() throws RegistrationException {
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(true);
        assertThrows(RegistrationException.class, () -> userService.register(registrationDto));
    }

    @Test
    void updateUser_validRequest_ok() throws RegistrationException {
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleUpdateDto.getRole())).thenReturn(roleAdmin);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        userService.updateUserRole(ID, roleUpdateDto);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_invalidRequest_exceptionOk() throws RegistrationException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRole(ID, roleUpdateDto));
    }
}
