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

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.*;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.ioc.Messages;
import cellHTS.classes.RInterface;
import cellHTS.classes.Configuration;
import cellHTS.dao.Semaphore;
import cellHTS.components.Layout;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;
import java.io.*;

import data.*;

/**
 *
 * this class/page contains the progressbar and on the fly information system which will
 * inform us about the state th R part is currently at.
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 04.12.2008
 * Time: 13:02:53
 * To change this template use File | Settings | File Templates.
 */

//we need livepipe and progressbar for rendering progress bars!
//this class communicates with the progressbar thread to keep it alive and updating :-) AJAX style
@Import(library={"livepipe.js","progressbar.js"})
public class Results {

    @Inject
    private Semaphore semaphore;

    @Persist
    private File runNameDir;
    @Persist
    private File jobNameDir;
    @Persist
    private String annotFile;
    @Persist
    private String descriptionFile;
    @Persist
    private String plateList;
    @Persist
    private String plateConf;
    @Persist
    private String screenLogFile;    
    @Persist
    private String channelTypes;
    @Persist
    private String channelLabel1;
    @Persist
    private String channelLabel2;
    @Persist
    private String normalMethod;
    @Persist
    private String normalScaling;
    @Persist
    private String varianceAdjust;
    @Persist
    private String useHTSAnalyzer;
    @Persist
    private String score;
    @Persist
    private String summaryMethod;
    @Persist
    private String logTransform;
    @Persist
    private String viabilityChannel;
    @Persist
    private String analysisType;
    @Persist
    private HashMap<String,String> paramMap;
    @Persist
    private RInterface rInterface;
    //this will add a potential prefix for the jobname
    @Persist
    private String jobNamePrefix;
    @InjectPage
    private CellHTS2 cellHTS2;
    @Environmental
    private RenderSupport pageRenderSupport;
    @Persist
    private boolean emailNotification;
    @Persist
    private String emailMSG;
    @Inject
    private Messages prop;
    @Persist
    private String resultZipFile;
    @Persist
    private String emailAddress;
    @Persist
    private String viabilityFunction;

    //this time we use the component annotation instead of tml component
    @Component
    private Layout layout;
    @Persist
    private String sessionFile;
    @Inject
    private Request request;


    //this holds the percentage of the progress the RInterface already calculated
    //we wrap it in an array to enable call by reference
    //we build our own json like string which should be more faster
    @Persist
    private String[] progressPercentage;

    //returns true only when R was successful
    @Persist
    private Boolean[] rSuccessStatus;

    @Inject
    private ComponentResources resources;

    //we need a variable that will only work for the first time we run the page
    //this is important for some init stuff
    @Persist
    private boolean notFirstRun;
    @Persist
    private String uploadPath;

    @Persist
    private HTSAnalyzerParameter htsAnalyzerParameter;

    /**
     * this method should be started when starting a new R cellHTS2 run ..
     *
     */
    public void initNewRun() {
        if(!notFirstRun) {


            if(System.getProperty("upload-path-webserver")!=null) {

            //get from command line
            uploadPath=System.getProperty("upload-path-webserver");


        }
        else {
            //else get from properties file
            uploadPath=prop.get("upload-path-webserver");
        }
        if(!uploadPath.endsWith(File.separator)) {
                uploadPath=uploadPath+File.separator;
        }
            //never go in here again
            notFirstRun=true;

            //this is important!!! do only init these variables once as long as the app lives
            //because we will use it as call by reference in the RInterface class
            //the RInterface will keep a Rengine obj as long as the app lives (because there can only be one)
            //and in this Rengine obj we have a function which will access the progressPercentage variable
            //so we cant change the reference (e.g. new)! or the sent messages will get lost in space
            progressPercentage=new String[1];
            rSuccessStatus = new Boolean[1];
            emailNotification=false;
            String buildType =prop.get("result-type");
            //for external version we will use email notification instead of progress bar and directly streaming the results
            if(buildType.equals("email")) {
                      emailNotification = true;
                  

            }

        }

        //if we ran through we will restart the whole process

        //overwrite everything
        runNameDir = null;
        jobNameDir=null;
        annotFile=null;
        descriptionFile=null;
        screenLogFile=null;
        plateList=null;
        plateConf=null;

        channelTypes=null;
        channelLabel1=null;        
        channelLabel2=null;
        normalMethod=null;
        normalScaling=null;
        varianceAdjust=null;
        score=null;
        summaryMethod=null;
        logTransform=null;
        analysisType=null;
        viabilityChannel=null;
        jobNamePrefix=null;
        emailAddress = null;
        sessionFile=null;
        viabilityFunction=null;

        progressPercentage[0]="0_starting job";
        rSuccessStatus[0] = false;
        paramMap=new HashMap<String,String> ();
        useHTSAnalyzer=null;
        htsAnalyzerParameter=null;
    }

