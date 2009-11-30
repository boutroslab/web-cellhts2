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

import cellHTS.components.ExportCSV;
import cellHTS.components.FileImporter;
import cellHTS.classes.FileCreator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.io.File;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import data.DataFile;

public class AdvancedFileImporter {
    @Persist
    private boolean convertedAllFiles;
    @Persist
    private ArrayList<String> uploadedFiles;
    //these are the outputfiles from the cvs export function
    @Persist
    private ArrayList<String> filesToImport;
    @Persist
    private ArrayList<String> headsToFindDatafile;
    @Persist
    private ArrayList<String> headsToFindPlateconfigfile;
    @Persist
    private ArrayList<String> headsToFindAnnotationfile;
    @Persist
    private String uploadPath;
    @Persist
    private boolean init;
    @Persist
    private boolean startFileImport;
    @InjectPage
    private CellHTS2 cellHTS2;
    @Persist
    private boolean plateWellDefined;
    @Persist
    private LinkedHashMap<String,DataFile> repChannelMap;


    @InjectComponent
    private ExportCSV exportCSV;
    @InjectComponent
    private FileImporter dataFileImporter;
    @InjectComponent
    private FileImporter plateConfigImporter;
    @InjectComponent
    private FileImporter annotationImporter;
    @Persist
    private boolean containsMultiChannelData;
     @Persist
    private boolean containsHeadline;
    @Persist
    private int replicateNumbers;
    @Inject
    private Messages msg;

    public void setupRender() {
        if(!init) {
            init=true;
            uploadedFiles = new ArrayList<String>();
            filesToImport = new ArrayList<String>();
            plateWellDefined=false;
            headsToFindDatafile= new  ArrayList<String>();
            initHeadsToFind();
            headsToFindPlateconfigfile = new ArrayList<String>();
            headsToFindPlateconfigfile.add("Value");
            headsToFindAnnotationfile=new ArrayList<String>();
            headsToFindAnnotationfile.add("GeneID");
            repChannelMap= new LinkedHashMap<String,DataFile>(); 

            uploadPath=msg.get("upload-path");
            startFileImport=false;
            convertedAllFiles=false;

            containsHeadline=true;
            containsMultiChannelData=false;
            replicateNumbers=1;

        }
    }
          //this is for testing only
    public ArrayList<String> getTempDebugFiles() {

         ArrayList<String> al = new ArrayList<String>();
     
        return al;
    }

    

    //if all files have been transfered to the server with the multifileupload component
    //this event is broadcasted from the multipleupload component
    void onlastFileTransferedFromMultipleUploadOne(Object[]submittedFiles) {

          for(Object file : submittedFiles) {                     
              uploadedFiles.add((String)file);
          }
    }
    void onAllFilesClearedFromMultipleUploadOne(Object[]dummy) {
        //reinit everything
        uploadedFiles.clear();
        filesToImport.clear();
        startFileImport=false;
        convertedAllFiles=false;
        exportCSV.setInit(false);
        dataFileImporter.setInit(false);
        plateConfigImporter.setInit(false);
        annotationImporter.setInit(false);

    }


    public void onSuccessfullyConvertedToCVSFromExportCSV(Object []objs) {                      
        filesToImport.clear();
        for(Object obj: objs) {
            filesToImport.add((String)obj);                
        }

         initDatafileHeaders();


        convertedAllFiles=true;
    }
    public void onFailedConvertedToCVSFromExportCSV(Object []dummy) {
       convertedAllFiles=false;
        //reinit everything
       dataFileImporter.setInit(false);
       initDatafileHeaders();
    }

    //this is deprecated ...we dont have an actionlink anymore
    public void onActionFromProcessFiles() {
        if(uploadedFiles.size()>0) {
            startFileImport=true;                     
        }
        else {
            startFileImport=false;
        }
        //now create the headsToFindDatafile out of the number of replicates, and multichannel flags

        //build a new datastructure which maps
        // "repXchannelY"  to  a datafile structure
        repChannelMap = generateHeadersReplicateChannel();
        //this is caused if we have multichannel experiments
        if(repChannelMap.size()<2) {
            headsToFindDatafile.add("Value");
        }
        else {
              initHeadsToFind();
              headsToFindDatafile.addAll(repChannelMap.keySet());
            //reinit the dataFileImporter so that new heads will shown
              dataFileImporter.setInit(false);
        }
    }

