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
var loginFlag=true;
if (!ToolBox.Policy || !ToolBox.Policy.init){
	ToolBox.Policy = {
		openAssignconfirm:function(){
			var $profileRows = $('#profilelist').jtable('selectedRows');
			var profileArray = [];	  
			$profileRows.each(function () {
				var record = $(this).data('record');
				var name = record.name;
				profileArray.push(name);
			});
			
			var $poolRows = $('#assignmentlist').jtable('selectedRows');
		  	var poolArray = [];
		  	$poolRows.each(function () {
		  		var record = $(this).data('record');
			    var name = record.name;
			    poolArray.push(name);
		  	});
		
			if(profileArray.length==0 || poolArray.length==0){
				return ;
			}
			
			if(loginFlag==true){
				$( "#assign-confirm" ).dialog( "open" );
			}
		},
		openassign: function(){
			
			document.getElementById("over").style.display = "block";
	        document.getElementById("layout").style.display = "block";

			
			var $profileRows = $('#profilelist').jtable('selectedRows');
			var profileArray = [];	  
			$profileRows.each(function () {
				var record = $(this).data('record');
				var name = record.name;
				profileArray.push(name);
			});
			var profileArrayStr = JSON.stringify(profileArray);
			
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
					document.getElementById("over").style.display = "none";
		            document.getElementById("layout").style.display = "none";
		  			if(data){
		  				$('#assignmentlist').jtable('reload');
		  				$poolRows = $('#assignmentlist').jtable('selectedRows');
		  				var $chkBox = $poolRows.find('td.jtable-selecting-column input').attr('checked', true); 
		  				$chkBox.attr('checked', false);
		  				$('#assignmentlist').jtable('reload');
		  				
		  				$('#profilelist').jtable('reload');
		  				$profileRows = $('#profilelist').jtable('selectedRows');
		  				var $chkPro = $profileRows.find('td.jtable-selecting-column input').attr('checked', true); 
		  				$chkPro.attr('checked', false);
		  				$('#profilelist').jtable('reload');
		  				
		  				loadingDiv.removeClass("loadingdiv");
		  				$( "#assign-success" ).dialog("open");
		  			}else{
		  				$( "#assign-error" ).dialog("open");
		  			}
		  		}, 
		  		error: function(data) {
					document.getElementById("over").style.display = "none";
		            document.getElementById("layout").style.display = "none";
		  		}
		  	});	
		},
		next: function(){	
			var id = $(this).parent().parent().attr("id");
			if(id=="CreateProfile")
			{
				if($( '#proname' ).val()==""){
					$( "#namecheck2" ).addClass("hide" );
					$( "#namecheck1" ).removeClass( "hide" );
					$( "#inputok" ).addClass("hide" );
					$( "#inputerror" ).removeClass("hide" );
					return;
				}
				
				var nameExist = false;
				if(editOrInit==0){	//init
					//var nameExist = false;
					$.ajaxSettings.async = false;	//同步
					$.ajax({
						url: './policy/profile/checkProfileName',
						type: "GET",
						data: {profileName:$('#proname').val()},
						success: function(data){
							if(!data){
								nameExist = true;
								$( "#namecheck1" ).addClass("hide" );
								$( "#namecheck2" ).removeClass( "hide" );
								$( "#inputok" ).addClass("hide" );
								$( "#inputerror" ).removeClass("hide" );
								return;
							}
						},
						error: function(data){
							
						}
					});
					$.ajaxSettings.async = true;
				}

				if(!nameExist){
					$( ".di-title" ).html("<p>Profile - "+$('#proname').val()+"</p>");
					$( "#hided-savebutton" ).removeClass("hide" );
					$( "#Tab #General" ).css({ color: "#0f0"});
					// $("#ui-id-11").replaceWith("Profile-"+$('#proname').val());
					$( "#namecheck1" ).addClass("hide" );
					$( "#namecheck2" ).addClass("hide" );
					$( "#commonTab" ).removeClass("hide" );
					$( "#pcoipTab" ).removeClass("hide" );
					$( "#usbTab" ).removeClass("hide" );
					$( '#Tab a[href="#Tabs-common"]' ).tab('show');  
					$( "#subTab1" ).find("li a").first().tab('show');
					$( "#inputerror" ).addClass("hide" );
					$( "#inputok" ).removeClass("hide" );
				}
			}
			else if(id=="Tabs-common")
			{
				$('#Tab a[href="#Tabs-pcoip"]').tab('show');  
				$("#subTab2").find("li a").first().tab('show');
			}
			else if(id=="Tabs-pcoip")
			{
				$('#Tab a[href="#Tabs-usb"]').tab('show');  
				$("#subTab3").find("li a").first().tab('show');
			}
		},
		back: function(){	
			var id = $(this).parent().parent().attr("id");
			if(id=="Tabs-usb")
			{
				$('#Tab a[href="#Tabs-pcoip"]').tab('show');  
				$("#subTab2").find("li a").first().tab('show');
			}
			else if(id=="Tabs-pcoip")
			{
				$('#Tab a[href="#Tabs-common"]').tab('show');  
				$("#subTab1").find("li a").first().tab('show');
			}
			else if(id=="Tabs-common")
			{
				$('#Tab a[href="#CreateProfile"]').tab('show'); 
			}
			
		},
		disableContent: function(){
			var id = $(this).attr("id");
			var radio_value = $(this).attr("value");
			var policyContent = $(this).parent().parent().next().next().find(".policy_content");
			
			if( radio_value=="0" ){
				policyContent.children().children().attr("disabled",false);	
			}
			else if( radio_value=="1"){
				policyContent.children().children().attr("disabled",true);
		    }
			else{
				policyContent.children().children().attr("disabled",true);
			}
		},
		cancel: function(){
			 $("#ConfigureProfile").dialog("close");
		},
		save:function(){
			document.getElementById("over").style.display = "block";
	        document.getElementById("layout").style.display = "block";

	        var paraStr = getPolicyParam();
	       
			if(editOrInit==0){	
				$.ajax({
					async: true,
				    url: './policy/profile/updateProfile',
					type: "POST",
					data: {profileName:$('#proname').val(), description:$('#desc').val(), policiesStr:paraStr},
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
							$('#create-profile-error').dialog("open");
							$('#profilelist').jtable('load');
						}
						document.getElementById("over").style.display = "none";
			            document.getElementById("layout").style.display = "none";
					}, 
					error: function(data) {
						alert("update error");
						document.getElementById("over").style.display = "none";
			            document.getElementById("layout").style.display = "none";
					}
			    });
				$("#ConfigureProfile").dialog("close");
				
			}else if(editOrInit==1){
				$.ajax({
					async: true,
				    url: './policy/profile/editProfile',
					type: "POST",
					data: {profileName:$('#proname').val(), description:$('#desc').val(), policiesStr:paraStr},
					success: function (data) {
						if(!data){
							//alert ("The profile are't exists");
						}
						$('#profilelist').jtable('reload');
					    document.getElementById("over").style.display = "none";
			            document.getElementById("layout").style.display = "none";
					}, 
					error: function(data) {
						//alert("edit error");
						$('#profilelist').jtable('reload');
					    document.getElementById("over").style.display = "none";
			            document.getElementById("layout").style.display = "none";
					}
			    });
				$("#ConfigureProfile").dialog("close");
			}	
		},
		init: function(){	
			$.ajax({
				url: './policy/login/check',
				type: "GET",
				success: function(data){
					if(!data){
						$("#loginAD").dialog("open");
					}else{
						$('#profilelist').jtable('load');
						$('#assignmentlist').jtable('load');
					}
				},
				error: function(data){
					alert("login check error");
				}
			});
			
			$("#openassign").click(ToolBox.Policy.openAssignconfirm);
			$(".next").click(ToolBox.Policy.next);
			$(".cancel").click(ToolBox.Policy.cancel);
			$(".save").click(ToolBox.Policy.save);
			$(".Back").click(ToolBox.Policy.back);
			$('input:radio').click(ToolBox.Policy.disableContent);
			$(".grid_add").bind("click",addRow);
			
			$("input:checkbox").click(function(){	//checkbox value反转
				if(this.value == "0"){
					this.value = "1";
				}else if(this.value == "1"){
					this.value = "0";
				}
			});

		}
	};
}

