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

if (!ToolBox.PowerPolicy) {
	function ToText(cron){
		//Seconds Minutes Hours DayofMonth Month DayofWeek
		var crons = cron.split(" ");
		for (var i=0;i<2;i++){
			if (crons[i].length<2){
				crons[i] = "0" + crons[i];
			}
		}
		
		var text = crons[2] + ":" + crons[1]+":"+crons[0] + " at every"
		
		
		var weekdays = crons[5].split(",");
		
		for (var i=0;i<weekdays.length;i++){
			text = text +" " + weekdays[i];
		}
		return text;
	}
	
	function powerController ($scope, $http){
		$scope.powerpolicys = "";
		$scope.reloadData = function(){
			$(".loadingrow").attr("style","");
			$scope.index = 1;
			$http.get('./power/all').success(function(data){
				$(".loadingrow").attr("style","display:none");
				if(data){	
					for (var i=0;i<data.length;i++){
						if (data[i].cron == null || data[i].cron == ""){
							data[i].crontext = "No Power Policy";
						}else{
							data[i].crontext = ToText(data[i].cron);
						}
					}
					
					$scope.powerpolicys =  data;
				}
			});
			
			$http.get('./power/result').success(function(data){
				$(".loadingresultrow").attr("style","display:none");
				if(data){	
					for (var i=0;i<data.length;i++){
						data[i].endTime = new Date(data[i].endTime).toLocaleString();
						data[i].startTime = new Date(data[i].startTime).toLocaleString();
						
					}
					
					$scope.history =  data;
				}
			});
			
			
		 }; 
		 $scope.reloadData();		
		 $scope.editPolicy = ToolBox.PowerPolicy.loadPolicy;
	}

	ToolBox.PowerPolicy = {
		controller: ToolBox.NgApp.controller('powerCtrl', powerController)

	};
	
	
	ToolBox.PowerPolicy.loadPolicy = function(poweronjob){
		 console.log("edit pool:"+poweronjob.poolName);
		  $("#desktopName").text(poweronjob.poolName);
		var cron = poweronjob.cron;
		if (cron ==null || cron==""){
			ToolBox.PowerPolicy._clearPolicy();
			
		}else{
			var interval = poweronjob.interval;
			$("#interval").val(interval);
			
			//Seconds Minutes Hours DayofMonth Month DayofWeek
			var crons = cron.split(" ");
			$('#minute').val(crons[1]);
			$('#hour').val(crons[2]);
			
			var weekdays = crons[5].split(",");
			
			for (var i=0;i<weekdays.length;i++){
				$("#"+weekdays[i]).addClass("wselected");
			}
		}
		
		
		
		$("#policyDialog").show();
	}
	
	ToolBox.PowerPolicy._clearPolicy = function(){

		$(".wselected").removeClass("wselected");

		document.getElementById("hour").value="0";
		document.getElementById("minute").value="0";
		document.getElementById("interval").value="5";
	
	}
	


	ToolBox.PowerPolicy._sendUpdateToServer = function addOrUpdatePolicy(poolname, cron, interval) {
		$.ajax({
			url: "./power/update",
			data: {poolName:poolname, cron:cron, interval:interval},
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
	
	ToolBox.PowerPolicy._sendRemoveToServer = function addOrUpdatePolicy(poolname) {
		$.ajax({
			url: "./power/remove",
			data: {poolName:poolname},
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
	
	ToolBox.PowerPolicy.setPolicy = function() {
		//TODO: check values
		var cron = "";
		cron +=  "0 "
			+ $('#minute').val() + " " + $('#hour').val() + " ? * ";

		
		var allselected = $(".wselected");
		if(allselected.length == 0){
			alert("Please select at least one weekday");
			return;
		}
		
		for (var i=0;i<allselected.length;i++){
			cron += allselected[i].id;
			if (i<allselected.length-1){
				cron += ","
			}
		}
		
		
		$("#policyDialog").hide();
		
		ToolBox.PowerPolicy._sendUpdateToServer($("#desktopName").text(),cron, $("#interval").val());
		
		
		
	}
	
	ToolBox.PowerPolicy.removePolicy = function() {
		
		
		ToolBox.PowerPolicy._sendRemoveToServer($("#desktopName").text());
		
		
		
	}
	
	
	ToolBox.PowerPolicy.init = function() {
		 $('a.close').click(function(){ 
		        $("#policyDialog").hide(); 
		        ToolBox.PowerPolicy._clearPolicy();
		    }); 
		 $('.week').click(function(){		
				if(this.classList.contains("wselected")){
					this.classList.remove("wselected");
				}else{
					this.classList.add("wselected");
				}
			});
		 
		 $('#setPolicy').click(ToolBox.PowerPolicy.setPolicy);
		 $('#removePolicy').click(ToolBox.PowerPolicy.removePolicy);
	};
	

	
}


$(window).load(ToolBox.PowerPolicy.init);







