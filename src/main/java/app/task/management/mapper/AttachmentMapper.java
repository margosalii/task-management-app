package app.task.management.mapper;

import app.task.management.config.MapperConfig;
import app.task.management.dto.attachment.AttachmentDto;
import app.task.management.model.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(target = "taskId", source = "task.id")
    AttachmentDto toDto(Attachment attachment);

    Attachment toModel(AttachmentDto attachmentDto);
}
