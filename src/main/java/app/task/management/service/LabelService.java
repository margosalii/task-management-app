package app.task.management.service;

import app.task.management.dto.label.CreateLabelDto;
import app.task.management.dto.label.LabelResponseDto;
import app.task.management.dto.label.UpdateLabelDto;
import java.util.Set;

public interface LabelService {
    LabelResponseDto save(CreateLabelDto createLabelDto);

    Set<LabelResponseDto> getAll();

    LabelResponseDto updateLabel(Long id, UpdateLabelDto labelDto);

    void delete(Long id);
}
