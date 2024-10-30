package app.task.management.dto.task;

import app.task.management.model.Priority;
import app.task.management.model.Status;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskDto {
    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private Status status;

    @NotNull
    private LocalDate dueDate;
}
