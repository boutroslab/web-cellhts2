/*
 * //
 * // Copyright (C) 2009 Boutros-Labs(German cancer research center) b110-it@dkfz.de
 * //
 * //
 * //    This program is free software: you can redistribute it and/or modify
 * //    it under the terms of the GNU General Public License as published by
 * //    the Free Software Foundation, either version 3 of the License, or
 * //    (at your option) any later version.
 * //
 * //    This program is distributed in the hope that it will be useful,
 * //    but WITHOUT ANY WARRANTY; without even the implied warranty of
 * //    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * //
 * //    You should have received a copy of the GNU General Public License
 * //    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

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
 *
 * This class provides tools for email sending through a email server
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 27.03.2009
 * Time: 15:50:35
 *
 */
public class MailTools {
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
    public void postMail(String recipient,
                         String subject,
                         String message,
                         String from,
                         String filename[]  //this is an attachment
    ) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "localhost");

            Session session = Session.getDefaultInstance(props);

            Message msg = new MimeMessage(session);

            InternetAddress addressFrom = new InternetAddress(from);
            msg.setFrom(addressFrom);
            InternetAddress addressTo = new InternetAddress(recipient);
            msg.setSubject(subject);
            msg.setRecipient(Message.RecipientType.TO, addressTo);


            MimeMultipart content = new MimeMultipart("alternative");


            MimeBodyPart text = new MimeBodyPart();

            text.setText(message);
            text.setHeader("MIME-Version", "1.0");
            text.setHeader("Content-Type", text.getContentType());
            content.addBodyPart(text);

            if (filename != null) {
                for(String file : filename) {
                    DataSource fileDataSource = new FileDataSource(file);

                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
                    messageBodyPart.setFileName(new File(file).getName());


                //add the file to the content
                    content.addBodyPart(messageBodyPart);
                }
            }

            //add the content to the message
            msg.setContent(content);

            Transport.send(msg);
        } catch (MessagingException e) {                 

            e.printStackTrace();
            throw new RuntimeException("did you forget to set up a mail server properly???");

        }


    }
}
