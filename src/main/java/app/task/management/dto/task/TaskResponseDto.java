package app.task.management.dto.task;

import app.task.management.model.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponseDto {
    private Long id;
    private String description;
    private Status status;
    private LocalDate dueDate;
    private Long projectId;
}
