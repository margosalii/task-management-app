package app.task.management.dto.attachment;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentDto {
    private Long id;
    private Long taskId;
    private String dropboxFileId;
    private String fileName;
    private LocalDateTime uploadDate;
}
