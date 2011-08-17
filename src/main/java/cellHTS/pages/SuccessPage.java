package cellHTS.pages;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import cellHTS.services.ZIPStreamResponse;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 29.09.2009
 * Time: 16:17:41
 * To change this template use File | Settings | File Templates.
 */
public class SuccessPage {
    @InjectPage
    private SuccessCellHTS2 successPage;
    @InjectPage
    private CellHTS2 cellHTS2;
    @SessionState
    private String galaxyURLState;
    @Inject
     private Request request;

    @Persist
    private String retrieveURL;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;


    @InjectPage
    private DirectDownloadCellHTS2Experiment directDownloadCellHTS2Experiment;

    @Inject
    private Messages msg;

    //check existence
    private boolean galaxyURLStateExists;


    public void setZipFile(String file)  {
        successPage.setZipFile(file);
    }
    public Object onActionFromDownloadResults() {
        if(successPage.getZipFile()!=null) {
            return successPage;
        }
        else {
            return this;
        }
    }
    public Object onActionFromGoBackwebCellHTS2() {
        cellHTS2.activatedFromOtherPage(this.getClass().getName());
        return cellHTS2;
    }

    public String getIsOK() {
       if(galaxyURLStateExists) {
           return "OK";
       }
       return "FAIL";
    }
    public String getRetrieveURL() {
        
        String file =    successPage.getZipFile();
        Pattern jp = Pattern.compile("JOB(\\d+)");
        Pattern rp = Pattern.compile("RUN(\\d+)");

        Matcher m;

        m = jp.matcher(file);

        String jobID=null;
        if(m.find()) {
            jobID = m.group(1);
        }
        m = rp.matcher(file);

        String runID=null;
        if(m.find()) {
            runID = m.group(1);
        }
        //create proper password for this job-run combination


        return directDownloadCellHTS2Experiment.getHost()+"/"+jobID+"/"+runID+"/"+getPassword(jobID,runID) ;
    }

    public boolean getIsFromGalaxy() {
        return galaxyURLStateExists;
    }

    public String getGalaxyURLState() {
        return galaxyURLState;
    }

    public void setGalaxyURLState(String galaxyURLState) {
        this.galaxyURLState = galaxyURLState;
    }
    //TODO: should we check for how often the file has been transfered to galaxy and then stop it when it has been transfered too much?
    //TODO: this method has to be outsourced into DLPropertiesDAO class
    public String getPassword(String jobID,String runID) {
        jobID="JOB"+jobID;
        runID=jobID+"_RUN"+runID;
        String uploadPath=msg.get("upload-path-webserver");
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
