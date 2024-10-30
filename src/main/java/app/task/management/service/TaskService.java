package app.task.management.service;

import app.task.management.dto.task.CreateTaskRequestDto;
import app.task.management.dto.task.TaskDetailsDto;
import app.task.management.dto.task.TaskResponseDto;
import app.task.management.dto.task.UpdateTaskDto;
import java.util.Set;

public interface TaskService {
    TaskDetailsDto save(Long userId, CreateTaskRequestDto requestDto);

    Set<TaskResponseDto> getAllTasks(Long userId);

    TaskDetailsDto getTaskDetails(Long userId, Long id);

    TaskDetailsDto updateTask(Long userId, Long id, UpdateTaskDto requestDto);

    void delete(Long userId, Long id);
}
