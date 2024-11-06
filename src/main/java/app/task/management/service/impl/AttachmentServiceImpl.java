package app.task.management.service.impl;

import app.task.management.dto.attachment.AttachmentDto;
import app.task.management.mapper.AttachmentMapper;
import app.task.management.model.Attachment;
import app.task.management.model.Task;
import app.task.management.repository.AttachmentRepository;
import app.task.management.repository.TaskRepository;
import app.task.management.service.AttachmentService;
import app.task.management.service.DropboxService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final DropboxService dropboxService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public AttachmentDto upload(MultipartFile file, Long taskId, Long userId) throws IOException {
        Task task = taskRepository.findByAssigneeIdAndId(userId, taskId).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                "Can't find task by user id: %s and task id: %s", userId, taskId))
        );
        String dropboxFileId = dropboxService.uploadFile(file);
        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setDropboxFileId(dropboxFileId);
        attachment.setFileName(file.getOriginalFilename());

        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public Set<AttachmentDto> getAllByTaskId(Long taskId, Long userId) {
        Task task = taskRepository.findByAssigneeIdAndId(userId, taskId).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                "Can't find task by user id: %s and task id: %s", userId, taskId))
        );
        return attachmentRepository.findAllByTaskId(task.getId())
            .stream()
            .map(attachmentMapper::toDto)
            .collect(Collectors.toSet());
    }

    @Override
    public Resource getByDropboxFileId(String dropboxFileId, Long userId) {
        Attachment attachment = attachmentRepository.findByDropboxFileId(dropboxFileId).orElseThrow(
                () -> new EntityNotFoundException(
                    "Can't find attachment in DB by dropbox file id: " + dropboxFileId));
        if (Objects.equals(attachment.getTask().getAssignee().getId(), userId)) {
            return dropboxService.getFile(attachment.getDropboxFileId());
        } else {
            throw new EntityNotFoundException(String.format(
                "Can't find file in Dropbox by user id: %s and dropbox file id: %s",
                userId, dropboxFileId));
        }
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find attachment in DB by id: " + id));
        if (Objects.equals(attachment.getTask().getAssignee().getId(), userId)) {
            dropboxService.delete(attachment.getDropboxFileId());
            attachmentRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(String.format(
                "Can't delete file in Dropbox by user id: %s and attachment id: %s",
                userId, id));
        }
    }
}
