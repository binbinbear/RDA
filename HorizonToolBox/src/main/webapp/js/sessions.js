if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Session || !ToolBox.Session.init){
	ToolBox.Session = {
			pools: [],
			farms: [],
			
			
			refreshModel: function(){
				$.ajax({
					url: './session/report',
					type: "GET",
					success: function (data) {
						 ToolBox.Session.pools = data.pools;
						 ToolBox.Session.farms = data.farms;
						 ToolBox.Session.updateTableView();
						 if (data.updatedDate){
							 var date = new Date(data.updatedDate);
							 $(".updateDate").text("Updated on "+ date.toLocaleString());
						 }else{
							 $(".updateDate").text("");
						 }
						
					}
				});    
				
			},
			
			

			
			updateTableView: function(){
				var table = $(".farmSessionTable");
				if (!table){
					   return;
				}
				$(".loadingrow").remove();
				
				var farms = ToolBox.Session.farms;
				var farmbody = $(".farmSessionTable tbody");
				
				
				for (var i= 0; i< farms.length; i++){
					var farm = farms[i];
					var tr = "<tr><td>" + farm.name + "</td><td>"  +   farm.appSessionCount + "</td></tr>";
					farmbody.append(tr);
				}
				
				var poolbody = $("#poolSessionTable tbody");
				var pools = ToolBox.Session.pools;
				for (var i = 0;i<pools.length;i++)
				{
					var pool = pools[i];
					var tr = "<tr><td>" + pool.name + "</td><td>"  +   pool.viewType +  "</td><td>"  +   pool.sessionCount +"</td></tr>";
					poolbody.append(tr);
				}
				
			},
			
			init: function(){
				ToolBox.Session.refreshModel();
			}
	};
}



ToolBox.Session.init();