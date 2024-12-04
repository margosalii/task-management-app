package app.task.management.dto.task;

import app.task.management.model.Status;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TaskResponseDto {
    private Long id;
    private String description;
    private Status status;
    private LocalDate dueDate;
    private Long projectId;
}
