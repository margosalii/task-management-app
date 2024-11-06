package app.task.management.service;

import app.task.management.dto.attachment.AttachmentDto;
import java.io.IOException;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentDto upload(MultipartFile file, Long taskId, Long userId) throws IOException;

    Set<AttachmentDto> getAllByTaskId(Long taskId, Long userId);

    Resource getByDropboxFileId(String id, Long userId);

    void delete(Long id, Long userId);
}
