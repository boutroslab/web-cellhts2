
//first gives the URL to the swf on the webserver
//second gives the URL to communicate with the webserver from flash
var lastFlashFileFinished;
var paramName;
function multipleupload(swfURL,callbackLink,lastFlashFileFinishedURL,parameterName,testImage) {
    lastFlashFileFinished=lastFlashFileFinishedURL;
    paramName=parameterName;
    var swfu;


           //var loc="http://"+location.host;
           //var swfFullURL=(loc+swfURL);
           //var uploadFullURL=(loc+callbackLink);
           //var buttonFullURL= (loc+testImage);
            var swfFullURL=swfURL;
            var uploadFullURL=callbackLink;
            var buttonFullURL=testImage;

            var settings = {
				flash_url : swfFullURL,//swfURL,
                //send the current session id  because the flash has a bug and will create a new session everytime
                // you upload a file on the webserver and we would otherwise
                //lose our original session. So we handle our existing session through here to access the
                //right one on the webserver
				upload_url: uploadFullURL,
				file_size_limit : "3mb",
				file_types : "*.*",
				file_types_description : "All Files",
				file_upload_limit : 100,
				file_queue_limit : 100,
				custom_settings : {
					progressTarget : "fsUploadProgress",
					cancelButtonId : "btnCancel"
				},
				debug: false,
				button_image_url: buttonFullURL,
				button_width: "65",
				button_height: "29",
				button_placeholder_id: "spanButtonPlaceHolder",
				button_text: '<span class="theFont"></span>',
				button_text_style: ".theFont {color: #000000; font-size: 14pt;}",
				button_text_left_padding: 12,
				button_text_top_padding: 3,

				// The event handler functions are defined in handlers.js
				file_queued_handler : fileQueued,
				file_queue_error_handler : fileQueueError,
				file_dialog_complete_handler : fileDialogComplete,
				upload_start_handler : uploadStart,
				upload_progress_handler : uploadProgress,
				upload_error_handler : uploadError,
				upload_success_handler : uploadSuccess,
				upload_complete_handler : uploadComplete,
				queue_complete_handler : queueComplete,	// Queue plugin event
                http_success : [201, 202],
                //debug: true
                
			};       

swfu = new SWFUpload(settings);


}
// This event comes from the Queue Plugin
function queueComplete(numFilesUploaded) {
	var status = document.getElementById("divStatus");
	status.innerHTML = numFilesUploaded + " file" + (numFilesUploaded === 1 ? "" : "s") + " uploaded.";

    new Ajax.Request(
                lastFlashFileFinished,
        {
          method: 'post',
            //send the ajax request parameters not anonymously but with a name for the parameters so we can better
            // access the parameters server side (using tapestry request.getParam e.g.)
          parameters: paramName+"=TRUE"
        }
        );
 }
