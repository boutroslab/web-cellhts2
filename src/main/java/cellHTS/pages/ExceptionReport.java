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

package cellHTS.pages;

import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;
import cellHTS.classes.MailTools;

import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 *
 * this class is called everytime an exception occurs and will be
 * used to redirect the exception to a email
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 10.11.2008
 * Time: 11:09:37
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionReport implements ExceptionReporter {

    @Inject
    private Messages msg;

    private String error;

    public void reportException(Throwable exception) {
        error = exception.getMessage();

        String hostname = "";

        try {
            InetAddress addr = InetAddress.getLocalHost();
            // Get hostname
            hostname = addr.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (msg.get("send-exception-notification-mails").equals("YES")) {
            MailTools mailTool = new MailTools();

            mailTool.postMail(msg.get("notification-email"),
                    "cellHTS2-java tapestry throwed an exception",
                    error,
                    "cellHTS2-results@" + hostname,
                    null   //no file attached
            );
        }
    }


    public String getError() {
        return error;
    }


}