    /**
     *
     * set all the important R parameters for cellHTS2 one setter method for all
     *
     * @param jobNameDir
     * @param jobNamePrefix
     * @param emailAddress
     * @param annotFile
     * @param descriptionFile
     * @param plateList
     * @param plateConf
     * @param screenLogFile
     * @param sessionFile
     * @param channelLabel1
     * @param channelLabel2
     * @param channelTypes
     * @param normalMethod
     * @param normalScaling
     * @param resultScalingTypes
     * @param summaryMethod
     * @param logTransform
     * @param viabilityChannel
     * @param viabilityFunction
     */
    public void putAll(File jobNameDir,String jobNamePrefix,String emailAddress,
                       String annotFile,String descriptionFile,String plateList,String plateConf,String screenLogFile,String sessionFile,
                       String channelLabel1,String channelLabel2,
                       ChannelTypes channelTypes, NormalizationTypes normalMethod, NormalScalingTypes normalScaling,
                       ResultsScalingTypes resultScalingTypes, SummerizeReplicates summaryMethod,LogTransform logTransform,
                       ViabilityChannel viabilityChannel,String viabilityFunction, UseHTSAnalyzer useHTSAnalyzer, HTSAnalyzerParameter htsAnalyzerParameter) {

        this.channelTypes = channelTypes.toString().split("_")[0];
        paramMap.put("channelTypes",this.channelTypes);

        this.jobNameDir=jobNameDir;
        paramMap.put("jobName",jobNameDir.getName());

        this.annotFile = annotFile;
        paramMap.put("annotFile",annotFile);
        this.descriptionFile=descriptionFile;
        paramMap.put("descriptionFile",descriptionFile);
         this.plateList=plateList;
        paramMap.put("screenLogFile",screenLogFile);
         this.screenLogFile=screenLogFile;

         paramMap.put("plateList",plateList);
        this.plateConf=plateConf;
        paramMap.put("plateConf",plateConf);
         this.channelLabel1= channelLabel1;
         paramMap.put("channelLabel1",channelLabel1);
        this.channelLabel2= channelLabel2;
        paramMap.put("channelLabel2",channelLabel2);
         this.normalMethod=normalMethod.toString();
        paramMap.put("normalMethod",this.normalMethod);
        this.normalScaling=normalScaling.toString();
         paramMap.put("normalScaling",this.normalScaling);
         this.varianceAdjust=resultScalingTypes.toString();
         paramMap.put("varianceAdjust",this.varianceAdjust);            
         this.summaryMethod=summaryMethod.toString();
         paramMap.put("summaryMethod",this.summaryMethod);
         this.logTransform= logTransform.toString();
         paramMap.put("logTransform",this.logTransform);
         this.viabilityChannel=viabilityChannel.toString();
         paramMap.put("viabilityChannel",this.viabilityChannel);
         this.sessionFile = sessionFile;
         paramMap.put("sessionFile",this.sessionFile);
         this.viabilityFunction=viabilityFunction;
         paramMap.put("viabilityFunction",this.viabilityFunction);
         this.useHTSAnalyzer=useHTSAnalyzer.toString();
         paramMap.put("useHTSAnalyzer",this.useHTSAnalyzer);
         this.htsAnalyzerParameter=htsAnalyzerParameter;


        //generateHTSAnalyzerParameters();
        String geneCollectionParameters = htsAnalyzerParameter.generateGeneCollectionParameters();
        if(geneCollectionParameters.equals("")) {
            throw new TapestryException("geneCollectionParameters is empty maybe organism names have changed/extended in HTSAnalyzer???",null);
        }
        paramMap.put("geneCollectionSetup",htsAnalyzerParameter.generateGeneCollectionParameters());
        paramMap.put("hTSAnalyzerParams",htsAnalyzerParameter.toHTSanalyzeRParameterString());


        this.jobNamePrefix = jobNamePrefix;
        this.emailAddress=emailAddress;
        emailMSG="Thank you for using web cellHTS2.  The results will be sent to "+emailAddress;


    }

    

