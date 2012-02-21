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

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.util.NotificationEventCallback;
import org.apache.tapestry5.internal.services.ComponentResultProcessorWrapper;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.FormSupport;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import data.DataFile;
import data.Plate;


/**
 *
 *
 * This class is the server side of the plate configurator and connects to the plate configurator html and javascript
 * through AJAX/JSON
 * this class can fire an own custom event type when marking/unmarking the first or killing the last existant well.
 * the event will fire 1 if first well was clicked and 0 if last marked well was erased
 * fired event can be catched like:
 *
 *
 *public Object onClickWellEventFromPlateDesigner(int firstLast) {
 *   return cellHTS2Page;
 *}
 *
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 03.11.2008
 * Time: 16:01:19
 *
 */
@Import(library={"config.js","plate.js"})
public class PlateDesigner {

     @Environmental
     private RenderSupport pageRenderSupport;

     @Inject
     private ComponentResources resources;

     @Inject
     private Request request;

    @Parameter(required=true)
    private int plateFormat;

    //this will be used to store all the clicked wells...the complete wellMap
    @Parameter(required = true)
    private ArrayList<Plate> clickedWellsAndPlates;


    //this keeps information about what was in wells before one clicked it again..we need this to store contaminated data
    @Persist
    private HashMap<Integer,HashMap<String,String>> memorizeOldWells;

    //these are for creating our own event which will be fired if we click the first or the last existing well
    @Persist
    static final String SELECTED_EVENT = "clickWellEvent";
    @Environmental
    private ComponentEventResultProcessor componentEventResultProcessor;
    @Environmental
    private FormSupport formSupport;
    //create a parameter name which will be sent by ajax so that we can access the correct parameter in the tapestry request server side event handler
    private final String PARAM_NAME="fileParamUpdateInfo";

    /**
     *
     * if page gets reloaded this func will be automatically be called
     *
     * @param writer
     */
    @BeginRender
    public void graph(MarkupWriter writer){

        //init our datastructure to store what is in the wells before someone change it to another welltype
        if(memorizeOldWells==null) {
            memorizeOldWells=new HashMap<Integer,HashMap<String,String>>();
        }
        for(int i = 0;i <clickedWellsAndPlates.size();i++) {
            //init our "memory" plates if not already done
            if(!memorizeOldWells.containsKey(i)) {
                memorizeOldWells.put(i,new HashMap<String,String>());
            }
        }
    
        //check if the clickedWellMap reference was given correctly
        if(clickedWellsAndPlates==null) {
            String exceptionText = "the component plateDesign needs a mandantory parameter clickedWellMap which must be"
                                  +" a reference to a exisiting HashMap (String,String) which returns the clicked wells"
                                  +" and their type";
            TapestryException missingClickedWellMap = new TapestryException(exceptionText,null);
            throw missingClickedWellMap;

        }
//        int plateFormat = Integer.parseInt(this.plateFormat);
        int cols=0;

        Boolean includeSample=true;
        Boolean includeLabel=false;
        Boolean includeSave=true;

        //the outer table which will hold the plateTable and the dropDown menu
        writer.element("table");
        writer.element("tr");
        writer.element("td");
        writer.element("div");
        if (plateFormat == 96){
		   cols = 12;
            writer.element("div","id","plateDesign","style","float:left;");
	    }
	    else if (plateFormat == 384){
		   cols = 24;
            writer.element("div","id","plateDesign","style","float:left;");
         
	     }
		else if (plateFormat == 1536){
			cols = 48;
            writer.element("div","id","plateDesign","style","float:left;");
			
		}
		
	    else {
               writer.write("<br>Error: wrong plate format");

     	}
        int rows = plateFormat/cols;
        writer.element("table","id","plateTable", "border", "1", "cellspacing","0", "cellpadding","0");

        //keep it simple stupid
        //<= because weve got one extra row and one extra column due to
        //header row and column
        for(int i =0;i<=rows;i++) {

            writer.element("tr");
            //get ascii code
            int ascii = (int) i + 64;
            char letter = (char)ascii;
            
            for(int j=0;j<=cols;j++) {
                String well;


                //first row, first column (left upper corner of table should contain an X)
                if(i==0&&j==0) {
                    well = "X";
                }
                else if(i==0) {
                    well = ""+(j);

                }
                //print first column==header column
                else if(j==0) {
                    well=""+letter;
                }
                else {
                   well = letter+String.format("%02d",j);
                }

                writer.element("td","id","well_"+well);
                   writer.write(well);
                   writer.end();
            }

             writer.end();  //end of tr
        }

  
           //end of table div
        writer.end();

     //end of div plateDesign
     writer.end();


        writer.end();
        writer.element("td");



        //Method selection..make a drop down menue
        writer.write("Choose a wellType: ");
        //make a selection - drop down box
        writer.element("form","name","listColors");
        writer.element("select","id","wellType","name","WellType");
        //the value will be the html color we will paint the table element with when clicking at a specific well
            writer.element("option","value","pos","selected","selected");
                writer.write("positive");
            writer.end();
            writer.element("option","value","neg");
                writer.write("negative");
            writer.end();
            writer.element("option","value","other");
                writer.write("other");
            writer.end();
            writer.element("option","value","empty");
                writer.write("empty");
            writer.end();
            writer.element("option","value","cont1");
                writer.write("contaminated");
            writer.end();

        //end of selectionBox div
         writer.end();
        //end of form
        writer.end();


        
        writer.write("Choose a plate: ");
        //make a selection - drop down box
        writer.element("form","name","plateNumber");
        writer.element("select","id","plateNum","name","PlateNum");
            //plate 0 is the "all" plate which means this design will be the design for all plates
            writer.element("option","value",0);
                writer.write("all");
            writer.end();

        

        for(int i=1;i<clickedWellsAndPlates.size();i++) {
            writer.element("option","value",i);
            //build a beautiful html drop down link
            Plate simplePlate = clickedWellsAndPlates.get(i);
            String dropDownElement = "Pl_"+simplePlate.getPlateNum()+"_REPL_"+simplePlate.getReplicateNum();
                writer.write(dropDownElement);
            writer.end();


        }         

        //end of selectionBox div
         writer.end();
        //end of form
        writer.end();
        

        //end of td
        writer.end();
       
        writer.end();
        writer.end();
        writer.end();

        //a button to write a file ....this is only temp

    }

