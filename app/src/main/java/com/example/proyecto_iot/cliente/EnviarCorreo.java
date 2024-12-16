package com.example.proyecto_iot.cliente;

import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EnviarCorreo extends AsyncTask<Void, Void, Boolean> {

    private String correoDestino, asunto, mensaje;

    public EnviarCorreo(String correoDestino, String asunto, String mensaje) {
        this.correoDestino = correoDestino;
        this.asunto = asunto;
        this.mensaje = mensaje;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            // Configuración de propiedades SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            // Autenticación
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("telecompucp39@gmail.com", "vpferdgxzulhxzyl");
                }
            });

            // Creación del mensaje
            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress("telecompucp39@gmail.com")); // Remitente
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(correoDestino)); // Destinatario
            mm.setSubject(asunto); // Asunto detel correo
            if (mensaje.contains("<") && mensaje.contains(">")) {
                // Mensaje contiene HTML
                mm.setContent(mensaje, "text/html; charset=utf-8");
            } else {
                // Mensaje es texto plano
                mm.setText(mensaje);
            }
            System.out.println("Intentando enviar correo a: " + correoDestino);
            System.out.println("Asunto: " + asunto);
            System.out.println("Mensaje: " + mensaje);

            // Enviar el correo
            Transport.send(mm);
            System.out.println("Correo enviado con éxito");

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error al enviar correo: " + e.getMessage());
            return false;
        }
    }
}
