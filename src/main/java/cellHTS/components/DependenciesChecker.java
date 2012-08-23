package cellHTS.components;

import org.apache.tapestry5.*;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.ioc.Messages;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;

import cellHTS.classes.RInterface;
import cellHTS.pages.CellHTS2;
import data.RInformation;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 07.12.2009
 * Time: 17:37:42
 * To change this template use File | Settings | File Templates.
 */
@Import(library={"${tapestry.scriptaculous}/prototype.js", "browserDetect.js", "flashdetect.js", "dependenciesChecker.js"})
public class DependenciesChecker {
    @Persist
    private boolean init;
    @Inject
    private ComponentResources componentResources;
    @Persist
    private String checkAllLink;
    @Persist
    private String successSentLink;
    @Persist
    private String flashInstallLink;
    @Persist
    private String reloadPagelink;
    @Persist
    private String B_PARAMNAME;
    @Persist
    private String F_PARAMNAME;
    @Persist
    private String SUCCESSEVENTNAME;
    @Inject
    private Request request;
    @InjectPage
    private CellHTS2 cellHTS2;
    @Environmental
    private RenderSupport pageRenderSupport;
    @Inject
    private Messages msg;
    @Persist
    private boolean allDependenciesAreMet;
    @SessionState
    private RInformation rInformation;
    private boolean rInformationExists;

    @Parameter(required = true, defaultPrefix = "literal")
    private String enableDIVOnSuccess;

    public void setupRender() {
        if (!init) {
            init = true;
            Link checkAllL = componentResources.createEventLink("checkAllSent", new Object[]{});
            Link successAllL = componentResources.createEventLink("successSent", new Object[]{});
            checkAllLink = checkAllL.toAbsoluteURI();
            successSentLink = successAllL.toAbsoluteURI();
            B_PARAMNAME = "B_PARAMNAME";

            F_PARAMNAME = "F_PARAMNAME";

            SUCCESSEVENTNAME = "allDependenciesMet";
            allDependenciesAreMet = false;
        }

    }

