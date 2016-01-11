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
			 var str = "";
			 if (items!=null){
				 for (var i=0;i<items.length;i++){
					str = str+ items[i].reg+"\n";
				 }
			 }
			 $("#desktopName").val(policy.poolName);
			 $("#iplist").val(str);
			 $("#policyDialog").show();
		 }
	
	}

	ToolBox.Devicefilter = {
		controller: ToolBox.NgApp.controller('devicefilterCtrl', devicefilterCtrl),
		setPolicy: function(){
			console.log("setPolicy To be developed");
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
			var poolname = $("#desktopName").val();
			var policy = {
					poolName: poolname,
					isBlack: false,
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
			$("#iplist").val("");
			$("#desktopName").val("");
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
		 $('#removePolicy').click(ToolBox.Devicefilter.removePolicy);
		 $('a.close').click(function(){ 
		        $("#policyDialog").hide(); 
		        ToolBox.Devicefilter._clearPolicy();
		    }); 
	};
	

	
}


$(window).load(ToolBox.Devicefilter.init);