    /**
     *
     * This method will be run after rendering before user interaction
     * We will using it to connect Javascript to it
     *
     * @param writer
     */
     void afterRender(MarkupWriter writer){
         //make a JSON object with all the clicked Wells and send it to the javascript so
         //that already marked wells will be drawn though the component/page gets reloaded
         JSONArray temp = HashMapToJSONArr();
         String JSONStringToSend=null;
         if(temp!=null) {
             JSONStringToSend=temp.toString();
         }

        //javascript will send the name of the clicked well names later to this java class back (see onreceiveWellID method)
        //this is communicating with the server using AJAX technique
        Link link = resources.createEventLink("receiveWellID");
        JSONStringToSend = JSONStringToSend.replaceAll("\n","");
        pageRenderSupport.addScript("new PlateDesigner('well_A','wellType','plateNum','%s','%s','%s');",JSONStringToSend,link.toAbsoluteURI(),PARAM_NAME);

    }

    /**
     *
     *
     *
     * this method is the main AJAX communicating method
     * it contains all the well logic for marking, coloring, erasing etc. so we dont have much logic in the javascript part
     *
     * it gets an JSON string from the javascript, does some calculating and transformation and
     * send back the processed request to javascript
     *
     * @return a JSON string to with all the processed well information
     */
    public JSONArray onReceiveWellID() {
        
        //the clicked well data is sent via JSON..which is always the last element in the arr
        final ComponentResultProcessorWrapper callback = new ComponentResultProcessorWrapper(componentEventResultProcessor);
        String jSONString = request.getParameter(PARAM_NAME);
        

        //i will fire an event if someone clicked the first well or unclicked the last well
        //so that we can process this from outside
        int amountWellsBefore = getWellAmountOfAllPlates();

       
        JSONArray jsonArr = new JSONArray(jSONString);
        int arrLength = jsonArr.length();


            //for every new well you just clicked
            for (int i = 0; i < arrLength; i++) {

                JSONObject tempJSONObj = jsonArr.getJSONObject(i);
                String wellType = (String) tempJSONObj.get("wellType");
                String wellID = (String) tempJSONObj.get("wellID");
                String jsID = (String) tempJSONObj.get("plateNum");

                System.out.println("component received this well:"+wellType+" "+wellID+" "+jsID);

                if (wellType==null || wellType.equals("")) {
                        System.out.println("error occured in receiving welltype");
                        wellType="sample";
                }

                int jsIDNum = Integer.parseInt(jsID);
                 //catch js complete table erasing event first!!-----------------------------




                if(wellID.equals("well_X")) {
                    erasePlates(jsIDNum);
                    //after erasing leave this loop..dont go any further
                    break;
                }


                //now lets see if we already have information in the well before clicking it (again)
                //otherwise we are sample
                String oldWellType = getWellTypeForJsPlate(jsIDNum,wellID);
                System.out.println("old well type: "+oldWellType);

                //now we have lots of different rules...........

                //first select the type for the clicked well depending on what was before and what we are and if we are a single element or clicked a whole row/column 
                wellType = selectNewWellType(jsIDNum,oldWellType,wellType,wellID,arrLength);
                System.out.println("new well type: "+wellType);

                //now set the well new in real :-)
                setWellForJsPlate(jsIDNum,wellID,wellType);

                //store whats in the well before if someone clicked the contaminated button..we dont want to overwrite but overlay here
                if(wellType.equals("cont1")) {
                    if(!oldWellType.equals("cont1")) {
                        //if you mark an contaminated well contaminated we want to keep the original information
                        memorizeOldWells.get(jsIDNum).put(wellID,oldWellType);
                    }
                }
                System.out.println("jsIDNum:"+jsIDNum);

                //first plate is the all plate
                if (jsIDNum == 0) {


                    //add this well to all of the other plates as well
                    for (int count = 1; count < clickedWellsAndPlates.size(); count++) {
                        //set this well on all plates
                         setWellForJsPlate(count,wellID,wellType);
                        //store whats in it before on all the other plates
                         if(wellType.equals("cont1")) {
                            memorizeOldWells.get(count).put(wellID,oldWellType);
                         }


                     }
                }
                //all the other plates
                else {                     
                    //if we are a contaminated well...this only works for one plate/replicate combination
                    //or if we deleted our contaminated well (which will be then the oldWellType defined as cont1)
                    if(wellType.equals("cont1")||oldWellType.equals("cont1")) {
                        //sample welltypes and contaminated wells are only valid for one plate and replicate combination
                        clickedWellsAndPlates.get(jsIDNum).getWellsArray().put(wellID,wellType);
                        //add to the memory here
                        memorizeOldWells.get(jsIDNum).put(wellID,oldWellType);
                    }
                    else {
                        
                     //pos,neg,empty,sample and other wells are valid for all the replicates of the same plate
                     //contaminated wells are only valid for single plates

                        //get the plate number for the selected plate javascript id
                        //this works only as long as our array element num = js ID num of the plate  ...TODO: make a cool class out of this array

                        int plateNum = clickedWellsAndPlates.get(jsIDNum).getPlateNum();

                        //1 because plate 0 is sample plate
                        for (int j = 1; j < clickedWellsAndPlates.size(); j++) {
                            Plate plate  = clickedWellsAndPlates.get(j);

                            //get all the plates with the same platenumber (they differ in replicates number)
                            if(plate.getPlateNum()==plateNum) {
                                
                                plate.getWellsArray().put(wellID,wellType);
                            }
                        }
                    }

                    
                }

            }

            //get all the wells which are defined on all of the plates...we need this info to mark such wells on the "all" plate
        HashMap<String,String> allWells = getAllWells();
        //quick fix...overwrite old ones which are no longer there
        clickedWellsAndPlates.get(0).setWellsArray(new HashMap<String,String>());
        for(String wellID:allWells.keySet()) {
            String wellType = allWells.get(wellID);
            clickedWellsAndPlates.get(0).getWellsArray().put(wellID,wellType);
        }

        //printAllPlatesAndWells();
        
        int amountWellsAfter = getWellAmountOfAllPlates();

        
        //TODO: this whole first well, last well routine has to go into the js and their execute not and event
        //TODO:...but change the html from layout component from within
        //TODO: we do not need this event handler method if we change the event sending mechanism to pure javascript
        //TODO:from PlateDesigner.js --> to Layout components back and next html buttons
        int eventNum = -1;
        //the event will fire 1 if first well was clicked and 0 if last marked well was erased
        if (amountWellsBefore == 0) {
            if (amountWellsAfter == 1) {
                //fire the event ->got one well clicks
                eventNum = 1;
            }

        } else {
            if (amountWellsAfter == 0) {
                //got killed last clicked well
                eventNum = 0;
            }
        }
        final int runVariable = eventNum;
        //if we got an valid event we want to fire it right now
        //the event can be catched by a event handler method from the outside such as onClickWellEventFromPlateDesigner(..)
        if (runVariable != -1) {
                    resources.triggerEvent(SELECTED_EVENT, new Object[]{runVariable}, callback);
        }

        //printAllPlatesAndWells();
        
        //rebuild json and return it
        return HashMapToJSONArr();
    }

