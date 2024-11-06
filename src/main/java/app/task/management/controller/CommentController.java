package app.task.management.controller;

import app.task.management.dto.comment.RequestCommentDto;
import app.task.management.dto.comment.ResponseCommentDto;
import app.task.management.model.User;
import app.task.management.service.CommentService;
import app.task.management.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;
    private final EmailService emailService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseCommentDto addComment(@RequestBody @Valid RequestCommentDto commentDto)
            throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        ResponseCommentDto comment = commentService.save(user.getId(), commentDto);
        if (comment != null) {
            emailService.sendEmail(user.getEmail(), "New comment!",
                    "Dear " + user.getUsername() + ", a new comment to your task was added. "
                    + "Timestamp: " + comment.getTimestamp()
                    + " You can view all info about it in Task Management application.");
        }
        return comment;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Set<ResponseCommentDto> getCommentsRelatedToTask(@Positive @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();
        return commentService.getCommentsByTaskId(userId, id);
    }
}
