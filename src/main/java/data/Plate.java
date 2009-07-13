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

package data;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 13.02.2009
 * Time: 15:01:38
 * To change this template use File | Settings | File Templates.
 */

//this is a class which represents a plate in the plateconfigurator / plateDesigner
//there will be platenumber*replicates objects of this class per analysis
public class Plate implements Serializable {
    Integer javaScriptID;   //the id we will communicate with javascript when selecting a plate from the drop down menue
    Integer plateNum; //the actual plate number
    Integer replicateNum; // the replicate number
    HashMap<String,String> wellsArray; //all the modified/manipulated wells of this plate

    public Plate(Integer javaScriptID, Integer plateNum, Integer replicateNum, HashMap<String,String> wellsArray) {
        this.javaScriptID = javaScriptID;
        this.plateNum = plateNum;
        this.replicateNum = replicateNum;
        this.wellsArray = wellsArray;
    }

    public Plate(Integer javaScriptID) {
        this.javaScriptID= javaScriptID;
        this.wellsArray= new HashMap<String,String>();
    }
    public Plate(Integer javaScriptID,Integer plateNum, Integer replicateNum) {
        this.javaScriptID= javaScriptID;
        this.plateNum = plateNum;
        this.replicateNum = replicateNum;
        this.wellsArray= new HashMap<String,String>();  //wellID=key,wellType=value
    }
    //sometimes the javascriptID is not given
    public Plate(Integer plateNum, Integer replicateNum) {
        this.plateNum = plateNum;
        this.replicateNum = replicateNum;
        this.wellsArray= new HashMap<String,String>();  //wellID=key,wellType=value
    }

    public Integer getJavaScriptID() {
        return javaScriptID;
    }

    public void setJavaScriptID(Integer javaScriptID) {
        this.javaScriptID = javaScriptID;
    }

    public Integer getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(Integer plateNum) {
        this.plateNum = plateNum;
    }

    public Integer getReplicateNum() {
        return replicateNum;
    }

    public void setReplicateNum(Integer replicateNum) {
        this.replicateNum = replicateNum;
    }

    public HashMap<String, String> getWellsArray() {
        return wellsArray;
    }

    public void setWellsArray(HashMap<String, String> wellsArray) {
        this.wellsArray = wellsArray;
    }
}
