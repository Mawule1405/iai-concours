package com.taurustex.api.tools.emails;


import com.taurustex.api.dtos.EmailDocumentRequest;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendConfirmationCode(String toEmail, int code) throws MessagingException;
    boolean sendAccountInformation(String toEmail, String username, String password) ;
    boolean sendNewPassword(String toEmail, String password);

    void sendUserInformation(String toEmail, String fullName, String username, String password, String universityName) throws MessagingException;

    void sendPasswordResetInfo(String toEmail, String fullName, String password, String universityName) throws MessagingException;

    boolean sendDocumentEmail(EmailDocumentRequest request);
}
