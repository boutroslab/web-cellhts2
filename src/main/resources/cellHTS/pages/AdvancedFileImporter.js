//when html has been loaded
jQuery(document)
		.ready(
				function() {
					// define tooltips
					jQuery( '#tooltip0' ).tooltip({
						bodyHandler: function() {
					        return "If flash >V.7 is enabled multiple files can be uploaded by holding down mouse button or using Shift+arrow key";

						},
						showURL: false
					});
					jQuery( '#tooltip1' ).tooltip({
						bodyHandler: function() {
					        return "The well column must be of standard well format, e.g.: A01, D15. The actual signal value column must be positive or negative floating point numbers.";

						},
						showURL: false
					});
					jQuery( '#tooltip2' ).tooltip({
						bodyHandler: function() {
					        return "valid column values here are pos, neg, sample, empty, other, cont1, cont and contaminated.";

						},
						showURL: false
					});
					//checkboxes 
					jQuery("*[id^='containsHeadline']").change(function(){
						var checked = false;
						if(jQuery(this).is(':checked')) {
							checked = true;
						}

						jQuery
						.ajax({
							url : jQuery(
									"#multi_channel_data_uri")
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
					jQuery("*[id^='containsMultiChannelData']").change(function(){
						var checked = false;
						if(jQuery(this).is(':checked')) {
							checked = true;
						}

						jQuery
						.ajax({
							url : jQuery(
									"#headline_uri")
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
					//ajax textfields
					jQuery("*[id^='replicateNumbers']").blur(function() {
						var label = jQuery(this).val();
						var currentId = jQuery(this).attr('id');
						jQuery
						.ajax({
							url : jQuery(
									"#change_replic_num")
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
					
					
				}
				
		
		
		);