    /**
     *
     * exces the javascript part (the progress bar), starts the R part in a new thread for live progress bar update
     *
     * @param writer
     */
    @AfterRender
    void afterRenderingProcess(MarkupWriter writer) {
        //make a new runid which is unique

            //add stuff from app.properties
        paramMap.put("rserve-host",prop.get("rserve-host"));
        paramMap.put("rserve-port",prop.get("rserve-port"));
        paramMap.put("rserve-username",prop.get("rserve-username"));
        paramMap.put("rserve-password",prop.get("rserve-password"));
        
            try {
                String jobName = jobNameDir.getName();
                //use jobname as the heading in the html result file
                paramMap.put("htmlResultName",jobName);
                runNameDir = File.createTempFile(jobName+"_RUN", File.separator, new File(uploadPath));
                
                //del the dir...we want to create a file instead
                runNameDir.delete();
                //do not create the dir...R must do it because otherwise we get problems with file permissions
                //runNameDir.mkdirs();

                //add a possible jobname prefix

                //generate new jobname
                paramMap.put("runNameDir",runNameDir.getAbsolutePath());

            } catch (IOException e) {
                String exceptionString = runNameDir.getName();
                TapestryException fileUploadError = new TapestryException("somethings wrong with creating a new run id:" + exceptionString, null);
                throw fileUploadError;
            }

        //how to call the zipfile
            String runName = runNameDir.getName();
             if (this.jobNamePrefix != null) {
                 if (!this.jobNamePrefix.equals("")) {
                     //spaces or slashes will kill valid unix filename description
                            this.jobNamePrefix=this.jobNamePrefix.replaceAll("[ \\/]+","_");
                            runName = this.jobNamePrefix + "_" + runName;
                     //how to call the html result page
                     paramMap.put("htmlResultName",this.jobNamePrefix);

                 }
             }

            resultZipFile = jobNameDir+File.separator + runName+".zip";
        //only show progress bar etc if we are not running at email notification
        if (!emailNotification) {
            //create a link for the javascript polling...this will be the java method in this obj here which will be called from js
            Link pollingLink = resources.createEventLink("getProgressPercentage", false);
            //create another link which will be executed from javascript if we have reached 100% in progress
            //the link will be a new page which we give
            //we need three things in our new page for displatyin success: path to zip files, path to save the zip and jobname
            //we are passing these parameters as an array to the page
            ArrayList<String> p = new ArrayList();




            //add the link to the js success url
            p.add(resultZipFile);
            Link successLink = resources.createPageLink("SuccessCellHTS2", true, p.toArray());

            String pollingURI = pollingLink.toAbsoluteURI();
            String successURI = successLink.toAbsoluteURI();

            //create a new progress bar and poll the url every xx seconds for a new progress response
            pageRenderSupport.addScript("(new ProgressBar('progress_bar','progress_output',{interval: 1.5 })).poll('%s','%s',1.5);", pollingURI, successURI);
            //start the R script!
        }
        else {
            //this is the link which the user can download (through a page emalDownloadLink which controls access)
            // the results and this is provided in the email results
            //i made this mechanism because a lot of times the attachments are too big
           String hostName =  request.getHeader("Host");
           String tempArr[] = {jobNameDir.getName(),runNameDir.getName()};
           hostName="http://"+hostName+resources.createPageLink("EmailDownload", true, tempArr ).toAbsoluteURI();


           paramMap.put("emailDownloadLink",hostName);
            //how many times you are allowed to download this archive in email
           paramMap.put("allowed-dl-numbers",prop.get("allowed-dl-numbers"));
        }

        
        //before we start check essential parameters here
        String errMsg=checkEssentialParams();
        if(errMsg!=null) {             
            throw new TapestryException(errMsg, null);
        }
        String maintainersMail= prop.get("notification-email");
        boolean sendErrorEmail = false;
        if(prop.get("send-exception-notification-mails").equals("YES")) {
            sendErrorEmail = true;
        }
        rInterface = new RInterface(paramMap,progressPercentage,rSuccessStatus,resultZipFile,semaphore,emailNotification,emailAddress,maintainersMail,sendErrorEmail,uploadPath);

        //run it in a seperate thread...the progressPercentage arr will be updated and is
        //visible outside (in here) through a call by reference
        rInterface.start();
        
    }

