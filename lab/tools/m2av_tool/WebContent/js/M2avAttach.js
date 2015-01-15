$(function() {
	$.extend(true, $.hik.jtable.prototype.options, {
		jqueryuiTheme: true,
		ajaxSettings: {
			type: 'GET'
		}
	});
	var g_conf = { poolId: null, targetType: null};
	function reloadDesktopTable(poolId, targetType) {
			
		if (poolId == null)
			poolId = g_conf.poolId;
		else
			g_conf.poolId = poolId;
		if (targetType == null)
			targetType = g_conf.targetType;
		else
			g_conf.targetType = targetType;
		
		$('#TableContainer').jtable('load', {
	        poolId: poolId,
	        targetType: targetType
	    });
	}
	
	function reloadAppStacks(){
		$.ajax({
			type: "GET",
			url: "./m2av?f=listAppStacks",
			dataType: "JSON"
		})
		.done(function(data) {
			
			var listHTML = '<ol class="selectable">';
			for (var i = 0; i < data.length; i++){
				name=data[i].substr(data[i].lastIndexOf("#")+1);
				listHTML += "<li class='ui-widget-content' value='"+data[i]+"' title='"+data[i]+"'>" + name + "</li>";
			}
			listHTML += '</ol>';
			
			$('#progress_icon_app_stacks').remove();
			$('#appStacks').empty();
			$('#appStacks').append(listHTML);
	        $(".selectable").selectable({
	            stop:function(event, ui){
	                $(event.target).children('.ui-selected').not(':first').removeClass('ui-selected');
	            }
	        });
		})
		.fail(function() {
			$("#alertMsg").html( "Error listing App Stacks" );
			alertDialog.dialog("open");
		})
		.always(function() {
		});
	}

	$(document).ready(function () {
		var confirmationDialog = $( "#dialog-confirm" ).dialog({
			autoOpen: false,
			resizable: false,
			height:240,
			width:400,
			modal: true,
			buttons: {
				"Yes": function() {
					$(this).dialog("close");
					$("#msg").html("Please wait for a while....");
					loadingDialog.dialog( "open" );
					$( "#dialog" ).dialog();
					$.ajax({
						type: "POST",
						url: "./m2av",
						data:{f:"assignments",appStack:$('#appStacks .ui-selected').attr("value"),poolId:$("option:selected").text(),targetType:g_conf.targetType,type:$("input[name='isReal'][checked]").val()},
						contentType:"application/x-www-form-urlencoded; charset=utf-8",
						dataType: "JSON"
							 
					})
					.done(function(data) {
						loadingDialog.dialog("close");
						if(data.successes){
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
						reloadDesktopTable($("option:selected").text(),g_conf.targetType);
					})
					.fail(function() {
						$("#alertMsg").html( "assign failed" );
						alertDialog.dialog("open");
					})
					.always(function() {
						reloadDesktopTable($("option:selected").text(),g_conf.targetType);
					});
				},
				"No": function() {
					$( this ).dialog( "close" );
				}
			}
		});
		var removeDialog = $( "#dialog-remove" ).dialog({
			autoOpen: false,
			resizable: false,
			height:240,
			width:400,
			modal: true,
			buttons: {
				"Yes": function() {
					$(this).dialog("close");
					$("#msg").html("Please wait for a while....");
					loadingDialog.dialog( "open" );
					$( "#dialog" ).dialog();
					$.ajax({
						type: "POST",
						url: "./m2av",
						data:{f:"unassignments",appStack:$('#appStacks .ui-selected').attr("value"),poolId:$("option:selected").text(),targetType:g_conf.targetType,type:$("input[name='isReal'][checked]").val()},
						contentType:"application/x-www-form-urlencoded; charset=utf-8",
						dataType: "JSON"
					})
					.done(function(data) {
						loadingDialog.dialog("close");
						if(data.successes){
							$("#alertMsg").html(data.successes[0]);
						alertDialog.dialog("open");
						}
						else if(data.warning){
							$("#alertMsg").html(data.warning);
						alertDialog.dialog("open");
						}
						else{
							$("#alertMsg").html(data);
							alertDialog.dialog("open");
						}
						reloadDesktopTable($("option:selected").text(),g_conf.targetType);
					})
					.fail(function() {
						$("#alertMsg").html( "unassign failed" );
						alertDialog.dialog("open");
					})
					.always(function() {
						reloadDesktopTable($("option:selected").text(),g_conf.targetType);
					});
					
				},
				"No": function() {
					$( this ).dialog( "close" );
				}
			}
		});
		var loadingDialog = $( "#dialog-loading" ).dialog({
			autoOpen: false,
			resizable: false,
			height:240,
			width:400,
			modal: true,
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
		$("#btnApply" ).click(function() {
			
			var stackName = $('#appStacks .ui-selected').text();
			var targets = "of these computers";
			var poolName = $("option:selected").text();
			var msg = "Do you want to apply app stack <b>" + stackName + "</b> to all " + targets + " in pool <b>" + poolName + "</b>?<p><input type='radio' name='isReal' value='0'>Attach AppStacks on next login or reboot<br/><input type='radio' name='isReal' checked='checked' value='1'>Attach AppStacks immediately</p>";
			if(""==stackName||null==stackName){
				$("#alertMsg").html("select app Stack");
				alertDialog.dialog("open");
			}else{
				$("#confirmMsg").html(msg);
				confirmationDialog.dialog("open");
			}
		});
		
		$("#btnRemove" ).click(function() {
			var stackName = $('#appStacks .ui-selected').text();
			var targets = "of these computers";
			var poolName = $("option:selected").text();
			var msg = "Do you want to remove app stack <b>" + stackName + "</b> from pool <b>" + poolName + "</b>?<p><input type='radio' name='isReal' value='0'>Detach AppStacks on next login or reboot<br/><input type='radio' name='isReal' checked='checked' value='1'>Detach AppStacks immediately</p>";
			if(""==stackName||null==stackName){
				$("#alertMsg").html("select app Stack");
				alertDialog.dialog("open");
			}else{
				$("#removeMsg").html(msg);
				removeDialog.dialog("open");
			}
			
		});
		
		//logic to handle the Appstack copy action
		$("#copy_appStack_btn" ).click(function() {
			var stackFullName = $('#appStacks .ui-selected').attr("value");

			if(""==stackFullName||null==stackFullName){
				$("#alertMsg").html("select app Stack");
				alertDialog.dialog("open");
			}else{
				var stackName=stackFullName.substr(stackFullName.lastIndexOf("#")+1);
				$("#appStackSelected").attr("value",stackName);
				//$("#volumeFilter").remove("#contentarea");
				$("#contentarea").addClass("invisible");
				//$("#volumeFilter").append($("#copy_appstack"));
				$('#ak_name').attr("value",stackName+"_copy");
				$("#copy_appstack").removeClass("invisible");
				
				//load and populate datastores in vCenter 
				var jqxhr = $.ajax({
						type: "GET",
						url: "./m2av?f=listDatastores",
						dataType: "JSON"
					})
					.done(function(data) {
						var listHTML = '<option value="">Choose a storage location:</option>';

						for (var i = 0; i < data.length; i++){
							listHTML += "<option value ='" + data[i] + "'>" + data[i] + "</option>";
						}
					
						$('#progress_icon_app_stacks').remove();
						$('#ak_datastores_sel').append(listHTML);
						temp=stackFullName.substr(stackFullName.lastIndexOf("]")+1);
						stackPath=temp.substr(0,temp.lastIndexOf("/"));
						$("#ak_path").attr("value",stackPath);

					})
					.fail(function() {
						$("#alertMsg").html( "Error listing datastores from vCenter" );
						alertDialog.dialog("open");
					})
					.always(function() {
					});
				
			}	
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
				data:{f:"copyAppStack",srcAppStack:$('#appStacks .ui-selected').attr("value"), tgtAppStack:$("#ak_name").val(),storageName:$("#ak_datastores_sel :selected").text(),tgtPath:$("#ak_path").val(),description:$("#ak_description").val()},
				contentType:"application/x-www-form-urlencoded; charset=utf-8",
				dataType: "JSON"	 
			})
			
			.done(function(data) {
				if(data.successes){
					progressDialog.dialog( "close" );
					$("#copy_appstack").addClass("invisible");
					$("#contentarea").removeClass("invisible");
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
		
		//logic to handle the Appstack import action
		$("#import_appStack_btn" ).click(function() {
				$("#contentarea").addClass("invisible");
				$("#import_appstack").removeClass("invisible");
				
				//load and populate datastores in vCenter 
				var jqxhr = $.ajax({
						type: "GET",
						url: "./m2av?f=listDatastores",
						dataType: "JSON"
					})
					.done(function(data) {
						var listHTML = '<option value="">Choose a storage location:</option>';

						for (var i = 0; i < data.length; i++){
							listHTML += "<option value ='" + data[i] + "'>" + data[i] + "</option>";
						}
						$('#progress_icon_app_stacks').remove();
						$('#import_datastores_sel').append(listHTML);
						$("#import_path").attr("value","appvolumes/apps");

					})
					.fail(function() {
						$("#alertMsg").html( "Error listing datastores from vCenter" );
						alertDialog.dialog("open");
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
					$("#import_appstack").addClass("invisible");
					$("#contentarea").removeClass("invisible");
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
		
		$("#typeRadio").buttonset();
		$("#radio_user").click(function(event) {
			reloadDesktopTable(null, "user");
		});
		$("#radio_computer").click(function(event) {
			reloadDesktopTable(null, "computer");
		});
		
		//Prepare jtable plugin
		$('#TableContainer').jtable({
			title: 'Computer/User in Pool',
			paging: false,
			sorting: false,
			selecting: false, //Enable selecting
			multiselect: false, //Allow multiple selecting
			selectingCheckboxes: false, //Show checkboxes on first column
			actions: {
				listAction: './m2av?f=listTargets'
			},
			fields: {
				desktop: {
					title: 'Desktop/User',
					width: '40%',
					key: true,
					type: 'text',
					create: false,
					edit: false,
					list: true
				},
				status: {
					title: "Status",
					width: '20%',
					type: 'text',
					create: false,
					edit: false
				},		
				stacks: {
					title: 'App Stacks',
					width: '40%',
					type: 'custom',
					create: false,
					edit: false,
					display: function (data) {
						var html = ''
						for (var i = 0; i < data.record.appStacks.length; i++) {
							html += data.record.appStacks[i] + "<br/>";
						}
						return html;
					}
				}
			}
		});
		

		//load and populate pools
		var jqxhr = $.ajax({
				type: "GET",
				url: "./m2av?f=listPools",
				dataType: "JSON"
			})
			.done(function(data) {
				
				var listHTML = '<select id="poolId">';

				for (var i = 0; i < data.length; i++)
					listHTML += '<option value ="' + data[i] + '">' + data[i] + '</option>';
				listHTML += '</select>';
				$('#progress_icon_pools').remove();
				$('#desktopPools').append(listHTML);
				if (data.length > 0)
					reloadDesktopTable(data[0], "computer");

				$('#poolId').on('change', function (e) {
				    var optionSelected = $("option:selected", this);
				    var poolId = this.value;
				    reloadDesktopTable(poolId, null);
				});
			})
			.fail(function() {
				$("#alertMsg").html( "Error listing Pools" );
				alertDialog.dialog("open");
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
				var listHTML = '<ol class="selectable">';
				for (var i = 0; i < data.length; i++){
					name=data[i].substr(data[i].lastIndexOf("#")+1);
					listHTML += "<li class='ui-widget-content' value='"+data[i]+"' title='"+data[i]+"'>" + name + "</li>";
				}
				listHTML += '</ol>';
				
				$('#progress_icon_app_stacks').remove();
				$('#appStacks').append(listHTML);
		        $(".selectable").selectable({
		            stop:function(event, ui){
		                $(event.target).children('.ui-selected').not(':first').removeClass('ui-selected');
		            }
		        });
			})
			.fail(function() {
				$("#alertMsg").html( "Error listing App Stacks" );
				alertDialog.dialog("open");
			})
			.always(function() {
			});
	});
});
