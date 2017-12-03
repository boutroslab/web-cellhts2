package cellHTS.classes;

import org.apache.tapestry5.ioc.internal.util.TapestryException;

import java.util.*;
import java.io.File;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import javax.activation.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: Jan 25, 2011
 * Time: 11:20:48 AM
 * To change this template use File | Settings | File Templates.
 *
 */
public class MailTools {
    private String SMTPHOST;
    private String PORTNUMBER;
    private String smtpUser;
    private String smtpUserHost;
    private String password;

    public String getSmtpUser() {
       return smtpUser;
    }
    public String getSmtpUserHost() {
       return smtpUserHost;
    }
    public String getPassword(){
       return password;
    }
    
	
	
    //filename is the name of an attachment, if you use null it will be empty
    /**
     *
     * sends an email. Note you need an running email server on localhost from where you run the JVM
     *
     * @param recipient  NAme of recipient to send the email to
     * @param subject    subject of the email
     * @param message    Email message
     * @param from       Email address from
     * @param filename   Array of attachment files
     */
    public MailTools(String smtpHost, String smtpPort, String smtpUser, String smtpUserHost, String password) {
	 this.SMTPHOST     = smtpHost;
	 this.PORTNUMBER   = smtpPort;
	 this.smtpUser     = smtpUser;
	 this.smtpUserHost = smtpUserHost;
	 this.password     = password;
    }

     class MyAuthenticator extends Authenticator {
	String userName = null;
        String host     = null;
	String password = null;

	public MyAuthenticator() {
	}

	public MyAuthenticator(String username, String host, String password) {
		this.userName = username;
                this.host     = host;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName + "@" + host, password);
	}
};


    public void postMail(String recipient,
                         String subject,
                         String message,
                         String from,
                         String filename[]  //this is an attachment
    ) {
        try {
		final Properties props = new Properties();
		props.put("mail.smtp.host", this.SMTPHOST);
		props.put("mail.smtp.port", this.PORTNUMBER);
		props.put("mail.transport.protocol","smtp");
		props.put("mail.smtp.auth", "true");
                //props.put("mail.smtp.starttls.enable", "true");
		//props.put("mail.smtp.tls", "true");
		//props.put("mail.smtp.ssl.checkserveridentity", "true");



		MyAuthenticator auth = new MyAuthenticator(getSmtpUser(), getSmtpUserHost(), getPassword());

		Session session = Session.getDefaultInstance(props, auth);

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		msg.setSubject(subject);
		msg.setText(message);


            //filename = null;
            if (filename != null) {
            MimeMultipart content = new MimeMultipart("alternative");
            MimeBodyPart text = new MimeBodyPart();

            text.setHeader("MIME-Version", "1.0");
            text.setHeader("Content-Type", text.getContentType());
            content.addBodyPart(text);

    
		    for(String file : filename) {
                    DataSource fileDataSource = new FileDataSource(file);

                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
                    messageBodyPart.setFileName(new File(file).getName());
		    System.out.println("damn filename: " + file);

                //add the file to the content
                    content.addBodyPart(messageBodyPart);
            
		    }
            //add the content to the message
            msg.setContent(content);
           
	    }

            //msg.saveChanges();
	    Transport.send(msg);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Mail server seems to be unreachable: "+e.getMessage());
        }


    }
}
 

