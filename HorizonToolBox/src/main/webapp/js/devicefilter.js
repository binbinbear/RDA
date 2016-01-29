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
							data[i].text = "No Access Policy";
						}else{
							data[i].text = "Click to Edit "+ (data[i].isBlack? "black list": "white list");
						}
					}
					$scope.dapolicies =  data;
					console.log( $scope.dapolicies );
				}
			});
			
			$http.get('./devicefilter/result').success(function(data){
				$(".loadingresultrow").attr("style","display:none");
				if(data){	
					$scope.history =  data;
				}
			});
			
			
		 }; 
		 $scope.reloadData();		
		 $scope.editPolicy = function(policy){
			 console.log("edit Policy");
			 
			 var items = policy.items;
			 // XU YUE MODIFIED ON 20150125
			 // ORIGIN CODE
			 /*
			 var str = "";
			 if (items!=null){
				 for (var i=0;i<items.length;i++){
					str = str+ items[i].reg+"\n";
				 }
			 }
			 $("#desktopName").val(policy.poolName);
			 $("#iplist").val(str);
			 */
			 
			 $scope.isBlack = (policy.isBlack)?"black":"white";
			 $scope.clearPolicy();
			 if( items != null ){
				 for( var i = 0; i < items.length; ++i ){
					 $scope.policytabledata[i] = {
							 "policyType": items[i].type,
							 "policyGrep": items[i].grep,
							 "policyValue": items[i].reg
					 };
				 }
			 }
			 $("#desktopName").text( policy.poolName );
			 //$("#desktopName").val( policy.poolName );
			 // MODIFICATION END
			 $("#policyDialog").show();
		 }
		 
		 // XU YUE MODIFIED ON 20160125
		 $scope.createPolicyItem = function(){
			 var L = $scope.policytabledata.length;
			 
			 $scope.policytabledata[ L ] = {
					 "policyType": "IP_Address",
					 "policyGrep": "matches",
					 "policyValue":""
			 };
		 }
		 $scope.removeCurrentPolicy = function( index ){
			 if( $scope.policytabledata.length > 0 ){
				 $scope.policytabledata.splice( index, 1);
			 }
		 }
		 $scope.clearPolicy = function(){
			 console.log("Clear Policy.")
			 $scope.policytabledata = [];
		 }
		 // MODIFICATION END
	
	}

	ToolBox.Devicefilter = {
		controller: ToolBox.NgApp.controller('devicefilterCtrl', devicefilterCtrl),
		setPolicy: function(){
			console.log("setPolicy To be developed");
			// XU YUE MODIFIED ON 20160125
			/* ORIGIN CODE
			var iplist = $("#iplist").val().split("\n");
			var items =[];
			for (var i=0;i<iplist.length;i++){
				var ip = iplist[i];
				var item = {
						type: "IP_Address",
						reg: ip
				}
				items[i] = item;
			}
			*/
			// MODIFICATION START
			var items = [];
			var i = 0;
			var appElement = document.querySelector('[ng-controller=devicefilterCtrl]');
			var $scope = angular.element(appElement).scope();
			
			var tdata = $scope.policytabledata;
			var L = tdata.length;
			//if( L < 1 )		return false;
			for( i = 0; i < L; ++i ){
				if( tdata[i].policyValue == "" )	continue;
				var item = {
						type: tdata[i].policyType,
						grep: tdata[i].policyGrep,
						reg:  tdata[i].policyValue
				};
				items[i] = item;
			}
			// MODIFICATION END
			
			var poolname = $("#desktopName").text();
			var policy = {
					poolName: poolname,
					//isBlack: false,
					isBlack: ($scope.isBlack == "black")?true:false,
					items: items
			};
			
			var str = JSON.stringify(policy);
			console.log("to server:"+str);
			ToolBox.Devicefilter._sendUpdateToServer(str);
			$("#policyDialog").hide();
		},
		
		removePolicy: function(){
			console.log("removePolicy To be developed");
		},
		
		_clearPolicy: function(){
			// XU YUE MODIFIED ON 20160128
			// ORIGIN CODE
			// $("#iplist").val("");
			// $("#desktopName").val("");
			// ORIGIN CODE ENDS
			var appElement = document.querySelector('[ng-controller=devicefilterCtrl]');
			var $scope = angular.element(appElement).scope();
			$scope.$apply( function(){
				$scope.clearPolicy();
			})
			ToolBox.Devicefilter.setPolicy();
			// MODIFICATION END
		},


		_sendUpdateToServer : function addOrUpdatePolicy(policyRequestStr) {
			$.ajax({
				url: "./devicefilter/update",
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
		 
		 $('#setPolicy').click(ToolBox.Devicefilter.setPolicy);
		 // XU YUE MODIFIED ON 20160128	
		 // ORIGIN CODE
		 //$('#removePolicy').click(ToolBox.Devicefilter.removePolicy);
		 // ORIGIN CODE END
		 //$('#clearPolicy').click( ToolBox.Devicefilter._clearPolicy);
		 // MODIFICATION END
		 $('a.close').click(function(){ 
		        $("#policyDialog").hide(); 
		        ToolBox.Devicefilter._clearPolicy();
		    }); 
	};
	

	
}


$(window).load(ToolBox.Devicefilter.init);







