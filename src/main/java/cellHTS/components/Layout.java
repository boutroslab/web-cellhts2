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
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

import cellHTS.classes.RInterface;
import cellHTS.dao.RCellHTS2VersionDAO;
import cellHTS.dao.RCellHTS2VersionDAOImpl;


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
   
    @Inject
    private Request request;

    public String getContext() {
        return request.getContextPath();
    }
    @SessionState
    private String galaxyURLState;
    private boolean galaxyURLStateExists;

    @Inject
    private RCellHTS2VersionDAO rCellHTS2Version;

    public String getBuild() {
        String buildCellHTS2Version= rCellHTS2Version.getBuildCellHTS2Version();
       
        return buildCellHTS2Version;
    }

    public String getVersion() {
        String versionCellHTS2Version = rCellHTS2Version.getVersionCellHTS2Version();
        
        return versionCellHTS2Version;
    }

    public String getCellHTS2Version() {
        return rCellHTS2Version.getCellHTS2Version();
    }

    public String getGalaxyEnabled() {
        if(galaxyURLStateExists) {
            return "Galaxy enabled";
        }
        else {
            return "";
        }
    }

}
