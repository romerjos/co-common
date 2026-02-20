package pe.gob.hospitalcayetano.cocommon.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
@RefreshScope
public class EmailService {

    private static final Properties PROPERTIES = new Properties();

    private final String from;

    private final Authenticator authenticator;

    private final String environment;

    @Autowired
    public EmailService(@Value("${notificacion.from:hchsigehoweb@hospitalcayetano.gob.pe}") String from,
                        @Value("${notificacion.host:mail.hospitalcayetano.gob.pe}") String host,
                        @Value("${notificacion.port:465}") String port,
                        @Value("${notificacion.username:hchsigehoweb@hospitalcayetano.gob.pe}") String username,
                        @Value("${notificacion.password:*Hch2024.*}") String password,
                        @Value("${spring.profiles.active:local}") String environment) {
        this.from = from;
        this.environment = environment;

        PROPERTIES.put("mail.smtp.host", host);
        PROPERTIES.put("mail.smtp.port", port);
        PROPERTIES.put("mail.smtp.auth", "true");
        PROPERTIES.put("mail.smtp.starttls.enable", "true");
        PROPERTIES.put("mail.smtp.socketFactory.port", port);
        PROPERTIES.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }

    @Async
    public void envioMensajePlano(List<String> tos, String asunto, String mensaje, Boolean enable) {
        if (tos == null || tos.isEmpty() || !enable || environment.equals("local")) {
            return;
        }

        Session session = Session.getInstance(PROPERTIES, authenticator);
        session.setDebug(false);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = new InternetAddress[tos.size()];

            for (int i = 0; i < tos.size(); i++) {
                address[i] = new InternetAddress(tos.get(i));
            }

            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(asunto);
            msg.setSentDate(new Date());

            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(mensaje, "utf-8");

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp);

            msg.setContent(mp);

            Transport.send(msg);
            log.info("Envio de notificación exitosa.");
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
