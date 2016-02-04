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
		 // MODIFICATION END
	
	}

	ToolBox.Devicefilter = {
		controller: ToolBox.NgApp.controller('devicefilterCtrl', devicefilterCtrl),
		setPolicy: function(){
			
			var appElement = document.querySelector('[ng-controller=devicefilterCtrl]');
			var $scope = angular.element(appElement).scope();

			
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
		 $('a.close').click(function(){ 
		        $("#policyDialog").hide(); 
		    }); 
	};
	

	
}


$(window).load(ToolBox.Devicefilter.init);







