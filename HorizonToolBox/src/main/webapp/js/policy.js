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

var editOrInit=0; //init
if (!ToolBox.Policy || !ToolBox.Policy.init){
	ToolBox.Policy = {

			getViewPools: function(){
				$.ajax({
					url: './common/desktoppools',
					type: "GET",
				///	 beforeSend: function() { $('#spinner').show(); },
				 //     complete: function() { $('#spinner').hide(); },
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
			openAssignconfirm:function(){
				
				$( "#assign-confirm" ).dialog( "open" );
				
			},
			openassign: function(){
		          
			//	$("#message").empty();
		    //	$("#message").append("<div class=\"loadingdiv\"></div>");
		    	
				  var $profileRows = $('#profilelist').jtable('selectedRows');
				  var profileArray = [];	  
				  $profileRows.each(function () {
					  var record = $(this).data('record');
					  var name = record.name;
					  profileArray.push(name);
				  });

				  var profileArrayStr = JSON.stringify(profileArray);
				  //alert("profileArrayStr: "+profileArrayStr);  
			
				  var $poolRows = $('#assignmentlist').jtable('selectedRows');
				  var poolArray = [];
				  $poolRows.each(function () {
					    var record = $(this).data('record');
					    var name = record.name;
					    poolArray.push(name);
				  });
				  var poolArrayStr = JSON.stringify(poolArray);
				  var loadingDiv = $(".loadingdiv");

				  $.ajax({
					    url: './policy/profile/assignprofiles',
						type: "GET",
						data: {profileNames:profileArrayStr, poolNames:poolArrayStr},
						success: function (data) {
							if(data){
								$('#assignmentlist').jtable('reload');
								$poolRows = $('#assignmentlist').jtable('selectedRows');
								var $chkBox = $poolRows.find('td.jtable-selecting-column input').attr('checked', true); 
				                $chkBox.attr('checked', false);
								$('#assignmentlist').jtable('reload');
								
						    	loadingDiv.removeClass("loadingdiv");
						    	$(function(){
								//	var tr="<div class='ui-state-highlight ui-corner-all' style='padding: 0 .7em; display:none;'><p><span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;'></span>Assign Success !</p></div>";
								//	$( "#message" ).empty();
								//	$( "#message" ).append( tr );
								//	$("div.ui-state-highlight").slideDown(1500,function(){setTimeout(function(){$("div.ui-state-highlight").slideUp(1500);},"2000");});
								});
							}else{
								//loadingDiv.removeClass("loadingdiv");
							}
							 $( "#assign-success" ).dialog("open");

						}, 
						error: function(data) {
							
					    	//loadingDiv.removeClass("loadingdiv");
					    	//$(function(){
							//	var tr="<div class='ui-state-error ui-corner-all' style='padding: 0 .7em; display:none;'><p><span class='ui-icon ui-icon-//alert' style='float: left; margin-right: .3em;'></span>Assign Error !</p></div>";
							//	$( "#message" ).empty();
							//	$( "#message" ).append( tr );
							//	$("div.ui-state-error").slideDown(1500,function(){setTimeout(function(){$("div.ui-state-error").slideUp(1500);},"2000");});
							//});
						}
				    });	
			},
			
			testdata: function(){	
				var para = [{"PolicyId":"SSL","enabled":$('#SSL_connections_enabled_2-3').val(),"items":{"policy2-3-1":$('#policy2-3-1').val(),"policy2-3-2":$('#policy2-3-2').val(),"policy2-3-3":$('#policy2-3-3').val(),"policy2-3-4":$('#policy2-3-4').val(),"policy2-3-5":$('#policy2-3-5').val(),"policy2-3-6":$('#policy2-3-6').val(),"policy2-3-7":$('#policy2-3-7').val()}}];
				var str = JSON.stringify(para); 
				$.ajax({
				    url: './policy/profile/testModel',
					type: "GET",
					data: {policiesStr:str},
					success: function (data) {
						alert("test success");
					}, 
					error: function(data) {
						alert("test error");
					}
			    });
			},
			next: function(){	
				var id = $(this).parent().parent().attr("id");
				if(id=="CreateProfile")
				{
					if($('#proname').val()==""){
						
						$( "#namecheck1" ).removeClass( "hide" );
						$( "#inputok" ).addClass("hide" );
						$( "#inputerror" ).removeClass("hide" );
						return;
					}
					 $(".di-title").html("<p>Profile - "+$('#proname').val()+"</p>");
					$( "#hided-savebutton" ).removeClass("hide" );
					$("#Tab #General").css({ color: "#0f0"});
					// $("#ui-id-11").replaceWith("Profile-"+$('#proname').val());
					$( "#namecheck1" ).addClass("hide" );
					$( "#commonTab" ).removeClass("hide" );
					$( "#pcoipTab" ).removeClass("hide" );
					$( "#usbTab" ).removeClass("hide" );
					$('#Tab a[href="#Tabs-common"]').tab('show');  
					$('#subTab1 a[href="#policy1-1"]').tab('show');
					$( "#inputerror" ).addClass("hide" );
					$( "#inputok" ).removeClass("hide" );
					
				}
				else if(id=="Tabs-common")
				{
					$('#Tab a[href="#Tabs-pcoip"]').tab('show');  
					$('#subTab2 a[href="#policy2-1"]').tab('show');
					
				}
				else if(id=="Tabs-pcoip")
				{
					$('#Tab a[href="#Tabs-usb"]').tab('show');  
					$('#subTab3 a[href="#policy3-1"]').tab('show');
				}
			},
			back: function(){	
				var id = $(this).parent().parent().attr("id");
				if(id=="Tabs-usb")
				{
					$('#Tab a[href="#Tabs-pcoip"]').tab('show');  
					$('#subTab2 a[href="#policy2-1"]').tab('show');
					
				}
				else if(id=="Tabs-pcoip")
				{
					$('#Tab a[href="#Tabs-common"]').tab('show');  
					$('#subTab1 a[href="#policy1-1"]').tab('show');
				}
				else if(id=="Tabs-common")
				{
					$('#Tab a[href="#CreateProfile"]').tab('show'); 
				}
				
			},

			disableContent: function(){
			
				var type = $('input[name="SSL_connections_enabled_2-3"]:checked').val();
				
				if( type=="0" ){
					$("#policy2-3_content").children().children().attr("disabled",false);	
				}
				else if( type=="1")
				{
					$("#policy2-3_content").children().children().attr("disabled",true);
			    }
				else{
					
					$("#policy2-3_content").children().children().attr("disabled",true);
				}
				
			},
			cancel: function(){
				 $("#ConfigureProfile").dialog("close");
				 
			},
			save:function(){
				document.getElementById("over").style.display = "block";
		        document.getElementById("layout").style.display = "block";
	          
				var para = [{"PolicyId":"SSL","enabled":$('#SSL_connections_enabled_2-3').val(),"items":{"policy2-3-1":$('#policy2-3-1').val(),"policy2-3-2":$('#policy2-3-2').val(),"policy2-3-3":$('#policy2-3-3').val(),"policy2-3-4":$('#policy2-3-4').val(),"policy2-3-5":$('#policy2-3-5').val(),"policy2-3-6":$('#policy2-3-6').val(),"policy2-3-7":$('#policy2-3-7').val()}}];
				var str = JSON.stringify(para); 
				if(editOrInit==0){
					
					$.ajax({
					    url: './policy/profile/updateProfile',
						type: "GET",
						data: {profileName:$('#proname').val(), description:$('#desc').val(), policiesStr:str},
						success: function (data) {
							if(data){
								$('#profilelist').jtable('updateRecord', {
								    record: {
										name:$('#proname').val(),
										description: $('#desc').val(),
								    }
								});
								
								$('#profilelist').jtable('load');
							}else{
								//alert("name already exit!");
								//$('#profilelist').jtable('reload');
							}
							document.getElementById("over").style.display = "none";
				            document.getElementById("layout").style.display = "none";
						}, 
						error: function(data) {
							//alert("update error");
						}
				    });
					$("#ConfigureProfile").dialog("close");
				}else if(editOrInit==1){
					$.ajax({
					    url: './policy/profile/editProfile',
						type: "GET",
						data: {profileName:$('#proname').val(), description:$('#desc').val(), policiesStr:str},
						success: function (data) {
							if(data){
				    			
							}else{
								//alert($('#proname').val()+"not exit!");
								$('#profilelist').jtable('reload');
							}
						}, 
						error: function(data) {
							//alert("edit error");
						}
				    });
				    document.getElementById("over").style.display = "none";
		            document.getElementById("layout").style.display = "none";
		           
				  //$('#profilelist').jtable('reload');
					$("#ConfigureProfile").dialog("close");
				}
				 
    			
    				
			},
			
			init: function(){
				$("#openassign").click(ToolBox.Policy.openAssignconfirm);
				$(".next").click(ToolBox.Policy.next);
				$(".cancel").click(ToolBox.Policy.cancel);
				$(".save").click(ToolBox.Policy.save);
				$(".Back").click(ToolBox.Policy.back);
				$("#SSL_connections_enabled_2-3").change(ToolBox.Policy.disableContent);
				 $('input:radio').click(
			             function(){
			            	 ToolBox.Policy.disableContent();   
			             }
			         );  
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
var profileName,poolName,flag,xx;
function editprofile(pName) {	
	editOrInit=1;
	$( "#namecheck1" ).addClass( "hide" );
	$( "#inputok" ).addClass("hide" );
	$( "#inputerror" ).addClass("hide" );
    $.ajax({
	    url: './policy/profile/getprofile',
		type: "GET",
		data: {profileName:pName},
		success: function (data) {
			$("#proname").val(data.profileName);
			$("#proname").attr("readonly",true);	
			$("#desc").val(data.descrpiton);
			
			var policies = data.policies;

			policies.forEach(function(e){  
			    if(e.PolicyId=="SSL"){
			    	//$("#SSL_connections_enabled").val( String(e.enabled) );
			    	//alert("e.enabled="+e.enabled);
			    	if(e.enabled==0){
			    		$("#SSL2-3_2").prop('checked', 'checked');
			    	}else if(e.enabled==1){
			    		$("#SSL2-3_3").prop('checked', 'checked');
			    	}else if(e.enabled==2){
			    		$("#SSL2-3_1").prop('checked', 'checked');
			    	}
			    	var items = e.items;
					$("#policy2-3-1").val(items["policy2-3-1"]);
					$("#policy2-3-2").val(items["policy2-3-2"]);
					$("#policy2-3-3").val(items["policy2-3-3"]);
					$("#policy2-3-4").val(items["policy2-3-4"]);
					$("#policy2-3-5").val(items["policy2-3-5"]);
					$("#policy2-3-6").val(items["policy2-3-6"]);
					$("#policy2-3-7").val(items["policy2-3-7"]);
			    }
			});

			$('#Tab a:first').tab('show');
			$("#ConfigureProfile").dialog({
			      autoOpen: false,
			      height: "750",
			      width: "750",
			      modal: true, 
			      hide: {
			        effect: "explode",
			        duration: 1000
			      }
			});
			$('[name="SSL_connections_enabled_1-1"][id="SSL1-1_1"]').prop("checked", true);
			$('[name="SSL_connections_enabled_1-2"][id="SSL1-2_1"]').prop("checked", true);
			$('[name="SSL_connections_enabled_1-3"][id="SSL1-3_1"]').prop("checked", true);
			$('[name="SSL_connections_enabled_2-1"][id="SSL2-1_1"]').prop("checked", true);
			$('[name="SSL_connections_enabled_2-2"][id="SSL2-2_1"]').prop("checked", true);
	
			$('[name="SSL_connections_enabled_3-1"][id="SSL3-1_1"]').prop("checked", true);
			$('[name="SSL_connections_enabled_3-2"][id="SSL3-2_1"]').prop("checked", true);
			$('#ConfigureProfile').dialog('widget').attr('id', 'configuredialog');
			$(".di-title").html("<p>Profile - "+$('#proname').val()+"</p>");
			$("#ConfigureProfile").dialog("open");
		}, 
		error: function(data) {
			//alert("error");
		}
    });	
 }

function deleteprofile(proName){
	 profileName=proName;
	$("#delete-profile-confirm").dialog("open");
	
}
function deletePoolprofile(proName,poName,data){
	 profileName=proName;
	 poolName=poName;
	 xx=data;
	$("#delete-poolprofile-confirm").dialog("open");
	
}
function priorityConfirm(poName,data)
{
	 poolName=poName;
	 xx=data;
	$("#priority-confirm").dialog("open");
}
var fixHelper = function(e, ui){
	ui.children().each(function(){
		$(this).width($(this).width());
	});
	return ui;
}


function initdialog(){
	editOrInit=0;
	$( "#namecheck1" ).addClass( "hide" );
	$( "#inputok" ).addClass("hide" );
	$( "#inputerror" ).addClass("hide" );
	$( "#commonTab" ).addClass("hide" );
	$( "#pcoipTab" ).addClass("hide" );
	$( "#usbTab" ).addClass("hide" );
	 $('#Tab a:first').tab('show');
	 $("#ConfigureProfile").dialog({
	      autoOpen: false,
	      height: "750",
	      width: "750",
	      modal: true, 
	      hide: {
	        effect: "explode",
	        duration: 1000
	      }
	   });
	 $('#ConfigureProfile').dialog('widget').attr('id', 'configuredialog');
	$('[name="SSL_connections_enabled_1-1"][id="SSL1-1_1"]').prop("checked", true);
	$('[name="SSL_connections_enabled_1-2"][id="SSL1-2_1"]').prop("checked", true);
	$('[name="SSL_connections_enabled_1-3"][id="SSL1-3_1"]').prop("checked", true);
	$('[name="SSL_connections_enabled_2-1"][id="SSL2-1_1"]').prop("checked", true);
	$('[name="SSL_connections_enabled_2-2"][id="SSL2-2_1"]').prop("checked", true);
	$('[name="SSL_connections_enabled_2-3"][id="SSL2-3_1"]').prop("checked", true);
	$('[name="SSL_connections_enabled_3-1"][id="SSL3-1_1"]').prop("checked", true);
	$('[name="SSL_connections_enabled_3-2"][id="SSL3-2_1"]').prop("checked", true);
	
	$( "#hided-savebutton" ).addClass("hide" );
	$("#proname").val("");
	$("#proname").attr("readonly",false);
	$("#desc").val("");
//	$("#SSL_connections_enabled_2-3").val("1");
	$("#policy2-3_content").children().children().attr("disabled",true);
	$("#policy2-3-1").val("0");
	$("#policy2-3-2").val("0");
	$("#policy2-3-3").val("MY");
	$("#policy2-3-4").val("1024");
	$("#policy2-3-5").val("ROOT");
	$("#policy2-3-6").val("0");
	$("#policy2-3-7").val("0");
	 $(".di-title").html("<p>Profile</p>");

}

$(function() { 
	 $( "#assign-confirm" ).dialog({
		  autoOpen: false,
	      resizable:true,
	      height:220,
	      width:360,
	      modal: true,
	      buttons: {
	        "Sure": function() {
	          ToolBox.Policy.openassign();
	          $( this ).dialog( "close" );
	        },
	        Cancel: function() {
	          $( this ).dialog( "close" );
	        }
	      }
	    });
	 $( "#delete-profile-confirm" ).dialog({
		  autoOpen: false,
	      resizable:true,
	      height:220,
	      width:360,
	      modal: true,
	      buttons: {
	        "Sure": function() {
	        	 $.ajax({
	     		    url: './policy/profile/delete',
	     			type: "GET",
	     			data: {profileName:profileName},
	     			success: function (data) {
	     				$( "#delete-profile-success" ).dialog("open");
	     				$('#profilelist').jtable('reload');
	     			}, 
	     			error: function(data) {
	     				
	     			}
	     	    });	
	          $( this ).dialog( "close" );
	        },
	        Cancel: function() {
	          $( this ).dialog( "close" );
	        }
	      }
	    });
	 $( "#delete-poolprofile-confirm" ).dialog({
		  autoOpen: false,
	      resizable:true,
	      height:220,
	      width:360,
	      modal: true,
	      buttons: {
	        "Sure": function() {
				  $.ajax({
					    url: './policy/profile/deletepoolprofile',
						type: "GET",
						data: {poolName:poolName, profileName: profileName},
						success: function (data) {
							  $( "#delete-profile-success" ).dialog("open");

							 xx.childTable.jtable('load');
						}, 
						error: function(data) {
						
						}
				    });
	          $( this ).dialog( "close" );
	        },
	        Cancel: function() {
	        	
	          $( this ).dialog( "close" );
	        }
	      }
	    });
	 
	  $('#subTab1 a').click(function (e) { 
	       e.preventDefault();//阻止a链接的跳转行为 
	       $(this).tab('show');//显示当前选中的链接及关联的content 
	     });
	   $('#subTab2 a').click(function (e) { 
	       e.preventDefault();//阻止a链接的跳转行为 
	       $(this).tab('show');//显示当前选中的链接及关联的content 
	     }) ;
   
	   $('#subTab3 a').click(function (e) { 
	       e.preventDefault();//阻止a链接的跳转行为 
	       $(this).tab('show');//显示当前选中的链接及关联的content 
	     });
	      $('#Tab a[href="#Tabs-common"]').click(function (e) { 
		   $('#Tab a[href="#Tabs-common"]').tab('show');  
			$('#subTab1 a[href="#policy1-1"]').tab('show');
	     });
	   $('#Tab a[href="#Tabs-pcoip"]').click(function (e) { 
		   $('#Tab a[href="#Tabs-pcoip"]').tab('show');  
			$('#subTab2 a[href="#policy2-1"]').tab('show');
	     });
	   $('#Tab a[href="#Tabs-usb"]').click(function (e) { 
		   $('#Tab a[href="#Tabs-usb"]').tab('show');  
			$('#subTab3 a[href="#policy3-1"]').tab('show');
	     });
	   

	 var name = $("#proname"),     
     autor = $( "#autor" ),
     allFields = $( [] ).add( name ).add( autor );
	 var profileNameOut;
    $("#ConfigureProfile").dialog({
	      autoOpen: false,
	      height: "800",
	      width: "1000",
	      modal: true, 
	      hide: {
	        effect: "explode",
	        duration: 1000
	      }
	    });
    $( "#change-priority-success" ).dialog({
		  autoOpen: false,
	      resizable:true,
	      height:220,
	      width:360,
	      modal: true,
	    buttons: {
	       "OK": function() {
	       	   $( this ).dialog( "close" );
	       },
	      }
	    });
    $( "#assign-success" ).dialog({
		  autoOpen: false,
	      resizable:true,
	      height:220,
	      width:360,
	      modal: true,
	    buttons: {
	       "OK": function() {
	       	   $( this ).dialog( "close" );
	       },
	      }
	    });
    $( "#delete-profile-success" ).dialog({
		  autoOpen: false,
	      resizable:true,
	      height:220,
	      width:360,
	      modal: true,
	    buttons: {
	       "OK": function() {
	       	   $( this ).dialog( "close" );
	       },
	      }
	    });
	 $( "#priority-confirm" ).dialog({
		  autoOpen: false,
	      resizable:true,
	      height:220,
	      width:360,
	      modal: true,
	      buttons: {
		        "Sure": function() {
		        	$.ajax({
		    			url: './policy/assignment/priority',
		    			type: "GET",
		    			data: {poolName:poolName , profilesStr:xx},
		    			success: function (data) {
		    				 $( "#change-priority-success" ).dialog("open");		    			}, 
		    			error: function(data) {
		    				//alert("error "+data);
		    			}
		    		});
		          $( this ).dialog( "close" );
		        },
		        Cancel: function() {
		          $( this ).dialog( "close" );
		        }
		      }
	      
	  
	    });
   
	  $('#profilelist').jtable({
			title: 'Profile',
			toolbar: {
			    items: [{
			    	icon: './img/add.png',
			    	tooltip:'Add a new profile',
			        click: function () {
			        //	$("#ConfigureProfile").dialog("destroy");
			        	initdialog();
			        	$( "#ConfigureProfile" ).dialog( "open" );
			        }
			    }]
			},
			paging: false, //Enable paging
			sorting: false,
			selecting: true, //Enable selecting
			multiselect: true, //Allow multiple selecting
			selectingCheckboxes: true,
			selectOnRowClick: false, 
			actions: {
				listAction: './policy/profile/getnamelist',
			},
			fields: {
				recordId: {
					key: true,
					edit: false,
					list: false
				},		
				name: {
					title: 'Profile name',
					width: '23%',
					type: 'text',
					edit: false
				},
				description: {
					title: 'Description',
					width: '35%',
					type: 'text',
					edit: false
				},
				Action: {
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
			},
			
		});
	  $('#profilelist .jtable ').wrap('<div class="jtable-main-container scroll-content" />');
	  $('#assignmentlist').jtable({
			title: 'Assignment',
			paging: false,
			sorting: false,
			selecting: true, //Enable selecting
			//multiselect: true, //Allow multiple selecting
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
				},	//CHILD TABLE 
	              Exams: {
	                  title: '',
	                  width: '5%',
	                  sorting: false,
	                  edit: false,
	                  create: false,
	                  display: function (data) {//Create an image that will be used to open child table
	                	  var $img = $('<img class="icon1" src="./jtable/themes/jqueryui/plus_16.png" />');//Open child table when user clicks the image
	                	  $img.click(function () {   
	                		
	                		var poolName = data.record.name;
	                    	var i=0;
	                    	var x;
						
							
	                    	i=$('#assignmentlist').jtable('isChildRowOpen', $img.closest('tr'));
	                    	  
	                    	if(i){
	                    		$('#assignmentlist').jtable('closeChildTable', $img.closest('tr'),
	          	                    	  function (data) { 
	                                        data.childTable.jtable('close');
	                                   });
									     $(this).attr("src", "./jtable/themes/jqueryui/plus_16.png" );
									   }
	                    	else
	                    		{
	                    		
								$(this).attr("src", "./jtable/themes/jqueryui/sub_16.png" );
	                          $('#assignmentlist').jtable('openChildTable',
	                                  $img.closest('tr'), 
	                                  {
	                                  actions: {          
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
	                      				  width: '5%',
	                      				    display: function (data) {
	                      				        return '<img  src="./img/subtable.png" style="position:relative; color:green;left:40px"/>';
	                      				    }
	                      				},
	                      				name: {
	                      					width: '73%',
	                      					type: 'text',
	                      					edit: false
	                      				},
	                      				
	                    				Action: {
	                    					width: '20%',
	                    					edit: false,
	                    					display: function(data) {
	                    					  var $bu1= $('<button class="jtable-command-button jtable-delete-command-button" title="delete"></button>');	                     
	                   	                    $bu1.click(function () { 
	                   	                    	deletePoolprofile(data.record.name,poolName,x);
	                   	                 
	                   	                      });
	                   	                      return $bu1;
	                    				  }	                    					
	                    				}	                                    
	                                  }
	                              }, function (data) {
	                            	  x=data;
	                                  data.childTable.jtable('load');
	                                
									     
	                              });	
	                          $(this).parent().parent().next("tr").attr("id", poolName );
	                           
	                              $("#assignmentlist div.jtable-main-container table tbody tr.jtable-child-row table tbody").sortable({
	                            	  	helper: fixHelper,
										axis:"y",
										start:function(e, ui){
											return ui;
											},
											stop:function(e, ui){
											return ui;
										}
							    	}).disableSelection();
	                       
	                    		}
	                      });
	                      //Return image to show on the person row	    
	                      return $img;
	                  }
	              },
				name: {
					title: 'Pool',
					width: '50%',
					type: 'text',
					create: false,
					edit: false
				},
				ou: {
					title: 'OU',
					width: '50%',
					type: 'text',
					create: false,
					edit: false
				},
				Action : {
					width : '20%',
					edit : false,
					display : function(data) {
						var $bu1 = $(' <button  title="Adjust the priority"><i style="color: green" 	class="glyphicon glyphicon-sort-by-attributes-alt"></i></button>');
						$bu1.click(function() {
					
							  var w=$(this).parent().parent().next("tr").find("table");
							  		 var sort_tr= $(w).find("tbody");
							  $(w).find("tr").each(function(){
							  	    y=$(this).children('td').eq(1).html();
   									  $(this).attr('id', y);
  									});	
						
							  var name=$(this).parent().parent().children('td').eq(2).html();
							  var sortedIDs = $(sort_tr).sortable( "toArray" );
								var sortedIDs_str = JSON.stringify(sortedIDs); 
								
								priorityConfirm(name,sortedIDs_str);
								
							
							  
						});
						
						return $bu1;
					}
				}
			}
		}); 
	
	  
	$('#profilelist').jtable('load');
	$('#assignmentlist').jtable('load');

	ToolBox.Policy.init();
	
	
});