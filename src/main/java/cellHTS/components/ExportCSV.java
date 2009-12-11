package cellHTS.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

import jxl.WorkbookSettings;
import jxl.Workbook;
import jxl.Sheet;
import jxl.Cell;

/**
 * Created by IntelliJ IDEA.
 * User: pelz
 * Date: 22.11.2009
 * Time: 13:36:03
 * To change this template use File | Settings | File Templates.
 */

//this component exports a excel or csv file to the standard csv tab file we will use in other components as well
public class ExportCSV {
    //parameters ----------------------------------------------------------------------------
    @Parameter(required = true)
    private ArrayList<String> filesToProcess;
//parameters end ----------------------------------------------------------------------------

    @Persist
    private String fileType;
    @Persist
    private String csvDelimter;

    @Persist
    private boolean errorFound;
    @Persist
    private String errorMsg;

    @Persist
    private boolean init;

    @Persist
    private ArrayList<String> convertedFilenames;
    @Inject
    private ComponentResources componentResources;
    @Persist
    private String SUCCESSEVENTNAME;
    @Persist
    private String FAILEVENTNAME;
    @Persist
    private String fileTypeModel;
    @Inject
    private RenderSupport renderSupport;
    @Persist
    private String sheetNumber;

    @Persist
    private String uniqueID;

    public void setupRender() {
        if (!init) {
            System.out.println("we init!");
            init = true;
            //this will be the please chose label:
            fileType = null;
            csvDelimter = "\\t";
            SUCCESSEVENTNAME = "successfullyConvertedToCVS";
            FAILEVENTNAME= "failedConvertedToCVS";
            fileTypeModel = "excel,csv";
            uniqueID = renderSupport.allocateClientId(componentResources);

        }
    }
   //TODO: variables dont get updated
    public void onSuccessFromBigForm() {
       //if you selected the item "please select"
        if(fileType==null || fileType.equals("")) {
            return;
        }
       
        if (filesToProcess.size() < 1) {
            return;
        }
        resetError();
        //outputfiles are inputfiles plus csv

        //show an grid
        if (fileType.equals("excel")) {
            System.out.println("Bam outside");
            if (excelToTabCVS()) {
                System.out.println("bam inside");                
                triggerSuccessEvent();
            }
            else {
                //if an error occured
                init=false;
                triggerFailEvent();
            }

        } else if (fileType.equals("csv")) {
            if (csvDelimter.equals("")) {
                setError("error: no valid delimter defined");
                return;
            }
            if (cvsToTabCVS()) {
                triggerSuccessEvent();
            }
            else {
                init=false;
                triggerFailEvent();
            }
        }
        //call an event which will be fired if we successfully converted everything

    }
    //AJAX event handlers

    @OnEvent(component = "fileType", value = "change")
    public JSONObject onFileTypeChangeEvent(String type) {
        fileType = type;
        if (fileType.equals("excel")) {
            // csvDelimter="\\t";
        }
        return new JSONObject().put("fileType", fileType);
    }


    public void triggerSuccessEvent() {
        //trigger an event that everything has been converted successfully and
        //parameters are the converted files
        ComponentEventCallback callback = new ComponentEventCallback() {
            public boolean handleResult(Object result) {
                return true;
            }
        };
        if (convertedFilenames.size() > 0) {
            Object[] objs = new Object[convertedFilenames.size()];
            int i = 0;
            for (String cf : convertedFilenames) {
                objs[i++] = cf;
            }
            componentResources.triggerEvent(SUCCESSEVENTNAME, objs, callback);
        }
    }
    public void triggerFailEvent() {
        //trigger an event that everything has been converted successfully and
        //parameters are the converted files
        ComponentEventCallback callback = new ComponentEventCallback() {
            public boolean handleResult(Object result) {
                return true;
            }
        };
        componentResources.triggerEvent(FAILEVENTNAME,new Object[]{}, callback);
        
    }

