package app.task.management.controller;

import app.task.management.dto.comment.RequestCommentDto;
import app.task.management.dto.comment.ResponseCommentDto;
import app.task.management.model.User;
import app.task.management.repository.UserRepository;
import app.task.management.service.CommentService;
import app.task.management.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
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

@Tag(name = "Comment management", description = "Endpoints for managing comments")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Operation(summary = "Add new comment", description = "Add new comment to existing task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseCommentDto addComment(@RequestBody @Valid RequestCommentDto commentDto)
            throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by username: "
                + authentication.getName()));
        ResponseCommentDto comment = commentService.save(user.getId(), commentDto);
        if (comment != null) {
            emailService.sendEmail(user.getEmail(), "New comment!",
                    "Dear " + user.getUsername() + ", a new comment to your task was added. "
                    + "Timestamp: " + comment.getTimestamp()
                    + " You can view all info about it in Task Management application.");
        }
        return comment;
    }

    @Operation(summary = "Get comments by task ID",
            description = "Get all comments related to task ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Set<ResponseCommentDto> getCommentsRelatedToTask(@Positive @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by username: "
                + authentication.getName())).getId();
        return commentService.getCommentsByTaskId(userId, id);
    }
}
