package app.task.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.task.management.dto.attachment.AttachmentDto;
import app.task.management.mapper.AttachmentMapper;
import app.task.management.model.Attachment;
import app.task.management.model.Task;
import app.task.management.model.User;
import app.task.management.repository.AttachmentRepository;
import app.task.management.repository.TaskRepository;
import app.task.management.service.impl.AttachmentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {
    private static final Long ID = 1L;
    private static final String DROPBOX_ID = "id";
    private static Attachment attachment;
    private static AttachmentDto attachmentDto;
    private static Task task;

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private DropboxService dropboxService;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @BeforeAll
    static void setUp() {
        User user = new User();
        user.setId(ID);
        task = new Task();
        task.setId(ID);
        task.setAssignee(user);

        attachment = new Attachment();
        attachment.setDropboxFileId(DROPBOX_ID);
        attachment.setId(ID);
        attachment.setTask(task);
        attachment.setFileName("file");

        attachmentDto = new AttachmentDto();
        attachmentDto.setId(attachment.getId());
        attachmentDto.setDropboxFileId(attachment.getDropboxFileId());
        attachmentDto.setFileName(attachment.getFileName());
        attachmentDto.setUploadDate(attachment.getUploadDate());
        attachmentDto.setTaskId(attachment.getTask().getId());
    }

    @Test
    void uploadAttachment_validRequest_ok() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(attachment.getFileName());

        when(taskRepository.findByAssigneeIdAndId(ID, ID)).thenReturn(Optional.of(task));
        when(dropboxService.uploadFile(file)).thenReturn(DROPBOX_ID);

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
        when(attachmentMapper.toDto(attachment)).thenReturn(attachmentDto);

        AttachmentDto uploaded = attachmentService.upload(file, ID, ID);

        assertEquals(attachmentDto.getFileName(), uploaded.getFileName());
    }

    @Test
    void uploadAttachment_exception_ok() {
        when(taskRepository.findByAssigneeIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> attachmentService.upload(mock(MultipartFile.class), ID, ID));
    }

    @Test
    void getAllAttachments_validRequest_ok() {
        when(taskRepository.findByAssigneeIdAndId(ID, ID)).thenReturn(Optional.of(task));
        when(attachmentRepository.findAllByTaskId(task.getId())).thenReturn(Set.of(attachment));
        when(attachmentMapper.toDto(attachment)).thenReturn(attachmentDto);

        Set<AttachmentDto> attachments = attachmentService.getAllByTaskId(ID, ID);

        assertEquals(Set.of(attachmentDto), attachments);
    }

    @Test
    void getAllAttachments_exception_ok() {
        when(taskRepository.findByAssigneeIdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> attachmentService.getAllByTaskId(ID, ID));
    }

    @Test
    void getFileByDropboxId_validRequest_ok() {
        Resource resource = mock(Resource.class);
        when(attachmentRepository.findByDropboxFileId(DROPBOX_ID))
                .thenReturn(Optional.of(attachment));
        when(dropboxService.getFile(DROPBOX_ID)).thenReturn(resource);

        Resource file = attachmentService.getByDropboxFileId(DROPBOX_ID, ID);

        assertEquals(resource, file);
    }

    @Test
    void getFileByDropboxId_exception_ok() {
        when(attachmentRepository.findByDropboxFileId(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> attachmentService.getByDropboxFileId(DROPBOX_ID, ID));
    }

    @Test
    void getFileByDropboxId_invalidRequest_ok() {
        User newUser = new User();
        newUser.setId(2L);

        Task newTask = new Task();
        newTask.setAssignee(newUser);

        Attachment newAttachment = new Attachment();
        newAttachment.setDropboxFileId(DROPBOX_ID);
        newAttachment.setTask(newTask);

        when(attachmentRepository.findByDropboxFileId(DROPBOX_ID))
                .thenReturn(Optional.of(newAttachment));
        assertThrows(EntityNotFoundException.class,
                () -> attachmentService.getByDropboxFileId(DROPBOX_ID, ID));
    }

    @Test
    void delete_validRequest_ok() {
        when(attachmentRepository.findById(ID)).thenReturn(Optional.of(attachment));

        attachmentService.delete(ID, ID);

        verify(dropboxService, times(1)).delete(DROPBOX_ID);
        verify(attachmentRepository, times(1)).deleteById(ID);
    }

    @Test
    void delete_exception_ok() {
        when(attachmentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> attachmentService.delete(ID, ID));
    }

    @Test
    void delete_invalidRequest_ok() {
        User newUser = new User();
        newUser.setId(2L);

        Task newTask = new Task();
        newTask.setAssignee(newUser);

        Attachment newAttachment = new Attachment();
        newAttachment.setDropboxFileId(DROPBOX_ID);
        newAttachment.setTask(newTask);

        when(attachmentRepository.findById(ID)).thenReturn(Optional.of(newAttachment));
        assertThrows(EntityNotFoundException.class, () -> attachmentService.delete(ID, ID));
    }
}
