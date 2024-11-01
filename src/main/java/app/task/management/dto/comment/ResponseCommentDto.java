package app.task.management.dto.comment;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCommentDto {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
}
