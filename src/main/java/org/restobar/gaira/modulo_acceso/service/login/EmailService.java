package org.restobar.gaira.modulo_acceso.service.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String groupCopyAddress;

    public EmailService(
            @Autowired(required = false) JavaMailSender mailSender,
            @Value("${spring.mail.from:restobar_lagaira@googlegroups.com}") String fromAddress,
            @Value("${spring.mail.group-copy:}") String groupCopyAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.groupCopyAddress = groupCopyAddress;
    }

    public boolean sendResetCode(String to, String username, String code) {
        String subject = "Código de recuperación de contraseña - Gaira Auth";
        String content = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px;'>"
                        +
                        "<h2 style='color: #660000;'>Recuperación de Contraseña</h2>" +
                        "<p>Hola,</p>" +
                        "<p><strong>Usuario:</strong> %s</p>" +
                        "<p>Has solicitado recuperar tu contraseña. Utiliza el siguiente código para verificar tu identidad:</p>"
                        +
                        "<div style='background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #333; margin: 20px 0; border-radius: 8px;'>"
                        +
                        "%s" +
                        "</div>" +
                        "<p>Este código vencerá en 15 minutos.</p>" +
                        "<p>Si no has solicitado este cambio, puedes ignorar este correo.</p>" +
                        "<br><p>Atentamente,<br><strong>Equipo de Gaira Auth</strong></p>" +
                        "</div>",
                username, code);

        return sendEmail(to, subject, content);
    }

    public boolean sendNewPassword(String to, String username, String newPassword) {
        String subject = "Tu nueva contraseña de acceso - Gaira Auth";
        String content = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px;'>"
                        +
                        "<h2 style='color: #660000;'>Nueva Contraseña Generada</h2>" +
                        "<p>Hola,</p>" +
                        "<p><strong>Usuario:</strong> %s</p>" +
                        "<p>Tu identidad ha sido verificada correctamente. Tu nueva contraseña temporal es:</p>" +
                        "<div style='background-color: #f9f9f9; padding: 15px; text-align: center; font-size: 20px; font-weight: bold; color: #660000; margin: 20px 0; border-radius: 8px;'>"
                        +
                        "%s" +
                        "</div>" +
                        "<p>Te recomendamos cambiar esta contraseña la próxima vez que inicies sesión.</p>" +
                        "<br><p>Atentamente,<br><strong>Equipo de Gaira Auth</strong></p>" +
                        "</div>",
                username, newPassword);

        return sendEmail(to, subject, content);
    }

    private boolean sendEmail(String to, String subject, String content) {
        log.info("Iniciando proceso de envío de email a {}...", to);
        if (mailSender == null) {
            log.warn(
                    "JavaMailSender no está configurado (Bean es null). No se puede enviar correo. Revise las propiedades spring.mail.*");
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            if (groupCopyAddress != null && !groupCopyAddress.isBlank()) {
                helper.setBcc(groupCopyAddress);
            }
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Email enviado exitosamente a {}", to);
            return true;
        } catch (MailAuthenticationException e) {
            log.error(
                    "Autenticación SMTP fallida al enviar a {}. Verifique SPRING_MAIL_USERNAME/SPRING_MAIL_PASSWORD (App Password de Gmail sin comillas ni espacios) y SPRING_MAIL_FROM.",
                    to);
            return false;
        } catch (MessagingException e) {
            log.error("No se pudo construir el mensaje de correo para {}: {}", to, e.getMessage());
            return false;
        } catch (MailException e) {
            log.error("Fallo crítico al enviar email a {}: {}. Verifique la configuración SMTP en el servidor.", to,
                    e.getMessage());
            return false;
        }
    }
}
