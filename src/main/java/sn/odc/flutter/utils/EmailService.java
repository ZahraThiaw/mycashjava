package sn.odc.flutter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${smtp.host}")
    private String smtpHost;

    @Value("${smtp.port}")
    private String smtpPort;

    @Value("${smtp.username}")
    private String smtpUsername;

    @Value("${smtp.password}")
    private String smtpPassword;

    public void sendEmailWithAttachment(String toEmail, String subject, String body, File attachment) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.debug", "true");  // Activer le debug

        try {
            // Vérifier si le fichier existe
            if (attachment != null && !attachment.exists()) {
                throw new IOException("Le fichier en pièce jointe n'existe pas : " + attachment.getPath());
            }

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpUsername));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject, "UTF-8");

            // Corps du message
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body, "UTF-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            // Ajouter la pièce jointe si elle existe
            if (attachment != null) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(attachment);
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            // Envoyer l'email
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, Integer.parseInt(smtpPort), smtpUsername, smtpPassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            logger.info("Email envoyé avec succès à : {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Erreur MessagingException lors de l'envoi de l'email", e);
            throw new RuntimeException("Échec de l'envoi de l'email : " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Erreur IOException lors de l'envoi de l'email", e);
            throw new RuntimeException("Échec de l'envoi de l'email : " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'envoi de l'email", e);
            throw new RuntimeException("Échec inattendu de l'envoi de l'email : " + e.getMessage(), e);
        }
    }
}