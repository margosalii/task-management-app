package app.task.management.service.impl;

import app.task.management.dto.label.CreateLabelDto;
import app.task.management.dto.label.LabelResponseDto;
import app.task.management.dto.label.UpdateLabelDto;
import app.task.management.mapper.LabelMapper;
import app.task.management.model.Label;
import app.task.management.model.Task;
import app.task.management.repository.LabelRepository;
import app.task.management.repository.TaskRepository;
import app.task.management.service.LabelService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public LabelResponseDto save(CreateLabelDto createLabelDto) {
        Label label = labelMapper.toModel(createLabelDto);
        Set<Task> tasks = new HashSet<>(taskRepository.findAllById(createLabelDto.getTaskIds()));
        label.setTasks(tasks);
        Label saved = labelRepository.save(label);
        tasks.forEach(task -> task.getLabels().add(saved));
        taskRepository.saveAll(tasks);
        return labelMapper.toDto(saved);
    }

    @Override
    public Set<LabelResponseDto> getAll() {
        return labelRepository.findAll()
            .stream()
            .map(labelMapper::toDto)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public LabelResponseDto updateLabel(Long id, UpdateLabelDto labelDto) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find label with id: " + id));
        labelMapper.updateFromDto(labelDto, label);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
