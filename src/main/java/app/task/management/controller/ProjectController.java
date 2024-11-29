package app.task.management.controller;

import app.task.management.dto.project.CreateProjectDto;
import app.task.management.dto.project.ProjectDetailsResponseDto;
import app.task.management.dto.project.ProjectResponseDto;
import app.task.management.dto.project.UpdateRequestProjectDto;
import app.task.management.model.User;
import app.task.management.repository.UserRepository;
import app.task.management.service.EmailService;
import app.task.management.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
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

@Tag(name = "Project management", description = "Endpoints for managing projects")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Operation(summary = "Create project", description = "Create a new project")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ProjectDetailsResponseDto createProject(@RequestBody @Valid
                                                       CreateProjectDto projectDto)
            throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by username: "
                + authentication.getName()));
        ProjectDetailsResponseDto savedProject = projectService.save(user.getId(), projectDto);
        if (savedProject != null) {
            emailService.sendEmail(user.getEmail(), "New project!",
                    "Dear " + user.getUsername() + ", you successfully created new project. "
                    + "Deadline: " + savedProject.getEndDate()
                    + " You can view all info about it in Task Management application.");
        }
        return savedProject;
    }

    @Operation(summary = "Get all projects", description = "Get all user projects")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public Set<ProjectResponseDto> getAllProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by username: "
                + authentication.getName())).getId();
        return projectService.getUsersProjects(userId);
    }

    @Operation(summary = "Get project details", description = "Get details of user project by ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ProjectDetailsResponseDto getProjectDetails(@Positive @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by username: "
                + authentication.getName())).getId();
        return projectService.getProjectDetails(userId, id);
    }

    @Operation(summary = "Update project", description = "Update project by its ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProjectDetailsResponseDto updateProject(@Positive @PathVariable Long id,
                                                   @RequestBody @Valid
                                                   UpdateRequestProjectDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by username: "
                + authentication.getName())).getId();
        return projectService.update(userId, id, requestDto);
    }

    @Operation(summary = "Delete project", description = "Delete project by its ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProject(@Positive @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by username: "
                + authentication.getName())).getId();
        projectService.delete(userId, id);
    }
}
