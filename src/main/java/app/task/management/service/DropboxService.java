package app.task.management.service;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface DropboxService {
    String uploadFile(MultipartFile file) throws IOException;

    Resource getFile(String dropboxFileId);

    void delete(String dropboxFileId);
}
