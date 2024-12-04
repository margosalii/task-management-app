package app.task.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.task.management.dto.project.CreateProjectDto;
import app.task.management.dto.project.ProjectDetailsResponseDto;
import app.task.management.dto.project.ProjectResponseDto;
import app.task.management.dto.project.UpdateRequestProjectDto;
import app.task.management.mapper.ProjectMapper;
import app.task.management.mapper.TaskMapper;
import app.task.management.mapper.impl.TaskMapperImpl;
import app.task.management.model.Project;
import app.task.management.model.Status;
import app.task.management.model.Task;
import app.task.management.model.User;
import app.task.management.repository.ProjectRepository;
import app.task.management.repository.UserRepository;
import app.task.management.service.impl.ProjectServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    private static CreateProjectDto createProjectDto;
    private static UpdateRequestProjectDto updateProjectDto;
    private static ProjectDetailsResponseDto detailsResponseDto;
    private static ProjectResponseDto responseDto;
    private static Project project;
    private static User user;
    private static final Long ID = 1L;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeAll
    static void setUp() {
        user = new User();
        user.setId(ID);

        createProjectDto = new CreateProjectDto();
        createProjectDto.setName("First project");
        createProjectDto.setDescription("Description");
        createProjectDto.setStartDate(LocalDate.now());
        createProjectDto.setEndDate(LocalDate.now().plusDays(5));

        updateProjectDto = new UpdateRequestProjectDto();
        updateProjectDto.setName("Updated project");
        updateProjectDto.setDescription("Description upd");
        updateProjectDto.setStartDate(LocalDate.now());
        updateProjectDto.setEndDate(LocalDate.now().plusDays(6));
        updateProjectDto.setStatus(Status.COMPLETED);

        project = new Project();
        project.setId(ID);
        project.setName(createProjectDto.getName());
        project.setStatus(Status.INITIATED);
        project.setDescription(createProjectDto.getDescription());
        project.setStartDate(createProjectDto.getStartDate());
        project.setEndDate(createProjectDto.getEndDate());
        project.setUser(user);
        project.setTasks(Set.of(new Task()));

        responseDto = new ProjectResponseDto();
        responseDto.setId(ID);
        responseDto.setName(project.getName());
        responseDto.setStatus(project.getStatus());
        responseDto.setDescription(project.getDescription());

        detailsResponseDto = new ProjectDetailsResponseDto();
        detailsResponseDto.setId(project.getId());
        detailsResponseDto.setDescription(project.getDescription());
        detailsResponseDto.setStatus(project.getStatus());
        detailsResponseDto.setName(project.getName());
        TaskMapper taskMapper = new TaskMapperImpl();
        detailsResponseDto.setTasks(project.getTasks()
                .stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toSet()));
        detailsResponseDto.setStartDate(project.getStartDate());
        detailsResponseDto.setEndDate(project.getEndDate());
    }

    @Test
    void createProject_validRequest_ok() {
        when(projectMapper.toModel(createProjectDto)).thenReturn(project);
        when(userRepository.findById(ID)).thenReturn(Optional.ofNullable(user));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectDetails(project)).thenReturn(detailsResponseDto);

        ProjectDetailsResponseDto saved = projectService.save(ID, createProjectDto);

        assertEquals(detailsResponseDto, saved);
    }

    @Test
    void getAllUserProjects_ok() {
        when(projectRepository.findAllByUserId(ID)).thenReturn(Set.of(project));
        when(projectMapper.toResponseDto(project)).thenReturn(responseDto);

        Set<ProjectResponseDto> usersProjects = projectService.getUsersProjects(ID);

        assertEquals(Set.of(responseDto), usersProjects);
    }

    @Test
    void getProjectDetails_validId_ok() {
        when(projectRepository.findByUserIdAndId(ID, ID)).thenReturn(Optional.ofNullable(project));
        when(projectMapper.toProjectDetails(project)).thenReturn(detailsResponseDto);

        ProjectDetailsResponseDto projectDetails = projectService.getProjectDetails(ID, ID);

        assertEquals(detailsResponseDto, projectDetails);
    }

    @Test
    void getProjectDetails_invalidId_exceptionOk() {
        when(projectRepository.findByUserIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectDetails(ID, ID));
    }

    @Test
    void updateProject_validRequest_ok() {
        when(projectRepository.findByUserIdAndId(ID, ID)).thenReturn(Optional.ofNullable(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectDetails(project)).thenReturn(detailsResponseDto);

        ProjectDetailsResponseDto updated = projectService.update(ID, ID, updateProjectDto);

        assertEquals(detailsResponseDto, updated);
    }

    @Test
    void updateProject_invalidId_exceptionOk() {
        when(projectRepository.findByUserIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> projectService.update(ID, ID, updateProjectDto));
    }

    @Test
    void deleteProject_validId_ok() {
        when(projectRepository.findByUserIdAndId(ID, ID)).thenReturn(Optional.ofNullable(project));
        projectService.delete(ID, ID);
        verify(projectRepository, times(1)).deleteById(ID);
    }

    @Test
    void deleteProject_invalidId_exceptionOk() {
        when(projectRepository.findByUserIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> projectService.delete(ID, ID));
    }
}
