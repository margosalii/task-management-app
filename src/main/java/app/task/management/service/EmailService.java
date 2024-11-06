package app.task.management.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String receiver, String subject, String message) throws MessagingException;
}
