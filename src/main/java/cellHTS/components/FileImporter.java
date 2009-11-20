package cellHTS.components;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.Block;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 20.11.2009
 * Time: 10:42:16
 * To change this template use File | Settings | File Templates.
 */
public class FileImporter {

//parameters ----------------------------------------------------------------------------
    @Parameter(required=true)
    private ArrayList<String> filesToProcess;

    @Parameter(required=true)
    private String[] inputColumns;

    @Parameter(required=true)
    private LinkedHashMap<String,Integer> outputColumns;

    @Parameter(required=true)
    private boolean showHeadline;
//parameters end ----------------------------------------------------------------------------

    
    @Persist
    private String plateDataModel;



    
    @Persist
    private String[] headerFields;
    //only for iterating
    private String headerField;
    @Persist
    private String[] firstLineFields;
    private String firstLineField;
    @Persist
    private String firstDataFile;
    @Persist
    private String selectedPlateColumn;
    @Persist
    private String selectedWellColumn;
    @Persist
    private String selectedValueColumn;
    @Persist
    private String selectedPlateConfigContentColumn;


    @Inject
    private Block plateConfigBlock;





    public void onSubmitFromForm1() {
        if(filesToProcess.size()<1) {
            return;
        }


        //show an grid
        if(fileType.equals("excel")) {
                //get the header and first line for displaying
                getHeaderLineItemsCSV(new File(filesToProcess.get(0)));
                //show header and first line in grid
                activateGrid=true;

        }
        else if (fileType.equals("csv")) {
                //get the header and first line for displaying
                getHeaderLineItemsCSV(new File(filesToProcess.get(0)));
                //generate the models for selecting approperiate plate data columns
                plateDataModel=generateCSNumberList(headerFields);
                initPlateDataDropDown();


                //show header and first line in grid
                activateGrid=true;

        }


    }


    //AJAX event handlers

    @OnEvent(component = "fileType", value = "change")
    public JSONObject onFileTypeChangeEvent(String type) {
          fileType = type;
          if(fileType.equals("excel")) {
             // csvDelimter="\\t";
          }
          return new JSONObject().put("fileType", fileType);
    }


   public boolean compareHeadersCSV() {
       String fileToCompare = filesToProcess.get(0);
       File compare = new File(fileToCompare);
       String headerLineToCompare = getHeaderLineStringCSV(compare);

       for (String fileCompareWith : filesToProcess) {
           File compareWith = new File(fileCompareWith);
            String headerLineCompareWith = getHeaderLineStringCSV(compareWith);
           if(!headerLineToCompare.equals(headerLineCompareWith))   {
                String errorMessage = "all the headers must be equal , file header from file: "+compare.getName()+" does not fit file header from: "+compareWith.getName()+":\n\n"+headerLineCompareWith+"\n\n"+headerLineToCompare;
                System.out.println(errorMessage);
                setError(errorMessage);

               return false;
           }

       }

       return true;
   }
   public boolean compareHeadersExcel() {
       return true;
   }
   public void getHeaderLineItemsCSV(File copied) {
       try {
			BufferedReader reader = new BufferedReader(new FileReader(copied));
			String headerLine = reader.readLine();
			String firstLine = reader.readLine();
			int i = 0;
			reader.close();
			headerFields = headerLine.split(csvDelimter);
            firstLineFields = headerLine.split(csvDelimter);


       }catch(IOException e) {
           setError("cannot read header or first line of file: "+copied.getName());

       }
   }
   public String getHeaderLineStringCSV(File copied) {
       try {
			BufferedReader reader = new BufferedReader(new FileReader(copied));
			String headerLine = reader.readLine();


			reader.close();
			return headerLine;


       }catch(IOException e) {
           setError("cannot read header or first line of file: "+copied.getName());

       }
       return null;
   }


