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

import java.io.*;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipOutputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.rosuda.REngine.*;

import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.Link;
import cellHTS.dao.Semaphore;

import javax.mail.MessagingException;



//the R program is running in the background
/**
 *
 * this class represents a R cellHTS2 run on the Rserve. It contains the complete cellHTS script and results zipping and
 * sending of the results
 * we have to make a new thread out of it that we can access the progressPercentage variable to update an progress bar while
 *
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 27.03.2009
 * Time: 15:50:35
 *
 */
public class RInterface extends Thread {

    private HashMap<String, String> stringParams;
    //we need this for a call by reference..we will read (only) this variable from outside this thread
    //you can only emulate call by reference with an arry
    private String[] progressPercentage;
    //call by refernce will return true only if R cellHTS2 was successful
    private Boolean[] successBool;
    private String link;
    private REngine rEngine;
    private ArrayList<Pattern> patterns;
    private ArrayList<Integer> patternPercentage;
    private String completeOutput;     
    private String rOutputFile;
    private String rOutputScriptFile;
    private Semaphore semaphore;
    private boolean emailNotification;
    private boolean dumpCellHTS2Obj2File;
    private String resultZipFile;
    private String htsResultZipFile;
    private String emailAddress;
    private long threadID;
    private boolean sendErrorEmail;
    //these two if we use email notification
    MailTools postMailTools;
    //get the hostname
    private String hostname;
    //send notification mails to the maintainer(s) of this tool in case of error
    private String maintainEmailAddress;
    private String uploadPath;
    private boolean builtCellHTS2ObjToFile;
    private final String CELLHTS2_OBJ_DUMP_FILENAME ="CELLHTS2_OBJ_DUMP.TXT";
    private final String HTSANALYZER_OUTPUT_DIR="htsanalyer_out";
    private final String HTSANALYZER_RESULTS_DIR = "HTSanalyzerReport";

