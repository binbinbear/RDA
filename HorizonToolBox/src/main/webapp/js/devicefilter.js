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

if (!ToolBox.Devicefilter) {
	function policyfilter( type, value, grep){
		if (!type){
			type="IP_Address"
		}
		if (!value){
			value = "";
		}
		if (!grep){
			grep = "MATCHES";
		}
		this.type=type;
		this.grep=grep;
		this.reg=value;
	}
	
	function devicefilterCtrl ($scope, $http){
		
		// XU YUE MODIFIED ON 20160125
		$scope.policytabledata = [];
		$scope.isBlack = "black";
		// MODIFICATION END
		
		$scope.dapolicies = "";
		$scope.reloadData = function(){
			$(".loadingrow").attr("style","");
			$scope.index = 1;
			$http.get('./devicefilter/all').success(function(data){
				$(".loadingrow").attr("style","display:none");
				if(data){	
					for (var i=0;i<data.length;i++){
						if (data[i].items == null || data[i].items.length == 0){
							//bug here: distinguish between no items and no policy
							data[i].text = "No Access Policy";
						}else{
							data[i].text = "Click to Edit "+ (data[i].isBlack? "black list": "white list");
						}
					}
					$scope.dapolicies =  data;
					console.log( $scope.dapolicies );
				}
			}).error(function(){
				window.location.href = "./Logout";
			});
			
			$http.get('./devicefilter/result').success(function(data){
				$(".loadingresultrow").attr("style","display:none");
				if(data){	
					
					for(var i = 0; i < data.length; i++){
						data[i].date = new Date(data[i].date).toLocaleString();
					}
					
					$scope.history =  data;
				}
			}).error(function(){
				window.location.href = "./Logout";
			});
			
			
		 }; 
		 $scope.reloadData();		
		 $scope.editPolicy = function(policy){
			 console.log("edit Policy");
			 
			 $scope.isBlack = (policy.isBlack)?"black":"white";
			 $scope.clearPolicy();
			 
			 $scope.policytabledata =  policy.items;
			 
			 if (!$scope.policytabledata || $scope.policytabledata.length==0){
				 $scope.policytabledata = [];
				 $scope.policytabledata[0] = new policyfilter();
			 }
			 $("#desktopName").text( policy.poolName );

			 $("#policyDialog").show();
		 }
		 
		 // XU YUE MODIFIED ON 20160125
		 $scope.createPolicyItem = function(){
			 var L = $scope.policytabledata.length;
			 
			 $scope.policytabledata[ L ] = new policyfilter();
		 }
		 $scope.removeCurrentPolicy = function( index ){
			 if( $scope.policytabledata.length > 0 ){
				 $scope.policytabledata.splice( index, 1);
			 }
		 }
		 $scope.clearPolicy = function(){
			 console.log("Clear Policy.");
			 $scope.policytabledata = [];
		 }
		 $scope.setPolicy = function(){

				var poolname = $("#desktopName").text();
				var policy = {
						poolName: poolname,
						//isBlack: false,
						isBlack: ($scope.isBlack == "black")?true:false,
						items: $scope.policytabledata
				};
				
				var str = JSON.stringify(policy);
				console.log("to server:"+str);
				ToolBox.Devicefilter._sendUpdateToServer(str);
				$("#policyDialog").hide();
			
		 }
		 $scope.removePolicy = function(){
					var poolname = $("#desktopName").text();
					ToolBox.Devicefilter._sendRemoveToServer(poolname);
					$("#policyDialog").hide();
				
			
		 }
		 // MODIFICATION END
	
	}

	ToolBox.Devicefilter = {
		controller: ToolBox.NgApp.controller('devicefilterCtrl', devicefilterCtrl),
	

		
		_sendRemoveToServer: function(poolname){
			$.ajax({
				url: "./devicefilter/remove",
				data: {pool: poolname},
				success: function(data){
					alert("Successful! This page will be refreshed.");
					window.location.reload();
				},
				failure: function(errMsg) {
					alert("Failed, this page will be refreshed.");
					window.location.reload();
				}
			});
			
		},


		_sendUpdateToServer : function addOrUpdatePolicy(policyRequestStr) {
			$.ajax({
				url: "./devicefilter/update",
				type: "POST",
				data: {policyStr: policyRequestStr},
				success: function(data){
					alert("Successful! This page will be refreshed.");
					window.location.reload();
				},
				failure: function(errMsg) {
					alert("Failed, this page will be refreshed.");
					window.location.reload();
				}
			});
			
		}

	};
	
	
	

	
	ToolBox.Devicefilter.init = function() {
		 
		 $(function () {
			    $( "#enable-confirm" ).dialog({
				      resizable: false,
				      autoOpen: false,
				        height: "auto",
				        width: "400px",
				      modal: true,
				      buttons: {
				        "Accept": function() {
				        	window.location.href = "./enablefilter";
				            $( this ).dialog( "close" );
				        },
				        Cancel: function() {
				          $( this ).dialog( "close" );
				        }
				      }
				    });
			    
			    $( "#disable-confirm" ).dialog({
				      resizable: false,
				      autoOpen: false,
				        height: "auto",
				        width: "400px",
				      modal: true,
				      buttons: {
				        "Accept": function() {
				        	window.location.href = "./disablefilter";
				            $( this ).dialog( "close" );
				        },
				        Cancel: function() {
				          $( this ).dialog( "close" );
				        }
				      }
				    });
			});
		 
		 $('#setPolicy').click(ToolBox.Devicefilter.setPolicy);
		 $('a.close').click(function(){ 
		        $("#policyDialog").hide(); 
		    }); 
		 
		 $(".enableFilterButton").click (function(){
			 $( "#enable-confirm" ).dialog("open");
			 
		 });
		 $(".disableFilterButton").click (function(){
			 $( "#disable-confirm" ).dialog("open");
			
		 });
	
	};
	

	
}


$(window).load(ToolBox.Devicefilter.init);







