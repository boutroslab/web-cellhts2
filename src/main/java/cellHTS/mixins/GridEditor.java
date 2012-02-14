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

package cellHTS.mixins;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.TapestryException;


/**
 *
 *  this class is a mixin class for grid components to add inplace editing for
 *  the elements of the table
 *
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 19.11.2008
 * Time: 08:32:56
 * To change this template use File | Settings | File Templates.
 *
 *
 */

@Import(library={"${tapestry.scriptaculous}/prototype.js","gridEditor.js"})
public class GridEditor {

    @Inject
    private ComponentResources resources;

    @Inject
     private Request request;

    //div element id which MUST directly surround our grid. This is a quickhack  
    @Parameter(required = true)
    private String divElement;

    //this is tricky here...we need the datastructure (a collection) which was used for
    //building the grid were using this mixin for. But we dont want to modify this datastucture
    //in here because this would build lots of unneeded dependencies. So we will use the link
    //the js will send via AJAX to the method were want to modify the datastructure.
    @Parameter(required = true)
    private String gridDataStructureModifyLink;


    //this parameter is optional, you can give a comma seperated string of the columns you want to exclude
    //from being clickable  e.g. "1" or "1,2,3,4"
    @Parameter
    private String excludeColumnNumber="";
    
    @Environmental
     private RenderSupport pageRenderSupport;

    //this variable is used as the parameter name which will be sent from our js to our Tapestry request
    @Parameter(required=true)
    private String paramName;

    /**
     *
     * this is mainly the heart of all mixins because it will add a custamized javascript to it!!!
     *
     * @param writer
     */
    void afterRender(MarkupWriter writer) {
        String exceptionText=null;
        //some checking and exception throwing for endusers using this mixin
        if(divElement.equals("")) {
           exceptionText="div element id \"\" as an empty string is not allowed as a parameter for this mixin";
        }
        else if (gridDataStructureModifyLink==null) {
            exceptionText="you are not allowed to use an empty grid data structure modification link in this mixin";
        }
        String excludeArr[] = excludeColumnNumber.split(",");
        for (String item : excludeArr) {
            int singleItem = Integer.parseInt(item);
            if(singleItem<1) {
                exceptionText="excluded column MUST not be smaller than 1 because this parameter is column number 1 based";
            }
        }

        if(exceptionText!=null) {
            throw new TapestryException(exceptionText,null);              
        }

        pageRenderSupport.addScript("new GridEditor('%s','%s','%s','%s');",divElement,gridDataStructureModifyLink,excludeColumnNumber,paramName);
    }
    
}
