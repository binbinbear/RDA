function enter(){
	if(event.keyCode==13) {  
		getUserEvent();  
        return false;  
    } 
}
function format(date){
	var year = date.getFullYear();
	var month = (1 + date.getMonth()).toString();
    month = month.length > 1 ? month : '0' + month;
	var day = date.getDate().toString();
	day = day.length > 1 ? day : '0' + day;
	var hours = date.getHours().toString();
	hours = hours.length > 1 ? hours : '0' + hours;
	var minutes = date.getMinutes().toString();
	minutes = minutes.length > 1 ? minutes : '0' + minutes;
	var seconds = date.getSeconds().toString();
	seconds = seconds.length > 1 ? seconds : '0' + seconds;
	return year + '/' + month + '/' + day + " " + hours + ":" + minutes + ":" + seconds;
}
function getUserEvent(){
	$.ajax({
		url: './userevent',
		type: "GET",
		data:{user:$("#user").val()},
		success: function (data) {
			var tbody = $("#infoTable tbody");
			tbody.empty();
			var headtr = " <tr> <th width=\"23%\">User Name</th> " +
		    " <th width=\"23%\">Usage Time</th> " +
			" <th width=\"23%\">Disconnection Time</th> " +
			" <th width=\"23%\">Connection Time</th> " +
			" <th width=\"23%\">Machine Name</th> </tr> ";
			tbody.append(headtr);
			if(data){	
			//	$(".loadingrow").remove();
				for(var i = 0; i < data.length; i++){
					var usageTime;
					var disconnection = format(new Date(data[i].disconnectionTime));
					var connection = format(new Date(data[i].connectionTime));
					
					
					if (data[i].usageTime < 60){
						var second = data[i].usageTime > 9 ? data[i].usageTime.toString() : "0" + data[i].usageTime.toString();
						usageTime = "00:00:" + second;
					}
					if (data[i].usageTime >= 60 && data[i].usageTime < 3600){
						var minutes = parseInt(data[i].usageTime/60) > 9 ? parseInt(data[i].usageTime/60).toString() : "0" + parseInt(data[i].usageTime/60).toString();
						seconds = data[i].usageTime % 60 > 9 ? (data[i].usageTime % 60).toString() : "0" + (data[i].usageTime % 60).toString();
						usageTime = "00:" + minutes + ":" + seconds;					
					}
					if(data[i].usageTime >= 3600){
						var hours = parseInt(data[i].usageTime/3600) > 9 ? parseInt(data[i].usageTime/3600).toString() : "0" + parseInt(data[i].usageTime/3600).toString();
						minutes = parseInt((data[i].usageTime % 3600) / 60 ) > 9 ? parseInt((data[i].usageTime % 3600) / 60 ).toString() : "0" + parseInt((data[i].usageTime % 3600) / 60 ).toString(); 
						seconds = (data[i].usageTime % 3600) % 60 > 9 ? ((data[i].usageTime % 3600) % 60).toString() : "0" + ((data[i].usageTime % 3600) % 60).toString();
						usageTime = hours + ":" + parseInt((data[i].usageTime % 3600) / 60 ) + ":" +  (data[i].usageTime % 3600) % 60;						
					}
					
					var tr = "<tr>"+"<td>" + data[i].userName + "</td>"
					    +"<td>" + usageTime + "</td>"
						+"<td>" + disconnection + "</td>"
						+"<td>" + connection + "</td>"
						+"<td>" + data[i].machineName + "</td>" +"</tr>";
					tbody.append(tr);
				}
			}
		}
	}); 
}
$(document).ready(function(){  
	getUserEvent();
});
