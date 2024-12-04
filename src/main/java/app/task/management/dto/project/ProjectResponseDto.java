package app.task.management.dto.project;

import app.task.management.model.Status;
import lombok.Data;

@Data
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Status status;
}
