package app.task.management.controller;

import app.task.management.dto.user.UserDto;
import app.task.management.dto.user.UserLoginRequestDto;
import app.task.management.dto.user.UserLoginResponseDto;
import app.task.management.dto.user.UserRegistrationDto;
import app.task.management.exceptions.RegistrationException;
import app.task.management.security.AuthenticationService;
import app.task.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto loginRequestDto) {
        return authenticationService.authenticate(loginRequestDto);
    }

    @PostMapping("/registration")
    public UserDto registration(@RequestBody UserRegistrationDto registrationDto)
            throws RegistrationException {
        return userService.register(registrationDto);
    }
}