    public void initDatafileHeaders() {
        //build a new datastructure which maps
        // "repXchannelY"  to  a datafile structure
        LinkedHashMap<String,DataFile> repChannelMap = generateHeadersReplicateChannel();
        initHeadsToFind();
        System.out.println("size:"+repChannelMap.size());
        //this is caused if we have multichannel experiments
        if(repChannelMap.size()==1) {
            headsToFindDatafile.add("Value");
        }
        if(repChannelMap.size()>1)  {

              headsToFindDatafile.addAll(repChannelMap.keySet());
            //reinit the dataFileImporter so that new heads will shown
              dataFileImporter.setInit(false);
        }
    }

    public LinkedHashMap<String,DataFile> generateHeadersReplicateChannel() {
         LinkedHashMap<String,DataFile>repChannelMap = new LinkedHashMap<String,DataFile>();

        //max replicate numbers to 9
        if(replicateNumbers>9) {
            replicateNumbers=9;
        }

        //build replicates
        ArrayList<Integer> replicateArr= new ArrayList<Integer>();
        for(int i=0;i<replicateNumbers;i++) {
            replicateArr.add(i+1);
        }
        //build channels
        ArrayList<Integer> channelArr = new ArrayList<Integer>();
        //every file has at least one channel
        channelArr.add(1);
        if(containsMultiChannelData) {
            channelArr.add(2);
        }
        //generate combinations

        for(Integer replicate : replicateArr) {
            for(Integer channel : channelArr) {
                DataFile df = new DataFile();
                df.setReplicate(replicate);
                df.setChannel(channel);
                String headName = "rep"+replicate+"channel"+channel;
                repChannelMap.put(headName,df);
            }
        }

        return repChannelMap;
    }
    //this will be fired if all the columns have been associated to the column names by the user form the dataFileImporter
    //component
    public void onSuccessfullySetupColumnsFromDatafileImporter(Object[]objs) {

        LinkedHashMap<String,Integer> returnMap = eventObjToColumnNamesAndNums(objs);
        if(returnMap.containsKey("Plate")&&returnMap.containsKey("Well")) {
            plateWellDefined=true;
        }
        else {
            plateWellDefined=false;
        }
        //now generate the data files if everything was defined           //for singlechannel              //for multichannel
        if(returnMap.containsKey("Plate")&&returnMap.containsKey("Well")&&(returnMap.containsKey("Value")||returnMap.size()>2)) {
           ArrayList<File> inputFiles= new ArrayList<File>();
        //these are the outputfiles from the cvs export function
            for(String tempFile : filesToImport) {
                inputFiles.add(new File(tempFile));
            }
            ArrayList<File> outputFiles= new ArrayList<File>();
       //these are all the uploaded files...we will use these as original names for the
       //outputfiles again!!
            for(String tempFile : uploadedFiles) {
                outputFiles.add(new File(tempFile));
            }
           repChannelMap = generateHeadersReplicateChannel();
            System.out.println(repChannelMap.size());
          
           if(FileCreator.createDataFilesFromCVSMultiFiles(inputFiles,outputFiles,
                                                      containsHeadline,
                                                      repChannelMap, 
                                                      replicateNumbers,
                                                      returnMap)) {
               System.out.println("creation of datafiles succeeded");
               for(File outfile: outputFiles) {
                   System.out.println("outputdatafile:"+outfile.getAbsolutePath());

               }                
               cellHTS2.setDatafilesFromAdvancedFileImporter(outputFiles);     //send the files to cellHTS2 so if
                                                                              //we switch later to it, this will be used

           }
            else {
                //create a error message
           }

        }

    }

    public Object onActionFromGoBackwebCellHTS2() {
        cellHTS2.activatedFromOtherPage(this.getClass().getName());
        return cellHTS2;
    }

