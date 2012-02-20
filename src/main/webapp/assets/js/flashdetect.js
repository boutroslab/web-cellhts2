// -----------------------------------------------------------------------------
// Globale Variablen
// Erforderliche Hauptversion von Flash
var requiredMajorVersion = 8;
// Erforderliche Unterversion von Flash
var requiredMinorVersion = 0;
// Erforderliche Flash-Revision
var requiredRevision = 0;
// Die unterst�tzte JavaScript-Version
var jsVersion = 1.0;
// -----------------------------------------------------------------------------
// -->
var isIE  = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1) ? true : false;
var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;
jsVersion = 1.1;



//this reports to the server to the context address callbackLink via the parameter Name if flash is installed
function checkIfFlashIsInstalled(paramName,callbackLink,multipleUploaderDIV,singleUploaderDIV) {
     var hasRightVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
     var isRightVersion;

     if(hasRightVersion) {  // sofern eine akzeptable Version ermittelt wurde
        isRightVersion="true";
        hidediv(singleUploaderDIV); 

    } else {  // Flash ist veraltet, oder das Plug-In wurde nicht ermittelt
        isRightVersion="false";
        hidediv(multipleUploaderDIV);
    }
    new Ajax.Request(
                callbackLink,
        {
          method: 'post',
            //send the ajax request parameters not anonymously but with a name for the parameters so we can better
            // access the parameters server side (using tapestry request.getParam e.g.)
          parameters: paramName+"="+isRightVersion
        }
        );
    

}
// JavaScript-Hilfsprogramm ist zur Ermittlung der Versionsinformationen des Flash Player Plug-Ins erforderlich
function JSGetSwfVer(i){
	// NS/Opera-Version >= 3 auf Flash-Plug-In im Plug-In-Array pr�fen
	if (navigator.plugins != null && navigator.plugins.length > 0) {
		if (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]) {
			var swVer2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
      		var flashDescription = navigator.plugins["Shockwave Flash" + swVer2].description;
			descArray = flashDescription.split(" ");
			tempArrayMajor = descArray[2].split(".");
			versionMajor = tempArrayMajor[0];
			versionMinor = tempArrayMajor[1];
			if ( descArray[3] != "" ) {
				tempArrayMinor = descArray[3].split("r");
			} else {
				tempArrayMinor = descArray[4].split("r");
			}
      		versionRevision = tempArrayMinor[1] > 0 ? tempArrayMinor[1] : 0;
            flashVer = versionMajor + "." + versionMinor + "." + versionRevision;
      	} else {
			flashVer = -1;
		}
	}
	// MSN/WebTV 2.6 unterst�tzt Flash 4
	else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.6") != -1) flashVer = 4;
	// WebTV 2.5 unterst�tzt Flash 3
	else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.5") != -1) flashVer = 3;
	// �lteres WebTV unterst�tzt Flash 2
	else if (navigator.userAgent.toLowerCase().indexOf("webtv") != -1) flashVer = 2;
	// Ermittlung in allen anderen F�llen nicht m�glich
	else {

		flashVer = -1;
	}
	return flashVer;
}
// Wenn der Funktionsaufruf ohne Parameter erfolgt, gibt diese Funktion einen Gleitkommawert zur�ck,
// bei dem es sich entweder um die Flash Player-Version oder um 0.0 handelt.
// Beispiel: Flash Player 7r14 gibt 7.14 zur�ck.
// Wenn reqMinorVer, reqMajorVer, reqRevision aufgerufen wird, wird 'true' zur�ckgegeben, sofern diese bzw. eine h�here Version verf�gbar ist
function DetectFlashVer(reqMajorVer, reqMinorVer, reqRevision)
{
 	reqVer = parseFloat(reqMajorVer + "." + reqRevision);
   	// Versionen r�ckw�rts durchlaufen, bis die neueste Version gefunden wird
	for (i=25;i>0;i--) {
		if (isIE && isWin && !isOpera) {
			versionStr = VBGetSwfVer(i);
		} else {
			versionStr = JSGetSwfVer(i);
		}
		if (versionStr == -1 ) {
			return false;
		} else if (versionStr != 0) {
			if(isIE && isWin && !isOpera) {
				tempArray         = versionStr.split(" ");
				tempString        = tempArray[1];
				versionArray      = tempString .split(",");
			} else {
				versionArray      = versionStr.split(".");
			}
			versionMajor      = versionArray[0];
			versionMinor      = versionArray[1];
			versionRevision   = versionArray[2];

			versionString     = versionMajor + "." + versionRevision;   // 7.0r24 == 7.24
			versionNum        = parseFloat(versionString);
        	// ist die Hauptrevision >= angeforderte Hauptrevision UND die Nebenversion >= angeforderte Nebenversion
			if ( (versionMajor > reqMajorVer) && (versionNum >= reqVer) ) {
				return true;
			} else {
				return ((versionNum >= reqVer && versionMinor >= reqMinorVer) ? true : false );
			}
		}
	}
	return (reqVer ? false : 0.0);
}

function hidediv(id) {
	//safe function to hide an element with a specified id
	if (document.getElementById) { // DOM3 = IE5, NS6
		document.getElementById(id).style.display = 'none';
	}
	else {
		if (document.layers) { // Netscape 4
			document.id.display = 'none';
		}
		else { // IE 4
			document.all.id.style.display = 'none';
		}
	}
}

