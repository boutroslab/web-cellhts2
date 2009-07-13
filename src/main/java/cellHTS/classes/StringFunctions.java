package cellHTS.classes;

import java.util.Iterator;
import java.util.Collection;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: moritz
 * Date: Aug 27, 2008
 * Time: 11:35:47 AM
 */
public class StringFunctions {

    /* This function concatenates all elements of any collection of strings with a delimiter */
    public static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    public static String join(Collection s, String delimiter,String flank) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(flank + iter.next() + flank);
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }


    public static Double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        }
        catch (Exception e) {
            return new Double(0);
        }
    }

    public static Integer parseInt(String input) {
        try {
            return Integer.parseInt(input);
        }
        catch (Exception e) {
            return 0;
        }
    }

    public static Date parseDate(String date, SimpleDateFormat df) {
        try {
            return df.parse(date);
        }
        catch (Exception e) {
            return null;
        }
    }
}
