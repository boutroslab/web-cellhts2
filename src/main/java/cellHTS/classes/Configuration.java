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

import data.DataFileParameter;

import java.util.regex.Pattern;
import java.util.HashMap;

/**
 * 
 * This class acts as a configuration file such as the properties file. We use this because here we
 * want to define java objects which acts as configuration objects. We could also use .properties files
 * for this but we would had to get everything as strings and out of this create our objects which isnt
 * very convenient
 *
 * Created by IntelliJ IDEA.
 * User: pelz
 * Date: 11.11.2008
 * Time: 16:57:00
 *
 * @author Oliver Pelz
 * @author <a href="mailto:jhunter@iDevelopment.info">jhunter@iDevelopment.info</a>

 */

public class Configuration {
    //todo: compile the patterns in the classes, put the strings in the properties file
    public static final Pattern ALLOWED_DATA_ARCHIVES = Pattern.compile("\\.[zZ][iI][pP]$");

    //this is for the R scripts
    public static final String scoreReplicates = "zscore";   //none,NPI
    public final static HashMap<Pattern, DataFileParameter[]> DATAFILE_PATTERNS =new HashMap<Pattern, DataFileParameter[]>();

 	static
 	{
         //these are all valid patterns, please note: if you add new patterns they must differ to those already in there!!!
         //the key is the pattern, the value is the order of the parameters matched by the grouping regex (bla)

         //this pattern matches e.g. SET28_P1.TXT
         DATAFILE_PATTERNS.put(Pattern.compile("^\\D+(\\d+)_\\D+(\\d+)"),new DataFileParameter[]{DataFileParameter.REPLIC,DataFileParameter.PLATE,null});

         DATAFILE_PATTERNS.put(Pattern.compile("(\\d+)_(\\d+)_\\d+_rep(\\d+)"),
                                              new DataFileParameter[]{DataFileParameter.PLATE,DataFileParameter.CHANNEL,DataFileParameter.REPLIC});
 		 DATAFILE_PATTERNS.put(Pattern.compile("(\\d+)_(\\d+)_(\\d+)\\."),
                                              new DataFileParameter[]{DataFileParameter.PLATE,DataFileParameter.REPLIC,DataFileParameter.CHANNEL});
 	     //the next pattern is for filenames such as RA01D01.TXT,RB01D01.TXT
         DATAFILE_PATTERNS.put(Pattern.compile("R(\\w)(\\d+)D(\\d+)"),
                                              new DataFileParameter[]{DataFileParameter.CHANNEL,DataFileParameter.PLATE,DataFileParameter.REPLIC});
         //this will be for e.g. 17B-pI2FL.TXT
         DATAFILE_PATTERNS.put(Pattern.compile("(\\d+)[a-zA-Z][\\-\\_][pP][IlL](\\d+)(\\w)L"),
                                              new DataFileParameter[]{DataFileParameter.REPLIC,DataFileParameter.PLATE,DataFileParameter.CHANNEL});
         //this is for gerrit
         DATAFILE_PATTERNS.put(Pattern.compile("DHGM_(\\d+)_(\\d+)\\."),
                                              new DataFileParameter[]{DataFileParameter.REPLIC,DataFileParameter.PLATE});
     }
    public static final Pattern ANNOTFILE_HEADER_PATTERN = Pattern.compile("^Plate\\tWell\\t\\w+");
    public static final Pattern ANNOTFILE_BODY_PATTERN = Pattern.compile("^\\w+\\t\\w+\t\\w+");
    public static final Pattern DESCRIPTIONFILE_HEADER_PATTERN = Pattern.compile("^\\[[\\w\\s]+\\]");
    public static final Pattern DESCRIPTIONFILE_BODY_PATTERN = Pattern.compile("[\\w\\n ]*");
     //this is the pattern to find errors in the datafile linewise with  (note datafiles do not have a header)
    public static final Pattern DATAFILE_PATTERN =Pattern.compile("[\\w+\\.\\+]+\\t[\\w+\\.\\+]+\\t"); //  \\t[\\w+\\,.\\+]+$");

    public static final Pattern PLATECONFIG_HEADER_PATTERN = Pattern.compile("\\D+(\\d+)\\D+(\\d+)");

    public static final Pattern PLATECONFIG_BODY_PATTERN = Pattern.compile("([0-9\\*]+)\\t(\\*|[a-zA-Z]{1}\\d+)\\t(\\w+)");

    public static final Pattern SCREENLOG_HEADER_PATTERN = Pattern.compile("Plate\\tSample\\t(Well|Channel\\tWell)\\tFlag\\tComment"); //the optional part can be channel in multichannel screens
   // public static final Pattern SCREENLOG_BODY_PATTERN = Pattern.compile("^(\\d+)\\t(\\d+)\\t(\\w+|\\d+\\t\\w+)\\t\\w+\\t(\\w+)$");   //the optional part is for possible channel

    public static final Pattern PLATELIST_HEADER_PATTERN=Pattern.compile("Filename\\tPlate\\tReplicate"); //channel is optional
    public static final Pattern PLATELIST_BODY_PATTERN=Pattern.compile("([^\\t]+)\\t(\\d+)\\t(\\d+)\\t*(\\d*)");
}