function addRow(){
	var button_id = $(this).attr("id");
	var indexAdd = button_id.indexOf("-Add");
	var base = button_id.substring(0,indexAdd);
	var gridId = base + "-Grid";
	var grid_len = $("#"+gridId).find("tr").length;
	var last_row = $("#" + gridId + " tr:eq("+(grid_len-1)+") td:eq(1)").find("input");
	var last_row_val = last_row.val().replace(/(^\s+)|(\s+$)/g,"").replace(/\s/g,"");
	
	if(last_row_val==''){
		return;
	}

	var newTr = $("#"+gridId).get(0).insertRow();
	var newTd0 = newTr.insertCell();
	var newTd1 = newTr.insertCell();
	newTd1.innerHTML="<input type='text'></input>";
	//newTd1.addEventListener("click", addRow(), false);
}

function setGridVal(policyId){
	var gridId = policyId+"-Grid";
	if(document.getElementById(gridId)){
		var len = $("#"+gridId).find("tr").length;
		var val_set;
		if(len>=2){
			val_set = $("#"+gridId+" tr:eq(1) td:eq(1)").find("input").val();
			for(var i=2; i<len; i++){
				var v = $("#" + gridId + " tr:eq("+(i)+") td:eq(1)").find("input").val();
				val_set += ";"+v;
			}
		}
		$("#"+policyId+"-Sub-1").val(val_set);
	}
}

