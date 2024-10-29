package app.task.management.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProjectDto {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
}
