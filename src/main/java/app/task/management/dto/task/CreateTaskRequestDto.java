package app.task.management.dto.task;

import app.task.management.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequestDto {
    @NotBlank
    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    @Positive
    private Long projectId;
}