    /**
     *
     * Constructor
     *
     * @param map a HashMap structure with all the important R input parameters and presetted variables
     * @param progressPercentage the progressPercentage, this is packed into a array obj to simulate call by reference because we want to see this progress percentage outside of this thread
     * @param successBool call by reference (therefore packed into a array object) if the run was successful or not
     * @param resultZipFile filename of the results zipped into a file
     * @param semaphore  this is a semaphore object to control how many instances are allowed to run in parallel
     * @param eMailNotification should emails be sent or not
     * @param emailAddress name of the recipient for the notification
     * @param maintainEmailAddress  email adress which occurs in the email in the from section and where to send questions etc to
     * @param sendErrorEmail should a email sent to the developer if Rserve reported a error?
     */
    public RInterface(HashMap<String, String> map, String[] progressPercentage,Boolean [] successBool,String resultZipFile,Semaphore semaphore,boolean eMailNotification,String emailAddress,String maintainEmailAddress,boolean sendErrorEmail,String uploadPath) {

        this.stringParams = map;
        this.progressPercentage = progressPercentage;
        this.completeOutput = new String("");
        this.successBool=successBool;
        this.semaphore =  semaphore;
        this.emailNotification=eMailNotification;
        this.resultZipFile=resultZipFile;
        this.htsResultZipFile=new File(resultZipFile).getParent()+File.separator+new File(stringParams.get("runNameDir")).getName()+"_HTSAnalyzerResults.zip";
        this.emailAddress=emailAddress;
        this.sendErrorEmail=sendErrorEmail;
        this.uploadPath = uploadPath;
         if(this.emailNotification) {
              postMailTools = new MailTools();
             try {
                InetAddress addr = InetAddress.getLocalHost();
                // Get hostname
                hostname = addr.getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
             this.maintainEmailAddress=maintainEmailAddress;
         }
        dumpCellHTS2Obj2File=false;
        builtCellHTS2ObjToFile=false;

        //if we are running HTSAnalyzer we will dump the cellhts2 obj to a file
        //so we can later use it in the HTSAnalyzer as input...create somekind of pipelining system
        if(stringParams.get("useHTSAnalyzer").equals("YES")) {
            dumpCellHTS2Obj2File=true;
        }

    }

    public RInterface() {

    }

    /**
     *
     * get the cellHTS2 version from the R server
     *
     * @return  a string containing the R version
     */
    public String getCellHTS2Version() {
        String version="not found";
        String rVersion="not found";

        try {                 
            //REngine eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine", new String[1], new REngineStdOutput(), false);
            REngine eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine");
            eng.parseAndEval("library(cellHTS2)", null, false); // don't return the result - it has a similar effect to voidEval in Rserve

            //REXP r = eng.parseAndEval("try(sessionInfo(),silent=TRUE)");
            String output=eng.parseAndEval("paste(capture.output(print(sessionInfo())),collapse=\"\\n\")").asString();
            //String output = r.asString();
            System.out.println("cellHTS2 version: "+output);

            //c.voidEval("library(cellHTS2)");
            //String output=eng.parseAndEval("paste(capture.output(print(sessionInfo())),collapse=\"\\n\")").asString();               

            Pattern p1 = Pattern.compile("R version ([\\d\\.]+) ");
            Matcher m1 = p1.matcher(output);

            if(m1.find()) {

                rVersion =  m1.group(1);
            }

            Pattern p2 = Pattern.compile("cellHTS2_([\\d\\.]+)");
            Matcher m2 = p2.matcher(output);
            if(m2.find()) {

                version =  m2.group(1);
                version = version+" (R:"+rVersion+")";
            }
            eng.close();
            
        }catch(Exception e) {e.printStackTrace();}
        

        return version;
    }

    /**
     *
     * threads run method
     *
     */
    public void run() {


        //run a complete cellHTS2 analysis and send results by mail
        successBool[0]=false;
        progressPercentage[0]="0_starting cellHTS2 analysis job";
        boolean success = runCellHTS2Analysis();    
        //if we have email notification run everything after another and send mails as soon as things have been finisheing
        if(success && emailNotification ) {
            sendcellHTS2SuccesMail();
            if(builtCellHTS2ObjToFile) {
                if(runHTSAnalyzeR()) {
                    sendHTSAnalyzerSuccessMail();
                }
            }
             return;
        }
        //if we are using progressbar: ...
        
        //if we are only analysing using cellhts2 return stream now
       if(success && !builtCellHTS2ObjToFile) {
           progressPercentage[0]="100_successfully done";
           successBool[0]=true;
           return;
       }
       // if we analysing using HTSAnalyzer...dont show 100 percent but stream everything alltogether in the end
       else if(success && builtCellHTS2ObjToFile) {            
           //dont show 100 percent
            if(runHTSAnalyzeR()) {
            //quick fix ...we will zip everything again because we can only stream one file and this is static and cannot be changed
           //because its already in javascript
                addHTSAnalyzerResultsToResultsZipFile();
           //now show in the end...so stream will started
                progressPercentage[0]="100_successfully done";
                successBool[0]=true;
                return;
            }
           else {
                //do not add the HTSAnalyzer Results but stream back the cellHTS2 results
                progressPercentage[0]="100_cellHTS2 was successful but HTSAnalyzer was unsuccessful (see HTSAnalyzer output): "+progressPercentage[0];
                successBool[0]=true;
                return;
            }
       }



    }



    public boolean runCellHTS2Analysis() {

        String queueFullMsg;
        queueFullMsg="99_queue is full, waiting for a free slot...hold on! (dont close the window)!";

        threadID = getId();
        
        //check if we still have place before running
        semaphore.p(progressPercentage,queueFullMsg,threadID);

        String jobID = stringParams.get("jobName");

        //make a new connection to the R server Rserve
        try {
            REngine eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine");
            //REngine eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine", new String[1], new REngineStdOutput(), false);
            setRengine(eng);
        }catch(Exception e) {
            String exceptionText = "failed making connection to Rserver maybe you forgot to start it \"R CMD Rserve\" ";//)+e.printStackTrace());
            exceptionText+="Note: this currently only works starting the RServer on the same server as this java is started from";
            progressPercentage[0]="101_"+exceptionText;
            e.printStackTrace();

            sendNotificationToMaintainer(e.getMessage(),jobID);
            sendNotificationToUser("General server problems. Please get in contact with program maintainers",jobID);
            return false;
            //throw new TapestryException(exceptionText, null);



        }
            String debugString="";
            String cmdString;
            String outputDir = stringParams.get("runNameDir");

            try {
                //TODO: this code is ugly and not elegant. Better: write the R Script into a file with VARIABLE SPACERS, load it here and replace all the spacers with the settings here
                //store original location where we started r
                cmdString= "orgDir=getwd()";
                debugString+=cmdString+"\n";
                voidEval(cmdString);
                //first we have to change to the Indir in order to make the R cellHTS script working
                cmdString= "setwd(\""+uploadPath+stringParams.get("jobName")+"\")";
                debugString+=cmdString+"\n";
                voidEval(cmdString);

                //assign java variables to our R interface

                cmdString="Indir=\""+uploadPath+stringParams.get("jobName")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("Indir", uploadPath+stringParams.get("jobName"));
                //create a outputfile for the results  under the "root" out dir ..thats the only location where it is sure
                //that we have rite permissions!
                
                stringParams.put("outputDir",outputDir);
                String evalOutput = "dir.create(\""+outputDir+"\", recursive = TRUE)";

                voidEval(evalOutput);


                rOutputFile=outputDir+File.separator+"R_OUTPUT.TXT";
                rOutputScriptFile=outputDir+File.separator+"R_OUTPUT.SCRIPT";

                //voidEval("options(warn=1)");
                String openFile = "zz <- file(\""+rOutputFile+"\", open=\"w\")";
                cmdString=openFile ;
                debugString+=cmdString+"\n";
                voidEval(openFile);

                //comment the next three lines for debugging
                //get messages not the output!
                String sinkMsg= "sink(file=zz,type=\"message\" )";
                cmdString=sinkMsg ;
                debugString+=cmdString+"\n";
                voidEval(sinkMsg);
                
               //how to call the htmls result page
                cmdString="Name=\""+stringParams.get("htmlResultName")+"\"";
                debugString+=cmdString+"\n";
                getRengine().assign("Name", stringParams.get("htmlResultName"));  

                cmdString="Outdir_report=\""+outputDir+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("Outdir_report", outputDir);
                //R has boolean support which is TRUE and FALSE and NOT! Strings "TRUE" or "FALSE"

                if(stringParams.get("logTransform").equals("NO")) {
                    cmdString="LogTransform=FALSE" ;
                    debugString+=cmdString+"\n";
                    voidEval("LogTransform=FALSE");
                }
                else {
                    cmdString="LogTransform=TRUE" ;
                    debugString+=cmdString+"\n";
                    voidEval("LogTransform=TRUE");
                }

                cmdString= "PlateList=\""+stringParams.get("plateList")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("PlateList", stringParams.get("plateList"));

                cmdString="Plateconf=\""+stringParams.get("plateConf")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("Plateconf", stringParams.get("plateConf"));

                cmdString="Description=\""+stringParams.get("descriptionFile")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("Description", stringParams.get("descriptionFile"));    //if we did not submit one we will generate one automaically
                cmdString="NormalizationMethod=\""+stringParams.get("normalMethod")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("NormalizationMethod", stringParams.get("normalMethod"));
                cmdString="NormalizationScaling=\""+stringParams.get("normalScaling")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("NormalizationScaling", stringParams.get("normalScaling"));
                cmdString= "VarianceAdjust=\""+stringParams.get("varianceAdjust")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("VarianceAdjust", stringParams.get("varianceAdjust"));
                cmdString="SummaryMethod=\""+stringParams.get("summaryMethod")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("SummaryMethod", stringParams.get("summaryMethod"));
                cmdString= "Screenlog=\""+stringParams.get("screenLogFile")+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("Screenlog",stringParams.get("screenLogFile"));

                //this should not be selected from the menue ...TODO: maybe we should add it later
                cmdString="Score=\""+Configuration.scoreReplicates+"\"" ;
                debugString+=cmdString+"\n";
                getRengine().assign("Score",Configuration.scoreReplicates);
                if (stringParams.get("annotFile") != null) {
                    cmdString="Annotation=\""+stringParams.get("annotFile") +"\"" ;
                    debugString+=cmdString+"\n";
                    getRengine().assign("Annotation", stringParams.get("annotFile"));
                }
                
                //TODO:make case here single channel or dual channel script


                try {
                if (stringParams.get("channelTypes").equals("single")) {
                    //TODO: this is somehow ugly code and could be more beautiful..it was written under time pressure :-(
                    //TODO: check if the REXP isnull checking works at all


                    progressPercentage[0]="15_loading cellHTS2 lib";
                    cmdString="library(cellHTS2)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);
                    progressPercentage[0]="20_reading plate list";
                    cmdString="x=readPlateList(PlateList, name = Name, path = Indir)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);
                    progressPercentage[0]="25_configuring layout";                    
                    cmdString="x=configure(x, descripFile=Description, confFile=Plateconf, logFile=Screenlog,path=Indir)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);
                    progressPercentage[0]="45_normalizing plates";
                    cmdString="xn=normalizePlates(x, scale =NormalizationScaling , log =LogTransform,method=NormalizationMethod, varianceAdjust=VarianceAdjust)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);
                    progressPercentage[0]="47_comparing to cellHTS";
                    cmdString="comp=compare2cellHTS(x, xn)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);
                    progressPercentage[0]="50_scoring replicates";
                    cmdString="xsc=scoreReplicates(xn, sign = \"-\", method = Score)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="60_summerizing replicates";
                    cmdString="xsc=summarizeReplicates(xsc, summary = SummaryMethod)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="65_scoring data";
                    cmdString="scores=Data(xsc)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="80_quantiling";
                    cmdString="ylim=quantile(scores, c(0.001, 0.999), na.rm = TRUE)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="81_writing the report....";

                   
                    if (stringParams.get("annotFile") != null) {
                       progressPercentage[0]="82_annotating data";
                        cmdString="xsc=annotate(xsc, geneIDFile = Annotation)";
                        debugString+=cmdString+"\n";
                        voidEval(cmdString);
                        //store the cellhts2 obj to file so we can later use it for pipelining purpose e.g. for
                        //after processing with HTSAnalyzer

                        if(dumpCellHTS2Obj2File) {
                            progressPercentage[0]="95_dumping the cellHTS2 object to file";
                            String cellHTS2ObjFile = outputDir+File.separator+CELLHTS2_OBJ_DUMP_FILENAME;
                            cmdString=String.format("dumpObj = annotate(xn, geneIDFile = Annotation)");
                            System.out.println(cmdString);
                            debugString+=cmdString+"\n";
                            voidEval(cmdString);
                            cmdString=String.format("save(dumpObj, file=\"%s\")",cellHTS2ObjFile);
                            //System.out.println(cmdString);
                            debugString+=cmdString+"\n";
                            voidEval(cmdString);
                            builtCellHTS2ObjToFile=true;
                        }
                    }

                       progressPercentage[0]="96_writing the output";
                        //this is for cellHTS2 < 2.7.9
                       // voidEval("out = writeReport(cellHTSlist = list(raw = x, normalized = xn, scored = xsc), outdir = Outdir_report, force = TRUE, plotPlateArgs = list(xrange = c(0.5,3)), imageScreenArgs = list(zrange = c(-4, 8), ar = 1),,progressReport=FALSE)");
                       //this is for the new cellHTS2 >=  2.7.9
                       cmdString="out=writeReport(raw = x, normalized = xn, scored = xsc, outdir = Outdir_report, force = TRUE, settings = list(xrange = c(0.5,3),zrange = c(-4, 8), ar = 1))";
                       debugString+=cmdString+"\n";
                       voidEval(cmdString);




                        //progressPercentage[0]="100_successfully done";
                        //successBool[0]=true;
                        //return true;


                    
                } else {
                    //this is the path for dual channel scripts
                    progressPercentage[0]="15_loading cellHTS2 lib";
                    cmdString="library(\"cellHTS2\")";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="20_reading plate list";
                    cmdString="x = readPlateList(PlateList,name=Name,path=Indir)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="25_configuring layout";
                    cmdString="x = configure(x , descripFile=Description, confFile=Plateconf, logFile=Screenlog,path=Indir)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="45_normalizing plates";
                    cmdString="xp = normalizePlates(x, log=LogTransform, scale=NormalizationScaling, method=NormalizationMethod, varianceAdjust=VarianceAdjust)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);
                    
                    progressPercentage[0]="60_summerizing channels";


                    if(stringParams.get("viabilityChannel").equals("NO")) {
                        cmdString="xs = summarizeChannels(xp)";
                        debugString+=cmdString+"\n";
                        voidEval(cmdString);
                    }
                    else {
                        if(stringParams.get("viabilityFunction")!=null) {
                          cmdString ="ViabilityMethod = "+stringParams.get("viabilityFunction");
                        }
                        else {
                        //this is our standard viability function
                            cmdString="ViabilityMethod = function(r1, r2) {ifelse(r2>(-1), -r1, NA)}";
                        }
                        debugString+=cmdString+"\n";
                        voidEval(cmdString);

                        cmdString="xs = summarizeChannels(xp, fun = ViabilityMethod)";
                        debugString+=cmdString+"\n";
                        voidEval(cmdString);
                    }
                    cmdString="xn = xs";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    cmdString="xn@state[\"normalized\"] = TRUE";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="65_scoring replicates";
                    cmdString="xsc = scoreReplicates(xn, sign = \"-\", method = Score)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="70_summerizing replicates";
                    cmdString="xsc = summarizeReplicates(xsc, summary = SummaryMethod)";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);

                    progressPercentage[0]="74_writing the report....";
                    if (stringParams.get("annotFile") != null) {
                        progressPercentage[0]="78_annotating data";
                       cmdString="xsc = annotate(xsc, geneIDFile = Annotation)";
                       debugString+=cmdString+"\n";
                       voidEval(cmdString);
                         //store the cellhts2 obj to file so we can later use it for pipelining purpose e.g. for
                        //after processing with HTSAnalyzer

                        if(dumpCellHTS2Obj2File) {
                            progressPercentage[0]="75_dumping the cellHTS2 object to file";
                            String cellHTS2ObjFile = outputDir+File.separator+CELLHTS2_OBJ_DUMP_FILENAME;
                            cmdString=String.format("dumpObj = annotate(xn, geneIDFile = Annotation)");
                            System.out.println(cmdString);
                            debugString+=cmdString+"\n";
                            voidEval(cmdString);
                            cmdString=String.format("save(dumpObj, file=\"%s\")",cellHTS2ObjFile);
                           // System.out.println(cmdString);
                            debugString+=cmdString+"\n";
                            voidEval(cmdString);
                            builtCellHTS2ObjToFile=true;
                        }
                    }
                    progressPercentage[0]="76_writing the output";
                    //this is for cellHTS2 < 2.7.9
                    //voidEval("out = writeReport(cellHTSlist = list(raw = x, normalized = xn, scored = xsc),imageScreenArgs = list(zrange = c(-4, 4)), plotPlateArgs=list(), outdir=Outdir_report, force=TRUE ,progressReport=FALSE)");
                    //this is for the new cellHTS2 >=  2.7.9
                    cmdString="out = writeReport(raw = x, normalized = xn, scored = xsc,settings = list(zrange = c(-4, 4),xrange = c(0.5,3)), outdir=Outdir_report, force=TRUE )";
                    debugString+=cmdString+"\n";
                    voidEval(cmdString);


                }
                                 //close the outputsteram
                   

                }catch(REngineException e) {
                    String step = progressPercentage[0].split("_")[1];
                    String tempString="101_Error occured at step:"+step+"<br/>Please consult R logfile under:<br/>"+rOutputFile+"<br/> if debugging is on also run the script "+rOutputScriptFile ;
                    //close the logging and the logfile before printing it or you will lose information!

                    String msg = e.getMessage();
                    String requestErrorDesc = e.getLocalizedMessage();


                    //uncomment try catch block for debugging
                    try {

                        voidEval("sink()");
                    } catch(REngineException m) {
                        tempString+=" <br/>AND Error occured closing the R_OUTPUTSTREAM (this will be 99% caused by a Rserve dynlib crash):maybe your logfile isnt complete!...which will be a bad thing:<br/>One reason is not correctly formatting your annotation files under mac, e.g. saving it as a dos text file will bring Rserve to make segfault<br/>another reason is the use of Rserve <0.6";
                    } 
                    progressPercentage[0]=tempString+"<br/><br/> <FONT COLOR=\"red\">received error Messages:"+getErrorMsgFromRLogfile(new File(rOutputFile))+"</FONT>"+"<br/>";
                    progressPercentage[0]+="msg:"+msg+"<br/>errorDesc:"+requestErrorDesc;//+"<br/>returnCode:"+returnCode+"<br/>";
                    FileCreator.stringToFile(new File(rOutputScriptFile),debugString);
                    //add script to the results zip file                          

                   sendNotificationToMaintainer(progressPercentage[0],jobID);
                   sendNotificationToUser("General Rserve problem:\n"+" at step: "+step+"\n"+msg+"  "+requestErrorDesc+" \nPlease get in contact with program maintainers",jobID);

                    getRengine().close();
                    semaphore.v(threadID);
                    return false;
                }



                    


            } catch (Exception e) {
                progressPercentage[0]="101_General error occured: "+e.getMessage();
                sendNotificationToMaintainer(progressPercentage[0],jobID);
                sendNotificationToUser("General exception occured. Please get in contact with a program maintainer soon, possible R errormsg: "+getErrorMsgFromRLogfile(new File(rOutputFile)),jobID);
                getRengine().close();
                semaphore.v(threadID);
                return false;
            }
       
        try {
            //at the end of our run
            //restore old values for the next one accessing the server :

            //back to old dir

            String orgDir = "setwd(orgDir)";
            debugString+=orgDir+"\n";
            voidEval(orgDir);

         }catch(Exception n) {
            progressPercentage[0]="101_Error occured setting original dir";
            sendNotificationToMaintainer(progressPercentage[0],jobID);
            sendNotificationToUser("General exception occured. Please get in contact with a program maintainer soon",jobID);
            getRengine().close();
            semaphore.v(threadID);
            return false;
        }



         //uncomment try catch block for debugging
        try {
            //close the logging and the logfile
            String sink = "sink()";
            debugString+=sink+"\n";
            voidEval(sink);
        }catch(Exception e) {
            progressPercentage[0]="101_Error occured closing the R_OUTPUTSTREAM:maybe your logfile isnt complete!...which will be a bad thing:\nOne reason is running Rserve binary on Mac Os X server.Check /var/log/system.log and search for a Rserve crash report";
            sendNotificationToMaintainer(progressPercentage[0]+"\n"+e.getMessage(),jobID);
            sendNotificationToUser("General exception occured. Please get in contact with a program maintainer soon",jobID);
            e.printStackTrace();
            getRengine().close();
            semaphore.v(threadID);
            return false;
        }

        //do a final check if any errors have occured
                                       if(this.rOutputHasErrors(new File(rOutputFile))) {
                                           progressPercentage[0]="101_Error occured ";
                                            sendNotificationToMaintainer(progressPercentage[0],jobID);
                                            sendNotificationToUser("General exception occured. Please get in contact with a program maintainer soon, possible R error "+this.getErrorMsgFromRLogfile(new File(rOutputFile)),jobID);
                                            getRengine().close();
                                            semaphore.v(threadID);
                                            return false;
                                       }

                                       //after we are done...create a zipfile out of the results
                                       //progressPercentage[0]="100_successfully done";
                                       //successBool[0]=true;


        //if were here we have won!
        FileCreator.stringToFile(new File(rOutputScriptFile),debugString);
        
        //zip the results
        if(!createResultsZipFile()) {
            progressPercentage[0] = "101_Error occured trying to zip the resultfiles!";

            sendNotificationToMaintainer(progressPercentage[0], jobID);
            sendNotificationToUser("General server problems. Please get in contact with program maintainers", jobID);

            getRengine().close();
            semaphore.v(threadID);
            //TODO:put all the exceptions and stuff in a seperate return function where you stop the semaphore and close everything
            return false;
        }

        //finally close this R Server connection
        getRengine().close();
        semaphore.v(threadID);


        //send results to email
        //if(this.emailNotification) {
        //    sendcellHTS2SuccesMail();
        //}
        return true;

    }

