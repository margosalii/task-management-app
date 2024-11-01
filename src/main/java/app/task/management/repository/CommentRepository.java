package app.task.management.repository;

import app.task.management.model.Comment;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Set<Comment> findAllByUserIdAndTaskId(Long userId, Long taskId);
}
