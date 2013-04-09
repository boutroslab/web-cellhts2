//when html has been loaded
jQuery(document)
		.ready(
				function() {

					
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
														"type" : selectedChannel
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
					//ajax textfields
					jQuery("*[id^='channelTextfield']").blur(function() {
						var label = jQuery(this).val();
						var currentId = jQuery(this).attr('id');
						jQuery
						.ajax({
							url : jQuery(
									"#change_desc_uri")
									.text(),
							data : {
								"label" : label,
								"currentID" : currentId
							},
							success : function() {								
							},
							error : function() {
								alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
							}
						});
					});
					jQuery("*[id^='viabilityFunctionTextfield']").blur(function() {
						var textField = jQuery(this).val();
						
						jQuery
						.ajax({
							url : jQuery(
									"#change_viabFunc_uri")
									.text(),
							data : {
								"textField" : textField								
							},
							success : function() {								
							},
							error : function() {
								alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
							}
						});
					});
					jQuery("*[id^='emailAddressTextfield']").blur(function() {
						var textField = jQuery(this).val();
						
						jQuery
						.ajax({
							url : jQuery(
									"#change_emailAddress_uri")
									.text(),
							data : {
								"textField" : textField								
							},
							success : function() {								
							},
							error : function() {
								alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
							}
						});
					});
					
					
					//ajax check boxes
					jQuery("#parseFileParams").change(function(){
						var checked = false;
						if(jQuery(this).is(':checked')) {
							checked = true;
						}

						jQuery
						.ajax({
							url : jQuery(
									"#change_inplace_checkbox")
									.text(),
							data : {
								"value" : checked,
							},
							success : function() {								
							},
							error : function() {
								alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
							}
						});
					});
					//radiogroup normalizationMethod auslesen
					jQuery("[name=normalTypesRadioGroup]").change(function() {
					      var value = jQuery('input[name=normalTypesRadioGroup]:checked').val();                                                                                       
					      jQuery
							.ajax({
								url : jQuery(
										"#change_norm_methods")
										.text(),
								data : {
									"method" : value,
								},
								success : function() {								
								},
								error : function() {
									alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
								}
							});                                                                                                                                      
					});
					//radiogroup logTransform auslesen
					jQuery("[name=logTransformRadioGroup]").change(function() {
					      var value = jQuery('input[name=logTransformRadioGroup]:checked').val();                                                                                       
					      jQuery
							.ajax({
								url : jQuery(
										"#change_logtransform")
										.text(),
								data : {
									"logTransform" : value,
								},
								success : function() {								
								},
								error : function() {
									alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
								}
							});                                                                                                                                      
					});
					//radiogroup scaling auslesen
					jQuery("[name=normalScalingRadioGroup]").change(function() {
					      var value = jQuery('input[name=normalScalingRadioGroup]:checked').val();                                                                                       
					      jQuery
							.ajax({
								url : jQuery(
										"#change_scaling_method")
										.text(),
								data : {
									"scalingMethod" : value,
								},
								success : function() {								
								},
								error : function() {
									alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
								}
							});                                                                                                                                      
					});
					//radiogroup scaling auslesen
					jQuery("[name=resultsScalingRadioGroup]").change(function() {
					      var value = jQuery('input[name=resultsScalingRadioGroup]:checked').val();                                                                                       
					      jQuery
							.ajax({
								url : jQuery(
										"#change_result_scaling_method")
										.text(),
								data : {
									"resultScalingMethod" : value,
								},
								success : function() {								
								},
								error : function() {
									alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
								}
							});                                                                                                                                      
					});
					//radiogroup summerize replicates auslesen
					jQuery("[name=sumRepRadioGroup]").change(function() {
					      var value = jQuery('input[name=sumRepRadioGroup]:checked').val();                                                                                       
					      jQuery
							.ajax({
								url : jQuery(
										"#change_summarize_replicate_method")
										.text(),
								data : {
									"sumRepMethod" : value,
								},
								success : function() {								
								},
								error : function() {
									alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
								}
							});                                                                                                                                      
					});
					//radiogroup summerize viablity channel auslesen
					jQuery("[name=viabilityChannelRadiogroup]").change(function() {
					      var value = jQuery('input[name=viabilityChannelRadiogroup]:checked').val();                                                                                       
					      jQuery
							.ajax({
								url : jQuery(
										"#change_viability_channel")
										.text(),
								data : {
									"viabChannel" : value,
								},
								success : function() {								
								},
								error : function() {
									alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
								}
							});                                                                                                                                      
					});
					//radiogroup summerize htsanalyzer auslesen
					jQuery("[name=useHTSAnalyzerRadioGroup]").change(function() {
					      var value = jQuery('input[name=useHTSAnalyzerRadioGroup]:checked').val();                                                                                       
					      jQuery
							.ajax({
								url : jQuery(
										"#change_hts_analyzer_settings")
										.text(),
								data : {
									"htsAnalyzerSet" : value,
								},
								success : function() {								
								},
								error : function() {
									alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
								}
							});                                                                                                                                      
					});
					
					//submit ueber change event steuern/ausfuerhen
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
					jQuery( '#uploadSessionFileTip' ).tooltip({
						bodyHandler: function() {
					        return "Previously saved session files can be used to restore complete analysis settings.";

						},
						showURL: false
					});
					jQuery( '#uploadDataFileTip' ).tooltip({
						bodyHandler: function() {
					        return "If the experiment has a lot of data files, it is best to pack them into a ZIP file and upload it.";

						},
						showURL: false
					});
					jQuery( '#updateFileParamsTip' ).tooltip({
						bodyHandler: function() {
					        return "Use this if the parameters of the uploaded files could not automatically be found. For syntax review the manual.";
						},
						showURL: false
					});
					jQuery( '#plateConfigFileTip' ).tooltip({
						bodyHandler: function() {
					        return "Upload all the positive, negative, other and empty wells information.";
						},
						showURL: false
					});
					jQuery( '#screenLogFileTip' ).tooltip({
						bodyHandler: function() {
					        return "Upload the contaminated wells information.";
						},
						showURL: false
					});
					jQuery( '#notAvailableMethodTip' ).tooltip({
						bodyHandler: function() {
					        return "This function is still under construction and will be available soon.";
						},
						showURL: false
					});
					jQuery( '#permanentButtonTip' ).tooltip({
						bodyHandler: function() {
					        return "Please click on the create/update button to make changes permanent.";
						},
						showURL: false
					});
					jQuery( '#saveSessionTip' ).tooltip({
						bodyHandler: function() {
					        return "Saved session files can be loaded later to restore the complete analysis.";
						},
						showURL: false
					});
					
					
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
