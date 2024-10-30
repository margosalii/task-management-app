package app.task.management.dto.label;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLabelDto {
    @NotBlank
    private String name;
    @NotBlank
    private String color;

    private Set<Long> taskIds;
}
