package app.task.management.controller;

import app.task.management.dto.project.CreateProjectDto;
import app.task.management.dto.project.ProjectDetailsResponseDto;
import app.task.management.dto.project.ProjectResponseDto;
import app.task.management.dto.project.UpdateRequestProjectDto;
import app.task.management.model.User;
import app.task.management.service.EmailService;
import app.task.management.service.ProjectService;
import jakarta.mail.MessagingException;
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

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;
    private final EmailService emailService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ProjectDetailsResponseDto createProject(@RequestBody @Valid
                                                       CreateProjectDto projectDto)
            throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        ProjectDetailsResponseDto savedProject = projectService.save(user.getId(), projectDto);
        if (savedProject != null) {
            emailService.sendEmail(user.getEmail(), "New project!",
                    "Dear " + user.getUsername() + ", you successfully created new project. "
                    + "Deadline: " + savedProject.getEndDate()
                    + " You can view all info about it in Task Management application.");
        }
        return savedProject;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public Set<ProjectResponseDto> getAllProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return projectService.getUsersProjects(userId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ProjectDetailsResponseDto getProjectDetails(@Positive @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return projectService.getProjectDetails(userId, id);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProjectDetailsResponseDto updateProject(@Positive @PathVariable Long id,
                                                   @RequestBody @Valid
                                                   UpdateRequestProjectDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return projectService.update(userId, id, requestDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProject(@Positive @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        projectService.delete(userId, id);
    }
}