    /**
     *
     * this method will be called by the Progress Bar javascript ajax poll and should return the current progress number (0-100)
     * and a possible message...
     * @return
     */
    public StreamResponse onGetProgressPercentage() {
        int percentage;
        percentage = Integer.parseInt(progressPercentage[0].split("_")[0]);

        return new TextStreamResponse("text/html",progressPercentage[0]);

    }

    /**
     *
     * if we leave the result page stop all threads and reset everything for another future run
     *
     * @return
     */
    public Object onActionFromBackLinkCellHTS2Page() {               
        rSuccessStatus[0] = false;
        //if we are using progressbar as a resulttype and the thread is not run through
        //and we are pressing the back button the run (thread should be canceled!)
        if(!emailNotification && rInterface!=null) {
            if(rInterface.isAlive()) {
                
                //stop it
                rInterface.killMe();

            }
        }
        return cellHTS2;
    }
    public Object onActionFromNewAnalysisCellHTS2Page() {
        
        cellHTS2.setNotNewRun(false);
        return cellHTS2;
    }

    /**
     *
     * bugfix test method
     *
     * @return
     */
    public String checkEssentialParams() {
        String errorMsg=null;
        if(this.runNameDir==null) {
            errorMsg="no dir for the run was generated";
        }
        else if (this.jobNameDir==null) {
            errorMsg = "no dir for the jobname was specified";
        }        
        else if(this.descriptionFile==null) {
            //this is essential...we will automatically generate one of not uploaded
            errorMsg="no descriptionfile";
        }
        else if(this.plateList==null) {
            errorMsg="no datafiles/platelist";
        }
        else if(this.plateConf==null) {
            errorMsg="no plate Configuration file";
        }
        else if(this.normalScaling==null) {
            errorMsg="no normalization scaling method";
        }
        else if(this.varianceAdjust==null) {
            errorMsg="no variance adjusting method";
        }
        else if(this.summaryMethod==null) {
            errorMsg="no summary method";
        }
        else if(this.logTransform==null) {
            errorMsg="no log transform method";
        }
        //else if (this.sessionFile==null)  {
        //    errorMsg="you forgot to create a session file before instantiating this class";
        //}
        if(errorMsg!=null) {
            errorMsg+=" defined";
        }

        return errorMsg;
    }

    public boolean getIsEmailNotification() {
        return emailNotification;
    }

    public String getEmailMSG() {
        return emailMSG;
    }

}
