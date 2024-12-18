package app.task.management.dto.task;

import app.task.management.dto.label.LabelDto;
import app.task.management.model.Priority;
import app.task.management.model.Status;
import java.time.LocalDate;
import java.util.Set;
import lombok.Data;

@Data
public class TaskDetailsDto {
    private Long id;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Long projectId;
    private Long assigneeId;
    private Set<LabelDto> labels;
}
