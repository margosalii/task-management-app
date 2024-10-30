package app.task.management.controller;

import app.task.management.dto.label.CreateLabelDto;
import app.task.management.dto.label.LabelResponseDto;
import app.task.management.dto.label.UpdateLabelDto;
import app.task.management.service.LabelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/labels")
@RequiredArgsConstructor
@Validated
public class LabelController {
    private final LabelService labelService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    LabelResponseDto createLabel(@Valid @RequestBody CreateLabelDto labelDto) {
        return labelService.save(labelDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    Set<LabelResponseDto> getAllLabels() {
        return labelService.getAll();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    LabelResponseDto updateLabel(@Positive @PathVariable Long id,
                                 @Valid @RequestBody UpdateLabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    void deleteLabel(@Positive @PathVariable Long id) {
        labelService.delete(id);
    }
}
