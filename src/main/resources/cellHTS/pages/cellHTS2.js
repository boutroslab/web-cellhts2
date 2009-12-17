        //define an event so we skip the need to use a submit buttons
        //on uploading stuff
       function initJSCellHTS() {


            //get browser name and version
            var browser = BrowserDetect.browser;
            var version = BrowserDetect.version;

//these are all three browser plus tested versions cellHTS2 was successfully run on
            if(browser=="Firefox" && version<3) {
                alert("Your firefox version is too old (<3). Use this program at your own risk");
            }
            if(browser=="Explorer" && version<8) {
             alert("Your Internet Explorer version is too old (<8). Use this program at your own risk");
            }
            if(browser=="Safari" && version <3) {
            alert("Your Safari version is too old (<3). Use this program at your own risk");
            }
            //this is the event when clicking the datafile upload button
            Event.observe($('channel'), 'change', clickedChannelSelectButton.bindAsEventListener());

            Event.observe($('sessionFileUploadInput'), 'change', clickedSessionFileSelectButton.bindAsEventListener());
            Event.observe($('dataFileUploadInput'), 'change', clickedDataFileSelectButton.bindAsEventListener());
            if($('platelistFileUploadInput')!=null) {
                Event.observe($('platelistFileUploadInput'), 'change', clickedPlatelistFileSelectButton.bindAsEventListener());
            }
            if($('plateConfigFileInput')!=null) {
                Event.observe($('plateConfigFileInput'), 'change', clickedPlateConfFileSelectButton.bindAsEventListener());
            }
            if($('screenlogFileInput')!=null) {
             Event.observe($('screenlogFileInput'), 'change', clickedScreenlogFileSelectButton.bindAsEventListener());
            }
            Event.observe($('annotFileUploadInput'), 'change', clickedAnnotFileSelectButton.bindAsEventListener());
            Event.observe($('descriptionFileUploadInput'), 'change',clickedDescriptionFileSelectButton.bindAsEventListener());
            //disable textbox if the use viabilty radiobutton is set to NO

            if(document['forms']['viabilityChannelForm']['viabilityChannelRadiogroup'][0].checked==false) {    //this is the proper way for getting to the radiogroup with protoype

                    toggleDisabled($('viabilityFunctionDIV'),true);

            }


         }

        //js callback function for tapestry js onEvent
        function onCompleteOnEvent(response) {

             //do nothing right now


        }

        //js callback function for tapestry js onEvent
        function onViabilityFunctionButton(response) {

             var yesNo = response.viabilityChannel;
             if(yesNo=="true") {
                 toggleDisabled($('viabilityFunctionDIV'),false);
             }
             else {
                 toggleDisabled($('viabilityFunctionDIV'),true);
             }


        }
        //lose the focus of the email textfield
        function onLoseFocusOnEmailTextfield(response) {
            //$('emailAddressTextfield').blur();
        }

        function toggleDisabled(el,disabledVar) {
            try {
                el.disabled = disabledVar;
            }
            catch(E){
            }
            if (el.childNodes && el.childNodes.length > 0) {
                for (var x = 0; x < el.childNodes.length; x++) {
                    toggleDisabled(el.childNodes[x],disabledVar);
                }
            }
        }


        function clickedPlatelistFileSelectButton() {
        document.platelistFileUpload.submit();
        }
        function clickedChannelSelectButton() {
        document.channelTypes.submit();
        }
        function clickedSessionFileSelectButton() {
        //submit the form dataFileUpload
        document.sessionFileUpload.submit();
        }
        function clickedDataFileSelectButton() {
        //submit the form dataFileUpload
        document.dataFileUpload.submit();
        }
        function clickedPlateConfFileSelectButton() {
        //submit the form plateConfig upload button
        document.plateConfigFileUpload.submit();
        }
        function clickedScreenlogFileSelectButton() {
          document.screenlogFileUpload.submit();
        }
        function clickedAnnotFileSelectButton() {
        //submit the form plateConfig upload button
        document.annotFileUpload.submit();
        }
        function clickedDescriptionFileSelectButton() {
        //submit the form plateConfig upload button
        document.descriptionFileUpload.submit();
        }
        function clickedStartCellHTS2Link() {
        //this only works if the beaneditforms id is "form"...which is the standard case when having only one beaneditform
        //...what happens when we have more than one beaneditform?
           document.form.submit();
        }