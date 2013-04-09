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

package cellHTS.components;

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.*;
import org.apache.tapestry5.util.TextStreamResponse;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

import cellHTS.classes.Configuration;
import cellHTS.classes.FileParser;

import data.DataFile;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 14.11.2008
 * Time: 14:59:38
 * To change this template use File | Settings | File Templates.
 */



/**
 *
 * this component creates a table grid with the uploaded files and parameters --which are editable through clicking on table elements
 *
 */
public final class UploadedFileGrid {

    @Inject
    private ComponentResources resources;

    @Environmental
    private JavaScriptSupport pageRenderSupport;

    @Persist
    private boolean noFirstRun;

    //this is the list we will add/remove datafiles from-to and
    // their appropriate parameters ...so that this could be used outside this compoent as well
    //when you first start this component the keys are the mandantory filenames
    //values (parsed filename parameters) are emtyp and will be calculated here..
    @Parameter(required = true)
    private HashMap<String, DataFile> dataFileList;

    @Parameter
    private boolean showTable;

    //this holds the info if the uploaded files should be parsed the parameters out of the filename
    @Parameter(required = true, defaultPrefix = "prop")
    private boolean parseFileParams;

    //this parameter will be a fix regexp you define to parse the filenames with
    @Parameter(required=false,defaultPrefix="prop")
    private String fixRegExp;

    //we need this to display our data in a grid environment (cannot handle hashmaps)
    @Persist
    private ArrayList<DataFile> gridDataFileList;
    @Inject
    private Request request;
    //this is the link to the callback function we will use for a target from the mixin class
    //which will modify the grid content (inplace editing)
    @Persist
    private String gridDataStructureModifyLink;
    //this obj holds all the files manually annotated
    @Parameter(required = false)
    private HashSet<String> excludeFilesFromParsing;
    @Parameter(required=true)
    private boolean dualChannel;

    //this variable holds the name of the parameter sent from js to tapestry which we can access our JSON from request obj
    private final String PARAM_NAME ="fileParamUpdateInfo";
    @Persist
    private String exclude;
    
    /**
     *
     * before rendering the table
     */
    @SetupRender
    public void setupRender() {
        //init stuff..only once
        if(!noFirstRun) {
            noFirstRun=true;            
            gridDataStructureModifyLink = resources.createEventLink("receiveChangedFileParameter").toAbsoluteURI();
            if(excludeFilesFromParsing==null){
               excludeFilesFromParsing = new HashSet<String>();
            }
        }
        checkParameter();
        //erase old list when rerendering
        gridDataFileList = new ArrayList<DataFile>();

        //if we have deleted all files from the dataFileList we should also delete the
        //excludeFilesFromParsing as well
        if (dataFileList.isEmpty()) {
            excludeFilesFromParsing.clear();
        }
        if(fixRegExp==null) {
            fixRegExp="";
        }
        //try to parse the filename parameters
        FileParser.parseDataFilenameParams(dataFileList, excludeFilesFromParsing,fixRegExp);

        Iterator dataFileIterator = dataFileList.keySet().iterator();
        while (dataFileIterator.hasNext()) {
            String thisFilename = (String) dataFileIterator.next();

            if (!parseFileParams) {
                //only consider files which have not been modified here too
                if(!excludeFilesFromParsing.contains(thisFilename)) {
                    //if we dont want to parse filename stuff
                   dataFileList.put(thisFilename, new DataFile(thisFilename, null, null, null));
                }
                //excludeFilesFromParsing.add(thisFilename);
            }

        }
        //build a list out of the datafilelist hash to make it usable for the grid component where we want to
        //display the data...sorted by filename
        List dataFileMapKeys = new ArrayList(dataFileList.keySet());
        //sort it
        TreeSet<String> sortedKeys = new TreeSet<String>(dataFileMapKeys);
        for (String validFile : sortedKeys) {
            DataFile value = (DataFile) dataFileList.get(validFile);
            gridDataFileList.add(value);
        }
        if(!dualChannel) {
            exclude="channel";
        }
        else {
            exclude="";
        }

    }

    public ArrayList<DataFile> getGridDataFileList() {
        return gridDataFileList;
    }

    public void setGridDataFileList(ArrayList<DataFile> gridDataFileList) {
        this.gridDataFileList = gridDataFileList;
    }

    public HashMap<String, DataFile> getDataFileList() {
        return dataFileList;
    }

    public void setDataFileList(HashMap<String, DataFile> dataFileList) {
        this.dataFileList = dataFileList;
    }


    public boolean isShowTable() {
        return showTable;
    }

    public void setShowTable(boolean showTable) {
        this.showTable = showTable;
    }

    public String getFirstGridDatafileEntry() {
        return gridDataFileList.get(0).getFileName();
    }



    /**
     *
     * this is the sever side ajax callback function for the modification of the grid datastructure
     * which will be called by the mixin cass gridEditor which will add a cool inplace editor for grid components
     *
     * @return returns the AJAX request in processed form
     */
    public JSONObject onReceiveChangedFileParameter() {
        JSONObject returnJSON = new JSONObject();
        //this is only to complete the ajax request with a response
        returnJSON.put("JSON", "successfully done");
        if (request==null) {
        	return new JSONObject("dummy","dummy");
        }
        String jSONString = request.getParameter(PARAM_NAME);
        if(jSONString == null) {
        	return new JSONObject("dummy","dummy");
        }
        
        //the actual json request is in fiefox3 and sagari always the last element TODO:check IE what happens there?
       // String jSONString = requestArr.get(requestArr.size()-1);


        //json string consists of row, column and the new value of the row and column as well
        //set the proper element in the grid datastructure
        JSONObject jsonObj = new JSONObject(jSONString);

        Integer row=null;
        Integer col=null;
        Integer value=null;
        String filename = null;
        try {
            row = Integer.parseInt((String) jsonObj.get("row"));
            col = Integer.parseInt((String) jsonObj.get("column"));
            value = Integer.parseInt((String) jsonObj.get("value"));
         }
        catch (NumberFormatException exc) {
            String exceptionText="number format exception: "+jsonObj.toString()+" this is often due browser/js incompatibilities...check gridEditor.js and browser";
        }
            filename = (String) jsonObj.get("filename");


            //the files which have been edited manually shouldnt be parsed the
            //next time you upload a new file
            excludeFilesFromParsing.add(filename);
            //coumn 1 is the plate number
            if (col == 1) {
                //get the datafile object from the original grid data ...to edit it
                dataFileList.get(filename).setPlateNumber(value);
            }
            //two is replicate
            else if (col == 2) {

                dataFileList.get(filename).setReplicate(value);
            }
            //three is the channel
            else if (col == 3) {
                dataFileList.get(filename).setChannel(value);
            }

            //this is just to end the ajax call correclty
            return returnJSON;
        }


        public String getGridDataStructureModifyLink() {
            return gridDataStructureModifyLink;
        }

        public void setGridDataStructureModifyLink
        (String
        gridDataStructureModifyLink){
        this.gridDataStructureModifyLink = gridDataStructureModifyLink;
    }

    /**
     *  this method checks if a datafile is empty
     *
     */
    public void checkParameter()
        {
            String exceptionText = null;

            if (dataFileList == null) {
                exceptionText = "component parameter: dataFileList MUST NOT be null..you have to submit an existend object";
            }
            if (exceptionText != null) {
                TapestryException exception = new TapestryException(exceptionText, null);
                throw exception;
            }

        }

    public String getPARAM_NAME() {
        return PARAM_NAME;
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }
}
