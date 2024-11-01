package app.task.management.mapper;

import app.task.management.config.MapperConfig;
import app.task.management.dto.label.CreateLabelDto;
import app.task.management.dto.label.LabelDto;
import app.task.management.dto.label.LabelResponseDto;
import app.task.management.dto.label.UpdateLabelDto;
import app.task.management.model.Label;
import app.task.management.model.Task;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    LabelResponseDto toDto(Label label);

    LabelDto toDtoWithoutIds(Label label);

    Label toModel(CreateLabelDto requestDto);

    void updateFromDto(UpdateLabelDto requestDto, @MappingTarget Label label);

    @AfterMapping
    default void setTaskIds(@MappingTarget LabelResponseDto labelDto, Label label) {
        Set<Long> ids = label.getTasks()
                .stream().map(Task::getId)
                .collect(Collectors.toSet());
        labelDto.setTaskIds(ids);
    }

    @AfterMapping
    default void setTasks(@MappingTarget Label label, CreateLabelDto labelDto) {
        if (labelDto.getTaskIds() != null) {
            label.setTasks(labelDto
                    .getTaskIds()
                    .stream()
                    .map(Task::new)
                    .collect(Collectors.toSet()));
        }
    }
}
