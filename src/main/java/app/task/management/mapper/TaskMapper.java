package app.task.management.mapper;

import app.task.management.config.MapperConfig;
import app.task.management.dto.task.CreateTaskRequestDto;
import app.task.management.dto.task.TaskDetailsDto;
import app.task.management.dto.task.TaskResponseDto;
import app.task.management.dto.task.UpdateTaskDto;
import app.task.management.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {
    Task toModel(CreateTaskRequestDto requestDto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    TaskDetailsDto toDetailsDto(Task task);

    @Mapping(target = "projectId", source = "project.id")
    TaskResponseDto toResponseDto(Task task);

    void updateFromDto(UpdateTaskDto updateTaskDto, @MappingTarget Task task);
}
