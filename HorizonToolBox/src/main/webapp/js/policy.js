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
				
				  var $profileRows = $('#profilelist').jtable('selectedRows');
				  var profileArray = "[";
				  $profileRows.each(function () {
					  var record = $(this).data('record');
					  var name = record.name;
					  alert(name);
					  profileArray = profileArray + "\"" + name +"\",";
				  });
				  profileArray = profileArray + "]";
				  
				  var $poolRows = $('#assignmentlist').jtable('selectedRows');
				  var poolArray = "[";
				  $poolRows.each(function () {
					    var record = $(this).data('record');
					    var name = record.name;
					    alert(name);
					    poolArray = poolArray + "\"" + name +"\",";
				  });
				  poolArray = poolArray +"]";
				  
				  alert(poolArray);
				  alert(profileArray);
				  $.ajax({
					    url: './policy/profile/assignprofiles',
						type: "GET",
						data: {poolNames:poolArray, profileNames:profileArray},
						success: function (data) {
							alert("assign success");
							//TODO
							$('#assignmentlist').jtable('load');
						}, 
						error: function(data) {
							alert("assign error");
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

function deletePoolProfile(pooName,proName){
/*	alert(pooName+"->"+proName);
	$.ajax({
	    url: './policy/profile/deletepoolprofile',
		type: "GET",
		data: {poolName:pooName, profileName:proName},
		success: function (data) {
			//$('#assignmentlist').jtable('load');
		}, 
		error: function(data) {
			
		}
    });*/	
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
								$('#profilelist').jtable('load');
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
						data:{profile:profileNameOut, blockAll:$("#blockAll").val(), daysToKeepLogs:$("#daysToKeepLogs").val()},
						success: function (data) {
/*							alert("updatecommon success"+data);
							if(data==null){
								alert("the profile has already been deleted");
								$("#ConfigureProfile").dialog("close");
				    			$('#profilelist').jtable('load');
							}*/
						}, 
						error: function(data) {
							alert("updatecommon error");
						}
					});
	    			//alert("clipboardRediretion="+$("#clipboardRediretion").val())
	    			$.ajax({
						url: './policy/profile/updatepcoip',
						type: "GET",
						data:{profile:profileNameOut, clipboardRediretion:$("#clipboardRediretion").val(),turnOffLossLess:$("#turnOffLossLess").val()},
						success: function (data) {
							//alert("updatepcoip success");
/*							if(data==null){
								alert("the profile has already been deleted");
								$("#ConfigureProfile").dialog("close");
				    			$('#profilelist').jtable('load');
							}*/
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
/*							if(data==null){
								alert("the profile has already been deleted");
								$("#ConfigureProfile").dialog("close");
				    			$('#profilelist').jtable('load');
							}*/
						}, 
						error: function(data) {
							////alert("updateusb error");
						}
					});
	    			$(this).dialog("close");
	    			$('#profilelist').jtable('load');
	    			
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
			toolbar: {
			    items: [{
			        text: '+Add',
			        click: function () {
			        	 $( "#CreateProfile" ).dialog( "open" );
			        }
			    }]
			},
			paging: false, //Enable paging
			// pageSize: 10, //Set page size (default: 10)
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
			//defaultSorting: 'ProfileTime DESC',
			selecting: true, //Enable selecting
			multiselect: true, //Allow multiple selecting
			selectingCheckboxes: true, //Show checkboxes on first column
			selectOnRowClick: false, //Enable this to only select using checkboxes
			actions: {
				listAction: './pool/viewpools/getviewpools',
			},
			fields: {
				recordId: {
					key: true,
					create: false,
					edit: false,
					list: false
				},		
				//CHILD TABLE DEFINITION FOR "EXAMS"
	              Exams: {
	                  title: '',
	                  width: '5%',
	                  sorting: false,
	                  edit: false,
	                  create: false,
	                  display: function (data) {
	                      //Create an image that will be used to open child table
	                	  var $img = $('<img src="./jtable/themes/jqueryui/plus_16.png" />');
	                      //Open child table when user clicks the image
	                	  $img.click(function () {    
	                		var poolName = data.record.name;
	                    	var i=0;
							var x;
	                    	i=$('#assignmentlist').jtable('isChildRowOpen', $img.closest('tr'));
	                    	  
	                    	if(i)
	                    		$('#assignmentlist').jtable('closeChildTable', $img.closest('tr'),
	          	                    	  function (data) { //opened handler
	                                        data.childTable.jtable('close');
	                                   });
	                    	else
	                          $('#assignmentlist').jtable('openChildTable',
	                                  $img.closest('tr'), //Parent row
	                                  {
	                                  //title: data.record.name + ' - Profiles',
	                                  actions: {
	                                      //listAction: '/Demo/ExamList?StudentId=' + studentData.record.StudentId,	             
	                                	  listAction: './policy/profile/getpoolprofiles?poolName=' + data.record.name,	   
	                                  },
	                                  fields: {
	                                	  recordId: {
	                      					key: true,
	                      					create: true,
	                      					edit: false,
	                      					list: false
	                      				},		
	                      				TestColumn: {
	                      				    title: '',
	                      				  width: '15%',
	                      				    display: function (data) {
	                      				        return '';
	                      				    }
	                      				},
	                      				name: {
	                      				//	title: 'Profile name',
	                      					width: '73%',
	                      					type: 'text',
	                      					//create: false,
	                      					edit: false
	                      				},
	                      				
	                    				Action: {
	                    					//title: 'Edit/Delete',
	                    					width: '20%',
	                    					edit: false,
	                    					display: function(data) {
	                    					  var $bu1= $('<button class="jtable-command-button jtable-delete-command-button" title="delete"></button>');	                     
	                   	                      //Open child table when user clicks the image
	                   	                      $bu1.click(function () { 
												  $.ajax({
													    url: './policy/profile/deletepoolprofile',
														type: "GET",
														data: {poolName:poolName, profileName:data.record.name},
														success: function (data) {
															//$('#assignmentlist').jtable('load');
														}, 
														error: function(data) {
															
														}
												    });
												  x.childTable.jtable('load');
	                   	                      });
	                   	                      return $bu1;
	                    				  }	                    					
	                    				}	                                    
	                                  }
	                              }, function (data) { //opened handler
	                            	  x=data;
	                                  data.childTable.jtable('load');
	                              });		                          
	                      });
	                      //Return image to show on the person row	    
	                      return $img;
	                  }
	              },
				name: {
					title: 'Pool',
					width: '100%',
					type: 'text',
					create: false,
					edit: false
				},
			}
		}); 
	  
	$('#profilelist').jtable('load');
	$('#assignmentlist').jtable('load');

	ToolBox.Policy.init();
	
});