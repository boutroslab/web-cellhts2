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

import cellHTS.classes.Configuration;
import cellHTS.classes.FileCreator;
import cellHTS.classes.FileParser;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Persist;

import javax.servlet.http.HttpServletRequest;

public class DebugPage {
    @Persist
    private boolean convertedAllFiles;
    @Persist
    private ArrayList<String> filesToImport;
    @Persist
    private ArrayList<String> headsToFind;
    @Persist
    private boolean showHeadline;
    @Persist
    private boolean init;

    public void setupRender() {
        if(!init) {
            init=true;
            filesToImport = new ArrayList<String>();
            headsToFind = new ArrayList<String>();
            headsToFind.add("Plate");
            headsToFind.add("Well");
            headsToFind.add("Value");
            showHeadline=true;
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

      al.add("/home/pelz/csvKaufmann");

     //    al.add("/temp/cellHTS2/JOB63651/A15_W01.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W02.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W03.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W04.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W05.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W06.TXT");
     //   al.add("/temp/cellHTS2/JOB63651/A15_W07.TXT");
        return al;
    }

    public void onSuccessfullyConvertedToCVSFromExportCSV(Object []objs) {

        System.out.println("I am here");
        filesToImport.clear();
        for(Object obj: objs) {
            filesToImport.add((String)obj);
            System.out.println("file:"+(String)obj);
        }
        convertedAllFiles=true;
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

    public ArrayList<String> getHeadsToFind() {
        return headsToFind;
    }

    public void setHeadsToFind(ArrayList<String> headsToFind) {
        this.headsToFind = headsToFind;
    }

    public boolean isShowHeadline() {
        return showHeadline;
    }

    public void setShowHeadline(boolean showHeadline) {
        this.showHeadline = showHeadline;
    }

    // end of getters and setters-------------------------------------------------------------------------
}
