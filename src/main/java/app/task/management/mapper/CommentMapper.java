package app.task.management.mapper;

import app.task.management.config.MapperConfig;
import app.task.management.dto.comment.RequestCommentDto;
import app.task.management.dto.comment.ResponseCommentDto;
import app.task.management.model.Comment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    ResponseCommentDto toDto(Comment comment);

    Comment toModel(RequestCommentDto commentDto);
}
