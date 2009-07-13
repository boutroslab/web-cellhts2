package cellHTS.classes;

import java.util.Map;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: moritz
 * Date: Aug 18, 2008
 * Time: 9:12:28 AM
 */
public class DescriptionParser {
    public static Map<String,String> parse(FileReader file){
        Map<String,String> ret = new HashMap<String,String>();
        BufferedReader buffer = new BufferedReader(file);

        Boolean eof = false;
        while(!eof){
            String line = null;
            try{
                line = buffer.readLine();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(line == null){
                eof = true;
            }
            else{
                String[] fields = line.split(":");
                if(fields.length == 2){
                    ret.put(fields[0].trim(),fields[1].trim());
                }
            }
        }
        return ret;
    }
}
