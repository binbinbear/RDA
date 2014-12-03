$.extend(true, $.hik.jtable.prototype.options, {
	//columnResizable: false,
	//animationsEnabled: false,
	jqueryuiTheme: true,
	ajaxSettings: {
		type: 'GET'
	}
	
});
if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Policy || !ToolBox.Policy.init){
	ToolBox.Policy = {

			getViewPools: function(){
				$.ajax({
					url: './common/desktoppools',
					type: "GET",
					success: function (data) {
						 var poolbody = $("#desktoppool");
						 for (var i= 0; i< data.length; i++){
								var dSD = data[i];
								var option = "<option value=\""   +  dSD.name + "\"> "+ dSD.name+"</option>";
							 	poolbody.append(option);
							}
					}, 
					error: function(data) {
					}
				});
			},
			AddProfile: function(){
				
				 $( "#CreateProfile" ).dialog( "open" );
				// $( "#accordion" ).accordion("open");
			},
			openassign: function(){
				
				 //alert("null");
			},
			
			clean: function(){
				$.ajax({
					url: './policy/profile/clean',
					type: "GET",
					success: function (data) {
						
					}, 
					error: function(data) {
					}
				});
			},

			init: function(){
				
				$("#AddProfile").click(ToolBox.Policy.AddProfile);
				$("#openassign").click(ToolBox.Policy.openassign);
				
			},

			
			updatePlicies: function (){
				$("#message").empty();
		    	$("#message").append("<div class=\"loadingdiv\"></div>");
		    	
				$.ajax({
					url: './policy/updatepolicies',
					type: "GET",
					//?????,???????for e: if $("#desktoppool").val() == "" | $("#desktoppool").val().equal(null)),???data
					data:{pool:$("#desktoppool").val(), clipboard:$("#clipboard").val(), 
						  device:$("#device").val(), productionLogs:$("#productionLogs").val(), 


						  lossless:$("#lossless").val(), maximum:$("#maximum").val(),
						  MTU:$("#MTU").val(), floor:$("#floor").val(),
						  enDisAudio:$("#enDisAudio").val(), limit:$("#limit").val(),
						  SSL:$("#SSL").val(), encryption:$("#encryption").val(),
						  USB:$("#USB").val(), TCP:$("#TCP").val(),
						  UDP:$("#UDP").val(), channels:$("#channels").val(),
						  image:$("#image").val(), FIPS:$("#FIPS").val(),
						  vSphere:$("#vSphere").val(), synchronization:$("#synchronization").val(),
						  alternate:$("#alternate").val(), CAD:$("#CAD").val(),
						  transport:$("#transport").val(), verbosity:$("#verbosity").val(),
						  timeindays:$("#timeindays").val(), sizeinmb:$("#sizeinmb").val(),
						  //
						  exclude:$("#exclude").val(), splitdevice:$("#splitdevice").val(),
						  other:$("#other").val(), HID:$("#HID").val(),
						  inputdevices:$("#inputdevices").val(), outputdevices:$("#outputdevices").val(),
						  keyboard:$("#keyboard").val(), videodevices:$("#videodevices").val(),
						  smartcards:$("#smartcards").val(), autodevice:$("#autodevice").val(),
						  excludeVP:$("#excludeVP").val(), includeVP:$("#includeVP").val(),
						  excludeDF:$("#excludeDF").val(), includeDF:$("#includeDF").val(),
						  exclludeAll:$("#exclludeAll").val(), MMR:$("#MMR").val(),
						  multimedia:$("#multimedia").val(), directRDP:$("#directRDP").val(),
						  singleSignon:$("#singleSignon").val(), timeout:$("#timeout").val(),
						  credentialFilter:$("#credentialFilter").val(), usingDNS:$("#usingDNS").val(),
						  disableTZName:$("#disableTZName").val(), toggle:$("#toggle").val(),
						  onConnect:$("#onConnect").val(), onReconnect:$("#onReconnect").val(),
						  onDisconnect:$("#onDisconnect").val(), showIcon:$("#showIcon").val(),
						  frameworkChannel:$("#frameworkChannel").val(), unity:$("#unity").val(),
						  maxFrames:$("#maxFrames").val(), maxImageHeight:$("#maxImageHeight").val(),
						  maxImageWidth:$("#maxImageWidth").val(), defaultImageHeight:$("#defaultImageHeight").val(),
						  defaultImageWidth:$("#defaultImageWidth").val(), disableRTAV:$("#disableRTAV").val(),
						  portNumber:$("#portNumber").val(), sessionTimeout:$("#sessionTimeout").val(),
						  disclaimerEnabled:$("#disclaimerEnabled").val(), disclaimerText:$("#disclaimerText").val(),
						  applictionsEnabled:$("#applictionsEnabled").val(), autoConnect:$("#autoConnect").val(),
						  alwaysConnect:$("#alwaysConnect").val(), screenSize:$("#screenSize").val(),
						  PCoIPport:$("#PCoIPport").val(), RDPport:$("#RDPport").val(),
						  blastPort:$("#blastPort").val(), IPaddress:$("#IPaddress").val(),
						  channelPort:$("#channelPort").val(), USBenabled:$("#USBenabled").val(),
						  MMRenabled:$("#MMRenabled").val(), resetEnabled:$("#resetEnabled").val(),
						  USBautoConnect:$("#USBautoConnect").val(), cacheTimeout:$("#cacheTimeout").val(),
						  DisSessionTimeout:$("#DisSessionTimeout").val() },
					success: function (data) {						
						var loadingDiv = $(".loadingdiv");
				    	loadingDiv.removeClass("loadingdiv");
				    	$(function(){
							var tr="<div class='ui-state-highlight ui-corner-all' style='padding: 0 .7em; display:none;'><p><span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;'></span>Successful !</p></div>";
							$( "#message" ).empty();
							$( "#message" ).append( tr );
							$("div.ui-state-highlight").slideDown(1500,function(){setTimeout(function(){$("div.ui-state-highlight").slideUp(1500);},"2000");});
							//setTimeout("$( '#message' ).empty();",2000);
							//$("div.ui-state-highlight").slideDown(1000,function(){$("div.ui-state-highlight").slideUp(2000);});
							//$("div.ui-state-highlight").slideUp(2000);
						});
				    	
					},
					error: function (){
						var loadingDiv = $(".loadingdiv");
				    	loadingDiv.removeClass("loadingdiv");
				    	$(function(){
							var tr="<div class='ui-state-error ui-corner-all' style='padding: 0 .7em; display:none;'><p><span class='ui-icon ui-icon-//alert' style='float: left; margin-right: .3em;'></span>ERROR !</p></div>";
							$( "#message" ).empty();
							$( "#message" ).append( tr );
							//setTimeout("$( '#message' ).empty();",2000);
							$("div.ui-state-error").slideDown(1500,function(){setTimeout(function(){$("div.ui-state-error").slideUp(1500);},"2000");});
						});
					}
				}); 
			}
	};
}
function checkLength( o, n, min, max ) {
    if ( o.val().length > max || o.val().length < min ) {
		//alert("profile name is required");
	  return false;
	} else {
	  return true;
	}
 }

