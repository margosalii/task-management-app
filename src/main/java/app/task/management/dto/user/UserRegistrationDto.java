package app.task.management.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String email;
    private String password;
    private String repeatPassword;
    private String username;
    private String firstName;
    private String lastName;
}
