
function checkAll(checkAllCallbackLink, successCallbackLink, C_PARAMNAME,F_PARAMNAME,enableDIV,disableDIV) {
    var majorVersion = 8;
    var minorVersion = 0;
    var revision = 0;

    //get browser name and version
    var browser = BrowserDetect.browser;
    var version = BrowserDetect.version;
    var hasRightFlashVersion = DetectFlashVer(majorVersion, minorVersion, revision);
     //lets display a wait msg until everything is fetched
     $('waitMsg').innerHTML="please wait while fetching dependencies...";
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
                        var color="green";
                        if(regExp.test(ajax))  {
                           color = "red";
                           allCorrect=false;
                        }



                        $('ajaxMessage').innerHTML="<font color=\""+color+"\">"+ajax+"</font color>" ;
                        var js = jsonObj.JAVASCRIPT;
                        color = "green";
                        if(regExp.test(js))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('javascriptMessage').innerHTML="<font color=\""+color+"\">"+js+"</font color>";
                        var browser = jsonObj.BROWSER;
                        color = "green";
                        if(regExp.test(browser))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('browserVersionMessage').innerHTML="<font color=\""+color+"\">"+browser+"</font color>";



                        //flash is optional so no need to make allCorrect false
                         $('flashMessage').innerHTML="<font color=\"green\">"+jsonObj.FLASH+"</font color>";

                        var uploadPath=jsonObj.UPLOADPATH;
                        color = "green";
                        if(regExp.test(uploadPath))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('temporaryFolderAccess').innerHTML="<font color=\""+color+"\">"+uploadPath+"</font color>";

                        var cellHTS2Version=jsonObj.CELLHTS2VERSION;
                        color = "green";
                        if(regExp.test(cellHTS2Version))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('rVersionMessage').innerHTML="<font color=\""+color+"\">"+cellHTS2Version+"</font color>";
                       // $('cellHTS2Message').innerHTML="<font color=\""+color+"\">"+cellHTS2Version+"</font color>";


                        var gotZip=jsonObj.ZIPENABLED;
                        color = "green";
                        if(regExp.test(gotZip))  {
                           color = "red";
                           allCorrect=false;
                        }
                        $('gotZipMessage').innerHTML="<font color=\""+color+"\">"+gotZip+"</font color>";

                        //produce an result-summary message
                        if(allCorrect) {
                             $('resultMessage').innerHTML="every dependency have been met, redirecting to web cellHTS2";
                            //erase waiting message
                             $('waitMsg').innerHTML="";
                            //redirecting to web cellHTS2
                           // alert("simulating redirect");
                            //send a new Ajax request to notify the server we are done
                            new Ajax.Request(
                              successCallbackLink,{
                              onSuccess: function(transport) {
                                 // alert(enableDIV+"_"+disableDIV);
                                  $(enableDIV).style.visibility = "visible";
                                  $(disableDIV).style.visibility = "hidden";

                              }
                            });

                        }
                        else {
                            // myPopupRelocate(disableDIV);
                             //$(enableDIV).className="hidden";
                             $(enableDIV).style.visibility ="hidden";
                             $(disableDIV).style.visibility = "visible";
                             $('resultMessage').innerHTML="missing dependencies, can't start web cellHTS2. Please fix and restart webapp.";
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

function myPopupRelocate(divID) {

   

 var scrolledX, scrolledY;
 if( self.pageYOffset ) {
   scrolledX = self.pageXOffset;
   scrolledY = self.pageYOffset;
 } else if( document.documentElement && document.documentElement.scrollTop ) {
   scrolledX = document.documentElement.scrollLeft;
   scrolledY = document.documentElement.scrollTop;
 } else if( document.body ) {
   scrolledX = document.body.scrollLeft;
   scrolledY = document.body.scrollTop;
 }

 var centerX, centerY;
 if( self.innerHeight ) {
   centerX = self.innerWidth;
   centerY = self.innerHeight;
 } else if( document.documentElement && document.documentElement.clientHeight ) {
   centerX = document.documentElement.clientWidth;
   centerY = document.documentElement.clientHeight;
 } else if( document.body ) {
   centerX = document.body.clientWidth;
   centerY = document.body.clientHeight;
 }

 var divWidth = $(divID).style.width;
 var divHeight = $(divID).style.height;


 var leftOffset = scrolledX + (centerX - divWidth) / 2;
 var topOffset = scrolledY + (centerY - divHeight) / 2;

 $(divID).style.top = topOffset + "px";
 $(divID).style.left = leftOffset + "px";
 $(divID).style.display = "block";
}