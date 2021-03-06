package cellHTS.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.*;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.Request;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import cellHTS.classes.SelectedColumn;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 20.11.2009
 * Time: 10:42:16
 * To change this template use File | Settings | File Templates.
 */                                  //implement the methods so we can use the selectedColumn in a loop
@Import(stylesheet = {"context:/assets/jquery.tooltip.css"},library={"context:/assets/js/jquery.min.js", "context:/assets/js/jquery.tooltip.pack.js","FileImporter.js"})
public class FileImporter{

//parameters ----------------------------------------------------------------------------
    @Parameter(required=true)
    private ArrayList<String> filesToProcess;
    @Parameter(required=true)
    private ArrayList<String> headsToFind;
    @Parameter(required=true)
    private boolean showHeadline;
    @Parameter(required=true)
    private Boolean compareHeader;

    //one column can be selected multiple times
    @Parameter(required=false)
    private boolean moreThanOne;
    @Parameter(required=false)
    private TreeSet<Integer> moreThanOneCols;
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
    @Persist
    private String multipleChangeSelect;

    @Inject
    private ComponentResources resources;
    @Inject
    private Request request;

    
    //for the ajax requests
    public String getAJAXRequestURI() {
        return resources.createEventLink("ChangeChannelEvent").toAbsoluteURI();
    }
    public JSONObject onChangeChannelEvent() {
        //this first select has to be executed so we will check here if the request was ajax enabled
        if(!request.isXHR()) {
            return null;
        }
        String type = request.getParameter("type");

      //this is the empty one
        if(type.equals("")) {
            moreThanOneCols.clear();
            return new JSONObject().put("selectHeader","");
        }

        String[] column = type.split(":");

        if(column[0]!=null) {
            Integer col = Integer.parseInt(column[0]);

            moreThanOneCols.add(col);
        }
        else {
            return new JSONObject().put("selectHeader", "");
        }
        
        String colsString="";
        for(Integer moreThanOne : moreThanOneCols){
            if(colsString.equals("")) {
                colsString=""+moreThanOne;
            }
            else {
                colsString+=","+moreThanOne;
            }
        }
        
        return new JSONObject().put("dummy", "dummy");
    }

    
   public void setupRender() {

        if(!init) {
            init=true;
            csvDelimter="\\t";
            DROPDOWNDELIMTER=":";


            EVENTNAME="successfullySetupColumns";
            uniqueID = renderSupport.allocateClientId(componentResources);

            if(moreThanOne) {
                moreThanOneCols=new TreeSet<Integer>();
            }
           

            selectedColumns=new SelectedColumn[headsToFind.size()];
            int i=0;
            for(String headToFind : headsToFind) {
                selectedColumns[i++]  = new SelectedColumn(headToFind);
             }
            //to show nothing on reload 
             multipleChangeSelect=null;
             resetError();

        }
       if(headerFields==null) {
           headerFields=new String[] {};
       }
       if(firstLineFields==null) {
           firstLineFields=new String[] {};
       }
       if(plateDataModel==null) {
           plateDataModel="";
       }


       File firstDataFileObj = new File(filesToProcess.get(0));
       firstDataFile=firstDataFileObj.getName();
       

       getHeaderLineItemsCSV(firstDataFileObj);
   }




  


   public boolean compareHeadersCSV() {
       if(!compareHeader) {
           return true;
       }
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
            firstLineFields = firstLine.split(csvDelimter);
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
              if(value.equals("")|| value.equals(" ")) {
                  continue;
              }
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
    


    
    

     public void setError(String msg) {
        errorFound=true;
        errorMsg=msg;
    }
    public void resetError() {
        errorFound=false;
        errorMsg="";
    }
    public Object[] selectedColumnsToObjects() {



        ArrayList<String> tempColumns=new ArrayList<String>();
        for(SelectedColumn tempColumn : selectedColumns) {
             if(tempColumn==null ) {
                 continue;
             }
             if(tempColumn.getColumnName()==null || tempColumn.getColumnName().equals("")) {
                 continue;
             }
             if(tempColumn.getMappedToColumn()==null || tempColumn.getMappedToColumn().equals("")) {
                 continue;
             }
             String name = tempColumn.getColumnName();
             String number="";
             if(tempColumn.getColumnNumber()!=null) {
                 number=""+tempColumn.getColumnNumber();
             }
            tempColumns.add(name);
            tempColumns.add(number);
        }
        if(moreThanOne)  {
            //this will be the name of the column which has multiple col IDs in it

            String colsString="";
            for(Integer moreThanOne : moreThanOneCols){
                if(colsString.equals("")) {
                    colsString=""+moreThanOne;
                }
                else {
                    colsString+=","+moreThanOne;
                }

            }
            if(!colsString.equals(""))  {
                tempColumns.add("multipleColumn");
                tempColumns.add(colsString);
                //System.out.println("XXXYYYZZZ:"+colsString);
            }
        }
        Object[] returnObjs = new Object[]{};
        if(tempColumns.size()>0) {
            returnObjs=new Object[tempColumns.size()];
            int i=0;
            for(String tmp : tempColumns) {
                returnObjs[i]=tmp;
                i++;
            } 
        }
        return returnObjs;
    }
    public void onSuccessFromBigForm1(String uniqueIdentifier) {         
             //all file headers must be equal to go on
      //make sure this was called from the right component
       if(uniqueIdentifier.equals(this.uniqueID)) {
            if(compareHeadersCSV()) {
                 triggerSuccessEvent();
            }
       }


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
                if(objs.length>0) {
                    componentResources.triggerEvent(EVENTNAME, objs, callback);
    }           }
    
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

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isMoreThanOne() {
        return moreThanOne;
    }

    public void setMoreThanOne(boolean moreThanOne) {
        this.moreThanOne = moreThanOne;
    }

    public String getMultipleChangeSelect() {
        return multipleChangeSelect;
    }

    public void setMultipleChangeSelect(String multipleChangeSelect) {
        this.multipleChangeSelect = multipleChangeSelect;
    }
    //end of setters---------------------------------------------------


    
}