function editprofile(pName) {
	//alert("profile Id="+pName);
	
    $.ajax({
	    url: './policy/profile/getprofile',
		type: "GET",
		data: {profileName:pName},
		success: function (data) {
			//TODO
			//alert("common: "+data.commonCategory.blockAll+","+data.commonCategory.daysToKeepLogs);
			//alert("pcoip: "+data.pcoipCategory.clipboardRediretion+","+data.pcoipCategory.turnOffLossLess);
			//alert("usb: "+data.usbCategory.allowOther+","+data.usbCategory.hidBootable);
			$("#ConfigureProfile").dialog("open");
			
			$("#blockAll").val( String(data.commonCategory.blockAll) );
			$("#daysToKeepLogs").val(data.commonCategory.daysToKeepLogs);
			$("#clipboardRediretion").val(data.pcoipCategory.clipboardRediretion);
			$("#turnOffLossLess").val( String(data.pcoipCategory.turnOffLossLess) );
			$("#allowOther").val( String(data.usbCategory.allowOther) ); 
			$("#hidBootable").val( String(data.usbCategory.hidBootable) );
		}, 
		error: function(data) {
			//alert("error");
		}
    });	
 }

function deleteprofile(proName){
	 //alert("delete "+proName);
	 $.ajax({
		    url: './policy/profile/delete',
			type: "GET",
			data: {profileName:proName},
			success: function (data) {
				$('#profilelist').jtable('load');
			}, 
			error: function(data) {
				
			}
	    });	
}


