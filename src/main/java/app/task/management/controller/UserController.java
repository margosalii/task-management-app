package app.task.management.controller;

import app.task.management.dto.user.UserDto;
import app.task.management.dto.user.UserRoleUpdateDto;
import app.task.management.dto.user.UserUpdateDto;
import app.task.management.model.User;
import app.task.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
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
    public UserDto updateProfileInfo(@RequestBody UserUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        return userService.updateUserInfo(id, updateDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public void updateUserRole(@PathVariable Long id, @RequestBody UserRoleUpdateDto updateDto) {
        userService.updateUserRole(id, updateDto);
    }
}