    /**
     *
     * this method converts our Wellmap to a JSON object so we can send it to the javascript back
     *
     *
     * @return A JSONArray object which contains all the neccessary well information in it to draw them
     */
    public JSONArray HashMapToJSONArr() {


        JSONArray jsonArr = new JSONArray();

        //this datastructure will summarize the platenums which have the same wellName/Type
        //to send an efficient JSON string
        HashMap<String,HashSet<Integer>> tempMap=new HashMap<String,HashSet<Integer>>();

        

        for(int i=0; i < clickedWellsAndPlates.size(); i++) {
           //TODO: write an iterator for this...because this is very repetitive code !!! 
            HashMap<String,String> wellsArray =clickedWellsAndPlates.get(i).getWellsArray();            
            Iterator wellIterator = wellsArray.keySet().iterator();
            while(wellIterator.hasNext()) {
                String wellID = (String)wellIterator.next();
                String thisWellType = wellsArray.get(wellID);
                //we dont send sample wells back to reduce overhead...we draw sample wellsin js if not otherwise defined anyways
                if(thisWellType.equals("sample")) {
                    continue;
                }
                //this string combines wellname and welltype to make unique summerization
                String wellNameNType = wellID+"-"+thisWellType;
                if(!tempMap.containsKey(wellNameNType)) {
                    tempMap.put(wellNameNType,new HashSet<Integer>());
                }

                tempMap.get(wellNameNType).add(i);
            }
        }
            

        //now add to JSON arr
        Iterator tempMapIterator = tempMap.keySet().iterator();
        while(tempMapIterator.hasNext()) {
            String keyValue = (String)tempMapIterator.next();
            String[]keyValueArr = keyValue.split("-");

            String wellID = keyValueArr[0];
            String wellName = keyValueArr[1];
            HashSet<Integer> plateList = tempMap.get(keyValue);

            JSONArray plateTempArr = new JSONArray();
            for(Integer singlePlate : plateList) {
                plateTempArr.put(singlePlate);
            }

            JSONObject tempObj = new JSONObject();
            tempObj.put("plateNum",plateTempArr);
            tempObj.put("wellID", wellID);
            tempObj.put("wellType", wellName);

            jsonArr.put(tempObj);

        }
       
        return jsonArr;
    }

