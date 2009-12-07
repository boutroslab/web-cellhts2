package cellHTS.pages;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 07.12.2009
 * Time: 17:37:42
 * To change this template use File | Settings | File Templates.
 */
@IncludeJavaScriptLibrary(value = {"${tapestry.scriptaculous}/prototype.js","browserDetect.js"})
public class DependenciesChecker {
    @Persist
    private boolean init;
     @Inject
    private ComponentResources componentResources;
    @Persist
    private String browserVersionLink;
    @Persist
    private String B_PARAMNAME;
    @Inject
    private Request request;
    @Persist
    private String browserVersionMessage;
    @Persist
    private boolean proceed;
    @InjectPage
    private CellHTS2 cellHTS2;


    public void setupRender() {
            if(!init) {
                init=true;
                Link browserVersionL = componentResources.createEventLink("browserVersionSent", new Object[]{});
                browserVersionLink = browserVersionL.toAbsoluteURI();
                B_PARAMNAME="B_PARAMNAME";
                browserVersionMessage="";
                proceed=false;
            }

    }

    @OnEvent(value = "browserVersionSent")
    public void  isJSActivated() {
        String []browserNVersion = request.getParameter(B_PARAMNAME).split(",");
        String browser = browserNVersion[0];
        float version = Float.parseFloat(browserNVersion[1]);


        //these are all three browser plus tested versions cellHTS2 was successfully run on
            if(browser.equals("Firefox")||browser.equals("Explorer")||browser.equals("Safari"))

            else if(browser.equals("Firefox") && version<3) {
                browserVersionMessage="Your firefox version is too old (<3). Can't proceed.";
                proceed=false;
            }
            else if(browser.equals"Explorer" && version<8) {
             browserVersionMessage="Your Internet Explorer version is too old (<8). Can't proceed.";
                proceed=false;
            }
            else if(browser.equals"Safari" && version <3) {
                browserVersionMessage="Your Safari version is too old (<3). Can't proceed.";
                proceed=false;
            }



        if(trueOrFalse.equals("TRUE")) {
            proceed=true;
        }
        else {
            browserVersionMessage="Javascript not activated, cannot proceed";

        }
        
    }
    public Object afterRender() {
        if(!proceed)   {
            return this;
        }
        else {
            return cellHTS2;
        }
    }


// getters and setters

    public String getBrowserVersionMessage() {
        return browserVersionMessage;
    }

    public void setBrowserVersionMessage(String browserVersionMessage) {
        this.browserVersionMessage = browserVersionMessage;
    }

    public String getBrowserVersionLink() {
        return browserVersionLink;
    }

    public void setBrowserVersionLink(String browserVersionLink) {
        this.browserVersionLink = browserVersionLink;
    }
}
