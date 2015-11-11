if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Usage || !ToolBox.Usage.init){
	
	/**
	 * @ngInject
	 */
	function  tableController($scope, ngTableParams) {

		$scope.days = "7";
		 $scope.$watch("days", function () {
		        $scope.reloadData();
		        
		  });
		 $scope.useagetable=window.l10Ntable;
		 $scope.data = [];
		 $scope.reloadData = function(){
			 if($("#loading2").length == 0){
			    	// $('#accumulatedUsing').append("<div class=\"loadingdiv\">Loading</div>"); 
					$('#connectionTable').append("<tr id=\"loading2\" class=\"loadingrow\"><td class=\"desktopName \" ></td>"
							+"<td class=\"desktopName \" ></td><td class=\"desktopName \" ></td><td class=\"desktopName \" ></td>"
							+ "<td class=\"desktopName \" ></td><td class=\"desktopName \" ></td></tr>");
			     }
	        var thisRequestData = {user:$("#user").val(),days:$scope.days};
	        
			 $.ajax({
					url: './usage/connection',
					type: "GET",
					data:thisRequestData,
					success: function (data) {
						$("#exportBtn").attr("href","./usage/connection/export?days="+ $scope.days+"&user="+encodeURIComponent(thisRequestData.user));
						$(".updateDate").text("");
						$(".loadingrow").remove();
						if(data){	
							for(var i = 0; i < data.length; i++){
								data[i].disconnectionTime = new Date(data[i].disconnectionTime).toLocaleString();
								data[i].connectionTime = new Date(data[i].connectionTime).toLocaleString();
								data[i].usageTime= ToolBox.Usage.toTimeString(data[i].usageTime);
								if (data[i].loginDelayTime ==0){
									data[i].loginDelayTime = "Unknown";
								}else{
									data[i].loginDelayTime= ToolBox.Usage.toTimeString(data[i].loginDelayTime/1000);
								}
								
							}
							$scope.data = data;
						}
						$scope.tableParams.reload();
					}, 
					error:function(XMLHttpRequest, textStatus, errorThrown){
						var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
						if ("timeout" == sessionstatus){
							window.location.reload();	
							return;
						}
						$(".loadingrow").remove();
						$scope.data = [];
						 $(".updateDate").text("Error happens when getting data from Event DB");
						 $scope.tableParams.reload();
				    }
				});
		 }
	    $scope.tableParams = new ngTableParams({
	        page: 1,            // show first page
	        count: 10           // count per page
	    }, {
	        total: 0, // length of data
	        getData: function($defer, params) {
	        	var data = $scope.data;
	        	if ((params.page() - 1) * params.count() >= data.length){
							params.page(1);
				}
	        	params.total(data.length);
	        	$defer.resolve(data.slice((params.page() - 1) * params.count(), params.page() * params.count()));		
												 
	        }
	    });
	
	}
	ToolBox.Usage = {
			app:  ToolBox.NgApp.controller('usageCtrl', tableController),
			toTimeString: function(seconds){
				var hours = Math.floor(seconds/3600);
				var minutes = Math.floor((seconds % 3600) /60);
				seconds = seconds - Math.floor(seconds/60) *60;
				hours = hours > 9 ? hours : "0" + hours;
				minutes = minutes > 9 ? minutes : "0" + minutes; 
				seconds = seconds >9 ? seconds: "0" + seconds;
				return hours + ":" + minutes + ":" + seconds;						
			},
			
			init: function(){
				$("#usageDays").change(ToolBox.Usage.refreshUsageChart);
				ToolBox.Usage.refreshUsageChart();
			},
			usageReport: [],
			_refreshUsageChartView: function(){
				if (!ToolBox.Usage.usageReport ||ToolBox.Usage.usageReport.length == 0){
					var loadingDiv = $(".loadingdiv");
					loadingDiv.removeClass("loadingdiv");
					loadingDiv.addClass("messagediv");
					loadingDiv.text(ToolBox.STR_NODATA);
					return;
				}
				$(".loadingdiv").remove();
				var data = ToolBox.Usage.usageReport;
				 var margin = {top: 40, right: 40, bottom: 80, left: 70};
                 var width = 800 - margin.left - margin.right;
                 var height = 400 - margin.top - margin.bottom;
                 if (data.length < 7){
                	 width = 100 * data.length;
                 }
                 var x = d3.scale.ordinal().rangeRoundBands([0, width], .1);
                 var y = d3.scale.linear().range([height, 0]);
                 var xAxis = d3.svg.axis()
                     .scale(x)
                     .orient("bottom");
                 var yAxis = d3.svg.axis()
                     .scale(y)
                     .orient("left");
                 var svg = d3.select("svg")
                     .attr("width", width + margin.left + margin.right)
                     .attr("height", height + margin.top + margin.bottom)
                   .append("g")
                     .attr("transform",
                           "translate(" + margin.left + "," + margin.top + ")");
                 x.domain(data.map(function(d) { return d.userName; }));
                 y.domain([0, d3.max(data, function(d) { return d.usageTime; })]);
                 
                 svg.append("g")
                     .attr("class", "x axis")
                     .call(xAxis)
                     .attr("transform", "translate(0," + height + ")")
                     .selectAll("text")
                     .style("text-anchor", "end")
                     .attr("transform", "rotate(270)" )
                     .attr('y', 0 - x.rangeBand()/2)
                     .attr('x', -5);;
                 
                 
                 var yg =svg.append("g")
                     .attr("class", "y axis")
                     .attr("transform", "rotate(0)")
                     .call(yAxis);
                 yg.selectAll("text")
                     .style("text-anchor", "end")
                     .attr("dx", "-.1em")
                     .attr("dy", "-.8em")
                     .attr("transform", "rotate(0)" )
		.text(function(d){return ToolBox.Usage.toTimeString(d);});
                 
                 yg.append("text")
			      .attr("y", -20)
			      .attr("dy", ".71em")
			      .style("text-anchor", "middle")
			      .text("Hours:Minutes:Seconds");
                 
                 svg.selectAll("bar")
                     .data(data)
                     .enter().append("rect")
                     .style("fill", "steelblue")
                     .attr("x", function(d){return x(d.userName);})
                     .attr("width",  x.rangeBand())
                     .attr("y", function(d) { return y(d.usageTime); })
                     .attr("height", function(d) { return height - y(d.usageTime); });
                 // use filters for the rotated text to display smoothly
                 // filters go in defs element
                 var defs = svg.append("defs");
                 // create filter with id #drop-shadow
                 // height=130% so that the shadow is not clipped
                 var filter = defs.append("filter")
                     .attr("id", "drop")
                     .attr("height", "130%");
                 // SourceAlpha refers to opacity of graphic that this filter will be applied to
                 // convolve that with a Gaussian with standard deviation 3 and store result
                 // in blur
                 filter.append("feGaussianBlur")
                     .attr("in", "SourceGraphic")
                     .attr("stdDeviation", .5)
                     .attr("result", "blur");
                 // translate output of Gaussian blur to the right and downwards with 2px
                 // store result in offsetBlur
                 filter.append("feOffset")
                     .attr("in", "blur")
                     .attr("dx", 0)
                     .attr("dy", 0)
                     .attr("result", "offsetBlur");
                 // overlay original SourceGraphic over translated blurred opacity by using
                 // feMerge filter. Order of specifying inputs is important!
                 var feMerge = filter.append("feMerge");
                 feMerge.append("feMergeNode")
                     .attr("in", "offsetBlur");
                 feMerge.append("feMergeNode")
                     .attr("in", "SourceGraphic");
			},
			refreshUsageChart: function(){
				$("svg").empty();
				var type = $("#usageDays").val();
				 var days="7";
			     if (type=="month"){
			    	  days="30";
			      }else if (type=="day"){
			    	  days="2";
			      }
				  else if (type=="halfyear"){
					  days="180"
				  }
			     if($(".loadingdiv").length == 0){
			    	 $(".messagediv").remove();
			    	 $('#accumulatedUsing').append("<div class=\"loadingdiv\">Loading</div>"); 
			     }
			     $.ajax({
						url: './usage/accumulated',
						type: "GET",
						data:{days:days},
						success: function (data) {
							if(data){	
								ToolBox.Usage.usageReport = data.usageReport;
								ToolBox.Usage._refreshUsageChartView();
							}
						},
						error:function(XMLHttpRequest, textStatus, errorThrown){
							var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
							if ("timeout" == sessionstatus){
								window.location.reload();	
								return;
							}
							ToolBox.Usage.usageReport = [];
							ToolBox.Usage._refreshUsageChartView();
					    }
					}); 
			},
	};
}
$(document).ready(ToolBox.Usage.init);