    @OnEvent(value = "checkAllSent")
    public JSONObject checkAllSentReceiver() {

        String ajaxMessage;
        String browserVersionMessage = "";

        //if we got here in this method we must be have enabled javascript
        boolean jsProceed = true;

        //check if this is a AJAX request
        if (!request.isXHR()) {
            //if this is not an ajax request we have to throw an exception
            //this can be if proxys such as ezproxy remove the XML Header from the request
            //so check before sending back a json obj which would result in an
            //Return type org.apache.tapestry5.json.JSONObject can not be handled.
            // Configured return types are java.lang.Class, java.lang.String, java.net.URL, org.apache.tapestry5.Link, org.apache.tapestry5.StreamResponse,
            //  org.apache.tapestry5.runtime.Component. 
            ajaxMessage = "1. Testing if your ISP proxy supports AJAX failed, Can't proceed. Please check your Proxy settings. Can't proceed";
            //what happens?
            throw new TapestryException(ajaxMessage, null);


        } else {
            ajaxMessage = "1. Ajax Request/Response can be made.";

        }
        String[] browserNVersion = request.getParameter(B_PARAMNAME).split(",");
        String browser = browserNVersion[0];
        float version = Float.parseFloat(browserNVersion[1]);

        boolean validBrowser = false;
        boolean validVersion = true;
        HashMap<String, String> browserVer = new HashMap<String, String>();
        for (String browserTmp : msg.get("allowed-browsers").split("\\|")) {
            String[] tmpAr = browserTmp.split("-");
            String brow = tmpAr[0];

            if (browser.equalsIgnoreCase(brow)) {
                validBrowser = true;
                //if we got version information
                if (tmpAr.length == 2) {
                    String ver = tmpAr[1];
                    Float minimumFloat = Float.parseFloat(ver);
                    browserVer.put(brow.toLowerCase(), ver);                       
                    if (version >= minimumFloat) {
                        break;
                    } else {
                        validVersion = false;
                    }
                }
                else { //version info not defined
                    break;
                }

            }

        }

        if (!validBrowser) {
            browserVersionMessage = "2. Your browser: " + browser + " is not supported by web CellHTS2. Can't proceed";
        }
        if (!validVersion) {
            browserVersionMessage = "2. Your browser: " + browser + " version is too old " + browserVer.get(browser.toLowerCase()) + ". Can't proceed.";
        }

        //now check out flash
        String[] flashEnabledNVersion = request.getParameter(F_PARAMNAME).split(",");
        String isTrue = flashEnabledNVersion[0];
        String fVersion = flashEnabledNVersion[1];
        String flashMessage = "";
        if (isTrue.equalsIgnoreCase("true")) {
            flashMessage = "3. Optional (no requirement):your installed flash is valid to use with web cellHTS2";
        } else {
            flashMessage = "3. Optional (no requirement):you have not installed flash or your version is smaller than:  v." + version;
        }


        JSONObject returnJSON = new JSONObject();
        returnJSON.put("AJAX", ajaxMessage);
        //can we make an ajax request
        //if we are here javascript must work (otherwise there would be no XHR request possible)
        returnJSON.put("JAVASCRIPT", "0. javascript is enabled");

        //this is only to complete the ajax request with a response
        returnJSON.put("BROWSER", browserVersionMessage);

        returnJSON.put("FLASH", flashMessage);

        returnJSON.put("UPLOADPATH", "4. " + checkAndCreateUploadDirectory());

        //get R and cellHTS Version
        RInformation info = getREssentials();
        String rVer = info.getRVersion();
        String cellHTS2Ver = info.getCellHTS2Version();
        String rServerVer = info.getrServeVersion();
        boolean zipEnabled = info.isRWithZipFunction();
        String gotZip = "Zip functionality in R can be found";
        if (rVer.equals("not found") || rVer.equals("<not available>")) {
            cellHTS2Ver = "R or cellHTS2 version can't be fetched. Maybe can't connect to RServer. Can't proceed";
        } else {
            // get R and cellHTS2 version and check if we are above the minimum version needs
            Pattern p = Pattern.compile("([\\d\\.]+)");
            Matcher m = p.matcher(rVer);
            if (m.find()) {
                String requiredCellHTS2Ver = msg.get("required-cellHTS2-version");
                String requiredRServerVer = msg.get("required-rServeVersion");
                if (compareTwoRVersions(rVer, msg.get("required-R-version")) && cellHTS2Ver.equals(requiredCellHTS2Ver) && rServerVer.equals(requiredRServerVer)) {
                    cellHTS2Ver = "R, cellHTS2 and RServer could be fe fetched and are in the right version.";
                } else {
                    cellHTS2Ver = "Fetched R deps " + rVer + " do not match required: <br/>   " +
                            "  R version: " + msg.get("required-R-version") + " ("+rVer+") <br/> " +
                            "  cellHTS2 version: " + requiredCellHTS2Ver +  " ("+cellHTS2Ver+") <br/> " +
                            "  or rServer version: " +  requiredRServerVer + " ("+rServerVer+") <br/> " +                   		
                            "  . Maybe can't connect to RServer. Can't proceed";
                }

            } else {
                cellHTS2Ver = "R or cellHTS2 version exists but can't be fetched. Maybe can't connect to RServer. Can't proceed";
            }


        }
        if (cellHTS2Ver.equals("")) {
            cellHTS2Ver = "R or cellHTS2 version can't be fetched. Maybe can't connect to RServer. Can't proceed";
        }
        if (!zipEnabled) {
            gotZip = "Can't find zip functionality in R. Please recompile it with it";
        }


        returnJSON.put("CELLHTS2VERSION", "5. " + cellHTS2Ver);


        returnJSON.put("ZIPENABLED", "6. " + gotZip);

        return returnJSON;

    }


