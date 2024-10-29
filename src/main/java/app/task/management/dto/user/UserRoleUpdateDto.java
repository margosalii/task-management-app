package app.task.management.dto.user;

import app.task.management.model.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleUpdateDto {
    @NotNull
    private RoleName role;
}
