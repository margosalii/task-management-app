package app.task.management.service.impl;

import app.task.management.dto.project.CreateProjectDto;
import app.task.management.dto.project.ProjectDetailsResponseDto;
import app.task.management.dto.project.ProjectResponseDto;
import app.task.management.dto.project.UpdateRequestProjectDto;
import app.task.management.mapper.ProjectMapper;
import app.task.management.model.Project;
import app.task.management.repository.ProjectRepository;
import app.task.management.repository.UserRepository;
import app.task.management.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    @Override
    public ProjectDetailsResponseDto save(Long userId, CreateProjectDto projectDto) {
        Project project = projectMapper.toModel(projectDto);
        project.setUser(userRepository.findById(userId).get());
        return projectMapper.toProjectDetails(projectRepository.save(project));
    }

    @Override
    public Set<ProjectResponseDto> getUsersProjects(Long userId) {
        return projectRepository.findAllByUserId(userId)
            .stream()
            .map(projectMapper::toResponseDto)
            .collect(Collectors.toSet());
    }

    @Override
    public ProjectDetailsResponseDto getProjectDetails(Long userId, Long id) {
        return projectMapper.toProjectDetails(projectRepository
            .findByUserIdAndId(userId, id).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                "Can't find project by user id: %s and project id: %s", userId, id))));
    }

    @Override
    public ProjectDetailsResponseDto update(Long userId, Long id,
                                            UpdateRequestProjectDto projectDto) {
        Project project = projectRepository.findByUserIdAndId(userId, id).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                    "Can't find project by user id: %s and project id: %s", userId, id)));
        projectMapper.updateProjectFromDto(projectDto, project);
        return projectMapper.toProjectDetails(projectRepository.save(project));
    }

    @Override
    public void delete(Long userId, Long id) {
        Project project = projectRepository.findByUserIdAndId(userId, id).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                    "Can't find project by user id: %s and project id: %s", userId, id)));
        projectRepository.deleteById(project.getId());
    }
}
