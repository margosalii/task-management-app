package app.task.management.dto.user;

import app.task.management.model.RoleName;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleUpdateDto {
    @NotBlank
    private RoleName role;
}
