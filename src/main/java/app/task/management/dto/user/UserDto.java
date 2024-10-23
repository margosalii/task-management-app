package app.task.management.dto.user;

import app.task.management.model.Role;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
}
