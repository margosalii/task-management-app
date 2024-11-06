package app.task.management.controller;

import app.task.management.dto.attachment.AttachmentDto;
import app.task.management.model.User;
import app.task.management.service.AttachmentService;
import jakarta.validation.constraints.Positive;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public AttachmentDto uploadAttachment(@RequestParam("file") MultipartFile file,
                                          @RequestParam("taskId") Long taskId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return attachmentService.upload(file, taskId, userId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{taskId}")
    public Set<AttachmentDto> getAttachmentsById(@PathVariable @Positive Long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return attachmentService.getAllByTaskId(taskId, userId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public Resource getAttachmentByDropboxFileId(@RequestParam ("dropboxFileId")
                                                     String dropboxFileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return attachmentService.getByDropboxFileId(dropboxFileId, userId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        attachmentService.delete(id, userId);
    }
}
