package entities;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
public class EmailSender {
    public static void sendEmail(String userEmail, String subject, String messageBody) {

        String email = userEmail ;

        // Paramètres de configuration pour le serveur SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");//465 pour ssl  , 587
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Informations d'identification pour l'envoi d'e-mails
        final String username = "syrine.benamara@esprit.tn";
        final String password = "211JFT2980s";

        // Création d'une session de messagerie avec authentification
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Création de l'objet MimeMessage
            Message message = new MimeMessage(session);

            // Définition de l'expéditeur
            message.setFrom(new InternetAddress(username));

            // Définition du destinataire
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));

            // Définition du sujet du message
            message.setSubject(subject);

            // Définition du contenu du message
            message.setText(messageBody);

            // Envoi du message
            Transport.send(message);

            System.out.println("E-mail envoyé avec succès à : " + userEmail);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
