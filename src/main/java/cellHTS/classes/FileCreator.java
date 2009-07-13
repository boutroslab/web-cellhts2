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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.text.DateFormat;

import data.DataFile;
import data.Experiment;
import data.Plate;

/**
 * This class is a typical file creation class
 * It has all the methods to build configuration files etc. or to build files in general
 *
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 01.12.2008
 * Time: 17:34:33
 *
 */

//this class

public class FileCreator {

    
    /**
     *  this will be the operating system dependendant linefeed symol
     */
    public static String lineFeed="\n";


    //write two output files...one PlateConfig file and one Screenlog file
    /**
     * This method creates/writes the plate config file and the screenlogfile out of a
     * datastructure defined in clickedWellsAndlates.
     * @param PlateConfFilename the filename for the plate configuration output file
     * @param ScreenlogFilename the filename for the screen log output file
     * @param clickedWellsAndPlates the datastructure to built the plateconfig and screenlog outputfiles from
     * @param plateFormat  the plateformat is the number of wells per plate, e.g. 384
     * @param isDualChannel if true we are building dual-channel outputfiles which is essentially needed for the screenlog which differs from single to dual channel
     * @return returns true if creation was fine, false otherwise
     */
    public static Boolean createPlateConfigFile(String PlateConfFilename, String ScreenlogFilename,ArrayList<Plate> clickedWellsAndPlates, int plateFormat,boolean isDualChannel) {

        int plateAmount = getPlateAmount(clickedWellsAndPlates);
        String plateConfHeaderline = createPlateConfHeaderLine(plateFormat,plateAmount);

        String screenlogHeaderline = createScreenlogHeaderLine(isDualChannel);

        try {
            FileWriter plateConfigFstream = new FileWriter(PlateConfFilename);
            BufferedWriter plateConfigOut = new BufferedWriter(plateConfigFstream);
            //write the header
            plateConfigOut.write(plateConfHeaderline);
            //write datalines as well

            //write a screenlog file as well
            FileWriter screenlogFstream = new FileWriter(ScreenlogFilename);
            BufferedWriter screenlogOut = new BufferedWriter(screenlogFstream);
            //write the header
            screenlogOut.write(screenlogHeaderline);


            String plateNumber = "";

            //for the screenlog file we need the replicate information as well!!!!
            //so this is the right datastructure for it:
            ArrayList<Plate> contaminatedPlatesNWells = new ArrayList<Plate>();

            //we disregard plate replicates and channels in the plateconfig file therefore we need
            // a special data structure to drop non uniques
            HashMap<Integer,HashSet<String>> uniquePlateConfIDs= new HashMap<Integer,HashSet<String>>();


            //plate zero is the all plate so we dont want to write any information to other plates which is already
            //print out on the all plate
            HashMap<String,String> allPlate = clickedWellsAndPlates.get(0).getWellsArray();
            
            for(Integer i=0; i < clickedWellsAndPlates.size(); i++) {
                HashMap<String,String> wellsArray =clickedWellsAndPlates.get(i).getWellsArray();
                int plateNum = clickedWellsAndPlates.get(i).getPlateNum();


                Integer repNum=clickedWellsAndPlates.get(i).getReplicateNum();
                //create an contaminated plates and wells datastructure (copy info from clickedWellsandPlates)
                contaminatedPlatesNWells.add(new Plate(i,plateNum,repNum));

            // sort everything by wellID
                ArrayList<String> sortedWellmapKeys = new ArrayList();
                sortedWellmapKeys.addAll(wellsArray.keySet());
                Collections.sort(sortedWellmapKeys);
                Iterator wellIterator = sortedWellmapKeys.iterator();

                while(wellIterator.hasNext()) {
                     String wellID = (String)wellIterator.next();
                     String wellType = wellsArray.get(wellID);
                   

                    //we dont want to store sample wells as this is true for all wells of one plate which are not explicitelty defined (this is due the line * * sample)
                     if(wellType.equals("sample")) {
                        continue;
                     }

                     //on the first plate print stars
                     if(i==0) {
                        plateNumber="*";
                     }
                     else {
                         plateNumber = ""+plateNum;
                     }
                    String orgWellID = wellID;
                    wellID = wellID.split("_")[1];


                    // here we get all the contaminated wells for the screenlog file
                    //quick hack
                    if(!(wellType.equals("pos")||wellType.equals("neg")||wellType.equals("other")||wellType.equals("empty"))) {
                         contaminatedPlatesNWells.get(i).getWellsArray().put(wellID,wellType);


                         continue;
                    }

                    //drop non-uniques for plateconf file
                    if(!uniquePlateConfIDs.containsKey(plateNum)) {
                       uniquePlateConfIDs.put(plateNum,new HashSet<String>());
                    }

                    if(!uniquePlateConfIDs.get(plateNum).contains(wellID)) {
                        //do never write that combination again...this is settled for all replicates of one plate
                        uniquePlateConfIDs.get(plateNum).add(wellID);

                        //do only write information which is not already written on the allplate
                        if(plateNum==0) {
                            plateConfigOut.write(plateNumber + "\t" + wellID + "\t" + wellType + lineFeed);
                        }
                        else {
                            //do only write information which is not already written on the allplate
                            if(allPlate.containsKey(orgWellID)) {
                                if(allPlate.get(orgWellID).equals(wellType))  {
                                    continue;
                                }
                            }

                            plateConfigOut.write(plateNumber + "\t" + wellID + "\t" + wellType + lineFeed);

                        }
                    }
                }

            }

            //Close the output stream
            plateConfigOut.close();

            //now print the screenlog file

            for(Integer i=0; i < contaminatedPlatesNWells.size(); i++) {
                //plate zero will not be put in the screenlog file because it
                //is the sample plate which cannot contain any contaminated wells
                if(contaminatedPlatesNWells.get(i).getPlateNum()==0) {
                    continue;
                }
                HashMap<String,String> wellsArray =contaminatedPlatesNWells.get(i).getWellsArray();
                Integer repNum=contaminatedPlatesNWells.get(i).getReplicateNum();
                int plateNum = contaminatedPlatesNWells.get(i).getPlateNum();


            // sort everything by wellID
                ArrayList<String> sortedWellmapKeys = new ArrayList();
                sortedWellmapKeys.addAll(wellsArray.keySet());
                Collections.sort(sortedWellmapKeys);
                Iterator wellIterator = sortedWellmapKeys.iterator();

                while(wellIterator.hasNext()) {
                     String wellID = (String)wellIterator.next();

                     //String pureWellName =wellName.split("_")[1];
                    if(!isDualChannel) {
                        screenlogOut.write(plateNum+"\t"+repNum+"\t"+wellID+"\tNA\tContaminated"+lineFeed);
                    }
                    else {
                        //we dont care about the channels in the plate configurator but actually cellHTS2 takes
                        //care of contaminated channels which in my opinion doesnt make sense but i have to output
                        //it just like this
                        screenlogOut.write(plateNum+"\t"+repNum+"\t"+1+"\t"+wellID+"\tNA\tContaminated"+lineFeed);
                        screenlogOut.write(plateNum+"\t"+repNum+"\t"+2+"\t"+wellID+"\tNA\tContaminated"+lineFeed);
                    }

                }
            }

            screenlogOut.close();


        } catch (Exception e) {//Catch exception if any
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * this method creates the header for the plate configuration file
     * @param well number of wells per plate
     * @param plates number of plates in your set
     * @return the header as a string
     */
    private static String createPlateConfHeaderLine(int well, int plates) {
        String returnString;
        //TODO: put this string in the configuration file
        returnString = "Wells: " + well + lineFeed+"Plates: " + plates + lineFeed+"Plate\tWell\tContent"+lineFeed+"*\t*\tsample"+lineFeed;
        return returnString;
    }

    /**
     *
     * this method creates the header for the screen log file
     *
     * @param isDualChannel  if set we are building a dual screen header otherwise single channel
     * @return the built header as string
     */
    public static String createScreenlogHeaderLine(boolean isDualChannel) {
        String returnString;
        if(!isDualChannel) {
            returnString="Plate\tSample\tWell\tFlag\tComment"+lineFeed;
        }
        else {
            //we dont care about the channels in the plate configurator but actually cellHTS2 takes
            //care of contaminated channels which in my opinion doesnt make sense but i have to output
            //it just like this
            returnString="Plate\tSample\tChannel\tWell\tFlag\tComment"+lineFeed;
        }
        return returnString;
    }

    /**
     *
     * this simple method creates a file out of a string
     *
     * @param aFile the filename for the outputfile
     * @param aContents the string to save in the file
     * @return returns true if everything went fine, false otherwise
     */
    public static boolean stringToFile(File aFile, String aContents) {
        try {
            //use buffering
            DataOutputStream output = new DataOutputStream(new FileOutputStream(aFile,false));
            try {
                //FileWriter always assumes default encoding is OK!
                output.write(aContents.getBytes());                   
            }
            finally {
                output.close();
            }

        } catch (IOException e) {
            return false;
        }
        ;

        return true;

    }

    /**
     *
     * this method appends a string to a existing or new file
     *
     * @param aFile the filename for the outputfile
     * @param aContents the string to save in the file
     * @return returns true if everything went fine, false otherwise
     */

    private static boolean appendStringToFile(File aFile, String aContents) {
        try {
            //use buffering
            DataOutputStream output = new DataOutputStream(new FileOutputStream(aFile,true));
            try {
                //FileWriter always assumes default encoding is OK!
                output.write(aContents.getBytes());
            }
            finally {
                output.close();
            }

        } catch (IOException e) {
            return false;
        }
        ;

        return true;

    }

    /**
     *
     * this method creates a plate list file out of a dataFiles data structure
     *
     * @param dataFiles the datastructure as for the plate list input. The Hashmpap keys are the filenames, the value a datafile object
     * @param file the file object of the outputfile
     * @return  returns true if everything went fine, else false
     */
    public static boolean createPlatelistFile(HashMap<String, DataFile> dataFiles,File file) {
		boolean goodFile=true;
        boolean hasChannel = false;
		String output="";
        Iterator iterator = dataFiles.keySet().iterator();
		//if weve got not uploaded any data file at all
		if(dataFiles.size()==0) {
		    return false;	
        }
		while(iterator.hasNext()) {
		    String key = (String)iterator.next();
			DataFile tmpFile = dataFiles.get(key);

            Integer plateNum = tmpFile.getPlateNumber();
			Integer replicate = tmpFile.getReplicate();
			Integer channel = tmpFile.getChannel();
			if(plateNum ==null || replicate==null){

                return false;
            }

            output+=key+"\t"+plateNum+"\t"+replicate;
            if(channel!=null) {
                hasChannel = true;
                output+="\t"+channel;
            }
            output+=lineFeed;
        }
		//write the header at last
        String headerLine="Filename\tPlate\tReplicate";
        if(hasChannel) {
            headerLine+="\tChannel";
        }
        headerLine+=lineFeed;
        output=headerLine+output;        

        goodFile = stringToFile(file,output);
		return goodFile;
	}

    /**
     *
     * This method creates a description file for cellHTS2
     *
     *
     * @param exp   an Experiment object which keeps all the descriptive information
     * @param descFile the filename as an outputfile
     */
    public static void createDescriptionFile(Experiment exp, String descFile) {
        HashMap<String,String> resultsMap=exp2hashMap(exp);
        String fileTxt = "[Screen description]"+lineFeed;

        String validNames[] = {"screen","title","date","experimenter","version","celltype","assay",
                               "assaytype","assaydesc","dualChannel","internal","channel1",
                               "channel2"};
        for(String name : validNames) {
            if(resultsMap.get(name)!=null)   {
                   fileTxt += name+":"+resultsMap.get(name)+lineFeed;
                 }
        }

        stringToFile(new File(descFile),fileTxt);
        

    }

    /**
     *
     * Transforms an Experiment object into a plain old HashMap format
     *
     * @param exp  The input experiment
     * @return an Hashmap with the transformed results
     */
    private static HashMap<String,String> exp2hashMap(Experiment exp) {
        HashMap<String,String> resultsMap= new HashMap<String,String>();

        if(exp.getDate()!=null) {
            String dateString = DateFormat.getDateInstance(DateFormat.SHORT).format(exp.getDate());
            Pattern p = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
            Matcher m = p.matcher(dateString);
            if(m.find()) {
                String day = m.group(1);
                String month = m.group(2);
                String year = m.group(3);
                dateString=month+"/"+day+"/"+year;
            }
            //convert to us string
            resultsMap.put("date",dateString);
        }
        resultsMap.put("screen",exp.getScreen());
        resultsMap.put("title",exp.getTitle());
        resultsMap.put("experimenter",exp.getExperimenter());
        resultsMap.put("version",exp.getVersion());
        resultsMap.put("celltype",exp.getCelltype());
        resultsMap.put("assay",exp.getAssay());
        resultsMap.put("assaytype",exp.getAssaytype());
        resultsMap.put("assaydesc",exp.getAssaydesc());
        resultsMap.put("dualChannel",""+exp.isDualChannel());
        resultsMap.put("internal",""+exp.isInternal());
        resultsMap.put("channel1",exp.getChannel1());
        resultsMap.put("channel2",exp.getChannel2());

        return resultsMap;
    }

    /**
     *
     * this method edits an already exisiting (or empty) descriptionfile
     *  with an changed experiment object (e.g. through beaneditform)
     * @param exp The output experiment object
     * @param descFile the input description file
     */

    public static void editDescriptionFile(Experiment exp, String descFile) {
        String resultText="";
        String beforeText="";
        String descText="";
        String afterText="";

        //transform the Experiment into a hashmap
        HashMap<String,String> resultsMap=exp2hashMap(exp);


        String validNames[] = {"screen","title","date","experimenter","version","celltype","assay",
                               "assaytype","assaydesc","dualChannel","internal","channel1",
                               "channel2"};
        try {
            FileReader reader = new FileReader(descFile);
            BufferedReader buffer = new BufferedReader(reader);

            String line;
            Matcher m;

            boolean beforeDesc=true;
            boolean afterDesc=false;
            boolean descFound=false;
            Pattern tag = Pattern.compile("\\[.*\\]");
            //collect before,description and after description texts
            while ((line = buffer.readLine()) != null) {
               if(!line.equals("[Screen description]")&&beforeDesc) {
                   beforeText+=line+lineFeed;
               }
               else {
                   if(beforeDesc){
                    descText+=line+lineFeed;
                    descFound=true;
                    beforeDesc=false;
                    continue;
                   }
               }
               if(descFound) {

                   if(tag.matcher(line).find()) {
                       descFound=false;
                       afterDesc=true;
                       afterText+=line+lineFeed;
                   }                                                                                        
                   else {
                       descText+=line+lineFeed;
                   }
               }
               else if(afterDesc) {
                   afterText+=line+lineFeed;
               }
               
            }
            //now search the description text and add/alter if not found
            String appendText="";             
            for(String name : validNames) {
                 String text="";
                 if(resultsMap.get(name)!=null)   {
                   text = resultsMap.get(name);
                 }

                //search for lines matching...not in the tag lines [.
                //TODO : does this work here???
                 Pattern p = Pattern.compile("[^\\[]"+name+"[^"+lineFeed+"]+",Pattern.CASE_INSENSITIVE);
                 m = p.matcher(descText);

                 if(m.find()) {
                   descText= m.replaceFirst(lineFeed+name+": "+text);
                 }
                //append fields which dont be in the file yet
                 else {

                    appendText+= name+": "+text+lineFeed;
                 }
            }

            //add the text not been found
            descText+=appendText;

            //mix everyhing together
            resultText=beforeText+descText+afterText;
        } catch (IOException e) {
             //TODO: what to do when file is corrupted??
        }
        //TODO:add a Screen Description tag if non is available
        //convert the string to a filename
        stringToFile(new File(descFile), resultText);
    }

    /**
     * this method gets the number of all plates from a ArrayList of Plates (clickedWellsAndPlates)
     * 
     * @param clickedWellsAndPlates  the input datastructure(a complete set of all the plates of your set)
     * @return number/amount of all the plates of your arrayList
     */
    private static int getPlateAmount(ArrayList<Plate> clickedWellsAndPlates) {
        int plateAmount=0;

        for(int i=0; i < clickedWellsAndPlates.size(); i++) {
            Plate plate = clickedWellsAndPlates.get(i);
            int currPlateNum = plate.getPlateNum();
            if(currPlateNum>plateAmount) {
                plateAmount=currPlateNum;
            }

        }
        return plateAmount;
    }

    /**
     *
     * converts dos files which have ^m instead of ^n as a newline character
     *
     * @param input input file to convert from
     * @return true if succeeded, false otherwise
     */
    public static boolean convertDos2UnixFile(File input) {
        File tempFile= new File(input.getAbsolutePath()+".temp");
        try {
            FileInputStream fis = new FileInputStream(input);
            BufferedReader ir = new BufferedReader(new InputStreamReader(fis));


            FileOutputStream fos = new FileOutputStream(tempFile);
            PrintWriter pw = new PrintWriter(fos);
            
            String line=null;
            while((line =ir.readLine())!=null) {
                //find carriage return at end of line
                line=line.replaceAll("\r$","");
                pw.write(line+lineFeed);
            }
            pw.flush();
            ir.close();
            pw.close();

        } catch (Exception e) {
            return false;
        }
        //now move old file to new file
        String fullInputFilename = input.getAbsolutePath();
        input.delete();
        tempFile.renameTo(new File(fullInputFilename));




        return true;
    }

    /**
     *  set the linefeeder , this is important for changing between windows and mac file format regarding the newlines
     * @param lineFeeder  e.g. '\n'
     */
    public static void setLineFeed(String lineFeeder) {
        lineFeed = lineFeeder;
    }



    /**
     *
     *  this method is deprecated, we will use Java standard api's properties class instead
     *  this method creates a new "download properties file" with the amount of current downloads "amount"
     *  this file will keep track of the number of downloaded results file
     *  one of those properties files keeps track of a complete folder for all results files
     *
     * @param file outpufile to write to
     * @param jobID write the jobid propery to the outpufile
     * @param amount write the amount property to the file
     */
    public static void writeDownloadPropertiesFile(File file, String jobID, int amount) {
        //get all existing key value pairs
        HashMap<String,String> map = new HashMap<String,String>();

        if(!file.exists()) {
            stringToFile(file,"");
        }

        try {
                    FileReader reader = new FileReader(file);
                    BufferedReader buffer = new BufferedReader(reader);

                    String line;


                    while ((line = buffer.readLine()) != null) {
                        line = line.replaceAll(" ","");
                        String[]keyValPair = line.split("=");
                        map.put(keyValPair[0],keyValPair[1]);
                    }
        }catch(IOException e) {
            e.printStackTrace();
            
             return;
        }
        map.put(jobID,""+amount);
        file.delete();
        //the properties key for amount of downloads is "#"
        String tempString="";
        for(String key : map.keySet()) {
            String value = map.get(key);
            tempString+=key+" = "+value+"\n";
        }
        stringToFile(file,tempString);


    }
}
