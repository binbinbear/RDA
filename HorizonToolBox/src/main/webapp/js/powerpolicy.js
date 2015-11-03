$.extend(true, $.hik.jtable.prototype.options, {
	//columnResizable: false,
	//animationsEnabled: false,
	jqueryuiTheme: true,
	ajaxSettings: {
		type: 'GET'
	}
});

var powerpolicy = {};
var cronpolicy = {};

if (!window.ToolBox){
	window.ToolBox= {};
}

if (!ToolBox.Power) {
	function powerController ($scope, $http){
		$scope.powerpolicys = "";
		$scope.reloadData = function(){
			$(".loadingrow").attr("style","");
			$scope.index = 1;
			$http.get('./pool/list').success(function(data){
//				$(".loadingrow").attr("style","display:none");
				$(".loadingrow").attr("style","display:none");
				if(data){	
					$scope.powerpolicys = data;
				}
			});
			
			
////			var testJson;
//			$http.get('./power/getpolicy').success(function(data){
//				//testJson = eval("(" + data + ")");
//				if (data == null) {
//					document.getElementById('VC6').innerHTML = "1111";
//				} else {
//					document.getElementById('VC6').innerHTML = "2222";
//				}
//				
//			});

			
		 }; 
		 $scope.reloadData();		 
	}

	ToolBox.Power = {
		controller: ToolBox.NgApp.controller('powerCtrl', powerController)

	};
	ToolBox.Power.init = function() {


	};
	
	
}
$(window).load(ToolBox.Power.init);

$(function(){  
	
	var v_id;
//	var i = 0;
	$('.week').click(function(){		
		if(this.style.backgroundColor=='lightgray'){
			this.style.backgroundColor='lightblue';
		}else{
			this.style.backgroundColor='lightgray';
		}
	});
 
    $('a.close').click(function(){ 
        $("#settime").hide(); 
    }); 

    function addRow(poolname, policy, pid){
    	var newTr =  forQaVmsTable.insertRow();
    	var newTd0 = newTr.insertCell();
    	var newTd1 = newTr.insertCell();
    	newTd0.innerText = poolname;
    	newTd1.setAttribute('class','alarm');
    	newTd1.id = pid;
    	newTd1.innerText = policy;
    }
    
    
}); 

function chosen(th) {
	if(this.style.backgroundColor=='lightgray'){
		this.style.backgroundColor='lightblue';
	}else{
	this.style.backgroundColor='lightgray';
	}
}

function setWindow(id) {
	v_id = id;
	//document.getElementById(v_id).innerHTML = v_id;
	$("#settime").show(); 
}

function setPolicy() {
	
	var chooseWeek = false;
	var cron = "";
	cron += eval(document.getElementById('second')).value + " "
		+ eval(document.getElementById('minute')).value + " " + eval(document.getElementById('hour')).value + " * * ";
	
	
	var pp = "The VMs in pool will power on at ";
	pp += eval(document.getElementById('hour')).value + ":" + eval(document.getElementById('minute')).value + ":" + eval(document.getElementById('second')).value
	         + ", ";
	if (document.getElementById('everyweek').checked){
		//cron += "1 ";
		pp += "every";
	} else{
		//cron == "0 ";
		pp += "only this";
	}
	
	if (document.getElementById('Mon').style.backgroundColor=='lightblue') {
		pp += " " + document.getElementById('Mon').innerText;
		cron += "1,";
		chooseWeek = true;
	}
	if (document.getElementById('Tues').style.backgroundColor=='lightblue') {
		pp += " " + document.getElementById('Tues').innerText;
		cron += "2,";
		chooseWeek = true;
	}
	if (document.getElementById('Wed').style.backgroundColor=='lightblue') {
		pp += " " + document.getElementById('Wed').innerText;
		cron += "3,";
		chooseWeek = true;
	}
	if (document.getElementById('Thur').style.backgroundColor=='lightblue') {
		pp += " " + document.getElementById('Thur').innerText;
		cron += "4,";
		chooseWeek = true;
	}
	if (document.getElementById('Fri').style.backgroundColor=='lightblue') {
		pp += " " + document.getElementById('Fri').innerText;
		cron += "5,";
		chooseWeek = true;
	}
	if (document.getElementById('Sat').style.backgroundColor=='lightblue') {
		pp += " " + document.getElementById('Sat').innerText;
		cron += "6,";
		chooseWeek = true;
	}
	if (document.getElementById('Sun').style.backgroundColor=='lightblue') {
		pp += " " + document.getElementById('Sun').innerText;
		cron += "0,";
		chooseWeek = true;
	}
	cron = cron.substring(0,cron.length-1);
	pp += ", the interval is " + eval(document.getElementById('interval')).value + "s";
	
	document.getElementById(v_id).innerHTML = pp.toString();
	
	powerpolicy[v_id.toString()] = pp;
	cronpolicy[v_id.toString()] = cron;
	
	if (chooseWeek == false) {
		alert("Please choose the workday!");
		return ;
	}
	
	
	
	$("#settime").hide();
	exitSetting();
	postPolicys();
}

function clearPolicy() {
	document.getElementById(v_id).innerHTML = 'There is no policy now. Click to add';
	delete powerpolicy[v_id.toString()];
	delete cronpolicy[v_id.toString()];
	$("#settime").hide(); 
	postPolicys();
}


function exitSetting() {
	document.getElementById("Mon").style.backgroundColor='lightgray';
	document.getElementById("Tues").style.backgroundColor='lightgray';
	document.getElementById("Wed").style.backgroundColor='lightgray';
	document.getElementById("Thur").style.backgroundColor='lightgray';
	document.getElementById("Fri").style.backgroundColor='lightgray';
	document.getElementById("Sat").style.backgroundColor='lightgray';
	document.getElementById("Sun").style.backgroundColor='lightgray';
	document.getElementById("thisweek").checked = true;
	document.getElementById("hour").value="0";
	document.getElementById("minute").value="0";
	document.getElementById("second").value="0";
	document.getElementById("interval").value="60";
}


function postPolicys() {
	$.ajax({
		url: "./power/myajax?content="+JSON.stringify(cronpolicy),
		success: function(data){alert(data);},
		failure: function(errMsg) {
			alert(errMsg);
		}
	});
	
}
