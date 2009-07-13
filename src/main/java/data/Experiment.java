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

import org.apache.tapestry5.beaneditor.Width;
import org.apache.tapestry5.beaneditor.NonVisual;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: moritz
 * Date: Aug 13, 2008
 * Time: 5:24:32 PM
 */

//TODO: this is a smaller version of the experiment class from genomeRNAi....if we will combine cellHTS2 and genomeRNAi later we should use the genomeRNAi and discard this class here because it is identical
public class Experiment implements Serializable {
    private String screen;
    private String screenID;
    private String title;
    private Date date;
    private String experimenter;
    private String version;
    private String celltype;
    private String assay;
    private String assaytype;
    private String assaydesc;
    private String screentype;

    private boolean dualChannel = false;
    private boolean internal = true;
   

    private String channel1 = "Fluc";
    private String channel2 = "Rluc";

 
    public void clear() {
        screenID=null;
        screen=null;
        title=null;
        date=null;
        experimenter=null;
        version=null;
        celltype=null;
        assay=null;
        assaytype=null;
        assaydesc=null;
        screentype=null;
        dualChannel=false;
        internal=true;
        channel1 = "Fluc";
        channel2 = "Rluc";
    }

    public Experiment() {
    }

    

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public boolean isDualChannel() {
        return dualChannel;
    }

    public void setDualChannel(boolean dualChannel) {
        this.dualChannel = dualChannel;
    }

    public String getScreenID() {
        return screenID;
    }
    @Width(value = 20)
    public void setScreenID(String screenID) {
        this.screenID = screenID;
    }
    public String getTitle() {
       return title;
    }
    @Width(value = 20)
    public void setTitle(String title) {
        this.title = title;
    }


    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getExperimenter() {
        return experimenter;
    }

    public void setExperimenter(String experimenter) {
        this.experimenter = experimenter;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getCelltype() {
        return celltype;
    }

    public void setCelltype(String celltype) {
        this.celltype = celltype;
    }

    public String getAssay() {
        return assay;
    }

    public void setAssay(String assay) {
        this.assay = assay;
    }

    public String getAssaytype() {
        return assaytype;
    }

    public void setAssaytype(String assaytype) {
        this.assaytype = assaytype;
    }

    public String getAssaydesc() {
        return assaydesc;
    }

    public void setAssaydesc(String assaydesc) {
        this.assaydesc = assaydesc;
    }


    public String getChannel1() {
        return channel1;
    }

    public void setChannel1(String channel1) {
        this.channel1 = channel1;
    }

    public String getChannel2() {
        return channel2;
    }

    public void setChannel2(String channel2) {
        this.channel2 = channel2;
    }

    public String getScreentype() {
        return screentype;
    }

    public void setScreentype(String screentype) {
        this.screentype = screentype;
    }


}
