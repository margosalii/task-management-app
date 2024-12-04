package app.task.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import app.task.management.dto.comment.RequestCommentDto;
import app.task.management.dto.comment.ResponseCommentDto;
import app.task.management.mapper.CommentMapper;
import app.task.management.model.Comment;
import app.task.management.model.Task;
import app.task.management.model.User;
import app.task.management.repository.CommentRepository;
import app.task.management.repository.TaskRepository;
import app.task.management.repository.UserRepository;
import app.task.management.service.impl.CommentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final Long ID = 1L;
    private static Comment comment;
    private static RequestCommentDto requestCommentDto;
    private static ResponseCommentDto responseCommentDto;
    private static User user;
    private static Task task;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeAll
    static void setUp() {
        user = new User();
        user.setId(ID);

        task = new Task();
        task.setId(ID);

        requestCommentDto = new RequestCommentDto();
        requestCommentDto.setTaskId(task.getId());
        requestCommentDto.setText("New comment");

        comment = new Comment();
        comment.setId(ID);
        comment.setTask(task);
        comment.setUser(user);
        comment.setText(requestCommentDto.getText());
        comment.setTimestamp(LocalDateTime.now());

        responseCommentDto = new ResponseCommentDto();
        responseCommentDto.setId(comment.getId());
        responseCommentDto.setText(comment.getText());
        responseCommentDto.setTimestamp(comment.getTimestamp());
    }

    @Test
    void saveComment_validRequest_ok() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(commentMapper.toModel(requestCommentDto)).thenReturn(comment);
        when(taskRepository.findById(requestCommentDto.getTaskId())).thenReturn(Optional.of(task));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(responseCommentDto);

        ResponseCommentDto saved = commentService.save(user.getId(), requestCommentDto);

        assertEquals(responseCommentDto, saved);
    }

    @Test
    void saveComment_invalidRequest_exceptionOk() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(commentMapper.toModel(requestCommentDto)).thenReturn(comment);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> commentService.save(ID, requestCommentDto));
    }

    @Test
    void getCommentsByTaskId_validRequest_ok() {
        when(commentRepository.findAllByUserIdAndTaskId(ID, ID)).thenReturn(Set.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(responseCommentDto);

        Set<ResponseCommentDto> actual = commentService.getCommentsByTaskId(ID, ID);

        assertEquals(Set.of(responseCommentDto), actual);
    }

}