    public void afterRender(MarkupWriter writer) {
        if (!allDependenciesAreMet) {
            pageRenderSupport.addScript("checkAll('%s','%s','%s','%s','%s','dependencyChecker')", checkAllLink, successSentLink, B_PARAMNAME, F_PARAMNAME, enableDIVOnSuccess);
        }
    }

    @OnEvent(value = "successSent")
    public JSONObject successReceiver() {
        if (!request.isXHR()) {
            String ajaxMessage = "1. Testing if your ISP proxy supports AJAX failed, Can't proceed. Please check your Proxy settings. Can't proceed";
            //what happens?
            throw new TapestryException(ajaxMessage, null);
        }
        //send out an tapestry event that we are done and successfully checked everything
        //triggerSuccessEvent();

        allDependenciesAreMet = true;
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("dummy", "dummy");
        return returnJSON;
    }

    public void triggerSuccessEvent() {
        //trigger an event that everything has been converted successfully and
        //parameters are the converted files
        ComponentEventCallback callback = new ComponentEventCallback() {
            public boolean handleResult(Object result) {
                return true;
            }
        };
        componentResources.triggerEvent(SUCCESSEVENTNAME, new Object[]{}, callback);
    }


    public boolean compareTwoRVersions(String version, String minimumVersion) {
        String cellHTS2AndRVersion;
        try {
            String[] versionPoints = version.split("\\.");
            String[] versionPointsDep = minimumVersion.split("\\.");

            if (versionPoints.length != versionPointsDep.length) {
                return false;
            }

            int i = 0;
            for (String versionPoint : versionPoints) {
                int ver = Integer.parseInt(versionPoint);
                int verDep = Integer.parseInt(versionPointsDep[i++]);
                if (ver < verDep) {
                    return false;
                }
            }

        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public String checkAndCreateUploadDirectory() {
        String uploadPath;
        if (System.getProperty("upload-path-webserver") != null) {

            //get from command line
            uploadPath = System.getProperty("upload-path-webserver");
        } else {
            //else get from properties file
            uploadPath = msg.get("upload-path-webserver");
        }

        if (!uploadPath.endsWith(File.separator)) {
            uploadPath = uploadPath + File.separator;
        }

        File uploadPathObj = new File(uploadPath);
        if (!uploadPathObj.exists()) {
            if (!uploadPathObj.mkdirs()) {
                return "Cannot create directory on the server to upload files: " + uploadPath + ". <br/>   " +
                        "Check read/write permissions or change file upload property in apps.properties file. Can't proceed";
            }
        }
        if (uploadPathObj.canRead() && uploadPathObj.canWrite()) {
            //check if we can create a temp file in the new dir
            File tmpFile = new File(uploadPath + "tmp.txt");
            try {

                tmpFile.createNewFile();
                if (tmpFile.canWrite()) {
                    //tmpFile.delete();
                    return "Reading/Writing in temp folder: " + uploadPath + " succeeded!";
                }

            } catch (IOException e) {
                e.printStackTrace();
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
                return "Cannot write a test file in the upload path: " + uploadPath + "tmp.txt" + "<br/>   " +
                        "Check read/write permissions or change file upload property in apps.properties file. Can't proceed";
            }

        } else {
            return "Cannot read or write directory: " + uploadPath + ".\nCheck read/write permissions. Can't proceed";
        }
        return "Cannot read or write directory: " + uploadPath + ".\nCheck read/write permissions. Can't proceed";
    }

    public RInformation getREssentials() {
        if (!rInformationExists) {
            RInterface rInterface = new RInterface();
            rInformation = rInterface.getEssentialCellHTS2Information(msg.get("rserve-host"), Integer.getInteger(msg.get("rserve-port")), msg.get("rserve-username"), msg.get("rserve-password"));
        }
        return rInformation;
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

    public boolean isAllDependenciesAreMet() {
        return allDependenciesAreMet;
    }

    public void setAllDependenciesAreMet(boolean allDependenciesAreMet) {
        this.allDependenciesAreMet = allDependenciesAreMet;
    }
}
