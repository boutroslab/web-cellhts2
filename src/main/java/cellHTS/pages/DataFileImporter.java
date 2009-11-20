package cellHTS.pages;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 20.11.2009
 * Time: 16:35:16
 * To change this template use File | Settings | File Templates.
 */
public class DataFileImporter {

    @Persist
    private LinkedHashMap<String,Integer> outputColumns;
    @Persist
    private String inputColumns;
    






    //getters and setters----------------------------------------------------------------------------------------------
     //this is for testing only
    public ArrayList<String> getTempDebugFiles() {
         ArrayList<String> al = new ArrayList<String>();
         al.add("/temp/cellHTS2/JOB63651/A15_W01.TXT");
        al.add("/temp/cellHTS2/JOB63651/A15_W02.TXT");
        al.add("/temp/cellHTS2/JOB63651/A15_W03.TXT");
        al.add("/temp/cellHTS2/JOB63651/A15_W04.TXT");
        al.add("/temp/cellHTS2/JOB63651/A15_W05.TXT");
        al.add("/temp/cellHTS2/JOB63651/A15_W06.TXT");
        al.add("/temp/cellHTS2/JOB63651/A15_W07.TXT");
        return al;
    }



    



    
}
