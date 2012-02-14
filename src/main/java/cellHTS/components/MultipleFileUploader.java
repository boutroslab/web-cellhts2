package cellHTS.components;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: Feb 14, 2012
 * Time: 1:28:42 PM
 * To change this template use File | Settings | File Templates.
 */

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Cookies;

import org.apache.tapestry5.*;
import org.apache.tapestry5.upload.services.MultipartDecoder;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.tapestry5.util.TextStreamResponse;


import java.io.File;
import java.util.Random;
import java.util.HashSet;


//context is the path to the webapp folder
@Import(stylesheet={"context:/assets/swfupload.css"},library={"${tapestry.scriptaculous}/prototype.js","js/swfupload.js", "js/fileprogress.js", "js/handlers.js", "js/swfupload.queue.js","js/singleupload.js", "js/multipleupload.js","js/flashdetect.js"})
public class MultipleFileUploader {
    @Inject
    private RequestGlobals requestGlobals;

    @Inject
    private Request request;

    @Inject
    private Response response;

    @Inject
    private RenderSupport support;

    @Inject
    private ComponentResources resources;

    @Inject
    private MultipartDecoder decoder;


    @Inject
    private ComponentResources componentResources;

    @Inject
    private Cookies cookies;

    @Inject
    private RenderSupport renderSupport;

    @Persist
    private String uniqueID;

    @Persist
    private Integer uploadedFilesAmount;


    @Inject
    @Path("swf/swfupload.swf")
    @Property
    private Asset swfFile;


    @Inject
    @Path("images/TestImageNoText_65x29.png")
    @Property
    private Asset testImage;

    //this should give the existing path on the webserver where to store the uploaded files, e.g. /tmp
    @Parameter(required=true)
    private File uploadPath;


    @Persist
    private HashSet<String> uploadedFiles;

    @Persist
    private boolean init;

    @Persist
    private UploadedFile singleFile;

    //this will be set if flash is installed and accessible from the outside
    @Persist
    private Boolean multipleFileUploadComponentStarted;

    @Persist
    private String PARAMNAME;

    @Persist
    private String TRIGGEREVENTNAMEFINISHED;

    @Persist
    private String TRIGGEREVENTNAMECLEARED;

    public void setupRender() {
            if(!init) {
                init=true;
                uploadedFiles=new HashSet<String>();
                uploadedFilesAmount=0;
                uniqueID=renderSupport.allocateClientId(componentResources);
            }

    }



    //will be called after rendering...its all about manipulating the DOM
    public void afterRender() {
        // uploadPath=new File("/tmp/multipleFileUpload");


        //first manage and get the browser client request information if flash is installed and in
        //a appropriate version
         Link isFlashInstalledAndValidCallbackLink = componentResources.createEventLink("setFlashInstalled", new Object[]{});
         //create a url parameter under which we will get the information if flash player is valid
        //if it is invalid we will not draw the multiplefileuploaderdiv which contains all the multiple upload html
         PARAMNAME="isFlashValid";
         TRIGGEREVENTNAMEFINISHED="lastFileTransfered";
         TRIGGEREVENTNAMECLEARED="allFilesCleared";
         support.addScript("checkIfFlashIsInstalled('%s','%s','multipleFileUploaderDIV','singleFileUploaderDIV')",//,'%s')",
                PARAMNAME,
                isFlashInstalledAndValidCallbackLink.toAbsoluteURI()
                );



        //due to a flash 10 error the jsessionid is not sent correctly from flash to the webserver
        //so tapestry will get a new page from the pool and you will lose the session
        //so we have to submit the jsessionid here in the link that we are landing in the same ssession
        //on flash response
        String sessionID = getSessionID();

        Link flashCallbackLink = componentResources.createEventLink("getFlashResponse", new Object[]{});

         String flashCallbackLinkString = flashCallbackLink.toAbsoluteURI();
         flashCallbackLinkString+=";jsessionid="+sessionID;


        Link lastFlashFileFinishedCallbackLink = componentResources.createEventLink("onLastMultipleFileHasFinished", new Object[]{});
        String lastFlashFileFinishedCallbackLinkString = lastFlashFileFinishedCallbackLink.toAbsoluteURI();
        lastFlashFileFinishedCallbackLinkString+=";jsessionid="+sessionID;


        //add the script which will fire initialize our SWFUpload instance and fire it up
        support.addScript("multipleupload('%s','%s','%s','%s','%s')",//,'%s')",
                swfFile.toClientURL(),
                flashCallbackLinkString,
                lastFlashFileFinishedCallbackLinkString,
                PARAMNAME,
                testImage.toClientURL()
                //,
             //   sessionID
                );

        support.addScript("initSingleFileUpload('singleUploadFileID','singleFileUploadForm')");

    }

