package app.task.management.controller;

import app.task.management.dto.label.CreateLabelDto;
import app.task.management.dto.label.LabelResponseDto;
import app.task.management.dto.label.UpdateLabelDto;
import app.task.management.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Label management", description = "Endpoints for managing labels")
@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@Validated
public class LabelController {
    private final LabelService labelService;

    @Operation(summary = "Create label", description = "Create a new label")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public LabelResponseDto createLabel(@Valid @RequestBody CreateLabelDto labelDto) {
        return labelService.save(labelDto);
    }

    @Operation(summary = "Get all labels", description = "Get all labels from DB")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public Set<LabelResponseDto> getAllLabels() {
        return labelService.getAll();
    }

    @Operation(summary = "Update label", description = "Update an existing label")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public LabelResponseDto updateLabel(@Positive @PathVariable Long id,
                                 @Valid @RequestBody UpdateLabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @Operation(summary = "Delete label", description = "Delete label by ID from DB")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteLabel(@Positive @PathVariable Long id) {
        labelService.delete(id);
    }
}
