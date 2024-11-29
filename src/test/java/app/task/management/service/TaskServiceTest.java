package app.task.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.task.management.dto.task.CreateTaskRequestDto;
import app.task.management.dto.task.TaskDetailsDto;
import app.task.management.dto.task.TaskResponseDto;
import app.task.management.dto.task.UpdateTaskDto;
import app.task.management.mapper.TaskMapper;
import app.task.management.model.Priority;
import app.task.management.model.Project;
import app.task.management.model.Status;
import app.task.management.model.Task;
import app.task.management.model.User;
import app.task.management.repository.ProjectRepository;
import app.task.management.repository.TaskRepository;
import app.task.management.service.impl.TaskServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    private static final Long ID = 1L;
    private static CreateTaskRequestDto createTaskRequestDto;
    private static TaskDetailsDto detailsDto;
    private static TaskResponseDto responseDto;
    private static UpdateTaskDto updateTaskDto;
    private static Task task;
    private static Project project;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeAll
    static void setUp() {
        project = new Project();
        project.setId(ID);

        User user = new User();
        user.setId(ID);

        createTaskRequestDto = new CreateTaskRequestDto();
        createTaskRequestDto.setProjectId(ID);
        createTaskRequestDto.setPriority(Priority.HIGH);
        createTaskRequestDto.setDescription("Task description");
        createTaskRequestDto.setDueDate(LocalDate.now());

        updateTaskDto = new UpdateTaskDto();
        updateTaskDto.setStatus(Status.IN_PROGRESS);
        updateTaskDto.setPriority(Priority.LOW);
        updateTaskDto.setDescription("Updated description");
        updateTaskDto.setDueDate(LocalDate.now());

        task = new Task();
        task.setId(ID);
        task.setProject(project);
        task.setAssignee(user);
        task.setPriority(createTaskRequestDto.getPriority());
        task.setDescription(createTaskRequestDto.getDescription());
        task.setDueDate(createTaskRequestDto.getDueDate());

        responseDto = new TaskResponseDto();
        responseDto.setId(ID);
        responseDto.setDueDate(task.getDueDate());
        responseDto.setProjectId(ID);
        responseDto.setStatus(task.getStatus());
        responseDto.setDescription(task.getDescription());

        detailsDto = new TaskDetailsDto();
        detailsDto.setId(ID);
        detailsDto.setProjectId(ID);
        detailsDto.setAssigneeId(ID);
        detailsDto.setDescription(task.getDescription());
        detailsDto.setPriority(task.getPriority());
        detailsDto.setStatus(task.getStatus());
        detailsDto.setDueDate(task.getDueDate());
    }

    @Test
    void createTask_validRequest_ok() {
        when(projectRepository.findByUserIdAndId(ID, ID)).thenReturn(Optional.ofNullable(project));
        when(taskMapper.toModel(createTaskRequestDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDetailsDto(task)).thenReturn(detailsDto);

        TaskDetailsDto saved = taskService.save(ID, createTaskRequestDto);

        assertEquals(detailsDto, saved);
    }

    @Test
    void createTask_invalidId_exceptionOk() {
        when(projectRepository.findByUserIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> taskService.save(ID, createTaskRequestDto));
    }

    @Test
    void getAllUserTasks_ok() {
        when(taskRepository.findAllByAssigneeId(ID)).thenReturn(Set.of(task));
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);

        Set<TaskResponseDto> allTasks = taskService.getAllTasks(ID);

        assertEquals(allTasks, Set.of(responseDto));
    }

    @Test
    void getTaskDetails_validId_ok() {
        when(taskRepository.findByAssigneeIdAndId(ID, ID)).thenReturn(Optional.of(task));
        when(taskMapper.toDetailsDto(task)).thenReturn(detailsDto);

        TaskDetailsDto taskDetails = taskService.getTaskDetails(ID, ID);

        assertEquals(taskDetails, detailsDto);
    }

    @Test
    void getTaskDetails_invalidId_exceptionOk() {
        when(taskRepository.findByAssigneeIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskDetails(ID, ID));
    }

    @Test
    void updateTask_validRequest_ok() {
        when(taskRepository.findByAssigneeIdAndId(ID, ID)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDetailsDto(task)).thenReturn(detailsDto);

        TaskDetailsDto taskDetailsDto = taskService.updateTask(ID, ID, updateTaskDto);

        assertEquals(taskDetailsDto, detailsDto);
    }

    @Test
    void updateTask_invalidId_exceptionOk() {
        when(taskRepository.findByAssigneeIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> taskService.updateTask(ID, ID, updateTaskDto));
    }

    @Test
    void deleteTask_validId_ok() {
        when(taskRepository.findByAssigneeIdAndId(ID, ID)).thenReturn(Optional.of(task));

        taskService.delete(ID, ID);
        verify(taskRepository, times(1)).deleteById(ID);
    }

    @Test
    void deleteTask_invalidId_exceptionOk() {
        when(taskRepository.findByAssigneeIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> taskService.delete(ID, ID));
    }
}