$(function() { 
	 var name = $("#proname"),     
     autor = $( "#autor" ),
     allFields = $( [] ).add( name ).add( autor );
	 var profileNameOut;
	 $("#CreateProfile").dialog({
	      autoOpen: false,
	      height: 300,
	      width: 350,
	      modal: true,
	      buttons:{
	      "Create": function() {
	    	  var bValid = true;
	          allFields.removeClass( "ui-state-error" );
	          bValid = bValid && checkLength( name, "proname", 1, 16 );
	          var profileName = $("#proname").val();
	          profileNameOut=profileName;
	          var desc = $("#desc").val();
	          //alert(profileName+","+desc);
	          if(bValid) {
		          $.ajax({
						url: './policy/profile/create',
						type: "GET",
						data: {proname:profileName, description:desc},
						success: function (data) {
							if(data){
								$( "#CreateProfile" ).dialog( "close" );
								$( "#ConfigureProfile" ).dialog( "open" );
							}else{
								alert("name already exit!");
							}
						}, 
						error: function(data) {
							//alert("error");
						}
				 });
	          }
	        },
	       "Cancel": function() {
	          $( this ).dialog( "close" );
	        }
      },
      close: function() {
        allFields.val( "" ).removeClass( "ui-state-error" );
      }
	 });
	 $("#ConfigureProfile").dialog({
	      autoOpen: false,
	      height: "auto",
	      width: "auto",
	      title: "Profile",
	      modal: true, 
	      buttons : [  {
	    		text : "save",
	    		click : function() {	
	    			$.ajax({
						url: './policy/profile/updatecommon',
						type: "GET",
						data:{profile:profileNameOut,blockAll:$("#blockAll").val(), daysToKeepLogs:$("#daysToKeepLogs").val()},
						success: function (data) {
							$('#profilelist').jtable('load');
							//alert("updatecommon success");
						}, 
						error: function(data) {
							//alert("updatecommon error");
						}
					});
	    			//alert("clipboardRediretion="+$("#clipboardRediretion").val())
	    			$.ajax({
						url: './policy/profile/updatepcoip',
						type: "GET",
						data:{profile:profileNameOut, clipboardRediretion:$("#clipboardRediretion").val(),turnOffLossLess:$("#turnOffLossLess").val()},
						success: function (data) {
							//alert("updatepcoip success");
						}, 
						error: function(data) {
							//alert("updatepcoip error");
						}
					});
	    			$.ajax({
						url: './policy/profile/updateusb',
						type: "GET",
						data:{profile:profileNameOut, allowOther:$("#allowOther").val(), hidBootable:$("#hidBootable").val()},
						success: function (data) {
							//alert("updateusb success");
						}, 
						error: function(data) {
							////alert("updateusb error");
						}
					});
	    			
	    			$(this).dialog("close");
	    		}
	    	},
	    	{
	    		text : "cancel",
	    		click : function() {
	    			$(this).dialog("close");
	    		}}],
	      hide: {
	        effect: "explode",
	        duration: 1000
	      }
	    });

	 $( "#tabs" ).tabs();
	 $( "#Tabs-common" ).tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
	 $( "#Tabs-common li" ).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
	 $( "#Tabs-pcoip" ).tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
	 $( "#Tabs-pcoip li" ).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
	 $( "#Tabs-usb" ).tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
	 $( "#Tabs-usb li" ).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );

	
	  $('#profilelist').jtable({
			title: 'Profile',
			 paging: false, //Enable paging
	      //   pageSize: 10, //Set page size (default: 10)
			sorting: false,
	       // defaultSorting:'recordId DSC',
			selecting: true, //Enable selecting
			multiselect: true, //Allow multiple selecting
			selectingCheckboxes: true,
			selectOnRowClick: false, 
			actions: {
				listAction: './policy/profile/getnamelist',
				//listAction: './policy/profile/list',
			},
			fields: {
				recordId: {
					key: true,
					//create: false,
					edit: false,
					list: false
				},		
				name: {
					title: 'Profile name',
					width: '23%',
					type: 'text',
					//create: false,
					edit: false
				},
				description: {
					title: 'Description',
					width: '35%',
					type: 'text',
					//create: false,
					edit: false
				},
				Action: {
					//title: 'Edit/Delete',
					width: '20%',
					edit: false,
					display: function(data) {
							return '<button class="jtable-command-button jtable-edit-command-button" onclick="editprofile(\''
								+ data.record.name
								+ '\')" title="edit"></button>&nbsp&nbsp&nbsp&nbsp<button class="jtable-command-button jtable-delete-command-button" onclick="deleteprofile(\''
								+ data.record.name
								+ '\')" title="delete"></button>';
					}
				}
			}
		});
	  $('#assignmentlist').jtable({
			title: 'Assignment',
			paging: false,
			sorting: false,
			defaultSorting: 'ProfileTime DESC',
			selecting: false, //Enable selecting
			multiselect: false, //Allow multiple selecting
			selectingCheckboxes: false, //Show checkboxes on first column
			//selectOnRowClick: false, //Enable this to only select using checkboxes
			actions: {
			},
			fields: {
				recordId: {
					key: true,
					create: false,
					edit: false,
					list: false
				},		
				ProfileName: {
					title: 'Assignment',
					width: '23%',
					type: 'text',
					create: false,
					edit: false
				},
				ProfileTime: {
					title: 'Time',
					width: '12%',
					type: 'text',
					create: false,
					edit: false
				},
			
				ProfileAutor: {
					title: 'Autor',
					width: '70px',
					type: 'text',
					create: false,
					edit: false,
					sorting: false
				}
			}
		});
	  
	$('#profilelist').jtable('load');
	  
	//$('#profilelist').jtable('load');
	//$('#assignmentlist').jtable('load');
	ToolBox.Policy.init();
	
});