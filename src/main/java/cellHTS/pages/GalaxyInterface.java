package cellHTS.pages;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.internal.services.ComponentResultProcessorWrapper;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: Apr 14, 2011
 * Time: 1:38:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class GalaxyInterface {


     @Inject
     private ComponentResources resources;

     @Inject
     private Request request;

     private final String PARAM_NAME="GALAXY_URL";

     @SessionState
     private String galaxyURLState;

     @InjectPage
     private CellHTS2 cellHTS2Page;

   //this action listener will be started whenever galaxy tries to "get data" from web cellhts22
    public Object onQueryTool() {
           String galaxyURL = request.getParameter(PARAM_NAME);
       //the url of the galaxy webcellhts2 callback URL will be stored GLOBALLY so all pages/components can access it
            galaxyURLState=galaxyURL;
            return cellHTS2Page;
    }
}
