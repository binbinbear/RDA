if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Session || !ToolBox.Session.init){
	/**
	 * @ngInject
	 */
	function  tableController($scope, ngTableParams) {

	    $scope.desktopParams = ToolBox.Session.desktopParams =new  ngTableParams({
	        page: 1,            // show first page
	        count: 10           // count per page
	    }, {
	        total: 0, // length of data
	        getData: function($defer, params) {
	        		params.total(ToolBox.Session.pools.length);
					$defer.resolve(ToolBox.Session.pools.slice((params.page() - 1) * params.count(), params.page() * params.count()));   
	        }
	    });
	    
	    $scope.appParams = ToolBox.Session.appParams = new  ngTableParams({
	        page: 1,            // show first page
	        count: 10           // count per page
	    }, {
	        total: 0, // length of data
	        getData: function($defer, params) {
	        		params.total(ToolBox.Session.farms.length);
					$defer.resolve(ToolBox.Session.farms.slice((params.page() - 1) * params.count(), params.page() * params.count()));   
	        }
	    });
	};
	ToolBox.Session = {
			pools: [],
			farms: [],
			history: [],
			desktopParams: null,
		    
		    
		    appParams: null,
		    
			app:  ToolBox.NgApp.controller('sessionCtrl', tableController),
		    _updateHistoryStatus: function(message){
		    	var loadingDiv = $(".loadingdiv");
		    	loadingDiv.removeClass("loadingdiv");
		    	loadingDiv.addClass("messagediv");
		    	loadingDiv.text(message);
		    },
		    getViewPools: function(){
				$.ajax({
					url: './common/poolfarms',
					type: "GET",
					success: function (data) {
						if (!data || !data.length){
							return;
						}
						 var poolbody = $("#desktoppool");
						 for (var i= 0; i< data.length; i++){
								var dSD = data[i];
								var option = "<option value=\""   +  dSD + "\"> "+ dSD+"</option>";
							 	poolbody.append(option);
							}
					}, 
					error: function(data) {
					}
				});
			},
			refreshHistorySession: function(){
				$("svg").empty();
				 var type = $("#viewType").val();
				 var days="7";
				 var period="1200";
			      if (type == "day"){
			    	  days="2";
			    	  period="360";
			      }else if (type=="week"){
			    	  days="7";
			    	  period="1800";
			      }else if (type=="month"){
			    	  days = "30";
			    	  period="7200";
			      }
			      
			      var type = $("#desktoppool").val();
					var poolname = "all";
					if (type == "all") {
						poolname = "";
					} else
						poolname = type;
					
			      if ($(".loadingdiv").length == 0){
			    	  $(".messagediv").remove();
			    	  $("#historySessions").append("<div class=\"loadingdiv\">Loading</div>");
			      }
				$.ajax({
					url: './session/concurrent?days='+ days+'&period=' + period	+ '&pool=' + poolname,
					type: "GET",
					success: function (data) {
						 ToolBox.Session.history = data.concurrentConnections;
						 ToolBox.Session._updateChartView();
						
					},
					error:function(XMLHttpRequest, textStatus, errorThrown){
						 ToolBox.Session.history = null;
						 ToolBox.Session._updateHistoryStatus("Error happens: " + errorThrown);
				    }
				}); 
			},
			refreshCurrentSession: function(){
				$.ajax({
					url: './session/report',
					type: "GET",
					success: function (data) {
						 ToolBox.Session.pools = data.pools;
						 ToolBox.Session.farms = data.farms;
						 $(".loadingrow").remove();
						 ToolBox.Session.desktopParams.reload();
						 ToolBox.Session.appParams.reload();
						 if (data.updatedDate){
							 var date = new Date(data.updatedDate);
							 $(".updateDate").text("Updated on "+ date.toLocaleString());
						 }else{
							 $(".updateDate").text("");
						 }
						
					},
					error:function(XMLHttpRequest, textStatus, errorThrown){
						$(".loadingrow").remove();
						ToolBox.Session.pools = [];
						ToolBox.Session.farms = [];
						ToolBox.Session.desktopParams.reload();
						ToolBox.Session.appParams.reload();
						 $(".updateDate").text("Error happens when getting current sessions");
				    }
				});    
				
			},
			
			
			_updateChartView: function(){
				
				if (!ToolBox.Session.history){
					ToolBox.Session._updateHistoryStatus(ToolBox.STR_NODATA);
					return;
				}
				$(".loadingdiv").remove();
				var margin = {top: 40, right: 20, bottom: 30, left: 50},
			    width = 800 - margin.left - margin.right,
			    height = 400 - margin.top - margin.bottom;


			var x = d3.time.scale()
			    .range([0, width]);

			var y = d3.scale.linear().range([height, 0]);

			
			var data = ToolBox.Session.history;
			data.forEach(function(d){
			    d.date = new Date(d.date);
			});
			 x.domain(d3.extent(data, function(d) { return d.date; }));
			 y.domain([0, d3.max(data, function(d) { return d.concurrent; })]);
			 
			 
			var xAxis = d3.svg.axis().scale(x).orient("bottom");

			var yAxis = d3.svg.axis().scale(y).tickFormat(d3.format("d")).orient("left");

			var line = d3.svg.line() 
			    .x(function(d) { return x(d.date); })
			    .y(function(d) { return y(d.concurrent); });

			var svg = d3.select("svg")
			    .attr("width", width + margin.left + margin.right)
			    .attr("height", height + margin.top + margin.bottom)
			  .append("g")
			    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			  svg.append("g")
			      .attr("class", "x axis")
			      .attr("transform", "translate(0," + height + ")")
			      .call(xAxis);

			  svg.append("g")
			      .attr("class", "y axis")
			      .call(yAxis)
			    .append("text")
			      .attr("y", -20)
			      .attr("dy", ".71em")
			      .style("text-anchor", "middle")
			      .text("Concurrent");

			  svg.append("path")
			      .datum(data)
			      .attr("class", "line")
			      .attr("d", line);
				
			},
			
			
			
			init: function(){
				ToolBox.Session.getViewPools();
				ToolBox.Session.refreshHistorySession();
				ToolBox.Session.refreshCurrentSession();
				$("#viewType").change(ToolBox.Session.refreshHistorySession);
				$("#desktoppool").change(ToolBox.Session.refreshHistorySession);
			}
	};
}

ToolBox.Session.init();