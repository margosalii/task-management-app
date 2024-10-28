package app.task.management.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(@NotBlank String username,
                                  @NotBlank String password) {
}
