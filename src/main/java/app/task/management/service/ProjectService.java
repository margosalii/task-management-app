package app.task.management.service;

import app.task.management.dto.project.CreateProjectDto;
import app.task.management.dto.project.ProjectDetailsResponseDto;
import app.task.management.dto.project.ProjectResponseDto;
import app.task.management.dto.project.UpdateRequestProjectDto;
import java.util.Set;

public interface ProjectService {
    ProjectDetailsResponseDto save(Long userId, CreateProjectDto projectDto);

    Set<ProjectResponseDto> getUsersProjects(Long userId);

    ProjectDetailsResponseDto getProjectDetails(Long userId, Long id);

    ProjectDetailsResponseDto update(Long userId, Long id, UpdateRequestProjectDto projectDto);

    void delete(Long userId, Long id);
}
