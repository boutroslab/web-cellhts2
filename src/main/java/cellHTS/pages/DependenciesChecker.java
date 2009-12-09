package cellHTS.pages;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.ioc.Messages;

import java.io.File;
import java.io.IOException;

import cellHTS.classes.RInterface;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 07.12.2009
 * Time: 17:37:42
 * To change this template use File | Settings | File Templates.
 */
@IncludeJavaScriptLibrary(value = {"${tapestry.scriptaculous}/prototype.js","browserDetect.js","flashdetect.js","dependenciesChecker.js"})
public class DependenciesChecker {
    @Persist
    private boolean init;
     @Inject
    private ComponentResources componentResources;
    @Persist
    private String checkAllLink;
    @Persist
    private String flashInstallLink;
    @Persist
    private String reloadPagelink;
    @Persist
    private String B_PARAMNAME;
    @Persist
    private String F_PARAMNAME;
    @Inject
    private Request request;
    @InjectPage
    private CellHTS2 cellHTS2;
    @Environmental
    private RenderSupport pageRenderSupport;
    @Inject
    private Messages msg;

    public void setupRender() {
            if(!init) {
                init=true;
                Link checkAllL = componentResources.createEventLink("checkAllSent", new Object[]{});
                checkAllLink = checkAllL.toAbsoluteURI();
                B_PARAMNAME="B_PARAMNAME";
           
                F_PARAMNAME="F_PARAMNAME";


            }

    }

    @OnEvent(value = "checkAllSent")
    public JSONObject  checkAllSentReceiver() {
        
        String ajaxMessage;
        String browserVersionMessage;

        //if we got here in this method we must be have enabled javascript
        boolean jsProceed=true;

        //check if this is a AJAX request
        if(!request.isXHR()) {
            //if this is not an ajax request we have to throw an exception
            //this can be if proxys such as ezproxy remove the XML Header from the request
            //so check before sending back a json obj which would result in an
            //Return type org.apache.tapestry5.json.JSONObject can not be handled.
            // Configured return types are java.lang.Class, java.lang.String, java.net.URL, org.apache.tapestry5.Link, org.apache.tapestry5.StreamResponse,
            //  org.apache.tapestry5.runtime.Component. 
            ajaxMessage="1. Testing if your ISP proxy supports AJAX failed, Can't proceed. Please check your Proxy settings. Can't proceed";
            //what happens?
            throw new TapestryException(ajaxMessage,null);


        }
        else {
            ajaxMessage="1. Ajax Request/Response can be made.";

        }
        String []browserNVersion = request.getParameter(B_PARAMNAME).split(",");
        String browser = browserNVersion[0];
        float version = Float.parseFloat(browserNVersion[1]);

       
        //these are all three browser plus tested versions cellHTS2 was successfully run on
            if(!(browser.equals("Firefox")||browser.equals("Explorer")||browser.equals("Safari"))) {
                browserVersionMessage="2. Your browser: "+browser+" is not supported by web CellHTS2. Can't proceed";

            }

            else if(browser.equals("Firefox") && version<3) {
                browserVersionMessage="2. Your firefox version is too old (<3). Can't proceed.";
            }
            else if(browser.equals("Explorer") && version<8) {
             browserVersionMessage="2. Your Internet Explorer version is too old (<8). Can't proceed.";
            }
            else if(browser.equals("Safari") && version <3) {
                browserVersionMessage="2. Your Safari version is too old (<3). Can't proceed.";
            }
            else {
                browserVersionMessage="2. browser and version are supported";
            }

        //now check out flash
         String []flashEnabledNVersion = request.getParameter(F_PARAMNAME).split(",");
       String isTrue = flashEnabledNVersion[0];
       String fVersion = flashEnabledNVersion[1];
        String flashMessage="";
       if(isTrue.equalsIgnoreCase("true")) {
           flashMessage="3. Optional (no requirement):your installed flash is valid to use with web cellHTS2";
       }
        else {
           flashMessage="3. Optional (no requirement):you have not installed flash or your version is smaller than:  v."+version;
       }




        JSONObject returnJSON = new JSONObject();
        returnJSON.put("AJAX",ajaxMessage);
        //can we make an ajax request
        //if we are here javascript must work (otherwise there would be no XHR request possible)
        returnJSON.put("JAVASCRIPT","0. javascript is enabled");

        //this is only to complete the ajax request with a response
        returnJSON.put("BROWSER", browserVersionMessage);

        returnJSON.put("FLASH", flashMessage);

        returnJSON.put("UPLOADPATH",checkAndCreateUploadDirectory());

        //get R and cellHTS Version
        String cellHTS2AndRVersion = getRVersion();
        if(cellHTS2AndRVersion.equals("not found")) {
           cellHTS2AndRVersion="R, cellHTS2 version can't be fetched. Maybe can't connect to RServer. Can't proceed"; 
        } else {
           cellHTS2AndRVersion+="cellHTS2 and R Version: "; 
        }
        returnJSON.put("CELLHTS2VERSION",cellHTS2AndRVersion);
        return returnJSON;

    }



     public void afterRender(MarkupWriter writer){
        pageRenderSupport.addScript("checkAll('%s','%s','%s')",checkAllLink, B_PARAMNAME,F_PARAMNAME);

    }

    public String checkAndCreateUploadDirectory() {
            String uploadPath = msg.get("upload-path");
            File uploadPathObj = new File(uploadPath);
            if(!uploadPathObj.exists()) {
                if(!uploadPathObj.mkdirs()) {
                    return "Cannot create directory on the server to upload files: "+uploadPath+".\nCheck read/write permissions or change file upload property in apps.properties file. Can't proceed";
                }
            }
            if(uploadPathObj.canRead()&&uploadPathObj.canWrite()) {
                //check if we can create a temp file in the new dir
                File tmpFile =new File(uploadPath+"tmp.txt");
                try{

                    tmpFile.createNewFile();
                    if(tmpFile.canWrite()) {
                        tmpFile.delete();
                        return "Reading/Writing in temp folder: "+uploadPath+" succeeded!";
                    }

                }catch(IOException e) {
                    e.printStackTrace();
                    if(tmpFile.exists()){
                        tmpFile.delete();
                    }
                    return "Cannot write a test file in the upload path: "+uploadPath+"tmp.txt"+".\nCheck read/write permissions or change file upload property in apps.properties file. Can't proceed";
                }

             }else {
                    return "Cannot read or write directory: "+uploadPath+".\nCheck read/write permissions. Can't proceed";
            }
             return "Cannot read or write directory: "+uploadPath+".\nCheck read/write permissions. Can't proceed";
        }
    public String getRVersion() {
        RInterface rInterface = new RInterface();
        return rInterface.getCellHTS2Version();
    }

    public String getB_PARAMNAME() {
        return B_PARAMNAME;
    }

    public void setB_PARAMNAME(String b_PARAMNAME) {
        B_PARAMNAME = b_PARAMNAME;
    }

    public String getF_PARAMNAME() {
        return F_PARAMNAME;
    }

    public void setF_PARAMNAME(String f_PARAMNAME) {
        F_PARAMNAME = f_PARAMNAME;
    }

    public String getFlashInstallLink() {
        return flashInstallLink;
    }

    public void setFlashInstallLink(String flashInstallLink) {
        this.flashInstallLink = flashInstallLink;
    }

    public String getReloadPagelink() {
        return reloadPagelink;
    }

    public void setReloadPagelink(String reloadPagelink) {
        this.reloadPagelink = reloadPagelink;
    }
}
