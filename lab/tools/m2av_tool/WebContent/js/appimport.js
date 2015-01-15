$(function() {
	$(document).ready(function() {
		
		//load and populate datastores in vCenter 
		var jqxhr = $.ajax({
				type: "GET",
				url: "./m2av?f=listDatastores",
				dataType: "JSON"
			})
			.done(function(data) {
				if(data==null){
					$("#import_appstack").addClass("invisible");
					$("#loginFilter").removeClass("invisible");
				}else{
				var listHTML = '<option value="">Choose a storage location:</option>';

				for (var i = 0; i < data.length; i++){
					listHTML += "<option value ='" + data[i] + "'>" + data[i] + "</option>";
				}
				$('#progress_icon_app_stacks').remove();
				$('#import_datastores_sel').append(listHTML);
				$("#import_path").attr("value","appvolumes/apps");
				}

			})
			.fail(function() {
				$("#import_appstack").addClass("invisible");
				$("#loginFilter").removeClass("invisible");
			})
			.always(function() {
			});
		});
	
	//logic to save the import action 
	$("#save_import_appstack_btn" ).click(function() {
		$("#dialog-progress").dialog('option', 'title', 'Import AppStacks');
		progressDialog.dialog( "open" );
		
		//importing VMDK files from the selected datastore
		$.ajax({
			type: "POST",
			url: "./m2av",
			data:{f:"importAppStack",storageName:$("#import_datastores_sel :selected").text(),tgtPath:$("#import_path").val()},
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
			$("#alertMsg").html( "import AppStack failed" );
			alertDialog.dialog("open");
		})
		.always(function() {
			reloadAppStacks();
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
});
