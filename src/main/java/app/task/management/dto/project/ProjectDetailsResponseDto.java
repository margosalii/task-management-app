package app.task.management.dto.project;

import app.task.management.model.Status;
import app.task.management.model.Task;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDetailsResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private Set<Task> tasks;
}
