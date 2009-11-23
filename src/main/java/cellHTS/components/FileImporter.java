package cellHTS.components;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;

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
    private ArrayList<String> headsToFind;
    @Parameter(required=true)
    private boolean showHeadline;


//parameters end ----------------------------------------------------------------------------

     private String loopVariable;
    
     @Persist
     private boolean errorFound;
     @Persist
     private String errorMsg;
     @Persist
     private String plateDataModel;
     @Persist
     private String csvDelimter;


    
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
    private String DROPDOWNDELIMTER;

    
    @Persist
    private boolean init;
    @Persist
    private SelectedColumn[] selectedColumns;
    private SelectedColumn selectedColumn;

    @Persist
    private String EVENTNAME;
    @Persist
    private String uniqueID;

    @Inject
    private ComponentResources componentResources;
     @Inject
    private RenderSupport renderSupport;

   public void setupRender() {
        if(!init) {
            init=true;
            csvDelimter="\t";
            selectedColumns=new SelectedColumn[headsToFind.size()];
            int i=0;
            for(String headToFind : headsToFind) {
                selectedColumns[i++]  = new SelectedColumn(headToFind);
            }
            File firstDataFileObj = new File(filesToProcess.get(0));
            firstDataFile=firstDataFileObj.getName();
            //all file headers must be equal to go on
            if(compareHeadersCSV()&&showHeadline) {
                 getHeaderLineItemsCSV(firstDataFileObj);
            }
            DROPDOWNDELIMTER=":";
            EVENTNAME="onSuccessfullySetupColumns";
            uniqueID = renderSupport.allocateClientId(componentResources);

        }
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
   public void getHeaderLineItemsCSV(File copied) {
       try {
			BufferedReader reader = new BufferedReader(new FileReader(copied));
			String headerLine = reader.readLine();
			String firstLine = reader.readLine();
			int i = 0;
			reader.close();
			headerFields = headerLine.split(csvDelimter);
            firstLineFields = headerLine.split(csvDelimter);
            plateDataModel="";
            plateDataModel=generateCSNumberList(headerFields);

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
                  returnString=count+DROPDOWNDELIMTER+value;
              }
              else {
                  returnString+=","+count+DROPDOWNDELIMTER+value;
              }
              count++;
          }
        return returnString;

   }

    //we use this inner class to keep track of the changed values in the drop down
    //we need this to build it dynamically
    public class SelectedColumn {
        private String columnName;
        private String mappedToColumn;
        private Integer mappedToColumnNumber;

        public SelectedColumn(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;

        }

        public String getMappedToColumn() {
            return mappedToColumn;
        }

        public void setMappedToColumn(String mappedToColumn) {
            this.mappedToColumn = mappedToColumn;
            mappedToColumnNumber = Integer.parseInt(mappedToColumn.split(DROPDOWNDELIMTER)[0]);
        }
    }

     public void setError(String msg) {
        errorFound=true;
        errorMsg=msg;
    }
    public void resetError() {
        errorFound=false;
        errorMsg="";
    }
    public Object[] selectedColumnsToObjects() {
        Object[] returnObjs = new Object[selectedColumns.length*2];
        int i=0;
        for(SelectedColumn tempColumn : selectedColumns) {
             returnObjs[i++]=tempColumn.getColumnName();
             returnObjs[i++]=tempColumn.getMappedToColumn();
        }
        return returnObjs;
    }
    public void onSuccessFromBigForm1() {
         triggerSuccessEvent();

    }
    public void triggerSuccessEvent() {
            //trigger an event that everything has been converted successfully and
            //parameters are the converted files
            ComponentEventCallback callback = new ComponentEventCallback() {
                public boolean handleResult(Object result) {
                    return true;
                }
            };

                Object[] objs =selectedColumnsToObjects();

                componentResources.triggerEvent(EVENTNAME, objs, callback);
    }
//getters and setters -----------------------------------------------------------------------------

    public boolean getErrorFound() {
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

    public ArrayList<String> getFilesToProcess() {
        return filesToProcess;
    }

    public void setFilesToProcess(ArrayList<String> filesToProcess) {
        this.filesToProcess = filesToProcess;
    }

    public String getLoopVariable() {
        return loopVariable;
    }

    public void setLoopVariable(String loopVariable) {
        this.loopVariable = loopVariable;
    }

    public ArrayList<String> getHeadsToFind() {
        return headsToFind;
    }

    public void setHeadsToFind(ArrayList<String> headsToFind) {
        this.headsToFind = headsToFind;
    }

    public String getPlateDataModel() {
        return plateDataModel;
    }

    public void setPlateDataModel(String plateDataModel) {
        this.plateDataModel = plateDataModel;
    }

    public String getHeaderField() {
        return headerField;
    }

    public void setHeaderField(String headerField) {
        this.headerField = headerField;
    }

    public String[] getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(String[] headerFields) {
        this.headerFields = headerFields;
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
        return firstDataFile;
    }

    public void setFirstDataFile(String firstDataFile) {
        this.firstDataFile = firstDataFile;
    }

    public SelectedColumn[] getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(SelectedColumn[] selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    public SelectedColumn getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(SelectedColumn selectedColumn) {
        this.selectedColumn = selectedColumn;
    }

    public boolean isShowHeadline() {
        return showHeadline;
    }

    public void setShowHeadline(boolean showHeadline) {
        this.showHeadline = showHeadline;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
//end of setters---------------------------------------------------


    
}
