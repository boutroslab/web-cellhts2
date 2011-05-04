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

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.annotations.Persist;

import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

import cellHTS.classes.ShellEnvironment;
import cellHTS.services.ZIPStreamResponse;
import cellHTS.services.GalaxyStreamResponse;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 10.11.2008
 * Time: 11:09:37
 * To change this template use File | Settings | File Templates.
 */
public class SuccessCellHTS2 {
    @Persist
    private String zipFile;
       @Inject
     private ComponentResources resources;

     @Inject
     private Request request;

     private final String PARAM_NAME="DOWNLOAD_ID";

     public Link getRequestURL() {
         return resources.createEventLink("DirectDownload");
     }


    //this page will be called if we have reached 100% and cellHTS2 ran successfully  (it will be called from javascript)
    
    public StreamResponse onActivate(String zipFile) {
        this.zipFile = zipFile;//arr[0];
        return streamIt(zipFile);
    }
    public StreamResponse onActivate() {
        return streamIt(zipFile);
    }



    public StreamResponse streamIt(String zipFile) {
//create an temporary directory for this session
        try {


                 //make inputstream out of outputstream
                FileInputStream iStream = new FileInputStream(zipFile);


                //list all running threads
                //ThreadLister.listAllThreads();

                return new ZIPStreamResponse(iStream,new File(zipFile).getName() );


        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }


    public String getParam_NAME() {
        return PARAM_NAME;
    }
}
