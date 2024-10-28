package app.task.management.dto.user;

import app.task.management.validation.Email;
import app.task.management.validation.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldMatch(first = "password", second = "repeatPassword")
public class UserRegistrationDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8)
    private String password;

    @NotBlank
    @Length(min = 8)
    private String repeatPassword;

    @NotBlank
    @Length(min = 4, max = 40)
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
