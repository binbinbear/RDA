if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Session || !ToolBox.Session.init){
	ToolBox.Session = {
			pools: [],
			farms: [],
			history: [],
			
			refreshModel: function(){
				$.ajax({
					url: './session/concurrent?days=7&period=3600',
					type: "GET",
					success: function (data) {
						 ToolBox.Session.history = data.concurrentConnections;
						 ToolBox.Session.updateChartView();
						
					}
				}); 
				
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
			
			
			updateChartView: function(){
				if (!ToolBox.Session.history){
					return;
				}
				var margin = {top: 20, right: 20, bottom: 30, left: 50},
			    width = 960 - margin.left - margin.right,
			    height = 500 - margin.top - margin.bottom;


			var x = d3.time.scale()
			    .range([0, width]);

			var y = d3.scale.linear()
			    .range([height, 0]);

			var xAxis = d3.svg.axis()
			    .scale(x)
			    .orient("bottom");

			var yAxis = d3.svg.axis()
			    .scale(y)
			    .orient("left");

			var line = d3.svg.line()
			    .x(function(d) { return x(d.date); })
			    .y(function(d) { return y(d.concurrent); });

			var svg = d3.select("svg")
			    .attr("width", width + margin.left + margin.right)
			    .attr("height", height + margin.top + margin.bottom)
			  .append("g")
			    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			var data = ToolBox.Session.history;
			data.forEach(function(d){

			    d.date = new Date(d.date);
			    d.concurrent = +d.concurrent;
			  
			});
			
			
			 x.domain(d3.extent(data, function(d) { return d.date; }));
			  y.domain(d3.extent(data, function(d) { return d.concurrent; }));

			  svg.append("g")
			      .attr("class", "x axis")
			      .attr("transform", "translate(0," + height + ")")
			      .call(xAxis);

			  svg.append("g")
			      .attr("class", "y axis")
			      .call(yAxis)
			    .append("text")
			      .attr("transform", "rotate(-90)")
			      .attr("y", 6)
			      .attr("dy", ".71em")
			      .style("text-anchor", "end")
			      .text("Concurrent");

			  svg.append("path")
			      .datum(data)
			      .attr("class", "line")
			      .attr("d", line);
				
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