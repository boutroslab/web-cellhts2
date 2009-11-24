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

public class DebugPage {
    @Persist
    private boolean convertedAllFiles;
    @Persist
    private ArrayList<String> uploadedFiles;
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


    @InjectComponent
    private ExportCSV exportCSV;
    @InjectComponent
    private FileImporter dataFileImporter;
    @InjectComponent
    private FileImporter plateConfigImporter;
    @InjectComponent
    private FileImporter annotationImporter;


    public void setupRender() {
        if(!init) {
            init=true;
            uploadedFiles = new ArrayList<String>();
            filesToImport = new ArrayList<String>();
            plateWellDefined=false;
            headsToFindDatafile = new ArrayList<String>();
            headsToFindDatafile.add("Plate");
            headsToFindDatafile.add("Well");
            headsToFindDatafile.add("Value");
            headsToFindPlateconfigfile = new ArrayList<String>();
            headsToFindPlateconfigfile.add("Value");
            headsToFindAnnotationfile=new ArrayList<String>();
            headsToFindAnnotationfile.add("GeneID");


            uploadPath="/tmp/bla";
            startFileImport=false;
            convertedAllFiles=false;

        }
    }
          //this is for testing only
    public ArrayList<String> getTempDebugFiles() {

         ArrayList<String> al = new ArrayList<String>();
       // al.add("/home/pelz/cellHTS2-auswertugn/cellHTS2/JOB5702_RUN5703/in/A02-W17-C.TXT");
       // al.add("/home/pelz/cellHTS2-auswertugn/cellHTS2/JOB5702_RUN5703/in/A02-W17-C.TXT");
       // al.add("/home/pelz/cellHTS2-auswertugn/cellHTS2/JOB5702_RUN5703/in/A01-W21-C.TXT");
       // al.add("/home/pelz/cellHTS2-auswertugn/cellHTS2/JOB5702_RUN5703/in/A02-W49-C.TXT");




      //  al.add("/home/pelz/Desktop/LabCollector/Filemaker_Last_Import_linuxFilemaker/orders.xls");
      //  al.add("/home/pelz/Desktop/LabCollector/Filemaker_Last_Import_linuxFilemaker/oligos.xls");
      //  al.add("/home/pelz/Desktop/LabCollector/Filemaker_Last_Import_linuxFilemaker/plasmids.xls");

         //al.add("/home/pelz/csvTAB");

      //al.add("/home/pelz/csvKaufmann");

     //    al.add("/temp/cellHTS2/JOB63651/A15_W01.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W02.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W03.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W04.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W05.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W06.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W07.TXT");
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
        System.out.println("I am here");
        filesToImport.clear();
        for(Object obj: objs) {
            filesToImport.add((String)obj);
            System.out.println("file:"+(String)obj);
        }
        System.out.println("filesToImport:"+filesToImport);
        convertedAllFiles=true;
    }
    public void onFailedConvertedToCVSFromExportCSV(Object []dummy) {
       convertedAllFiles=false;
        //reinit everything
       dataFileImporter.setInit(false);
    }
    public void onActionFromProcessFiles() {
        if(uploadedFiles.size()>0) {
            startFileImport=true;
            System.out.println("bam");
        }
        else {
            startFileImport=false;
        }
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
        //now generate the data files
        if(returnMap.containsKey("Plate")&&returnMap.containsKey("Well")&&returnMap.containsKey("Value")) {
           ArrayList<File> inputFiles= new ArrayList<File>();
            for(String tempFile : filesToImport) {
                inputFiles.add(new File(tempFile));
            }
            ArrayList<File> outputFiles= new ArrayList<File>();
            for(String tempFile : uploadedFiles) {
                outputFiles.add(new File(tempFile));
            }

           if(FileCreator.createDataFilesFromCVSFiles(inputFiles,outputFiles,returnMap)) {
               System.out.println("creation succeeded");
                 //send the files to cellHTS2

           }
            else {
                //create a error message
           }

        }

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
    // end of getters and setters-------------------------------------------------------------------------
}