    public boolean sendcellHTS2SuccesMail() {

                String runName=this.extractRunName(stringParams.get("runNameDir"));
                //create a recycable downloadlink as well...therefore we have to keep track about how often a file is allowed to be downloaded
                //which we will write into a properties file
                File dlPropertiesFile = new File(uploadPath+stringParams.get("jobName")+File.separator+".dlProperties");

                String downloadLink = stringParams.get("emailDownloadLink");

                //init a properties file

                Properties propObj = new Properties();
                if(dlPropertiesFile.exists()) {
                    try {
                    propObj.load(new FileInputStream(dlPropertiesFile));
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                //set the relation between runname and result zip file
                propObj.setProperty(runName+"_RESULT_ZIP",resultZipFile);
                //put in the properties file that we downloaded this runid zero times
                propObj.setProperty(runName,"0");
                //generate a password for downloading
                String pw = PasswordGenerator.get(20);
                propObj.setProperty(runName+"_password",pw);
                try {
                    propObj.store(new FileOutputStream(dlPropertiesFile),"properties file for email Download information");
                }catch(Exception e) {
                    e.printStackTrace();
                }
                //FileCreator.writeDownloadPropertiesFile(new File(dlPropertiesFile),runName,0);


                String emailMsg="";//"Your job ID was: "+runName+'\n';
                String file=null;
                //int percentage = Integer.parseInt(progressPercentage[0].split("_")[0]);
                //String msg = progressPercentage[0].split("_")[1];
                //if(percentage==100) {
                    emailMsg+="Dear web cellHTS2 user,\n" +
                            "\n" +
                            "the calculations have been completed. Please download the report\n" +
                            "from our server (there is a limit to download the results "+stringParams.get("allowed-dl-numbers")+" times)\n" +
                            "at:\n\n"+
                            downloadLink+"\n"+
                            "Password: "+pw+"\n\n"+
                            "JOB ID: "+runName+"(for reference purposes)\n\n"+
                            "Save the file and unpack it using an unzip program.\n" +
                            "The \"index.html\" file can be opened by any web browser for view the  \n" +
                            "analysis results. We have also included a session file that can be  \n" +
                            "used to modify analysis settings.\n" +
                            "\n" +
                            "Please do not hesitate to contact us if you have any question or  \n" +
                            "suggestions for improvement.\n" +
                            "\n" +
                            "Sincerely,\n" +
                            "\n" +
                            "web cellHTS Team\n" +
                            "Email: "+ this.maintainEmailAddress+"\n";
                            /**
                            "Please use the following citation when using cellHTS:\n" +
                            "Boutros, M., L. Bras, and W. Huber. (2006). Analysis of cell-based RNAi screens. Genome Biology 7:R66.\n"+
                            "Abstract: http://genomebiology.com/2006/7/7/R66\n"+
                            "Full Text: http://genomebiology.com/content/pdf/gb-2006-7-7-r66.pdf\n"+
                            "PubMed: http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=pubmed&amp;cmd=Retrieve&amp;dopt=AbstractPlus&amp;list_uids=16869968";
                             **/
                    file=resultZipFile;

                //}
                //else {
                //    emailMsg+="Sorry, but your run was not successful! Error percentage: "+percentage+"\nSystem output:\n"+msg+"\nPlease consult the manual or ask the developers of this tool for help!";
                //    file =null;
                //    return false;
                //
                //}
                //create array of attachements
                String sessionFile =  stringParams.get("sessionFile");
                if(sessionFile==null) {
                    return false;

                }
                String []files = {file,sessionFile};
                postMailTools.postMail( emailAddress,
                                       "Your web cellHTS2 report",//"cellHTS2 report (\""+runName+"\"):",
                                        emailMsg,
                                        this.maintainEmailAddress,
                                         null //file  if we want to send the result as file
                                         );
        return true;

}


    public boolean runHTSAnalyzeR() {

        String queueFullMsg;
        queueFullMsg="99_queue is full, waiting for a free slot...hold on! (dont close the window)!";

        threadID = getId();

        //check if we still have place before running
        semaphore.p(progressPercentage,queueFullMsg,threadID);

        String outputDir = stringParams.get("runNameDir");
        String cellHTS2ObjFile = outputDir+File.separator+CELLHTS2_OBJ_DUMP_FILENAME;
        String jobID = stringParams.get("jobName");
        //start R Server, load the file into R Obj using load function etc.//make a new connection to the R server Rserve
        try {
            //RConnection c = new RConnection();
            //setRengine(c);
            REngine eng = REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine");
            setRengine(eng);
        }catch(Exception e) {
            String exceptionText = "failed making connection to Rserver maybe you forgot to start it \"R CMD Rserve\" ";//)+e.printStackTrace());
            exceptionText+="Note: this currently only works starting the RServer on the same server as this java is started from";
            progressPercentage[0]="101_"+exceptionText;
            e.printStackTrace();

            sendNotificationToMaintainer(e.getMessage(),jobID);
            sendNotificationToUser("General server problems. Please get in contact with program maintainers",jobID);
            return false;
            //throw new TapestryException(exceptionText, null);



        }

        //begin rservering :'-)
        
        String debugString="";

        String rOutputHTSAnalyzerFile=outputDir+File.separator+"R_OUTPUT_HTSANALYZER.TXT";

        String rOutputHTSAnalyzerScriptFile=outputDir+File.separator+StringFunctions.getFileNameWithoutExtension(rOutputScriptFile)+"_HTSANALYZER.SCRIPT";

        try {

        
                String cmdString;

                cmdString= "orgDir=getwd()";
                debugString+=cmdString+"\n";
                voidEval(cmdString);
                //HTSAnalyzer lacks feature of setting up outputdir
                String htsOutDir = stringParams.get("runNameDir")+File.separator+HTSANALYZER_OUTPUT_DIR;
                cmdString = "dir.create(\""+htsOutDir+"\", recursive = TRUE)";
                debugString+=cmdString+"\n";
                voidEval(cmdString);


//first we have to change to the Indir in order to make the R cellHTS script working
                cmdString= "setwd(\""+htsOutDir+"\")";
                debugString+=cmdString+"\n";
                voidEval(cmdString);
                

                String openFile = "zz <- file(\""+rOutputHTSAnalyzerFile+"\", open=\"w\")";
                cmdString=openFile ;
                debugString+=cmdString+"\n";
                voidEval(openFile);

                //comment the next three lines for debugging
                //get messages not the output!
                String sinkMsg= "sink(file=zz,type=\"message\" )";
                cmdString=sinkMsg ;
                debugString+=cmdString+"\n";
                voidEval(sinkMsg);
                

                progressPercentage[0]="15_loading HTSanalyzeR and dependent libs";
                cmdString=String.format("library(cellHTS2);library(HTSanalyzeR);library(org.Dm.eg.db);library(GO.db);library(KEGG.db)");
                debugString+=cmdString+"\n";
                voidEval(cmdString);

                progressPercentage[0]="20_loading cellHTS2 object from file";
            //  the loaded cellHTS2 obj will be available in a variable called dumpObj afterwards  
                cmdString=String.format("load(\"%s\")",cellHTS2ObjFile);
               // System.out.println("HERE WE ARE: "+cmdString);
                debugString+=cmdString+"\n";
                voidEval(cmdString);

                /*cmdString="Dm.GO.CC<-GOGeneSets(species=\"Drosophila_melanogaster\",ontologies=c(\"CC\"))\n" +
                        "     kegg.droso<-KeggGeneSets(species=\"Drosophila_melanogaster\");\n" +
                        "     gsc.list<-list(Dm.GO.CC=Dm.GO.CC,kegg.droso=kegg.droso)";
                debugString+=cmdString+"\n";
                voidEval(cmdString);
                */
                if(!stringParams.get("geneCollectionSetup").equals("")) {
                    progressPercentage[0]="20_doing some go, kegg and gsc initalization";

                     cmdString=stringParams.get("geneCollectionSetup");
                     debugString+=cmdString+"\n";
                     voidEval(cmdString);

                   
                }


                progressPercentage[0]="25_starting the HTSAnalyzer routine";
                /*cmdString="cellHTS2DrosoData<-dumpObj;\n" +
                        "HTSanalyzeR(\n" +
                        " \tx=cellHTS2DrosoData,\n" +
                        " \tannotationColumn=\"GeneID\",\n" +
                        " \tspecies=c(\"Drosophila_melanogaster\"),\n" +
                        " \tinitialIDs=\"FlybaseCG\",\n" +
                        " \tlistOfGeneSetCollections=gsc.list,\n" +
                        " \twhichSetIsKEGGIds=2,\n" +
                        " \twhichSetIsGOIds=1,\n" +
                        " \tnetworkObject=NA,\n" +
                        " \tminGeneSetSize=5\n" +
                        "\t)";    */
                cmdString="cellHTS2DrosoData<-dumpObj;\n" +"HTSanalyzeR(x=cellHTS2DrosoData,listOfGeneSetCollections=gsc.list,"
                          +stringParams.get("hTSAnalyzerParams")
                        +");\n";

                debugString+=cmdString+"\n";
                voidEval(cmdString);

                    try {
                                           String sinkMe = "sink()";
                                           debugString+=sinkMe+"\n";
                                           voidEval(sinkMe);

                                       } catch(REngineException m) {
                                           String tempString =" <br/>AND Error occured closing the R_OUTPUTSTREAM (this will be 99% caused by a Rserve dynlib crash):maybe your logfile isnt complete!...which will be a bad thing:<br/>One reason is not correctly formatting your annotation files under mac, e.g. saving it as a dos text file will bring Rserve to make segfault<br/>another reason is the use of Rserve <0.6";
                                           throw new REngineException(getRengine(),tempString);
                                       }

                                       //do a final check if any errors have occured
                                       if(this.rOutputHasErrors(new File(rOutputHTSAnalyzerFile))) {
                                           throw new REngineException(getRengine(),"R output has errors");
                                       }

                                       //after we are done...create a zipfile out of the results
                                       //progressPercentage[0]="100_successfully done";
                                       //successBool[0]=true;

        }catch(Exception e) {
                    String step = progressPercentage[0].split("_")[1];
                    String tempString="101_Error occured at step:"+step+"<br/>Please consult R logfile under:<br/>"+rOutputHTSAnalyzerFile+"<br/> if debugging is on also run the script "+rOutputHTSAnalyzerScriptFile ;
                    //close the logging and the logfile before printing it or you will lose information!

                    String msg = e.getMessage();
                    String requestErrorDesc = e.getLocalizedMessage();
                   // int returnCode = e.getRequestReturnCode();


                    //uncomment try catch block for debugging
                    try {
                        String cmdString= "sink()";
                        voidEval(cmdString);
                        debugString+=cmdString+"\n";
                        voidEval(cmdString);
                    } catch(Exception re) {
                        tempString+=" <br/>AND Error occured closing the R_OUTPUTSTREAM (this will be 99% caused by a Rserve dynlib crash):maybe your logfile isnt complete!...which will be a bad thing:<br/>One reason is not correctly formatting your annotation files under mac, e.g. saving it as a dos text file will bring Rserve to make segfault<br/>another reason is the use of Rserve <0.6";
                    }
                    progressPercentage[0]=tempString+"<br/><br/> <FONT COLOR=\"red\">received error Messages:"+getErrorMsgFromRLogfile(new File(rOutputHTSAnalyzerFile))+"</FONT>"+"<br/>";
                    progressPercentage[0]+="msg:"+msg+"<br/>errorDesc:"+requestErrorDesc+"<br/>";//returnCode:"+returnCode+"<br/>";

                    FileCreator.stringToFile(new File(rOutputHTSAnalyzerScriptFile),debugString);
                    //add script to the results zip file

                   sendNotificationToMaintainer(progressPercentage[0],jobID);
                   sendNotificationToUser("General Rserve problem:\n"+" at step: "+step+"\n"+msg+"  "+requestErrorDesc+" \nPlease get in contact with program maintainers",jobID);

                    getRengine().close();
                    semaphore.v(threadID);
                    return false;
                }

        //try to close the outputstream
        try {
            //at the end of our run
            //restore old values for the next one accessing the server :
            String cmdString= "setwd(orgDir)";
            voidEval(cmdString);
            debugString+=cmdString+"\n";


         }catch(Exception e) {
            progressPercentage[0]="101_Error occured setting original dir";
            sendNotificationToMaintainer(progressPercentage[0],jobID);
            sendNotificationToUser("General exception occured. Please get in contact with a program maintainer soon",jobID);
            getRengine().close();
            semaphore.v(threadID);
            return false;
        }
         //uncomment try catch block for debugging
        try {
            //close the logging and the logfile

            voidEval("sink()");
        }catch(Exception e) {
            progressPercentage[0]="101_Error occured closing the R_OUTPUTSTREAM:maybe your logfile isnt complete!...which will be a bad thing:\nOne reason is running Rserve binary on Mac Os X server.Check /var/log/system.log and search for a Rserve crash report";
            sendNotificationToMaintainer(progressPercentage[0]+"\n"+e.getMessage(),jobID);
            sendNotificationToUser("General exception occured. Please get in contact with a program maintainer soon, possible R error message: "+getErrorMsgFromRLogfile(new File(rOutputHTSAnalyzerFile)),jobID);
            e.printStackTrace();
            getRengine().close();
            semaphore.v(threadID);
            return false;
        }
//if were here we have won!
        FileCreator.stringToFile(new File(rOutputHTSAnalyzerScriptFile),debugString);
        //zip the results
        if(!this.createHTSAnalyzerResultsZipFile()) {
            progressPercentage[0] = "101_Error occured trying to zip the resultfiles!";

            sendNotificationToMaintainer(progressPercentage[0], jobID);
            sendNotificationToUser("General server problems. Please get in contact with program maintainers", jobID);

            getRengine().close();
            semaphore.v(threadID);
            //TODO:put all the exceptions and stuff in a seperate return function where you stop the semaphore and close everything
            return false;
        }

        //finally close this R Server connection
        getRengine().close();
        semaphore.v(threadID);


        //send results to email
        //if(this.emailNotification) {
        //    sendHTSAnalyzerSuccessMail(htsResultZipFile);
        //}
        return true;


        
    }
    public boolean sendHTSAnalyzerSuccessMail() {
        String runName=this.extractRunName(stringParams.get("runNameDir"));
            //create a recycable downloadlink as well...therefore we have to keep track about how often a file is allowed to be downloaded
            //which we will write into a properties file
            File dlPropertiesFile = new File(uploadPath+stringParams.get("jobName")+File.separator+".dlProperties");

            String downloadLink = stringParams.get("emailDownloadLink")+"_HTSANALYZER";

            //init a properties file

            Properties propObj = new Properties();
            if(dlPropertiesFile.exists()) {
                try {
                propObj.load(new FileInputStream(dlPropertiesFile));
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }


            //set the relation between runname and result zip file
            propObj.setProperty(runName+"_HTSANALYZER_RESULT_ZIP",htsResultZipFile);
            //put in the properties file that we downloaded this runid zero times
            propObj.setProperty(runName+"_HTSANALYZER","0");
            //generate a password for downloading
            String pw = PasswordGenerator.get(20);
            propObj.setProperty(runName+"_HTSANALYZER_password",pw);
            try {
                propObj.store(new FileOutputStream(dlPropertiesFile),"properties file for email Download information");
            }catch(Exception e) {
                e.printStackTrace();
            }
            //FileCreator.writeDownloadPropertiesFile(new File(dlPropertiesFile),runName,0);


            String emailMsg="";//"Your job ID was: "+runName+'\n';
            String file=null;
            int percentage = Integer.parseInt(progressPercentage[0].split("_")[0]);
            String msg = progressPercentage[0].split("_")[1];
            if(percentage==100) {
                emailMsg+="Dear HTS Analyzer user,\n" +
                        "\n" +
                        "the calculations have been completed. Please download the report\n" +
                        "from our server (there is a limit to download the results "+stringParams.get("allowed-dl-numbers")+" times)\n" +
                        "at:\n\n"+
                        downloadLink+"\n"+
                        "Password: "+pw+"\n\n"+
                        "JOB ID: "+runName+"(for reference purposes)\n\n"+
                        "Save the file and unpack it using an unzip program.\n" +
                        "The \"index.html\" file can be opened by any web browser for view the  \n" +
                        "analysis results. We have also included a session file that can be  \n" +
                        "used to modify analysis settings.\n" +
                        "\n" +
                        "Please do not hesitate to contact us if you have any question or  \n" +
                        "suggestions for improvement.\n" +
                        "\n" +
                        "Sincerely,\n" +
                        "\n" +
                        "web cellHTS Team\n" +
                        "Email: "+ this.maintainEmailAddress+"\n";

                file=htsResultZipFile;

            }
            else {
                emailMsg+="Sorry, but your run was not successful! Error percentage: "+percentage+"\nSystem output:\n"+msg+"\nPlease consult the manual or ask the developers of this tool for help!";
                file =null;
                return false;

            }
            //create array of attachements
            String sessionFile =  stringParams.get("sessionFile");
            if(sessionFile==null) {
                return false;

            }
            String []files = {file,sessionFile};
        System.out.println("before sending out message");
            postMailTools.postMail( emailAddress,
                                   "Your HTS Analyzer report",//"cellHTS2 report (\""+runName+"\"):",
                                    emailMsg,
                                    this.maintainEmailAddress,
                                     null //file  if we want to send the result as file
                                     );
        System.out.println("after sending out message");


             return true;

        }


    /**
     *
     *  this method sends a message to the maintainer
     *
     * @param msg  the message to send
     * @param runName  the runname ID to send
     */
    public void sendNotificationToMaintainer(String msg,String runName) {
        if(this.emailNotification) {
            if(this.sendErrorEmail) {
                 postMailTools.postMail( this.maintainEmailAddress,
                                    "Error report for job ID: "+runName,
                                     msg,
                                     //"cellHTS2-results@"+hostname,
                                     this.maintainEmailAddress,
                                     null   //no file attached
                                );
            }
        }
    }

    /**
     *
     * sends a notification to the user e.g. in case of error
     *
     * @param msg   message to send
     * @param runName the runname to send
     */
    public void sendNotificationToUser(String msg,String runName) {
        if(this.emailNotification) {
        String errorMsg = "There were some problems executing your analysis job.\nPlease check your input or send an email to the maintainers (mentioning your job ID): "+this.maintainEmailAddress+"\n";
        errorMsg+="\n\n\n------------\nError message: \n";
        msg =errorMsg+msg;
        postMailTools.postMail( emailAddress,
                                "Error report for job ID: "+runName,
                                 msg,
                                 this.maintainEmailAddress,
                                 //"cellHTS2-results@"+hostname,
                                 null   //no file attached
                              );
        }
    }

    public HashMap<String, String> getStringParams() {
        return stringParams;
    }

    public void setStringParams(HashMap<String, String> stringParams) {
        this.stringParams = stringParams;
    }//inner class



    public synchronized REngine getRengine()  {
       return rEngine;
    }
    public synchronized void setRengine(REngine e) {
        rEngine = e;
    }

    /**
     *
     * parses an R logfile
     * 
     * @return  the parsed error message
     */
    //TODO: this sub works only as long as were running this webapp from the same server as R and RServe is installed on
    //TODO: otherwise this has to be changed to copy the log file from the server first
    public String getErrorMsgFromRLogfile(File outputFile) {
        String fileContent = FileParser.readFileAsStringWithNewline(outputFile);
        //begin returning error message at first error occurence
        String returnErrorMsg="";
        String[]lines = fileContent.split("\n");
        Pattern p = Pattern.compile("Fehler|Error");
        boolean foundErr=false;
        for(String line : lines) {
            Matcher m = p.matcher(line);
            if(m.find()) {
                foundErr=true;
            }
            if(foundErr) {
                returnErrorMsg+=line+"\n";
            }
        }
        return returnErrorMsg;
    }
    public boolean rOutputHasErrors(File outputFile) {
        String fileContent = FileParser.readFileAsStringWithNewline(outputFile);
        String[]lines = fileContent.split("\n");
        Pattern p = Pattern.compile("Fehler|Error");

        for(String line : lines) {
            Matcher m = p.matcher(line);
            if(m.find()) {
                return true;
            }

        }
        return false;
    }

    /**
     *
     * creates a zip file out of a results folder
     *
     * @return  true if zipping succeeded, false otherwise
     */
    public boolean createResultsZipFile() {
        boolean returnValue = true;
        //zip the results if there are any at all!


            //create an temporary directory for this session
            String zipDir=stringParams.get("runNameDir");
            
        try {


            ZipOutputStream zos = new
                    ZipOutputStream(new FileOutputStream(resultZipFile));
             //we have to give the root dir ...this is a limitation of the zipDir function


            String[]tmpDirs = zipDir.split(File.separator);

             String rootDir = tmpDirs[tmpDirs.length-1];

             if (ShellEnvironment.zipDir(zipDir, zos, rootDir)) {

                zos.close();

             }
            }catch(Exception e) {
                e.printStackTrace();
                returnValue=false;
            }
        
        return returnValue;
    }
    public boolean addHTSAnalyzerResultsToResultsZipFile() {

        try {

            File[] files = new File[1];
            files[0]=new File(htsResultZipFile);

           ShellEnvironment.addFilesToExistingZip(new File(resultZipFile),files);
        }catch(IOException e) {
            return false;
        }
        return true;
    }

    public void voidEval(String cmd) throws REngineException,REXPMismatchException{
        this.getRengine().parseAndEval(cmd, null, false);
    }

    public boolean createHTSAnalyzerResultsZipFile() {
        boolean returnValue = true;
        //the dir where results are stored
        String htsOutDir = stringParams.get("runNameDir")+File.separator+HTSANALYZER_OUTPUT_DIR+File.separator+HTSANALYZER_RESULTS_DIR;
        //zip the results if there are any at all!


            //create an temporary directory for this session
            String zipDir=htsOutDir;

        try {


            ZipOutputStream zos = new
                    ZipOutputStream(new FileOutputStream(htsResultZipFile));
             //we have to give the root dir ...this is a limitation of the zipDir function


            String[]tmpDirs = zipDir.split(File.separator);

             String rootDir = tmpDirs[tmpDirs.length-1];

             if (ShellEnvironment.zipDir(zipDir, zos, rootDir)) {

                zos.close();

             }
            }catch(Exception e) {
                e.printStackTrace();
                returnValue=false;
            }

        return returnValue;
    }

    public String extractRunName(String dir) {
        return (new File(dir)).getName();
        
    }

    /**
     *  kill the thread id and thread from the semaphore (only if it is still available..if we are at 100% this isnt true anymore)
     */
    public void killMe() {

        semaphore.removeRunningJob(threadID);
        this.interrupt();
    }
}