function getPolicyParam(){
	var policy_param = new Array();
	var dataroot = location.href.substring(0,location.href.lastIndexOf('/')) + "/resources/polFiles/PolicyConfig.json";
	$.ajaxSettings.async = false;	//同步
	$.getJSON(dataroot, function(data){
		var policies = data.policies;
		for(var policy_entry_id in policies){
			var policy_obj = new Object();
			policy_obj.policyId = policy_entry_id;
			var policy_radio = "input[name=" + policy_entry_id + "-Radio]:checked";
			policy_obj.enabled = $(policy_radio).val();
			if( policy_obj.enabled != "0" && policy_obj.enabled != "1" ){
				continue;
			}
			var policy_items_map = new Object();
			var policy_items = policies[policy_entry_id].items;
			setGridVal(policy_entry_id);
			for(var sub_item_id in policy_items){
				var sub_val = $("#" + sub_item_id).val();
				policy_items_map[sub_item_id]=sub_val;
			}
			
			policy_obj.items = policy_items_map;
			policy_param.push(policy_obj);
		}
	});
	$.ajaxSettings.async = true;
	var policy_param_str = JSON.stringify(policy_param);
	return policy_param_str;
}

function checkLength( o, n, min, max ) {
    if ( o.val().length > max || o.val().length < min ) {
	  return false;
	} else {
	  return true;
	}
 }

var profileName,poolName,flag,priorityList;
function editprofile(pName) {
	editOrInit=1;	//edit == 1
	$( "#namecheck1" ).addClass( "hide" );
	$( "#inputok" ).addClass("hide" );
	$( "#inputerror" ).addClass("hide" );
    $.ajax({
	    url: './policy/profile/getprofile',
		type: "POST",
		data: {profileName:pName},
		success: function (data) {
			if(!data){	// can't use "data==null"
				$('#cannot-edit').dialog("open");
				return;
			}
			$("#proname").val(data.profileName);
			$("#proname").attr("readonly",true);	
			$("#desc").val(data.descrpiton);
			var policies = data.policies;
			autoInitEditDialog(policies);
			$('#Tab a:first').tab('show');
			$("#ConfigureProfile").dialog({
			      autoOpen: false,
			      height: "750",
			      width: "1200",
			      modal: true, 
			      hide: {
			    	  effect: "explode",
			    	  duration: 1000
			      }
			});
			$('#ConfigureProfile').dialog('widget').attr('id', 'configuredialog');
			$(".di-title").html("<p>Profile - "+$('#proname').val()+"</p>");
			$("#ConfigureProfile").dialog("open");
			
		}, 
		error: function(data) {
			//alert("error");
		}
    });	
}

