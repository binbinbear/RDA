if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Console) {
	function consoleController ($scope, $http){
		
		$scope.reloadData = function(){
			$(".loadingrow").attr("style","");
			$scope.vms = [];
			var pool=$scope.pool;
			if (!pool){
				pool="";
			}
			var power=$scope.power;
			
			if (!power){
				power="";
			}
			
			var key=$scope.key;
			if (!key){
				key="";
			}
			 $http.get('./console/list?pool='+pool+"&key="+key).success(function(data){
						$(".loadingrow").attr("style","display:none");
						if(data){	
							for(var i = 0; i < data.length; i++){
								data.href='webmks/?vmPatternId='+data.id;
							}
							$scope.vms = data;
						}
				});
		 };
		
		 $scope.$watch("pool", function () {
		        $scope.reloadData();
		        
		  });
		 
		 $scope.$watch("power", function () {
		        $scope.reloadData();
		        
		  });

		 $("#searchInput").keypress(function(event){
		        if (event.keyCode == 13 || event.keyCode == 3){
					// if regular Enter key or Enter on Mac numeric keypad,
					// submit the search
		        	$scope.reloadData();
		        }
		    });
		    
		 $scope.reloadData();
	}
	



	ToolBox.Console = {
		controller: ToolBox.NgApp.controller('consoleCtrl', consoleController)

	};
	ToolBox.Console.init = function() {


	};
}

$(window).load(ToolBox.Console.init);