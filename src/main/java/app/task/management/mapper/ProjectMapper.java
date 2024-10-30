package app.task.management.mapper;

import app.task.management.config.MapperConfig;
import app.task.management.dto.project.CreateProjectDto;
import app.task.management.dto.project.ProjectDetailsResponseDto;
import app.task.management.dto.project.ProjectResponseDto;
import app.task.management.dto.project.UpdateRequestProjectDto;
import app.task.management.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = TaskMapper.class)
public interface ProjectMapper {
    ProjectDetailsResponseDto toProjectDetails(Project project);

    ProjectResponseDto toResponseDto(Project project);

    Project toModel(CreateProjectDto projectDto);

    void updateProjectFromDto(UpdateRequestProjectDto projectDto, @MappingTarget Project project);
}