    //this method is the callback method which communicates with the flash part of the program (client side)
    @OnEvent(value = "getFlashResponse")
    public synchronized StreamResponse getFlashCallback(Object[] obj) {
        System.out.println("in callback function");


        if(decoder.getFileUpload("Filedata")!=null) {
            //System.out.println("received Filedata");
            //the injected Multipartdecoder gets the currently uploaded file
            //this is the same as from the SWFUpload example php demos: $_FILES["Filedata"])
            UploadedFile file = decoder.getFileUpload("Filedata");
            //save the uploaded file on the server
            File copied = new File(uploadPath.getAbsolutePath() + "/" + file.getFileName());
            file.write(copied);
            //System.out.println("file written: "+copied.getAbsolutePath());

            uploadedFiles.add(copied.getAbsolutePath());
            uploadedFilesAmount=uploadedFiles.size();
            //generate a response
            String file_id = file.getFileName() + new Random().nextInt() * 100000;

            String responseString = "FILEID:" + file_id;    // Return the file id to the script


            StreamResponse response = new TextStreamResponse("application/txt", responseString);
             return response;
        }
        return new TextStreamResponse("application/txt", "ERROR: !");
    }



    @OnEvent(value = "setFlashInstalled")
    public void  setFlashInstalledOrNot() {
         String trueOrFalse = request.getParameter(PARAMNAME);
         if(trueOrFalse.equals("true")) {
             multipleFileUploadComponentStarted=true;
         }
         else {
             multipleFileUploadComponentStarted=false;
         }


    }

    //this will be fired if the last of a set of multipe files has been uploaded
    @OnEvent(value = "onLastMultipleFileHasFinished")
    public void  lastMultipleFileHasFinished() {
         triggerCustomEvent(TRIGGEREVENTNAMEFINISHED);

    }

    //these methods are for uploading single files via the tapestry upload component -----------------------
    void onSuccessFromSingleFileUploadForm()  {
            UploadedFile file =  singleFile;
            //save the uploaded file on the server
            File copied = new File(uploadPath.getAbsolutePath() + "/" + file.getFileName());
            file.write(copied);

            uploadedFiles.add(copied.getAbsolutePath());
            uploadedFilesAmount=uploadedFiles.size();
            triggerCustomEvent(TRIGGEREVENTNAMEFINISHED);

    }

    void triggerCustomEvent(String eventName) {
        ComponentEventCallback callback = new ComponentEventCallback() {
            public boolean handleResult(Object result) {
                return true;
            }
        };
        //build list with the uploaded filenames
        Object[] arrObj = new Object[uploadedFiles.size()];
        int i = 0;
        for(String uploadedFile : uploadedFiles) {
            arrObj[i++]=uploadedFile;
        }
       componentResources.triggerEvent(eventName,arrObj,callback);
    }
   //getters and setters----------------------------------

    public File getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(File uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getSessionID() {
        return requestGlobals.getHTTPServletRequest().getSession().getId();
    }
    public void setSessionID(String sessionID) {
         requestGlobals.getHTTPServletResponse().addCookie(new javax.servlet.http.Cookie("JSESSIONID",sessionID));

        requestGlobals.getHTTPServletRequest().getSession().setAttribute("JSESSIONID",sessionID);
         cookies.writeCookieValue("JSESSIONID",sessionID);
    }

    public UploadedFile getSingleFile() {
        return singleFile;
    }

    public void setSingleFile(UploadedFile singleFile) {
        this.singleFile = singleFile;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Integer getUploadedFilesAmount() {
        return uploadedFilesAmount;
    }

    public void setUploadedFilesAmount(Integer uploadedFilesAmount) {
        this.uploadedFilesAmount = uploadedFilesAmount;
    }

    public void onActionFromDeleteAllUploadedFiles() {
        if(uploadedFiles.size()>0) {
            for(String uploadFile : uploadedFiles) {
                new File(uploadFile).delete();
            }
            uploadedFiles.clear();
            uploadedFilesAmount=0;
        }

        //trigger an event so we announce it outside of this component
        triggerCustomEvent(TRIGGEREVENTNAMECLEARED);
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
