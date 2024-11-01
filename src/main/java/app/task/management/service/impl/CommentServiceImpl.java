package app.task.management.service.impl;

import app.task.management.dto.comment.RequestCommentDto;
import app.task.management.dto.comment.ResponseCommentDto;
import app.task.management.mapper.CommentMapper;
import app.task.management.model.Comment;
import app.task.management.model.Task;
import app.task.management.model.User;
import app.task.management.repository.CommentRepository;
import app.task.management.repository.TaskRepository;
import app.task.management.repository.UserRepository;
import app.task.management.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public ResponseCommentDto save(Long userId, RequestCommentDto commentDto) {
        User user = userRepository.findById(userId).get();
        Comment comment = commentMapper.toModel(commentDto);
        Task task = taskRepository.findById(commentDto.getTaskId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find task by id: "
                    + commentDto.getTaskId())
        );
        comment.setTask(task);
        comment.setUser(user);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public Set<ResponseCommentDto> getCommentsByTaskId(Long userId, Long taskId) {
        return commentRepository.findAllByUserIdAndTaskId(userId, taskId)
            .stream()
            .map(commentMapper::toDto)
            .collect(Collectors.toSet());
    }
}