    //this will be fired from the plateConfigImporter component
    public void onSuccessfullySetupColumnsFromPlateConfigImporter(Object[]objs) {
        System.out.println("plateConfigImporter called");
        LinkedHashMap<String,Integer> returnMap = eventObjToColumnNamesAndNums(objs);
        for(String key : returnMap.keySet()) {
            System.out.println(key+":"+returnMap.get(key));
        }

    }
    //this will be fired from the annotationImporter component
    public void onSuccessfullySetupColumnsFromAnnotationImporter(Object[]objs) {
        System.out.println("plateConfigImporter called");
        LinkedHashMap<String,Integer> returnMap = eventObjToColumnNamesAndNums(objs);
        for(String key : returnMap.keySet()) {
            System.out.println(key+":"+returnMap.get(key));
        }
    }
    public LinkedHashMap<String,Integer> eventObjToColumnNamesAndNums(Object []objs) {
        LinkedHashMap<String,Integer> returnMap = new LinkedHashMap<String,Integer>();
        try {
        for(int i=0;i<objs.length;i+=2) {
            returnMap.put((String)objs[i],Integer.parseInt((String)objs[i+1]));
        }

        }catch(NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return returnMap;
    }


    public Object[] getEmptyObject() {
        return new Object[]{"test"};
    }
    public void onActionFromProceedWebCellHTS2() {
        //cellHTS2.
    }
    @OnEvent(component = "containsMultiChannelData", value = "change")
     public JSONObject onContainsMultiChannelData(boolean type) {
            containsMultiChannelData=type;                            
            return new JSONObject().put("multi",type);
        }
    @OnEvent(component = "containsHeadline", value = "change")
    public JSONObject onContainsHeadline(boolean type) {
        containsHeadline=type;
        System.out.println("changed on server");
        return new JSONObject().put("dummy", "");
    }
   @OnEvent(component = "replicateNumbers", value = "blur")
    public JSONObject onBluredreplicateNumbers(int value) {
        this.replicateNumbers=value;
       return new JSONObject().put("dummy", "");
    }
    public void initHeadsToFind() {
        headsToFindDatafile.clear();
        headsToFindDatafile.add("Plate");
        headsToFindDatafile.add("Well");
        //headsToFindDatafile.add("Value");
    }

     //getters and setters--------------------------------------------------------------------------------

    public boolean isConvertedAllFiles() {
        return convertedAllFiles;
    }

    public void setConvertedAllFiles(boolean convertedAllFiles) {
        this.convertedAllFiles = convertedAllFiles;
    }

    public ArrayList<String> getFilesToImport() {
        return filesToImport;
    }

    public void setFilesToImport(ArrayList<String> filesToImport) {
        this.filesToImport = filesToImport;
    }

    public ArrayList<String> getHeadsToFindDatafile() {
        return headsToFindDatafile;
    }

    public void setHeadsToFindDatafile(ArrayList<String> headsToFindDatafile) {
        this.headsToFindDatafile = headsToFindDatafile;
    }

    


    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public boolean isStartFileImport() {
        return startFileImport;
    }

    public void setStartFileImport(boolean startFileImport) {
        this.startFileImport = startFileImport;
    }

    public ArrayList<String> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(ArrayList<String> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public ArrayList<String> getHeadsToFindPlateconfigfile() {
        return headsToFindPlateconfigfile;
    }

    public void setHeadsToFindPlateconfigfile(ArrayList<String> headsToFindPlateconfigfile) {
        this.headsToFindPlateconfigfile = headsToFindPlateconfigfile;
    }

    public ArrayList<String> getHeadsToFindAnnotationfile() {
        return headsToFindAnnotationfile;
    }

    public void setHeadsToFindAnnotationfile(ArrayList<String> headsToFindAnnotationfile) {
        this.headsToFindAnnotationfile = headsToFindAnnotationfile;
    }

    public boolean isPlateWellDefined() {
        return plateWellDefined;
    }

    public void setPlateWellDefined(boolean plateWellDefined) {
        this.plateWellDefined = plateWellDefined;
    }

    public boolean isContainsMultiChannelData() {
        return containsMultiChannelData;
    }

    public void setContainsMultiChannelData(boolean containsMultiChannelData) {
        this.containsMultiChannelData = containsMultiChannelData;
    }

    public boolean isContainsHeadline() {
        return containsHeadline;
    }

    public void setContainsHeadline(boolean containsHeadline) {
        this.containsHeadline = containsHeadline;
    }

    public int getReplicateNumbers() {
        return replicateNumbers;
    }

    public void setReplicateNumbers(int replicateNumbers) {
        this.replicateNumbers = replicateNumbers;
    }
    
    // end of getters and setters-------------------------------------------------------------------------
}
