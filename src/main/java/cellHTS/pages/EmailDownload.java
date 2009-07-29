
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

import cellHTS.classes.Configuration;
import cellHTS.classes.FileParser;
import cellHTS.classes.FileCreator;
import cellHTS.components.Layout;
import cellHTS.services.ZIPStreamResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.ComponentResources;

/**
 *
 * this class/page regulates and manages downloads of results which are shared/requested through a link e.g. via email
 * mainly it regulates how many times a file can be downloaded at all. The information is persistet through a .properties file
 *
 */
public class EmailDownload {
    @Persist
    private boolean notFoundIDPath;
    @Persist
    private boolean amountDLExceeded;
    @Persist
    private String redirectURL;
    @Persist
    private boolean readyToDownload;
    @Persist
    private boolean passwordWrong;

    @Inject
    private Messages msg;
    @InjectPage
    private SuccessCellHTS2 successPage;
    @Persist
    private String emailAddress;
    //this time we use the component annotation instead of tml component
    @Component
    private Layout layout;
    @Inject
    private Request request;
    @Inject
    private ComponentResources resources;
    @Persist
    private String emailDownloadPassword;
    @Persist
    private String password;
    @Persist
    private String streamFile;
    @Persist
    private String passwordSuccessfully;
    @Persist
    private Properties propObj;
    @Persist
    private String runID;
    @Persist
    private File dlPropFile;


    /**
     *
     * when page is loaded. Parameters are obtained e.g. through generating a page link: http://...../EmailDownload/JOB12345/RUN12345
     *
     * @param jobID  the jobname id (one job is one new main analysis)
     * @param runID  the runname id (one job can have several runs)
     * @return the pageobject
     */
    public Object onActivate(String jobID,String runID) {
        this.runID=runID;

        String hostName =  request.getHeader("Host");
        for(String test : request.getHeaderNames()) {
        }
         redirectURL="http://"+hostName+resources.createPageLink("EmailDownload", true, null ).toAbsoluteURI();
        
         //init once
         notFoundIDPath=false;
         amountDLExceeded=false;      
         readyToDownload=false;       //false


       //init
        if(passwordSuccessfully==null) {
            passwordSuccessfully="";
        }

         emailAddress= msg.get("notification-email");

         String uploadPath = Configuration.UPLOAD_PATH;
         Integer maxDownloads = Integer.parseInt(msg.get("allowed-dl-numbers"));

         if(maxDownloads==null) {
             System.out.println("Property \"allowed-dl-numbers\" was not found");

         }

         File fullPath = new File(uploadPath+jobID);
         if(fullPath.exists()) {
             notFoundIDPath=false;


         }
         else {
             notFoundIDPath=true;

             return this;
         }


        


        dlPropFile =  new File(fullPath.getAbsolutePath()+"/"+".dlProperties");
        if(dlPropFile.exists()) {

             notFoundIDPath=false;
         }
         else {
             notFoundIDPath=true;


             return this;
         }

        
        //check if we didnt exceed the current max number of downloads
        //Integer currentDownloads = FileParser.readAmountFromDownloadPropertiesFile(dlPropFile,runID);
        //check if we didnt exceed the current max number of downloads
        propObj = new Properties();
        
        Integer currentDownloads=null;        
        String zipFileName=null;
        try {
            propObj.load(new FileInputStream(dlPropFile));
            currentDownloads = Integer.parseInt(propObj.getProperty(runID));
            password = propObj.getProperty(runID+"_password");
            zipFileName = propObj.getProperty(runID+"_RESULT_ZIP");
        }
        catch(Exception e) {
             e.printStackTrace();
        }
        if(currentDownloads==null||zipFileName==null) {

            notFoundIDPath=true;
            return this;
        }
        if(currentDownloads>=maxDownloads) {
            amountDLExceeded=true;

            return this;
        }
        else {

            amountDLExceeded=false;
        }

        if(password==null) {
            notFoundIDPath=true;
            return this;
        }

        readyToDownload=true;
        streamFile = zipFileName;
        return this;
    }


    public boolean getIsNotFoundIDPath() {
        return notFoundIDPath;
    }

    public boolean getIsAmountDLExceeded() {
        return amountDLExceeded;
    }

   

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     *
     * this method will be started if you submit a password string
     *
     * @return
     */
    public Object onSuccessFromEmailDownloadPasswordForm() {
       if(password.equals(emailDownloadPassword)) {
           int currDLs = Integer.parseInt(propObj.getProperty(this.runID));
           currDLs++;
           //increase the number of downloads we just started
           propObj.setProperty(this.runID,""+currDLs);
           try {
           propObj.store(new FileOutputStream(dlPropFile),"properties file for email Download information");
           }catch(Exception e) {
                e.printStackTrace();
            }    
           successPage.setZipFile(streamFile);
           passwordWrong=false;
           //TODO this does not work...no pagereload of this so use js instead here
           passwordSuccessfully="successfully entered password";
           return successPage;
       }
       else {
           passwordWrong=true;
           return this;
       }
    }

    public String getEmailDownloadPassword() {
        return emailDownloadPassword;
    }

    public void setEmailDownloadPassword(String emailDownloadPassword) {
        this.emailDownloadPassword = emailDownloadPassword;
    }

    public boolean isReadyToDownload() {
        return readyToDownload;
    }

    public void setReadyToDownload(boolean readyToDownload) {
        this.readyToDownload = readyToDownload;
    }

    public boolean isPasswordWrong() {
        return passwordWrong;
    }

    public void setPasswordWrong(boolean passwordWrong) {
        this.passwordWrong = passwordWrong;
    }

    public String getPasswordSuccessfully() {
        return passwordSuccessfully;
    }

    public void setPasswordSuccessfully(String passwordSuccessfully) {
        this.passwordSuccessfully = passwordSuccessfully;
    }
}
