package app.task.management.service.impl;

import app.task.management.dto.task.CreateTaskRequestDto;
import app.task.management.dto.task.TaskDetailsDto;
import app.task.management.dto.task.TaskResponseDto;
import app.task.management.dto.task.UpdateTaskDto;
import app.task.management.mapper.TaskMapper;
import app.task.management.model.Project;
import app.task.management.model.Task;
import app.task.management.model.User;
import app.task.management.repository.ProjectRepository;
import app.task.management.repository.TaskRepository;
import app.task.management.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public TaskDetailsDto save(Long userId, CreateTaskRequestDto requestDto) {
        Project project = projectRepository.findByUserIdAndId(
                userId, requestDto.getProjectId()).orElseThrow(() -> new EntityNotFoundException(
                    String.format("Can't find project by user id: %s and project id: %s",
                        userId, requestDto.getProjectId()))
        );
        User user = project.getUser();
        Task task = taskMapper.toModel(requestDto);
        task.setAssignee(user);
        task.setProject(project);
        return taskMapper.toDetailsDto(taskRepository.save(task));
    }

    @Override
    public Set<TaskResponseDto> getAllTasks(Long userId) {
        return taskRepository.findAllByAssigneeId(userId)
            .stream()
            .map(taskMapper::toResponseDto)
            .collect(Collectors.toSet());
    }

    @Override
    public TaskDetailsDto getTaskDetails(Long userId, Long id) {
        return taskMapper.toDetailsDto(taskRepository.findByAssigneeIdAndId(userId, id).orElseThrow(
            () -> new EntityNotFoundException(String.format(
                "Can't find task by user id: %s and task id: %s", userId, id))
        ));
    }

    @Override
    @Transactional
    public TaskDetailsDto updateTask(Long userId, Long id, UpdateTaskDto requestDto) {
        Task task = taskRepository.findByAssigneeIdAndId(userId, id).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                "Can't find task by user id: %s and task id: %s", userId, id))
        );
        taskMapper.updateFromDto(requestDto, task);
        return taskMapper.toDetailsDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        Task task = taskRepository.findByAssigneeIdAndId(userId, id).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                "Can't find task by user id: %s and task id: %s", userId, id))
        );
        taskRepository.deleteById(task.getId());
    }
}