    /**
     *
     *  this method gets all the wells which are defined on all of the plates (without the all plate)...TODO: I already did this in celLHTS2 class so make a class out of clickedWellsAndPlates and provide such a method to reduce duplicated code
     * TODO: there are so much iterating over the clickedWellsAndPlates array we should make an own class out of it with fancy iterating and finding and manipulating methods
     * this method gets all wells which are defined on all the plates so we can put them into the all plate
     *
     * @return a HashMap datastructure containing all the wells which are commun among all the plate/replicates
     */
    public HashMap<String,String> getAllWells() {
        int size = clickedWellsAndPlates.size();
        HashMap<String,String> returnList = new HashMap<String,String>();

        HashMap<String,Integer> countWells = new HashMap<String,Integer>();

        //dont count the all plate here
         for(int i=1; i < clickedWellsAndPlates.size(); i++) {
            HashMap<String,String> wellsArray =clickedWellsAndPlates.get(i).getWellsArray();
            Iterator wellIterator = wellsArray.keySet().iterator();
            while(wellIterator.hasNext()) {
                String wellID = (String)wellIterator.next();
                String thisWellType = wellsArray.get(wellID);
                if(thisWellType !=null && wellID != null) {
                     //init if not before
                     if(!countWells.containsKey(wellID+"-"+thisWellType)) {
                         //quick hack...i am in a hurry ...TODO: better would be a two dimensional array here
                        countWells.put(wellID+"-"+thisWellType,0);
                     }

                         int tempCount = countWells.get(wellID+"-"+thisWellType);
                         tempCount++;
                         countWells.put(wellID+"-"+thisWellType,tempCount);

                }
            }

        }

        for(String wellID : countWells.keySet()) {           
           if(countWells.get(wellID)==size-1) {  //minus one because we dont add the all plate because on this we want to add it
               String idTypeArr[]= wellID.split("-");
               
               returnList.put(idTypeArr[0],idTypeArr[1]);
           }
        }
        return returnList;
    }

    

