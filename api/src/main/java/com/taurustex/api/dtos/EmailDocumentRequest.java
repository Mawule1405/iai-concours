package com.taurustex.api.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDocumentRequest {
    private String recipientEmail;
    private String fullName;

    // Champs dynamiques du template
    private String universityName;
    private String emailSubject;
    private String headerTitle;
    private String mainTitle;
    private String messageBody;
    private String documentIcon;        // Ex: "📄", "💳", "🎓"
    private String documentTitle;       // Ex: "Bulletin_S1.pdf"
    private String documentDescription; // Ex: "Bulletin du 1er semestre"
    private String customNote;
    private String actionUrl;
    private String actionButtonText;

    // Pièce jointe
    private byte[] attachmentBytes;
    private String attachmentFileName;
}