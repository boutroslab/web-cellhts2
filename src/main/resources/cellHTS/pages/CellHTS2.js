//when html has been loaded
jQuery(document)
		.ready(
				function() {

					// this is the event when clicking the datafile upload
					// button

					jQuery('#channel')
							.change(
									function() {
										var selectedChannel = jQuery(this)
												.val();
										// ajax it
										jQuery
												.ajax({
													url : jQuery(
															"#change_channel_uri")
															.text(),
													data : {
														"type" : jQuery(this)
																.val()
													},
													success : function() {
														if (selectedChannel == "dual_channel") {
															jQuery(
																	"#channelForm")
																	.show();
														} else {
															jQuery(
																	"#channelForm")
																	.hide();
														}
													},
													error : function() {
														alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
													}
												});
									});
					jQuery('#dataFileUploadInput').change(function() {
						jQuery('#dataFileUpload').submit();
					});
					if (jQuery('#platelistFileUploadInput')[0]) {
						jQuery('#platelistFileUploadInput').change(function() {
							jQuery('#platelistFileUpload').submit();
						});
					}
					if (jQuery('#plateConfigFileInput')[0]) {
						jQuery('#plateConfigFileInput').change(function() {
							jQuery('#plateConfigFileUpload').submit();
						});
					}
					if (jQuery('#screenlogFileInput')[0]) {
						jQuery('#screenlogFileInput').change(function() {
							jQuery('#screenlogFileUpload').submit();
						});
					}
					jQuery('#annotFileUploadInput').change(function() {
						jQuery('#annotFileUpload').submit();
					});
					jQuery('#descriptionFileUploadInput').change(function() {
						jQuery('#descriptionFileUpload').submit();
					});
					jQuery('#sessionFileUploadInput').change(function() {
						jQuery('#sessionFileUpload').submit();
					});
					// disable textbox if the use viabilty radiobutton is set to
					// NO
					if (document['forms']['viabilityChannelForm']['viabilityChannelRadiogroup'][0].checked == false) { // this
																														// is
																														// the
																														// proper
																														// way
																														// for
																														// getting
																														// to
																														// the
																														// radiogroup
																														// with
																														// protoype
						toggleDisabled('#viabilityFunctionDIV', true);
					}
					// define tooltips
					jQuery('#mylink').cluetip({
						splitTitle : '|',
						positionBy:       'auto', 
						fx: {
		                      open:       'fadeIn'		                      
						},
					})
				});

// js callback function for tapestry js onEvent
function onViabilityFunctionButton(response) {

	var yesNo = response.viabilityChannel;
	if (yesNo == "true") {
		toggleDisabled('#viabilityFunctionDIV', false);
	} else {
		toggleDisabled('#viabilityFunctionDIV', true);
	}

}
//lose the focus of the email textfield
function onLoseFocusOnEmailTextfield(response) {
	//$('emailAddressTextfield').blur();
}

function toggleDisabled(id, disabledVar) {
	el = jQuery(id);
	try {
		el.disabled = disabledVar;
	} catch (E) {
	}
	if (el.childNodes && el.childNodes.length > 0) {
		for ( var x = 0; x < el.childNodes.length; x++) {
			toggleDisabled(el.childNodes[x], disabledVar);
		}
	}
}
