//when html has been loaded
document.observe("dom:loaded", function() {

    //this is the event when clicking the datafile upload button
    $('channel').observe('change', function() {
        $('channelTypes').submit();

    });
    $('dataFileUploadInput').observe('change', function() {
        $('dataFileUpload').submit();         
    });
    if ($('platelistFileUploadInput') != null) {
        $('platelistFileUploadInput').observe('change', function() {
            $('platelistFileUpload').submit();
    });
    }
    if ($('plateConfigFileInput') != null) {
        $('plateConfigFileInput').observe('change', function() {
            $('plateConfigFileUpload').submit();           
    });
        
    }
    if ($('screenlogFileInput') != null) {
        $('screenlogFileInput').observe('change', function() {
            $('screenlogFileUpload').submit();                        
         
    });
    }
    $('annotFileUploadInput').observe('change', function() {
        $('annotFileUpload').submit();                    
    });
    $('descriptionFileUploadInput').observe('change', function() {
        $('descriptionFileUpload').submit();          
    });
    //disable textbox if the use viabilty radiobutton is set to NO

    if (document['forms']['viabilityChannelForm']['viabilityChannelRadiogroup'][0].checked == false) {    //this is the proper way for getting to the radiogroup with protoype
        toggleDisabled($('viabilityFunctionDIV'), true);
    }

    $('sessionFileUploadInput').observe('change', function() {
         $('sessionFileUpload').submit();
    });
});



//js callback function for tapestry js onEvent
function onCompleteOnEvent(response) {

    //do nothing right now


}

//js callback function for tapestry js onEvent
function onViabilityFunctionButton(response) {

    var yesNo = response.viabilityChannel;
    if (yesNo == "true") {
        toggleDisabled($('viabilityFunctionDIV'), false);
    }
    else {
        toggleDisabled($('viabilityFunctionDIV'), true);
    }


}
//lose the focus of the email textfield
function onLoseFocusOnEmailTextfield(response) {
    //$('emailAddressTextfield').blur();
}

function toggleDisabled(el, disabledVar) {
    try {
        el.disabled = disabledVar;
    }
    catch(E) {
    }
    if (el.childNodes && el.childNodes.length > 0) {
        for (var x = 0; x < el.childNodes.length; x++) {
            toggleDisabled(el.childNodes[x], disabledVar);
        }
    }
}


