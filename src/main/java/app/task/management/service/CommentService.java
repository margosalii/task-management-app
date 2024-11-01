package app.task.management.service;

import app.task.management.dto.comment.RequestCommentDto;
import app.task.management.dto.comment.ResponseCommentDto;
import java.util.Set;

public interface CommentService {
    ResponseCommentDto save(Long userId, RequestCommentDto commentDto);

    Set<ResponseCommentDto> getCommentsByTaskId(Long userId, Long taskId);
}
