package app.task.management.controller;

import app.task.management.dto.attachment.AttachmentDto;
import app.task.management.model.User;
import app.task.management.service.AttachmentService;
import app.task.management.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
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

@Tag(name = "Attachment management", description = "Endpoints for managing attachments")
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final EmailService emailService;

    @Operation(summary = "Upload attachment to task",
            description = "Upload attachment to existing task and save it to Dropbox")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public AttachmentDto uploadAttachment(@RequestParam("file") MultipartFile file,
                                          @RequestParam("taskId") Long taskId)
            throws IOException, MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        AttachmentDto attachment = attachmentService.upload(file, taskId, user.getId());
        if (attachment != null) {
            emailService.sendEmail(user.getEmail(), "Attachment was uploaded!",
                    "Dear " + user.getUsername() + ", a new attachment: "
                    + attachment.getFileName()
                    + " to your task was successfully uploaded to Dropbox!"
                    + " You can view all info about it in Task Management application.");
        }
        return attachment;
    }

    @Operation(summary = "Get attachments by task ID",
            description = "Get all attachments by task ID from DB")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{taskId}")
    public Set<AttachmentDto> getAttachmentsById(@PathVariable @Positive Long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return attachmentService.getAllByTaskId(taskId, userId);
    }

    @Operation(summary = "Get attachment by its Dropbox ID",
            description = "Get file by its Dropbox ID from Dropbox")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public Resource getAttachmentByDropboxFileId(@RequestParam ("dropboxFileId")
                                                     String dropboxFileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return attachmentService.getByDropboxFileId(dropboxFileId, userId);
    }

    @Operation(summary = "Delete attachment by ID",
            description = "Delete attachment by ID from Dropbox and DB")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        attachmentService.delete(id, userId);
    }
}
