package api;

import exceptions.CouldNotSendEmailException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import oma.wsdl.pxprof.sms._1_0.send._interface.local.SendSMS;
import oma.wsdl.pxprof.sms._1_0.send._interface.local.SendSMSSoapSecure;
import oma.wsdl.pxprof.sms._1_0.types.DeliveryInformation;

/**
 *
 * @author claudia
 */
@WebService(serviceName = "NotificationsServiceImp")
public class NotificationsServiceImp {

    private static String username = "a4393863@drdrb.com";
    private static String password = "passemail";
    private static String accessKey = "C2BFF5B2-1125-4B41-B27D-DE099DA4A332";

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "sendEmail")
    public void sendEmail(@WebParam(name = "sourceEmail") String sourceEmail,
            @WebParam(name = "sourcePassword") String sourcePassword,
            @WebParam(name = "destinationEmail") String destinationEmail,
            @WebParam(name = "subject") String subject,
            @WebParam(name = "body") String body,
            @WebParam(name = "attachment") byte[] attachment,
            @WebParam(name = "attachmentName") String attachmentName) throws CouldNotSendEmailException {
        int tentativas = 0;
        if (sourceEmail == null || sourcePassword == null
                || destinationEmail == null || subject == null || body == null) {
            System.out.println("Somethin null");
            throw new CouldNotSendEmailException("Couldn't send the email.");
        }
        if (sourceEmail.length() == 0 || sourcePassword.length() == 0
                || destinationEmail.length() == 0 || subject.length() == 0 || body.length() == 0) {
            System.out.println("Somethin 0");
            throw new CouldNotSendEmailException("Couldn't send the email.");
        }

        while (tentativas <= 2) {
            try {
                this.send(sourceEmail, sourcePassword,
                        destinationEmail, subject, body, attachment, attachmentName);
                return;
            } catch (CouldNotSendEmailException ex) {
                tentativas++;
            }
        }
        System.out.println("Something wrong");
        throw new CouldNotSendEmailException("Couldn't send the email.");
    }

    private void send(final String username,
            final String password, String recipient,
            String subject, String body, byte[] attachment, String attachmentName) throws CouldNotSendEmailException {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            if (attachment != null) {
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(body);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                try {
                    File file = new File("/home/safeguard/profilesReport/" + attachmentName);
                    FileOutputStream fileOutputStream;
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(attachment);
                    fileOutputStream.close();
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName("Profile.pdf");
                    multipart.addBodyPart(messageBodyPart);
                    message.setContent(multipart);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    throw new CouldNotSendEmailException("File not found Exception");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new CouldNotSendEmailException("IO Exception");
                }

            }
            Transport.send(message);
            System.out.println("Sent message");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new CouldNotSendEmailException(e.getMessage());
        }

    }

    @WebMethod(operationName = "sendSMS")
    public String sendSMS(
            @WebParam(name = "addresses") List<String> addresses,
            @WebParam(name = "senderName") String senderName,
            @WebParam(name = "message") String message) {
        SendSMS service = new SendSMS();
        HeaderHandler headerHandler = new HeaderHandler(username, password, accessKey);
        service.setHandlerResolver(new HeaderHandlerResolver(headerHandler));
        SendSMSSoapSecure port = service.getSendSMSSoapSecure();
        return port.sendSms(addresses, senderName, null, message, null);
    }

    @WebMethod(operationName = "getSMSDeliveryStatus")
    public List<DeliveryInformation> getSMSDeliveryStatus(@WebParam(name = "requestIdentifier") String requestIdentifier) {
        SendSMS service = new SendSMS();
        HeaderHandler headerHandler = new HeaderHandler(username, password, accessKey);
        service.setHandlerResolver(new HeaderHandlerResolver(headerHandler));
        SendSMSSoapSecure port = service.getSendSMSSoapSecure();
        return port.getSmsDeliveryStatus(requestIdentifier);
    }
}
