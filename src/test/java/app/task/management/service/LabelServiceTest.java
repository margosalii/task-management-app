package app.task.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.task.management.dto.label.CreateLabelDto;
import app.task.management.dto.label.LabelResponseDto;
import app.task.management.dto.label.UpdateLabelDto;
import app.task.management.mapper.LabelMapper;
import app.task.management.model.Label;
import app.task.management.model.Task;
import app.task.management.repository.LabelRepository;
import app.task.management.repository.TaskRepository;
import app.task.management.service.impl.LabelServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {
    private static final Long ID = 1L;
    private static CreateLabelDto createLabelDto;
    private static Label label;
    private static LabelResponseDto responseDto;
    private static LabelResponseDto updatedDto;
    private static UpdateLabelDto updateLabelDto;
    private static Task task;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private LabelServiceImpl labelService;

    @BeforeAll
    static void setUp() {
        task = new Task();
        task.setId(ID);

        createLabelDto = new CreateLabelDto();
        createLabelDto.setColor("RED");
        createLabelDto.setName("Label");
        createLabelDto.setTaskIds(Set.of(task.getId()));

        label = new Label();
        label.setId(ID);
        label.setName(createLabelDto.getName());
        label.setColor(createLabelDto.getColor());
        label.setTasks(Set.of(task));

        responseDto = new LabelResponseDto();
        responseDto.setId(label.getId());
        responseDto.setName(label.getName());
        responseDto.setColor(label.getColor());
        responseDto.setTaskIds(createLabelDto.getTaskIds());

        updateLabelDto = new UpdateLabelDto();
        updateLabelDto.setTaskIds(responseDto.getTaskIds());
        updateLabelDto.setName("Updated");
        updateLabelDto.setColor("GREEN");

        updatedDto = new LabelResponseDto();
        updatedDto.setTaskIds(updateLabelDto.getTaskIds());
        updatedDto.setColor(updateLabelDto.getColor());
        updatedDto.setName(updateLabelDto.getName());
        updatedDto.setId(ID);
    }

    @Test
    void saveLabel_validRequest_ok() {
        when(labelMapper.toModel(createLabelDto)).thenReturn(label);
        when(taskRepository.findAllById(createLabelDto.getTaskIds())).thenReturn(List.of(task));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        LabelResponseDto saved = labelService.save(createLabelDto);

        verify(taskRepository, times(1)).saveAll(Set.of(task));
        assertEquals(responseDto, saved);
    }

    @Test
    void getAllLabels_ok() {
        when(labelRepository.findAll()).thenReturn(List.of(label));
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        Set<LabelResponseDto> labels = labelService.getAll();

        assertEquals(Set.of(responseDto), labels);
    }

    @Test
    void updateLabel_validRequest_ok() {
        when(labelRepository.findById(ID)).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(updatedDto);

        LabelResponseDto updatedActual = labelService.updateLabel(ID, updateLabelDto);

        verify(labelMapper, times(1)).updateFromDto(updateLabelDto, label);
        assertEquals(updatedDto, updatedActual);
    }

    @Test
    void updateLabel_exception_ok() {
        when(labelRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> labelService.updateLabel(ID, updateLabelDto));
    }

    @Test
    void deleteLabel_ok() {
        labelService.delete(ID);

        verify(labelRepository, times(1)).deleteById(ID);
    }
}
