package app.task.management.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCommentDto {
    @Positive
    @NotNull
    private Long taskId;
    @NotBlank
    private String text;
}
