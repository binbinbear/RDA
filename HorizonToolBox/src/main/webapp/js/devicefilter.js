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
		 $scope.editPolicy = function(){
			 console.log("to be developed");
		 }
	
	}

	ToolBox.Devicefilter = {
		controller: ToolBox.NgApp.controller('devicefilterCtrl', devicefilterCtrl)

	};
	
	
	

	
	ToolBox.Devicefilter.init = function() {};
	

	
}


$(window).load(ToolBox.Devicefilter.init);







