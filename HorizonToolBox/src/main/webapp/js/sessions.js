if (!window.AuditingApp){
	window.AuditingApp= {};
}
if (!AuditingApp.Session || !AuditingApp.Session.init){
	AuditingApp.Session = {
			pools: [],
			farms: [],
			
			
			refreshModel: function(){
				$.ajax({
					url: './session/report',
					type: "GET",
					success: function (data) {
						 AuditingApp.Session.pools = data.pools;
						 AuditingApp.Session.farms = data.farms;
						 AuditingApp.Session.updateTableView();
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
				
				var farms = AuditingApp.Session.farms;
				var farmbody = $(".farmSessionTable tbody");
				
				
				for (var i= 0; i< farms.length; i++){
					var farm = farms[i];
					var tr = "<tr><td>" + farm.name + "</td><td>"  +   farm.appSessionCount + "</td></tr>";
					farmbody.append(tr);
				}
				
				var poolbody = $("#poolSessionTable tbody");
				var pools = AuditingApp.Session.pools;
				for (var i = 0;i<pools.length;i++)
				{
					var pool = pools[i];
					var tr = "<tr><td>" + pool.name + "</td><td>"  +   pool.viewType +  "</td><td>"  +   pool.sessionCount +"</td></tr>";
					poolbody.append(tr);
				}
				
			},
			
			init: function(){
				AuditingApp.Session.refreshModel();
			}
	};
}



AuditingApp.Session.init();