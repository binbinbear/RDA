$(function() {
	$(document).ready(function () {
		//logic to handle the Appstack copy action
		$(function() {
				//load and populate datastores in vCenter 
				var jqxhr = $.ajax({
						type: "GET",
						url: "./m2av?f=listDatastores",
						dataType: "JSON"
					})
					.done(function(data) {
						if(data==null){
							$("#copy_appstack").addClass("invisible");
							$("#loginFilter").removeClass("invisible");
						}else{
						var listHTML = '<option value="">Choose a storage location:</option>';

						for (var i = 0; i < data.length; i++){
							listHTML += "<option value ='" + data[i] + "'>" + data[i] + "</option>";
						}
					
						$('#progress_icon_app_stacks').remove();
						$('#ak_datastores_sel').append(listHTML);
						}

					})
					.fail(function() {
						$("#copy_appstack").addClass("invisible");
						$("#loginFilter").removeClass("invisible");
						return;
					})
					.always(function() {
					});
				//load and populate app stacks
				var jqxhr = $.ajax({
						type: "GET",
						url: "./m2av?f=listAppStacks",
						dataType: "JSON"
					})
					.done(function(data) {
							var listHTML = '<option value="">Choose a source AppStack:</option>';
							for (var i = 0; i < data.length; i++){
								name=data[i].substr(0,data[i].lastIndexOf("#"));
								listHTML += "<option value ='" + data[i] + "'>" + name+ "</option>";
							}
							$('#progress_icon_app_stacks').remove();
							$('#ak_source').append(listHTML);
						
					})
					.fail(function() {
					})
					.always(function() {
					});
		});
		var progressDialog = $( "#dialog-progress" ).dialog({
			autoOpen: false,
			resizable: false,
			height:110,
			width:500,
			modal: true,
		});
		
		 var alertDialog=$('#alertMessage').dialog({

             autoOpen: false,
             width: 300,
             modal: true,
             buttons: {
                 "OK": function() {
                     $(this).dialog("close");
                 }
             }
         });
		 
		$("#ak_source").change(function () {
			var file_location=unescape($(this).children('option:selected').val());
			$("#ak_name").val(file_location.substr(file_location.lastIndexOf("#")+1));
			$("#ak_path").val("appVolumes/app/copy");
		});

		
		//logic to save the copy action 
		$("#save_copy_appstack_btn" ).click(function() {
			
			//add progress bar with AppVolumes style
			$("#dialog-progress").dialog('option', 'title', 'Copying AppStacks');
			progressDialog.dialog( "open" );
			
			//Copy Source AppStack vmdk files to specified location
			$.ajax({
				type: "POST",
				url: "./m2av",
				data:{f:"copyAppStack",srcAppStack:$("#ak_source :selected").val(), tgtAppStack:$("#ak_name").val(),storageName:$("#ak_datastores_sel :selected").text(),tgtPath:$("#ak_path").val(),description:$("#ak_description").val()},
				contentType:"application/x-www-form-urlencoded; charset=utf-8",
				dataType: "JSON"	 
			})
			.done(function(data) {
				if(data.successes){
					progressDialog.dialog( "close" );
					$("#alertMsg").html(data.successes[0]);
					alertDialog.dialog("open");
				}
				else if(data.warning){
					$("#alertMsg").html(data.warning);
					alertDialog.dialog("open");}
				else{
					$("#alertMsg").html(data);
					alertDialog.dialog("open");
				}
				reloadAppStacks();
			})
			.fail(function() {
				$("#alertMsg").html( "copy AppStack failed" );
				alertDialog.dialog("open");
			})
			.always(function() {
				reloadAppStacks();
			});			
		});
		
		
		
		
	});
});