function autoInitEditDialog(policies){	//根据后端传递的数据， 初始化 Edit菜单
	//$("input[value='2']:radio").prop('checked',true);  //初始化所有radio
	autoInitNewDialog();
	policies.forEach(function(e){
		var policyEntryId = e.policyId;
		//radio、checkbox元素值比较混乱，enable、on一般是1，disable、off一般是0
		if(e.enabled==0){	//enabled
    		$("#" + policyEntryId + "-enabled").prop('checked', true);
    		$("#" + policyEntryId + "-Content").children().children().prop("disabled",false);
    	}else if(e.enabled==1){		//disabled
    		$("#" + policyEntryId + "-disabled").prop('checked', true);
    		$("#" + policyEntryId + "-Content").children().children().prop("disabled",true);
    	}else if(e.enabled==2){
    		$("#" + policyEntryId + "-not").prop('checked', true);
    		$("#" + policyEntryId + "-Content").children().children().prop("disabled",true);
    	}

    	var subItems = e.items; //得到一条policy的子项
    	
    	// set grid cmds
		if( document.getElementById(policyEntryId+"-Grid") ){	
			var gridId = policyEntryId+"-Grid";
			var gridCmdsStr = subItems[ policyEntryId+"-Sub-1" ];
			var gridCmds = gridCmdsStr.split(";");
			for(var cmdNum in gridCmds){
				$("#" + gridId + " tr:eq("+(Number(cmdNum)+1)+") td:eq(1)").find("input").val( gridCmds[cmdNum] );
				var newTr = $("#"+gridId).get(0).insertRow();
				var newTd0 = newTr.insertCell();
				var newTd1 = newTr.insertCell();
				newTd1.innerHTML="<input type='text'></input>";
			}
		}
    	
		for(var sub_item_id in subItems){
			$( "#" + sub_item_id ).val(subItems[sub_item_id]);
			//checkbox
			if( $( "#" + sub_item_id ).attr("class")=="check-enable" ){	// check -> enable 1代表check
				if( subItems[sub_item_id]=="0" ){
					$("#"+sub_item_id).prop("checked",false);							// $("#"+sub_id).attr("checked",'false');
				}else if( subItems[sub_item_id]=="1" ){
					$("#"+sub_item_id).prop("checked",true);							// $("#"+sub_id).attr("checked",'true');
				}
			}else if( $( "#" + sub_item_id ).attr("class")=="check-disable" ){	// check -> disable 0代表check
				if( subItems[sub_item_id]=="1" ){
					$("#"+sub_item_id).prop("checked",false);							
				}else if( subItems[sub_item_id]=="0" ){
					$("#"+sub_item_id).prop("checked",true);							
				}
			}			
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
	priorityList=data;
	$("#delete-poolprofile-confirm").dialog("open");
}

function priorityConfirm(poName,data)
{
	poolName=poName;
	priorityList=data;
	$("#priority-confirm").dialog("open");
}

var fixHelper = function(e, ui){
	ui.children().each(function(){
		$(this).width($(this).width());
	});
	return ui;
}

function initdialog(){
	editOrInit=0;	// init == 0
	$( "#namecheck1" ).addClass("hide");
	$( "#namecheck2" ).addClass("hide");
	$( "#inputok" ).addClass("hide");
	$( "#inputerror" ).addClass("hide");
	$( "#commonTab" ).addClass("hide");
	$( "#pcoipTab" ).addClass("hide");
	$( "#usbTab" ).addClass("hide");
	$( '#Tab a:first' ).tab('show');
	$( "#ConfigureProfile" ).dialog({
		autoOpen: false,
	    height: "750",
	    width: "1200",
	    modal: true, 
	    hide: {
	    	effect: "explode",
	        duration: 1000
	    }
	});
	$( '#ConfigureProfile' ).dialog('widget').attr('id', 'configuredialog');
	$( "#hided-savebutton" ).addClass("hide" );
	$("#proname").val("");
	$("#proname").attr("readonly",false);
	$("#desc").val("");
	autoInitNewDialog();
	$(".di-title").html("<p>Profile</p>");
}

function autoInitNewDialog(){
	var policy_param = new Array();
	var dataroot = location.href.substring(0,location.href.lastIndexOf('/')) + "/resources/polFiles/PolicyConfig.json";
	$.ajaxSettings.async = false;	//同步
	$.getJSON(dataroot, function(data){
		var policies = data.policies;
		for(var policy_id in policies){
			$("#" + policy_id + "-not").prop("checked",true);
			$("#" + policy_id + "-Content").children().children().attr("disabled",true);
			var policy = policies[policy_id];
			if(policy.items){
				var policy_items = policy.items;
				for(var item_entry_id in policy_items){
					var item_entry = policy_items[item_entry_id];
					if(item_entry.elementType == "ELE_CHECKBOX"){
						$("#" + item_entry_id).prop("checked",false);
					}
					else if(item_entry.elementType == "ELE_CHECKBOX_V"){
						$("#" + item_entry_id).prop("checked",false);
					}else{
						$("#" + item_entry_id).val(item_entry.defaultData);
					}
					//Send log messages to the log file 默认是选中的，不考虑
				}
			}
			if( document.getElementById(policy_id+"-Grid") ){
				var gridId = policy_id+"-Grid";;
				var len = $("#"+gridId).find("tr").length;
				if( len>2 ){ //删除多余的
					for(var i=2;i<len;i++){
						var v = $("#" + gridId + " tr:eq("+(1)+")").remove();
					}
				}
				val_set = $("#"+gridId+" tr:eq(1) td:eq(1)").find("input").val("");
			}
			
		}
	});
	$.ajaxSettings.async = true;	//异步
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
	    		document.getElementById("over").style.display = "block";
	    		document.getElementById("layout").style.display = "block";
	    		$.ajax({
	    			url: './policy/profile/delete',
	     			type: "GET",
	     			data: {profileName:profileName},
	     			success: function (data) {
	     				document.getElementById("over").style.display = "none";
				        document.getElementById("layout").style.display = "none";
	     				if(!data){	// can't use "data==null"
	     					$('#cannot-delete').dialog("open");
	     					return;
	     				}
	     				
	     				if(data){
		     				$( "#delete-profile-success" ).dialog("open");
	     				}else{
	     					$( "#delete-profile-error" ).dialog("open");
	     				}
	     				$('#profilelist').jtable('reload');
	     			}, 
	     			error: function(data) {
	     				document.getElementById("over").style.display = "none";
				        document.getElementById("layout").style.display = "none";
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
	        	document.getElementById("over").style.display = "block";
			    document.getElementById("layout").style.display = "block";
	        	
			    $.ajax({
			    	url: './policy/profile/deletepoolprofile',
				    type: "GET",
				    data: {poolName:poolName, profileName: profileName},
				    success: function (data) {
				    	document.getElementById("over").style.display = "none";
				    	document.getElementById("layout").style.display = "none";
				    	$( "#delete-profile-success" ).dialog("open");
						priorityList.childTable.jtable('load');
					}, 
					error: function(data) {
						document.getElementById("over").style.display = "none";
		    			document.getElementById("layout").style.display = "none";
					}
				});
			    $( this ).dialog( "close" );
	        },
	        Cancel: function() {	
	        	$( this ).dialog( "close" );
	        }
	    }
	});
	
    $( "#loginAD" ).dialog({
		  autoOpen: false,
	      resizable:true,
	      height:300,
	      width:580,
	      modal: true,
	      buttons: {
		       "Confirm": function() {
		    	   var pw = $('#ad_password').val();
		    	   var cname = $('#ad_computerName').val();
		    	   pw = $.trim(pw); 
		    	   cname = $.trim(cname);
		    	   if (pw == '' || cname == '') {
		    		   return false;
				   };
					
		    	   document.getElementById("over").style.display = "block";
			       document.getElementById("layout").style.display = "block";
		    	   $.ajax({
		    		   url: './policy/login',
		    		   type: "POST",
		    		   data: {user: $('#ad_username').val(), pass: $('#ad_password').val(), computerName: $('#ad_computerName').val()},
		    		   success: function (data) {
		    			   document.getElementById("over").style.display = "none";
		    			   document.getElementById("layout").style.display = "none";;
		    			   if(data){
		    				   loginFlag=true;
		    				   $("#loginAD").dialog( "close" );
		    				   $('#profilelist').jtable('load');
		    				   $('#assignmentlist').jtable('load');
		    				   $( "#passworderror" ).addClass("hide" );
							   $( "#computernameerror" ).addClass("hide" );
		    			   }else{
		    				   loginFlag=false;
		    				   $('#ad_username').val("administrator");
		    				   $('#ad_password').val("");
		    				   $('#ad_computerName').val("");
		    				   $( "#passworderror" ).removeClass("hide" );
							   $( "#computernameerror" ).removeClass("hide" );
		    			   }
		    		   }, 
		    		   error: function(data) {
		    			   loginFlag=false;
		    			   document.getElementById("over").style.display = "none";
		    			   document.getElementById("layout").style.display = "none";
		    		   }
		    	   });	
		       },
		       "Cancel": function() {
		    	   loginFlag=false;
		       	   $( this ).dialog( "close" );
		       },
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
		$("#subTab1").find("li a").first().tab('show');
	});
	   
	$('#Tab a[href="#Tabs-pcoip"]').click(function (e) { 
		$('#Tab a[href="#Tabs-pcoip"]').tab('show');  
		$("#subTab2").find("li a").first().tab('show');
	});
	   
	$('#Tab a[href="#Tabs-usb"]').click(function (e) { 
		$('#Tab a[href="#Tabs-usb"]').tab('show');  
		$("#subTab3").find("li a").first().tab('show');
	});
	   
	var name = $("#proname"),     
	autor = $( "#autor" ),
	allFields = $( [] ).add( name ).add( autor );
	var profileNameOut;
    
	$("#ConfigureProfile").dialog({
		autoOpen: false,
	    height: "750",
	    width: "1200",
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
	
	$( "#create-profile-error" ).dialog({
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
	
	$( "#delete-profile-error" ).dialog({
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
	
	$( "#assign-error" ).dialog({
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
	
	$( "#cannot-edit" ).dialog({
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
	
	$( "#cannot-delete" ).dialog({
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
	    		document.getElementById("over").style.display = "block";
	    		document.getElementById("layout").style.display = "block";
	    		$.ajax({
	    			url: './policy/assignment/priority',
	    			type: "GET",
	    			data: {poolName:poolName , profilesStr:priorityList},
	    			success: function (data) {
	    				document.getElementById("over").style.display = "none";
				        document.getElementById("layout").style.display = "none";
	    				if(data){
					        $( "#change-priority-success" ).dialog("open");		  
	    				}		
				    }, 
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
					if(loginFlag==true){
						initdialog();
						$( "#ConfigureProfile" ).dialog( "open" );
					}
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
		recordsLoaded: function (event, data) {
			for (var i in data.records) {
				if (data.records[i].type == "IN_AD") {
					$('#profilelist').find(".jtable tbody tr:eq(" + i + ")").css("background", "#FAF0E6"); //F5F5DC  //FAF0E6  //FFF8DC
				}
			}
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
				edit: false,
			},
			description: {
				title: 'Description',
				width: '35%',
				type: 'text',
				edit: false,
			},
			type: {
				edit: false,
				list: false
			},
			Action: {
				width: '20%',
				edit: false,
				display: function(data) {
					if(data.record.type=="IN_LDAP"){
						return '&nbsp&nbsp&nbsp<button class="jtable-command-button jtable-edit-command-button" onclick="editprofile(\''
						+ data.record.name
						+ '\')" title="edit"></button>&nbsp&nbsp&nbsp&nbsp&nbsp<button class="jtable-command-button jtable-delete-command-button" onclick="deleteprofile(\''
						+ data.record.name
						+ '\')" title="delete"></button>';
					}
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
                    	else{
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
                    return $img;
                }
            },
			name: {
				title: 'Pool',
				width: '50%',
				type: 'text',
				create: false,
				edit: false,
/*				display : function(data){
					var str_ou = data.record.ou; 
					var ou_Prefix = "OU=";
					if(str_ou==null){
						return '<font color="737373"><b>'+data.record.name+'</b></font>';
					}else if(str_ou.substr(0,ou_Prefix.length)==ou_Prefix){
						return '<font color="63B8FF"><b>'+data.record.name+'</b></font>';
					}else {
						return '<font color="737373"><b>'+data.record.name+'</b></font>';
					} 
				}*/
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
					var $bu1 = $(' <button  title="Adjust the priority"><i style="color: green" class="glyphicon glyphicon-sort-by-attributes-alt"></i></button>');
					$bu1.click(function() {
						var w=$(this).parent().parent().next("tr").find("table");
						var sort_tr= $(w).find("tbody");
						$(w).find("tr").each(function(){
							y=$(this).children('td').eq(1).html();
							$(this).attr('id', y);
						});	
						//var name=$(this).parent().parent().children('td').eq(2).html();
						var sortedIDs = $(sort_tr).sortable( "toArray" );
						var sortedIDs_str = JSON.stringify(sortedIDs); 	
						priorityConfirm(data.record.name,sortedIDs_str);				  
					});
					return $bu1;
				}
			}
		}
	}); 
	  

	
	ToolBox.Policy.init();
});