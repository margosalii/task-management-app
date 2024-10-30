package app.task.management.dto.label;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLabelDto {
    private String name;
    private String color;
    private Set<Long> taskIds;
}
