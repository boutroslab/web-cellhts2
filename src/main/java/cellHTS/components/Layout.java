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

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

import cellHTS.classes.RInterface;
import data.RCellHTS2Version;

/**
 * This class generates the general layout of the webtool so its like a frame where the different sites
 * are embedded. It shows info about R version etc. too
 * <p/>
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 15.12.2008
 * Time: 13:53:30
 */
public class Layout {
    //to access .pom file entries etc.

    @Inject
    private ApplicationGlobals applicationGlobals;

    @Persist
    private boolean initOnce;

    @SessionState
    private RCellHTS2Version rCellHTS2Version;
    private boolean rCellHTS2VersionExists;

    /**
     * initalize stuff, do this only once such as retriving cellHTS version (Singleton call)
     */
    @SetupRender
    public void init() {
        //do this only once
        if (!initOnce) {
            initOnce = true;
            //check the sessionstate variables 

                String path = applicationGlobals.getServletContext().getRealPath(File.separator);
                //this will actually start and end a rserver instance



                    String buildCellHTS2Version="";
                    String versionCellHTS2Version = "";

                try {
                    File manifestFile = new File(path, "META-INF/MANIFEST.MF");

                    Manifest mf = new Manifest();
                    mf.read(new FileInputStream(manifestFile));

                    Attributes atts = mf.getMainAttributes();

                    buildCellHTS2Version = atts.getValue("Implementation-Build");
                    versionCellHTS2Version = atts.getValue("Implementation-Version");

                } catch (Exception e) {
                    e.printStackTrace();

                }
                finally {
                   if(!rCellHTS2VersionExists)  {
                        RInterface rInterface = new RInterface();
                        String cellHTS2Version = rInterface.getCellHTS2Version();
                        rCellHTS2Version = new RCellHTS2Version(buildCellHTS2Version,versionCellHTS2Version,cellHTS2Version);
                    } 
                }
            }
    }



    public String getBuild() {
        String buildCellHTS2Version= rCellHTS2Version.getBuildCellHTS2Version();
        if (buildCellHTS2Version==null) {
            buildCellHTS2Version = "<not available>";
        }
        if (buildCellHTS2Version.equals("")) {
            //when we are running through a jetty we cant get the MANIFEST so we will
            //output not available
            buildCellHTS2Version = "<not available>";
        }
        return buildCellHTS2Version;
    }

    public String getVersion() {
        String versionCellHTS2Version = rCellHTS2Version.getVersionCellHTS2Version();
        if (versionCellHTS2Version==null) {
            versionCellHTS2Version = "<not available>";
        }
        if (versionCellHTS2Version.equals("")) {
            versionCellHTS2Version = "<not available>";
        }
        return versionCellHTS2Version;
    }

    public String getCellHTS2Version() {
        return rCellHTS2Version.getCellHTS2Version();
    }


}
