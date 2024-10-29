package app.task.management.repository;

import app.task.management.model.Project;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Set<Project> findAllByUserId(Long userId);

    Optional<Project> findByUserIdAndId(Long userId, Long id);
}
