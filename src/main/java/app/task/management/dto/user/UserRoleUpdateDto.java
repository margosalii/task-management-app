package app.task.management.dto.user;

import app.task.management.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleUpdateDto {
    private Role role;
}
