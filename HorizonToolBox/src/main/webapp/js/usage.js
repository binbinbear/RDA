if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Usage || !ToolBox.Usage.init){
	ToolBox.Usage = {
			toTimeString: function(seconds){
				var hours = Math.floor(seconds/3600);
				var minutes = Math.floor((seconds % 3600) /60);
				seconds = seconds%60;
				
				hours = hours > 9 ? hours : "0" + hours;
				minutes = minutes > 9 ? minutes : "0" + minutes; 
				seconds = seconds >9 ? seconds: "0" + seconds;
				return hours + ":" + minutes + ":" + seconds;						
		
			},
			init: function(){
				var enterfn = function(event){
					if(event.keyCode==13) {  
						ToolBox.Usage.getUserEvent();  
				        return false;  
				    } 
				};
				$("#user").keypress(enterfn);
				
				$("#submitBtn").click(ToolBox.Usage.getUserEvent);
				
				ToolBox.Usage.getUserEvent();
			},


		
			
			getUserEvent: function (){
				var tbody = $("#infoTable tbody");
				tbody.empty();
				var headtr = " <tr> <th width=\"23%\">User Name</th> " +
				" <th width=\"23%\">Connection Time</th> " +
				" <th width=\"23%\">Disconnection Time</th> " +
				 " <th width=\"23%\">Usage Time</th> " +
				" <th width=\"23%\">Machine Name</th> </tr> ";
				tbody.append(headtr);
				var loading =  " <tr class=\"loadingrow\"> <td/><td/><td/><td/><td/> </tr>"; 
				tbody.append(loading);
				$.ajax({
					url: './usage/connection',
					type: "GET",
					data:{user:$("#user").val()},
					success: function (data) {
						
						tbody.empty();
						tbody.append(headtr);
						
						if(data){	
						//	$(".loadingrow").remove();
							for(var i = 0; i < data.length; i++){
								var disconnection = new Date(data[i].disconnectionTime).toLocaleString();
								var connection = new Date(data[i].connectionTime).toLocaleString();
								
								var usageTime= ToolBox.Usage.toTimeString(data[i].usageTime);
								
								
								var tr = "<tr>"+"<td>" + data[i].userName + "</td>"
									+"<td>" + connection + "</td>"
									+"<td>" + disconnection + "</td>"
									 +"<td>" + usageTime + "</td>"
									+"<td>" + data[i].machineName + "</td>" +"</tr>";
								tbody.append(tr);
							}
						}
					}
				}); 
			}
	};
}


$(document).ready(function(){  
	ToolBox.Usage.init();
});