   //generate a comma seperated list
    public String generateCSList(String[]values) {
          String returnString="";
          for(String value: values) {
              if(returnString.equals("")) {
                  returnString=value;
              }
              else {
                  returnString+=","+value;
              }
          }
        return returnString;
    }
    public String generateCSNumberList(String[]values) {
          String returnString="";
          int count=1;
          for(String value: values) {
              if(returnString.equals("")) {
                  returnString=count+":"+value;
              }
              else {
                  returnString+=","+count+":"+value;
              }
              count++;
          }
        return returnString;

   }
    public void initPlateDataDropDown() {
        selectedPlateColumn="";
        selectedWellColumn="";
        selectedValueColumn="";
        if(firstLineFields[0]!=null) {
           selectedPlateColumn="1:"+firstLineFields[0];
        }
        if(firstLineFields[1]!=null) {
           selectedWellColumn="2:"+firstLineFields[1];
        }
        if(firstLineFields[2]!=null) {
           selectedValueColumn="3:"+firstLineFields[2];
        }
        if(firstLineFields[3]!=null) {
           selectedPlateConfigContentColumn="4:"+firstLineFields[2];
        }


    }
    public Block onActionFromFilesContainPlateConfigData() {
         return plateConfigBlock;
    }

//getters and setters -----------------------------------------------------------------------------

    public void setError(String msg) {
        errorFound=true;
        errorMsg=msg;
    }
    public void resetError() {
        errorFound=false;
        errorMsg="";
    }
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getCsvDelimter() {
        return csvDelimter;
    }

    public void setCsvDelimter(String csvDelimter) {
        this.csvDelimter = csvDelimter;
    }

    public String getFileTypeModel() {
        return fileTypeModel;
    }

    public void setFileTypeModel(String fileTypeModel) {
        this.fileTypeModel = fileTypeModel;
    }

    public Boolean getActivateGrid() {
        return activateGrid;
    }

    public void setActivateGrid(Boolean activateGrid) {
        this.activateGrid = activateGrid;
    }

    public Boolean getErrorFound() {
        return errorFound;
    }

    public void setErrorFound(Boolean errorFound) {
        this.errorFound = errorFound;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String[] getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(String[] headerFields) {
        this.headerFields = headerFields;
    }

    public String getHeaderField() {
        return headerField;
    }

    public void setHeaderField(String headerField) {
        this.headerField = headerField;
    }

    public String[] getFirstLineFields() {
        return firstLineFields;
    }

    public void setFirstLineFields(String[] firstLineFields) {
        this.firstLineFields = firstLineFields;
    }

    public String getFirstLineField() {
        return firstLineField;
    }

    public void setFirstLineField(String firstLineField) {
        this.firstLineField = firstLineField;
    }

    public String getFirstDataFile() {
        if(filesToProcess.size()<1) {
            return "<NO FILE AVAILABLE>";
        }
        return new File(filesToProcess.get(0)).getName();
    }

    public void setFirstDataFile(String firstDataFile) {
        this.firstDataFile = firstDataFile;
    }

    public String getPlateDataModel() {
        return plateDataModel;
    }

    public void setPlateDataModel(String plateDataModel) {
        this.plateDataModel = plateDataModel;
    }

    public String getSelectedPlateColumn() {
        return selectedPlateColumn;
    }

    public void setSelectedPlateColumn(String selectedPlateColumn) {
        this.selectedPlateColumn = selectedPlateColumn;
    }

    public String getSelectedWellColumn() {
        return selectedWellColumn;
    }

    public void setSelectedWellColumn(String selectedWellColumn) {
        this.selectedWellColumn = selectedWellColumn;
    }

    public String getSelectedValueColumn() {
        return selectedValueColumn;
    }

    public void setSelectedValueColumn(String selectedValueColumn) {
        this.selectedValueColumn = selectedValueColumn;
    }

    public Block getPlateConfigBlock() {
        return plateConfigBlock;
    }

    public void setPlateConfigBlock(Block plateConfigBlock) {
        this.plateConfigBlock = plateConfigBlock;
    }

    public String getSelectedPlateConfigContentColumn() {
        return selectedPlateConfigContentColumn;
    }

    public void setSelectedPlateConfigContentColumn(String selectedPlateConfigContentColumn) {
        this.selectedPlateConfigContentColumn = selectedPlateConfigContentColumn;
    }
}