    public int getPlateFormat() {
        return plateFormat;
    }

    public void setPlateFormat(int plateFormat) {
        this.plateFormat = plateFormat;
    }
    //TODO: there are so much iterating over the clickedWellsAndPlates array we should make an own class out of it with fancy iterating and finding and manipulating methods
    /**
     *
     * get the amount of all defined wells of a plate
     *
     * @return the amount
     */
    public int getWellAmountOfAllPlates() {
        int returnCnt=0;
        for(int i=0; i < clickedWellsAndPlates.size(); i++) {
            HashMap<String,String> wellsArray =clickedWellsAndPlates.get(i).getWellsArray();
            Iterator wellIterator = wellsArray.keySet().iterator();
            while(wellIterator.hasNext()) {
                String wellID = (String)wellIterator.next();
                String thisWellType = wellsArray.get(wellID);
                if(thisWellType !=null && wellID != null) {
                     returnCnt++;
                }
            }

        }
        return returnCnt;
    }
    //TODO: another method which should be outsourced
    //returns sample if no well info was found otherwise return the welltype of a certain plate (with a certain javascript ID) on a certain wellID
    /**
     *
     * get the welltype for a given plateNumber(from js numbers) and wellID
     *
     * @param plateNoJSID
     * @param wellID
     * @return the type of the well
     */
    public String getWellTypeForJsPlate(Integer plateNoJSID, String wellID) {
        if(clickedWellsAndPlates.get(plateNoJSID)!=null) {
           if(clickedWellsAndPlates.get(plateNoJSID).getWellsArray().containsKey(wellID)) {
               return clickedWellsAndPlates.get(plateNoJSID).getWellsArray().get(wellID);
           }
        }

        return "sample";
    }
    //TODO:another method which should be outsourced
    /**
     *
     * sets a well for a certain wellID on a certain plate number
     *
     * @param plateNoJSID
     * @param wellID
     * @param wellType
     * @return
     */
    public boolean setWellForJsPlate(Integer plateNoJSID,String wellID,String wellType) {
        if(clickedWellsAndPlates.get(plateNoJSID)==null) {
            return false;
        }
        else {
            clickedWellsAndPlates.get(plateNoJSID).getWellsArray().put(wellID,wellType);
        }
        return true;
    }



    /**
     *
     *  this is for debugging only
     *
     */
    public void printAllPlatesAndWells() {           
        for(int i=0; i < clickedWellsAndPlates.size(); i++) {
            HashMap<String,String> wellsArray =clickedWellsAndPlates.get(i).getWellsArray();
            Integer jsID = clickedWellsAndPlates.get(i).getJavaScriptID();
            Integer plateNumber = clickedWellsAndPlates.get(i).getPlateNum();
            Integer repliNum = clickedWellsAndPlates.get(i).getReplicateNum();
            System.out.print("Array element:"+i+" jsID: "+jsID+" plateNumber: "+plateNumber+" repliNum: "+repliNum);
            Iterator wellIterator = wellsArray.keySet().iterator();
            while(wellIterator.hasNext()) {
                String wellID = (String)wellIterator.next();
                String thisWellType = wellsArray.get(wellID);
                System.out.print(" wellID: "+wellID+" thisWellType: "+thisWellType);
            }
            System.out.println();

        }
       
    }

