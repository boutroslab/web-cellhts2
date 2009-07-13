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

/**
 *
 *  This class generates the general layout of the webtool so its like a frame where the different sites
 *  are embedded. It shows info about R version etc. too
 *
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 15.12.2008
 * Time: 13:53:30
 *
 */
public class Layout {
        //to access .pom file entries etc.
        @Inject
        private ApplicationGlobals applicationGlobals;

        @Persist
        private String build;
        @Persist
        private String version;
        @Persist
        private boolean initOnce;
        @Persist
        private String cellHTS2Version;

    /**
     *
     * initalize stuff, do this only once such as retriving cellHTS version (Singleton call)
     *
     */
        @SetupRender
        public void init() {
            //do this only once
            if (!initOnce) {
                build="";
                version="";
                cellHTS2Version="";
                initOnce = true;
                String path = applicationGlobals.getServletContext().getRealPath("/");
               //this will actually start and end a rserver instance 
                RInterface rInterface = new RInterface();
                cellHTS2Version = rInterface.getCellHTS2Version();

                try {
                    File manifestFile = new File(path, "META-INF/MANIFEST.MF");

                    Manifest mf = new Manifest();
                    mf.read(new FileInputStream(manifestFile));

                    Attributes atts = mf.getMainAttributes();

                    build = atts.getValue("Implementation-Build");
                    version = atts.getValue("Implementation-Version");



                    


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public String getBuild() {
            if(build.equals("")) {
                //when we are running through a jetty we cant get the MANIFEST so we will
                //output not available
                build="<not available>";
            }
            return build;
        }

        public String getVersion() {
            if(version.equals("")) {
                version="<not available>";
            }
            return version;
        }

        public String getCellHTS2Version() {
          return cellHTS2Version;
        }
}
