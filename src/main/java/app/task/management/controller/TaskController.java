package app.task.management.controller;

import app.task.management.dto.task.CreateTaskRequestDto;
import app.task.management.dto.task.TaskDetailsDto;
import app.task.management.dto.task.TaskResponseDto;
import app.task.management.dto.task.UpdateTaskDto;
import app.task.management.model.User;
import app.task.management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Task management", description = "Endpoints for managing tasks")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "Create task", description = "Create a new task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public TaskDetailsDto createTask(@Valid @RequestBody CreateTaskRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return taskService.save(userId, requestDto);
    }

    @Operation(summary = "Get all tasks", description = "Get all user tasks")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public Set<TaskResponseDto> getAllTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return taskService.getAllTasks(userId);
    }

    @Operation(summary = "Get task details", description = "Get task details by its ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public TaskDetailsDto getTaskDetails(@Positive @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return taskService.getTaskDetails(userId, id);
    }

    @Operation(summary = "Update task", description = "Update task by its ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public TaskDetailsDto updateTask(@Positive @PathVariable Long id,
                                     @RequestBody @Valid UpdateTaskDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return taskService.updateTask(userId, id, requestDto);
    }

    @Operation(summary = "Delete task", description = "Delete task by its ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteTask(@Positive @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        taskService.delete(userId, id);
    }
}
