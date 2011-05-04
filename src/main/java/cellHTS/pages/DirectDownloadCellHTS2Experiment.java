package cellHTS.pages;

import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.services.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.security.MessageDigest;

import cellHTS.services.GalaxyStreamResponse;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: Apr 27, 2011
 * Time: 2:08:53 PM
 * To change this template use File | Settings | File Templates.
 */

//this class is for communitcating or better streaming toptable files for galaxy
//note: this does not work with localhost installations unless the galaxy installation knows the host name
// a valid url which will be POSTed to galaxy looks like:
// http://web-cellhts2.dkfz.de/cellHTS-java/directdownloadcellhts2experiment/8875493731422896122/4526436150789093070/Gp9LdaDsdsD22223
public class DirectDownloadCellHTS2Experiment {

    @Inject
    private Messages msg;
    @Inject
    private Request request;
    @Inject
    private ComponentResources resources;
    @SessionState
    private String galaxyURLState;
     //check existence
    private boolean galaxyURLStateExists;

   //TODO: invent an mechanism to make this more secure e.g. through session IDs comparison...generate unique session id at startup or such.
    public StreamResponse onActivate(String jobID,String runID,String passwordSubmitted) {
        if(!galaxyURLStateExists)   {
            return null;
        }
        String passwordPlain = getPassword(jobID,runID);

       //plain and simple password check
        if(!passwordPlain.equals(passwordSubmitted)) {
            return null;
        }

       //TODO: limit the download times by writing to the .dlPropertiesfile here.
         File file = new File(msg.get("upload-path")+"JOB"+jobID+"_"+"RUN"+runID+File.separator+"in"+File.separator+"topTable.txt");
       System.out.println("Full path: "+file.getAbsolutePath());
        try {
            FileInputStream iStream = new FileInputStream(file);
            return new GalaxyStreamResponse(iStream,file.getName() );
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public String getHost() {
        String hostName = request.getHeader("Host");
        String fullHost ="http://"+hostName+resources.createPageLink("DirectDownloadCellHTS2Experiment",true).toAbsoluteURI();
        return fullHost;
    }


    //TODO: should we check for how often the file has been transfered to galaxy and then stop it when it has been transfered too much?
    //TODO: this method has to be outsourced into DLPropertiesDAO class
    public String getPassword(String jobID,String runID) {
        jobID="JOB"+jobID;
        runID=jobID+"_RUN"+runID;
        String uploadPath=msg.get("upload-path");
        File fullPath = new File(uploadPath+jobID);
        File dlPropFile =  new File(fullPath.getAbsolutePath()+File.separator+".dlProperties");
        //System.out.println(dlPropFile);
        Properties propObj = new Properties();
        try {
            propObj.load(new FileInputStream(dlPropFile));
            Integer currentDownloads = Integer.parseInt(propObj.getProperty(runID));
            String password = propObj.getProperty(runID+"_password");
            return password;

        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