    /**
     *
     * erase complete plate layouts. This method erases a complete plate layout out of our plate set
     * it can erase all the plates or a single one dependent on the plate Number to be erased:
     *
     * 1. the first plate if erased-> all other plates will be erased too
     * 2. all the other plates: only this plate will be erased the others will be left
     *     if there are more than one replicate for one plate it will also be erased without the contaminated
     *     wells, because they are only valid for one plate/replicate combo
     *
     *
     * @param jsIDNum
     */
    public void erasePlates(int jsIDNum) {

        //if we are on the first "all" plate erase everything (all plates)
        if(jsIDNum==0) {
            for(Plate tempPlate : clickedWellsAndPlates) {
                tempPlate.setWellsArray(new HashMap<String,String>());
            }
            //clear all the memorized wells as well
            for(Integer key : memorizeOldWells.keySet()) {
                HashMap<String,String> tmpHash = memorizeOldWells.get(key);
                tmpHash.clear();
            }
        }
        //else erase only the plates with the same id ...on all the other plates with the same id (a.k.a. different replicates)
        //erase all wells without contaminated (they only are valid for one replicate )
        else {

             clickedWellsAndPlates.get(jsIDNum).setWellsArray(new HashMap<String,String>());
             int plateNum = clickedWellsAndPlates.get(jsIDNum).getPlateNum();
            //now for all the other plates with the same id
            for (int j = 1; j < clickedWellsAndPlates.size(); j++) {
                Plate plate  = clickedWellsAndPlates.get(j);

                //get all the plates with the same platenumber (they differ in replicates number)
                if(plate.getPlateNum()==plateNum) {

                    HashMap<String,String> tmpPlate = plate.getWellsArray();
                    ArrayList<String> wellsToDelete = new ArrayList<String>();
                    for(String tmpWellID : tmpPlate.keySet()) {
                         String tmpWellType= tmpPlate.get(tmpWellID);
                        //erase all wells without contaminated because these are only valid for individual plate/replicate combos
                         if(!tmpWellType.equals("cont1")) {
                             wellsToDelete.add(tmpWellID);
                         }
                    }
                    //erase all flagged
                    for(String tmpWellID : wellsToDelete) {
                        plate.getWellsArray().remove(tmpWellID);
                        //erase the memorized old well as well
                        memorizeOldWells.get(j).remove(tmpWellID);
                    }
                }
            }
        }


    }


    /**
     *
     *  this method selects the type of the well you clicked
     *  which mean you get clicked well from the javascript t the server and from it you only have the information
     *  which well from which plate and which type selected click-type it was
     *  this method here calculates which color actually will be set from this information dependant what was in the well before,
     *  how many wells were clicked with one ajax call and so on.
     *
     *
     * @param plateNum  the plateNUm of the clicked well
     * @param oldWellType what was in the well before you clicked?
     * @param wellType  what for a well type you selected in the javascript drop down menue (well you want to colorize)
     * @param wellID  the id of the well you clicked
     * @param wellLength  how many wells were submitted from the js to the server at once
     * @return gets the new and calculated welltype
     */
    public String selectNewWellType(Integer plateNum, String oldWellType, String wellType,String wellID, int wellLength) {
    //here we must apply/consider some rules:

    //1. if we click on a well which before was a sample well we will just have to use the welltype we had in the first place

    //2. if we click on a well which was defined before we WILL HAVE TO DELETE IT so we have two options
    // 2a. contaminated before...restore the old value which was underlying under it..if no memory than set to sample as well
    // 2b. for all the other wells: if we are clicking it again it will be put to the sample well
    // 2c. if we have selected a whole row/column we should just overwrite it and not regard what was in it before
    //3. if we are now a contaminated well we will keep this information because contaminated wells just overwrite existing wells
    // and do not  first clear them (see later)
        if(oldWellType.equals("sample")) {
            //case 1.
            return wellType;
        }
        if(wellLength>1) {
            //case 2c.
            return wellType;
        }
        if(oldWellType.equals("cont1")) {
            //if we have an old value stored..restore it

            if(memorizeOldWells.containsKey(plateNum)) {
                if(memorizeOldWells.get(plateNum).containsKey(wellID)) {
                    //restore old value
                    //case 2a.
                    return memorizeOldWells.get(plateNum).get(wellID);

                }
                else {
                    //case 2a
                    return "sample";
                }
            }

        }
        if(wellType.equals("cont1")) {
            //case 3
            return wellType;
        }
        //in this case we have to erase the well
        //case 2b.
        return "sample";



    }

    
}