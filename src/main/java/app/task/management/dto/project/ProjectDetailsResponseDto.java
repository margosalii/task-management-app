package app.task.management.dto.project;

import app.task.management.dto.task.TaskResponseDto;
import app.task.management.model.Status;
import java.time.LocalDate;
import java.util.Set;
import lombok.Data;

@Data
public class ProjectDetailsResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private Set<TaskResponseDto> tasks;
}
