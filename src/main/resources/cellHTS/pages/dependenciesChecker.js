
function checkAll(checkAllCallbackLink, C_PARAMNAME,F_PARAMNAME) {

    
    //alert(browserVersionLink);
    //get browser name and version
    var browser = BrowserDetect.browser;
    var version = BrowserDetect.version;
    var hasRightFlashVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
   
    //alert(browser);
            new Ajax.Request(
                    checkAllCallbackLink,
            {
                method: 'post',
                parameters: { B_PARAMNAME :  browser + "," + version,
                              F_PARAMNAME: hasRightFlashVersion+","+requiredMajorVersion+"_"+requiredMinorVersion+"r"+requiredRevision
                            },
                onSuccess: function(transport) {
                        var response = transport.responseText;
                      //  alert(response);
                        var jsonObj = response.evalJSON();

                        var allCorrect=true;

                        var ajax = jsonObj.AJAX;
                        var regExp=/Can't/i;
                        var color="black";
                        if(regExp.test(ajax))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('ajaxMessage').innerHTML="<font color=\""+color+"\">"+ajax+"</font color>" ;
                        var js = jsonObj.JAVASCRIPT;
                        color = "black";
                        if(regExp.test(js))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('javascriptMessage').innerHTML="<font color=\""+color+"\">"+js+"</font color>";
                        var browser = jsonObj.BROWSER;
                        color = "black";
                        if(regExp.test(browser))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('browserVersionMessage').innerHTML="<font color=\""+color+"\">"+browser+"</font color>";



                        //flash is optional so no need to make allCorrect false
                         $('flashMessage').innerHTML="<font color=\"blue\">"+jsonObj.FLASH+"</font color>";

                        var uploadPath=jsonObj.UPLOADPATH;
                        color = "black";
                        if(regExp.test(uploadPath))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('temporaryFolderAccess').innerHTML="<font color=\""+color+"\">"+uploadPath+"</font color>";

                        var cellHTS2Version=jsonObj.CELLHTS2VERSION;
                        color = "black";
                        if(regExp.test(cellHTS2Version))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('rVersionMessage').innerHTML="<font color=\""+color+"\">"+cellHTS2Version+"</font color>";
                        $('cellHTS2Message').innerHTML="<font color=\""+color+"\">"+cellHTS2Version+"</font color>";

                        //produce an result-summary message
                        if(allCorrect) {
                             $('resultMessage').innerHTML="every dependency have been met, redirecting to web cellHTS2";
                            //redirecting to web cellHTS2
                            alert("simulating redirect");
                        }
                        else {
                             $('resultMessage').innerHTML="missing dependencies, can't start web cellHTS2";
                            //doing nothing
                        }



                  },
                onFailure: function(transport) {
                       var response = transport.responseText || "Ajax call could not be made, probably your proxy settings or ISP provider supresses AJAX calls";
                       $('generalErrorBox').innerHTML="<font color=\"red\">"+response+"</font color>";
                },
                onException: function(transport) {
                       var response = transport.responseText || "Ajax call could not be made, probably your proxy settings or ISP provider supresses AJAX calls";
                       $('generalErrorBox').innerHTML="<font color=\"red\">"+response+"</font color>";
                }
            }
                    );




}