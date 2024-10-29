package app.task.management.dto.project;

import app.task.management.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Status status;
}