    public boolean cvsToTabCVS() {

        String currentFile = "";
        convertedFilenames = new ArrayList<String>();
        try {
            for (String inputFile : filesToProcess) {
                currentFile = inputFile;
                String outFile = inputFile + ".out";
                File f = new File(outFile);
                //the readers
                FileReader reader = new FileReader(inputFile);
                BufferedReader buffer = new BufferedReader(reader);
                //The writers
                FileWriter fileWriter = new FileWriter(outFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


                String line;

                boolean headerFound = false;
                //read the line
                while ((line = buffer.readLine()) != null) {
                     //transfer the line
                    String newLine=line.replaceAll(csvDelimter,"\t");
                  //write the line
                     bufferedWriter.write(newLine+"\n");

                }
                buffer.close();
                reader.close();
                bufferedWriter.close();
                fileWriter.close();
                 convertedFilenames.add(outFile);

            }
        } catch (IOException e) {
            e.printStackTrace();
            setError("error: input output exception" + " in file: " + new File(currentFile).getName());
            System.err.println(e.toString());
            return false;
        }

        return true;
    }

    public boolean excelToTabCVS() {

        {
            convertedFilenames = new ArrayList<String>();
            int sheet = 0;
            try {
                sheet = Integer.parseInt(sheetNumber);
            }
            catch (NumberFormatException e) {
                sheet = 0;
            }

            String currentFile = "";
            try {
                for (String inputFile : filesToProcess) {
                    currentFile = inputFile;
                    //File to store data in form of CSV
                    String outFile = inputFile + ".out";
                    File f = new File(outFile);


                    OutputStream os = (OutputStream) new FileOutputStream(f);
                    String encoding = "UTF8";
                    OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
                    BufferedWriter bw = new BufferedWriter(osw);

                    //Excel document to be imported
                    String filename = inputFile;
                    WorkbookSettings ws = new WorkbookSettings();
                    ws.setLocale(new Locale("en", "EN"));
                    Workbook w = Workbook.getWorkbook(new File(filename), ws);

                    // Gets the sheets from workbook
                    //for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
                    Sheet s = w.getSheet(sheet);

                    // bw.write(s.getName());
                    // bw.newLine();

                    Cell[] row = null;

                    // Gets the cells from sheet
                    for (int i = 0; i < s.getRows(); i++) {
                        row = s.getRow(i);

                        if (row.length > 0) {
                            bw.write(row[0].getContents());
                            for (int j = 1; j < row.length; j++) {
                                bw.write('\t');
                                bw.write(row[j].getContents());
                            }
                        }
                        bw.newLine();
                    }
                    //}
                     convertedFilenames.add(outFile);
                    bw.flush();
                    bw.close();
                }
            }
            catch (UnsupportedEncodingException e) {
                setError("error: unsupported encoding" + " in file: " + new File(currentFile).getName());
                System.err.println(e.toString());
                return false;
            }
            catch (IOException e) {
                setError("error: input output exception" + " in file: " + new File(currentFile).getName());
                System.err.println(e.toString());
                return false;
            }
            catch (Exception e) {
                setError("generic error occured:" + e.getMessage() + " in file: " + new File(currentFile).getName());
                System.err.println(e.toString());
                return false;
            }
        }
        return true;
    }

    public void setError(String msg) {
        errorFound = true;
        errorMsg = msg;
    }

    public void resetError() {
        errorFound = false;
        errorMsg = "";
    }

    public String getFileTypeModel() {
        return fileTypeModel;
    }

    public void setFileTypeModel(String fileTypeModel) {
        this.fileTypeModel = fileTypeModel;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isErrorFound() {
        return errorFound;
    }

    public void setErrorFound(boolean errorFound) {
        this.errorFound = errorFound;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getCsvDelimter() {
        return csvDelimter;
    }

    public void setCsvDelimter(String csvDelimter) {
        this.csvDelimter = csvDelimter;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
