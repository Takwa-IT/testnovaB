package com.example.testnova.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Objects;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.sender}")
    private String senderEmail;

    @Value("${app.email.sender-name}")
    private String senderName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String userName, String verificationLink) {
        String subject = "🔐 Vérification de votre email - TestNova";
        String content = buildVerificationEmailContent(userName, verificationLink);
        sendHtmlEmail(to, subject, content);
    }

    public void sendPasswordResetEmail(String to, String userName, String resetLink) {
        String subject = "🔑 Réinitialisation de votre mot de passe - TestNova";
        String content = buildPasswordResetEmailContent(userName, resetLink);
        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(Objects.requireNonNull(senderEmail), Objects.requireNonNull(senderName));
            helper.setTo(Objects.requireNonNull(to));
            helper.setSubject(Objects.requireNonNull(subject));
            helper.setText(Objects.requireNonNull(htmlContent), true);

            mailSender.send(message);
            System.out.println("✅ Email envoyé avec succès à: " + to);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    private String buildVerificationEmailContent(String userName, String verificationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .header { text-align: center; padding-bottom: 20px; border-bottom: 2px solid #4CAF50; }
                        .header h1 { color: #4CAF50; margin: 0; }
                        .content { padding: 30px 0; text-align: center; }
                        .btn { display: inline-block; padding: 15px 30px; background-color: #4CAF50; color: white !important; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }
                        .btn:hover { background-color: #45a049; }
                        .footer { text-align: center; padding-top: 20px; border-top: 1px solid #eee; color: #888; font-size: 12px; }
                        .code { background-color: #f0f0f0; padding: 10px 20px; border-radius: 5px; font-family: monospace; font-size: 18px; letter-spacing: 2px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎯 TestNova</h1>
                        </div>
                        <div class="content">
                            <h2>Bonjour %s ! 👋</h2>
                            <p>Merci de vous être inscrit sur TestNova.</p>
                            <p>Pour activer votre compte, veuillez cliquer sur le bouton ci-dessous :</p>
                            <a href="%s" class="btn">✅ Vérifier mon email</a>
                            <p style="color: #888; font-size: 14px;">Ce lien expire dans 24 heures.</p>
                            <p style="color: #888; font-size: 12px;">Si vous n'avez pas créé de compte, ignorez cet email.</p>
                        </div>
                        <div class="footer">
                            <p>© 2024 TestNova - Plateforme de Tests Techniques</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(userName, verificationLink);
    }

    private String buildPasswordResetEmailContent(String userName, String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .header { text-align: center; padding-bottom: 20px; border-bottom: 2px solid #FF6B6B; }
                        .header h1 { color: #FF6B6B; margin: 0; }
                        .content { padding: 30px 0; text-align: center; }
                        .btn { display: inline-block; padding: 15px 30px; background-color: #FF6B6B; color: white !important; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }
                        .btn:hover { background-color: #ee5a5a; }
                        .footer { text-align: center; padding-top: 20px; border-top: 1px solid #eee; color: #888; font-size: 12px; }
                        .warning { background-color: #fff3cd; border: 1px solid #ffc107; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔑 TestNova</h1>
                        </div>
                        <div class="content">
                            <h2>Bonjour %s ! 👋</h2>
                            <p>Vous avez demandé à réinitialiser votre mot de passe.</p>
                            <p>Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe :</p>
                            <a href="%s" class="btn">🔐 Réinitialiser mon mot de passe</a>
                            <p style="color: #888; font-size: 14px;">Ce lien expire dans 1 heure.</p>
                            <div class="warning">
                                ⚠️ Si vous n'avez pas demandé cette réinitialisation, ignorez cet email. Votre mot de passe restera inchangé.
                            </div>
                        </div>
                        <div class="footer">
                            <p>© 2024 TestNova - Plateforme de Tests Techniques</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(userName, resetLink);
    }
}
