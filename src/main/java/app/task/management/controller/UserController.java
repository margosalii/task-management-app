package app.task.management.controller;

import app.task.management.dto.user.UserDto;
import app.task.management.dto.user.UserRoleUpdateDto;
import app.task.management.dto.user.UserUpdateDto;
import app.task.management.model.User;
import app.task.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "User management", description = "Endpoints for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get profile info", description = "Get all information about user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/me")
    public UserDto getProfileInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return userService.getUserInfo(userId);
    }

    @Operation(summary = "Update profile info", description = "Update information about user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/me")
    public UserDto updateProfileInfo(@RequestBody @Valid UserUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return userService.updateUserInfo(userId, updateDto);
    }

    @Operation(summary = "Update user role", description = "Update user role (Only for admins)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public void updateUserRole(@Positive @PathVariable Long id,
                               @RequestBody @Valid UserRoleUpdateDto updateDto) {
        userService.updateUserRole(id, updateDto);
    }
}
