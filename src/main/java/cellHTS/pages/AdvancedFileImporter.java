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
import cellHTS.components.MultipleFileUploader;
import cellHTS.classes.FileCreator;
import cellHTS.classes.FileParser;

import java.util.*;
import java.util.regex.Pattern;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import data.DataFile;
import data.Plate;

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
    private ArrayList<String> headsToFindFurtherAnnotationfile;
    @Persist
    private TreeSet<Integer> additionalAnnotCols;

    @Persist
    private String uploadPath;
    @Persist
    private boolean init;
    @Persist
    private boolean startFileImport;
    @InjectPage
    private CellHTS2 cellHTS2;
    @Persist
    private boolean dataFileCreated;
    @Persist
    private LinkedHashMap<String,DataFile> repChannelMap;
    @Persist
    private ArrayList<Plate> clickedWellsAndPlates;

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
    @Persist
    private String datafileImporterMsg;
    @Persist
    private String plateConfigFileImporterMsg;
    @Persist
    private String annotationfileImporterMsg;
    @Persist
    private LinkedHashMap<String,Integer> plateNameToNum;
    @Persist
    private int plateFormat;
    @InjectComponent
    private MultipleFileUploader multipleUploadOne;
    @Persist
    private LinkedHashMap<String,Integer> plateDatafileColMap;

    public void setupRender() {
        if(!init) {
            if(uploadPath==null) {
                //throw new TapestryException("upload path has not been submitted to this page",null);
            	System.out.println("upload path has not been submitted to this page");
            	return;
            }
            else {
                File pathFileObj=new File(uploadPath);
                if(!pathFileObj.exists()) {
                    pathFileObj.mkdirs();
                }
            }    

            init=true;
            uploadedFiles = new ArrayList<String>();
            filesToImport = new ArrayList<String>();
            dataFileCreated=false;
            headsToFindDatafile= new  ArrayList<String>();
            initHeadsToFind();
            headsToFindPlateconfigfile = new ArrayList<String>();
            headsToFindPlateconfigfile.add("WellAnno");
            headsToFindAnnotationfile=new ArrayList<String>();
            headsToFindAnnotationfile.add("GeneID");
            headsToFindFurtherAnnotationfile=new ArrayList<String>();
            headsToFindFurtherAnnotationfile.add("choose further cols");
            repChannelMap= new LinkedHashMap<String,DataFile>(); 

            plateDatafileColMap= new LinkedHashMap<String,Integer>();
            startFileImport=false;
            convertedAllFiles=false;

            containsHeadline=true;
            //containsMultiChannelData=false;
            replicateNumbers=1;
            datafileImporterMsg="";
            plateConfigFileImporterMsg="";
            annotationfileImporterMsg="";
            plateNameToNum = new LinkedHashMap<String,Integer>();
            clickedWellsAndPlates=new ArrayList<Plate>();
            multipleUploadOne.setInit(false);
            dataFileImporter.setInit(false);
            plateConfigImporter.setInit(false);
            annotationImporter.setInit(false);
            exportCSV.setInit(false);
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
        //this is a dynamical process

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
        dataFileCreated=false;
        datafileImporterMsg="";
        plateConfigFileImporterMsg="";
        plateNameToNum.clear();
        clickedWellsAndPlates.clear();
        containsHeadline=true;
       // containsMultiChannelData=false;
        replicateNumbers=1;
        plateDatafileColMap.clear();

    }


    public void onSuccessfullyConvertedToCVSFromExportCSV(Object []objs) {                      
        filesToImport.clear();
        for(Object obj: objs) {
            filesToImport.add((String)obj);                
        }

        //check if our files got a header and if so set boolean got header
        //else do not set it
        if(checkIfFileGotHeader(new File(filesToImport.get(0)))) {
            containsHeadline=true;
        }
        else {
            containsHeadline=false;
        }
        initDatafileHeaders();

        convertedAllFiles=true;
        dataFileCreated=false;
    }
    public void onFailedConvertedToCVSFromExportCSV(Object []dummy) {
       convertedAllFiles=false;
        //reinit everything
       //dataFileImporter.setInit(false);
       //plateConfigImporter.setInit(false);
       //annotationImporter.setInit(false);
       initDatafileHeaders();
    }


     public void initDatafileHeaders() {
        //build a new datastructure which maps
        // "repXchannelY"  to  a datafile structure
        repChannelMap = generateHeadersReplicateChannel();
        initHeadsToFind();
        System.out.println("size:"+repChannelMap.size());
        //this is caused if we dont have multichannel experiments
        if(repChannelMap.size()==1) {
            headsToFindDatafile.add("Value");
            dataFileImporter.setInit(false);
        }
        else if(repChannelMap.size()>1)  {

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
        //get the column num for headline header
        LinkedHashMap<String,Integer> returnMap = eventObjToColumnNamesAndNums(objs);

        for(String dbg : returnMap.keySet()) {
            System.out.println(dbg);
        }
        //now generate the data files if everything was defined           //for singlechannel              //for multichannel
        if((returnMap.containsKey("Plate")&&returnMap.containsKey("Well")&&(returnMap.containsKey("Value"))||returnMap.size()==headsToFindDatafile.size())) {
            plateDatafileColMap.clear();
            plateDatafileColMap.putAll(returnMap);
            ArrayList<File> inputFiles= new ArrayList<File>();
        //these are the outputfiles from the cvs export function
            for(String tempFile : filesToImport) {
                inputFiles.add(new File(tempFile));
            }
            ArrayList<File> outputFiles= new ArrayList<File>();

            for(String tempFile : uploadedFiles) {
                //add a file extension
                tempFile+=".tab";
                outputFiles.add(new File(tempFile));
            }


           if(FileCreator.createDataFilesFromCVSMultiFiles(inputFiles,outputFiles,
                                                      containsHeadline,
                                                      repChannelMap, 
                                                      replicateNumbers,                                                    
                                                      returnMap,
                                                      plateNameToNum
                   )) {
               dataFileCreated=true;
               datafileImporterMsg= "creation of "+outputFiles.size()+" datafiles succeeded";
               
               for(File outfile: outputFiles) {
                   System.out.println("outputdatafile:"+outfile.getAbsolutePath());

               }


                plateFormat = FileParser.countNumberOfLinesForFile(outputFiles.get(0));

                if(! (plateFormat==96|| plateFormat==384|| plateFormat==1536)) {
                   datafileImporterMsg="plate format of uploaded file isnt valid: "+plateFormat+" must be one of 96,384,1536";
                   return;

                }

               cellHTS2.setDatafilesFromAdvancedFileImporter(outputFiles);     //send the files to cellHTS2 so if
                                                                              //we switch later to it, this will be used
               

           }
            else {
                datafileImporterMsg="Association of well or value columns e.g. value column contains letters failed or general IO problem. Please check selected columns/files.";
                dataFileCreated=false;
           }

        }
        else {
            datafileImporterMsg="";
            dataFileCreated=false;
        }

    }
    //this will be fired from the plateConfigImporter component
    public void onSuccessfullySetupColumnsFromPlateConfigImporter(Object[]objs) {
        if(plateDatafileColMap.get("Plate")==null || plateDatafileColMap.get("Well")==null) {
            return;
        }
        LinkedHashMap<String,Integer> returnMap = eventObjToColumnNamesAndNums(objs);
        returnMap.putAll(plateDatafileColMap);

        if(returnMap.containsKey("WellAnno")) {
            ArrayList<File> inputFiles= new ArrayList<File>();

        //these are the outputfiles from the cvs export function
            for(String tempFile : filesToImport) {
                inputFiles.add(new File(tempFile));
            }
            
            if(inputFiles.size()<1) {
               plateConfigFileImporterMsg="error: cant access uploaded files";
                return; 
            }

            clickedWellsAndPlates = new ArrayList<Plate>();
            //add the plate zero to it
            clickedWellsAndPlates.add(0,new Plate(0, 0, 0));

            File plateConfFile= new File(uploadPath+File.separator+"PlateConfig.txt");
            File screenLogFile = new File(uploadPath+File.separator+"Screenlog.txt");

            if(FileCreator.createPlateconfigFromCVSMultiFiles(inputFiles,
                                    plateConfFile,
                                    screenLogFile,
                                    plateFormat,
                                    containsHeadline,                                     
                                     clickedWellsAndPlates,
                                     repChannelMap,
                                     returnMap,
                                     plateNameToNum
                                      )) {


               plateConfigFileImporterMsg= "Plate config layout successfully generated";

               System.out.println(plateConfFile.getAbsolutePath()); 
               cellHTS2.setPlateConfScreenlogFileFromAdvancedFileImporter(plateConfFile,screenLogFile);



           }
            else {
                plateConfigFileImporterMsg="Association of wellAnno failed (only pos,neg,sample,empty, other and contaminated are allowed) or general IO problem. Please check your files.";
            }
        }
        else {
            plateConfigFileImporterMsg="";
           
        }
    }
    //this will be fired from the annotation component fired successfully set up
    public void onSuccessfullySetupColumnsFromAnnotationImporter(Object[]objs) {
         if(plateDatafileColMap.get("Plate")==null || plateDatafileColMap.get("Well")==null) {
            return;
        }


        LinkedHashMap<String,Integer> returnMap = eventObjToColumnNamesAndNums(objs);
        returnMap.putAll(plateDatafileColMap);
        if(returnMap.containsKey("GeneID")) {


            ArrayList<File> inputFiles= new ArrayList<File>();

        //these are the outputfiles from the cvs export function
            for(String tempFile : filesToImport) {
                inputFiles.add(new File(tempFile));
            }

            if(inputFiles.size()<1) {
               plateConfigFileImporterMsg="error: cant access uploaded files";
                return;
            }
            ArrayList<Integer> additionalCols = new ArrayList<Integer>(){};
            String addColString = eventObjToColumValueString(objs,"multipleColumn");
           
            String[]additionalColsArr = addColString.split(",");

            for(String addColsString : additionalColsArr) {
                Integer intObj;
                try{
                 intObj = Integer.parseInt(addColsString);
                }catch(NumberFormatException e) {
                    continue;
                }
                 additionalCols.add(intObj);
            }
            File annotationOutFile= new File(uploadPath+File.separator+"Annotation.txt");
            

            if(FileCreator.createAnnotFileFromCVSMultiFiles(inputFiles,
                                     annotationOutFile,
                                     returnMap.get("Plate"),
                                      returnMap.get("Well"),
                                     returnMap.get("GeneID"),
                                     additionalCols,
                                     containsHeadline)) {


               annotationfileImporterMsg= "Annotation successfully generated";

                System.out.println(annotationOutFile.getAbsolutePath());

              cellHTS2.setAnnotationFileFromAdvancedFileImporter(annotationOutFile);



           }
            else {
                plateConfigFileImporterMsg="general IO error occured. Please check your files";
            }
        }
        else {
            plateConfigFileImporterMsg="";

        }
    }
    
    public boolean checkIfFileGotHeader(File file) {
        Pattern ps[] = new Pattern[6];
        ps[0]= Pattern.compile("plate",Pattern.CASE_INSENSITIVE);
        ps[1]= Pattern.compile("well",Pattern.CASE_INSENSITIVE);
        ps[2]= Pattern.compile("value",Pattern.CASE_INSENSITIVE);
        ps[3]= Pattern.compile("position",Pattern.CASE_INSENSITIVE);
        ps[4]= Pattern.compile("raw",Pattern.CASE_INSENSITIVE);
        ps[5]= Pattern.compile("normaliz",Pattern.CASE_INSENSITIVE);


       try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String headerLine = reader.readLine();
			reader.close();
			String [] headerFields = headerLine.split("\t");
           for(String head : headerFields) {
                for(Pattern p : ps) {
                    if(p.matcher(head).find()) {
                        return true;
                    }
                }
           }


       }catch(IOException e) {
          throw new TapestryException("cant read inputfile: "+file.getName(),null);

       }

       return false;
    }

    public Object onActionFromGoBackwebCellHTS2() {        
        cellHTS2.activatedFromOtherPage(this.getClass().getName());
        cellHTS2.setErrorDatafileMsg("");
        return cellHTS2;
    }

    public LinkedHashMap<String,Integer> eventObjToColumnNamesAndNums(Object []objs) {
        LinkedHashMap<String,Integer> returnMap = new LinkedHashMap<String,Integer>();

        for(int i=0;i<objs.length;i+=2) {
            try {
                returnMap.put((String)objs[i],Integer.parseInt((String)objs[i+1]));
            }catch(NumberFormatException e) {
                continue;
            }
        }


        return returnMap;
    }
    public String eventObjToColumValueString(Object []objs,String key) {
        System.out.println("searching:"+key);
        for(int i=0;i<objs.length;i++) {
            System.out.println((String)objs[i]);
          if(((String)objs[i]).equals(key)) {
              return (String)objs[i+1];
          }
        }


       return "";
    }


    public Object[] getEmptyObject() {
        return new Object[]{"test"};
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

    public static String changeFileExtension(String originalName, String newExtension) {
        int lastDot = originalName.lastIndexOf(".");
        if (lastDot != -1) {
            return originalName.substring(0, lastDot) + newExtension;
        } else {
          return originalName + newExtension;
        }
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

    public ArrayList<String> getHeadsToFindFurtherAnnotationfile() {
        return headsToFindFurtherAnnotationfile;
    }

    public void setHeadsToFindFurtherAnnotationfile(ArrayList<String> headsToFindFurtherAnnotationfile) {
        this.headsToFindFurtherAnnotationfile = headsToFindFurtherAnnotationfile;
    }

    public boolean getDataFileCreated() {
        return dataFileCreated;
    }

    public void setDataFileCreated(boolean dataFileCreated) {
        this.dataFileCreated = dataFileCreated;
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

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public String getDatafileImporterMsg() {
        return datafileImporterMsg;
    }

    public void setDatafileImporterMsg(String datafileImporterMsg) {
        this.datafileImporterMsg = datafileImporterMsg;
    }
    public String getPlateConfigFileImporterMsg() {
        return plateConfigFileImporterMsg;
    }

    public void setPlateConfigFileImporterMsg(String plateConfigFileImporterMsg) {
        this.plateConfigFileImporterMsg = plateConfigFileImporterMsg;
    }

    public TreeSet<Integer> getAdditionalAnnotCols() {
        return additionalAnnotCols;
    }

    public void setAdditionalAnnotCols(TreeSet<Integer> additionalAnnotCols) {
        this.additionalAnnotCols = additionalAnnotCols;
    }

    public String getAnnotationfileImporterMsg() {
        return annotationfileImporterMsg;
    }

    public void setAnnotationfileImporterMsg(String annotationfileImporterMsg) {
        this.annotationfileImporterMsg = annotationfileImporterMsg;
    }
    // end of getters and setters-------------------------------------------------------------------------
}
