package app.task.management.repository;

import app.task.management.model.Task;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Set<Task> findAllByAssigneeId(Long userId);

    Optional<Task> findByAssigneeIdAndId(Long userId, Long id);
}
