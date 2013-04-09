//when html has been loaded
jQuery(document)
		.ready(
				function() {
					//define select ajax element
					jQuery('#fileType')
					.change(
							function() {
								var selectedFiletype = jQuery(this)
										.val();
								// ajax it
								jQuery
										.ajax({
											url : jQuery(
													"#filetype_uri")
													.text(),
											data : {
												"type" : selectedFiletype
											},
											success : function() {
												
											},
											error : function() {
												alert('An error occurred, Could not send/receive data, check ISP if AJAX is supported/blocked by firewall');
											}
										});
							});
					
					
				}
				
		
		
		);