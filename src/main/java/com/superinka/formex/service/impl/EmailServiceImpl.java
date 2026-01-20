package com.superinka.formex.service.impl;

import com.superinka.formex.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendWelcomeEmail(String to, String name) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("¡Bienvenido a FORMEX!");

            //Html del correo
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; color: #333;">
                    <h1 style="color: #FF5722;">¡Hola, %s!</h1>
                    <p>Estamos muy emocionados de que te unas a <strong>Formex</strong>.</p>
                    <p>Tu cuenta ha sido creada exitosamente. Ahora puedes acceder a todos nuestros cursos en vivo.</p>
                    <br/>
                    <a href="http://localhost:5173/login" style="background-color: #FF5722; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Ir a la Plataforma</a>
                    <br/><br/>
                    <p>Saludos,<br/>El equipo de Formex</p>
                </div>
            """.formatted(name);

            helper.setText(htmlContent,true);

            mailSender.send(message);
            System.out.println("Correo de bienvenida enviado a: " + to);
        } catch (MessagingException e){
            System.err.println("Error enviando correo: " + e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String token){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Recuperacion de contraseña - FORMEX");

            String resetUrl = "http://localhost:5173/reset-password?token=" + token;

            String htmlContent = """
                <div style="font-family: Arial;">
                    <h2 style="color: #FF5722;">Recupera tu acceso</h2>
                    <p>Hemos recibido una solicitud para restablecer tu contraseña.</p>
                    <p>Haz clic en el siguiente botón para crear una nueva clave:</p>
                    <a href="%s" style="background-color: #FF5722; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Restablecer Contraseña</a>
                    <p>Si no fuiste tú, ignora este correo.</p>
                </div>
            """.formatted(resetUrl);

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error enviando email recuperacion: " + e.getMessage());
        }
    }
}
