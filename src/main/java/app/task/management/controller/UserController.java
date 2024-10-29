package app.task.management.controller;

import app.task.management.dto.user.UserDto;
import app.task.management.dto.user.UserRoleUpdateDto;
import app.task.management.dto.user.UserUpdateDto;
import app.task.management.model.User;
import app.task.management.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/me")
    public UserDto getProfileInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        return userService.getUserInfo(id);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/me")
    public UserDto updateProfileInfo(@RequestBody @Valid UserUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        return userService.updateUserInfo(id, updateDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public void updateUserRole(@Positive @PathVariable Long id,
                               @RequestBody @Valid UserRoleUpdateDto updateDto) {
        userService.updateUserRole(id, updateDto);
    }
}
