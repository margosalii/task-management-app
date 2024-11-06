package app.task.management.repository;

import app.task.management.model.Attachment;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Set<Attachment> findAllByTaskId(Long id);

    Optional<Attachment> findByDropboxFileId(String id);
}
