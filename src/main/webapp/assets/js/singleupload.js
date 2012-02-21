//by olip

var formIdentifier;
function initSingleFileUpload(uploadID,formID) {
        formIdentifier=formID;
        Event.observe($(uploadID), 'change', clickedFileSelectButton.bindAsEventListener());
}
function clickedFileSelectButton(formID) {
        //submit the form dataFileUpload

        $(formIdentifier).submit();
        //document.singleFileUploadForm.submit();
        //alert("Fruchtalarm");
}


