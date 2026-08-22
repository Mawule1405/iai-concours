package com.taurustex.api.tools.emails;


import com.taurustex.api.dtos.EmailDocumentRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String EMAIL;
    @Value("${app.frontend.url}")
    private String webUrl;

    @Override
    public void sendConfirmationCode(String toEmail, int code) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        Context context = new Context();
        context.setVariable("confirmationCode", code);
        String htmlContent = templateEngine.process("emails/confirmation-code", context);

        helper.setTo(toEmail);
        helper.setSubject("Votre code de confirmation - CEVIMEC");
        helper.setText(htmlContent, true);
        helper.setFrom(EMAIL);

        mailSender.send(mimeMessage);
    }

    @Override
    public boolean sendAccountInformation(String toEmail, String username, String password) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("password", password);
            String htmlContent = templateEngine.process("emails/account-infos", context);

            helper.setTo(toEmail);
            helper.setSubject("Vos informations de connexion");
            helper.setText(htmlContent, true);
            helper.setFrom(EMAIL);

            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    @Override
    public boolean sendNewPassword(String toEmail, String password) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            Context context = new Context();
            context.setVariable("password", password);
            String htmlContent = templateEngine.process("emails/initialize-password-infos", context);

            helper.setTo(toEmail);
            helper.setSubject("Vos informations de connexion");
            helper.setText(htmlContent, true);
            helper.setFrom(EMAIL);

            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    @Override
    public void sendUserInformation(String toEmail, String fullName, String username, String password, String universityName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        Context context = new Context();
        context.setVariable("universityName", universityName);
        context.setVariable("fullName", fullName);
        context.setVariable("username", username);
        context.setVariable("password", password);
        context.setVariable("loginUrl", webUrl);

        String htmlContent = templateEngine.process("emails/user-credentials", context);

        helper.setTo(toEmail);
        helper.setSubject("Bienvenue sur TSC API - Vos identifiants d'accès");
        helper.setText(htmlContent, true);
        helper.setFrom(EMAIL);

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendPasswordResetInfo(String toEmail, String fullName, String password, String universityName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        Context context = new Context();
        context.setVariable("universityName", universityName);
        context.setVariable("fullName", fullName);
        context.setVariable("password", password);
        context.setVariable("loginUrl", webUrl);

        String htmlContent = templateEngine.process("emails/password-reset", context);

        helper.setTo(toEmail);
        helper.setSubject("Mise à jour de votre mot de passe - " + universityName);
        helper.setText(htmlContent, true);
        helper.setFrom(EMAIL);

        mailSender.send(mimeMessage);
    }

    @Override
    public boolean sendDocumentEmail(EmailDocumentRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("universityName", request.getUniversityName() != null ? request.getUniversityName() : "TaurusTeX");
            context.setVariable("emailSubject", request.getEmailSubject());
            context.setVariable("headerTitle", request.getHeaderTitle());
            context.setVariable("mainTitle", request.getMainTitle());
            context.setVariable("fullName", request.getFullName());
            context.setVariable("messageBody", request.getMessageBody());
            context.setVariable("documentIcon", request.getDocumentIcon() != null ? request.getDocumentIcon() : "📄");
            context.setVariable("documentTitle", request.getDocumentTitle());
            context.setVariable("documentDescription", request.getDocumentDescription());
            context.setVariable("customNote", request.getCustomNote());
            context.setVariable("actionUrl", request.getActionUrl());
            context.setVariable("actionButtonText", request.getActionButtonText());

            // Correction 1 : Chemin sous /templates/emails/
            String htmlContent = templateEngine.process("emails/generic-document-email", context);

            helper.setTo(request.getRecipientEmail());
            helper.setSubject(request.getEmailSubject());
            helper.setText(htmlContent, true);

            // Correction 2 : Ajout de l'expéditeur manquant
            helper.setFrom(EMAIL);

            if (request.getAttachmentBytes() != null && request.getAttachmentFileName() != null) {
                helper.addAttachment(
                        request.getAttachmentFileName(),
                        new ByteArrayResource(request.getAttachmentBytes())
                );
            }

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}