package app.task.management.dto.comment;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ResponseCommentDto {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
}
