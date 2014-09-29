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
				
				$("#viewType").change(ToolBox.Usage.getUserEvent);
				
				$("#usageDays").change(ToolBox.Usage.refreshUsageChart);
				
				ToolBox.Usage.getUserEvent();
				ToolBox.Usage.refreshUsageChart();
			},

			usageReport: [],
		
			_refreshUsageChartView: function(){
				if (!ToolBox.Usage.usageReport){
					return;
				}
				$(".loadingdiv").remove();
				var data = ToolBox.Usage.usageReport;
				 var margin = {top: 30, right: 40, bottom: 80, left: 70};
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
                     .attr("transform", "rotate(270)" );

                 svg.append("g")
                     .attr("class", "y axis")
                     .attr("transform", "rotate(0)")
                     .call(yAxis)
                     .selectAll("text")
                     .style("text-anchor", "end")
                     .attr("dx", "-.1em")
                     .attr("dy", "-.8em")
                     .attr("transform", "rotate(0)" )
		.text(function(d){return ToolBox.Usage.toTimeString(d);});

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
			    	  days="1";
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
						}
					}); 
			},
			
			
			
			getUserEvent: function (){
				var type = $("#viewType").val();
				 var days="7";
			     if (type=="month"){
			    	  days="30";
			      }else if (type=="day"){
			    	  days="1";
			      }
				var tbody = $("#infoTable tbody");
				tbody.empty();
				var headtr = " <tr> <th width=\"17%\">User Name</th> " +
				" <th width=\"17%\">Connection Time</th> " +
				" <th width=\"17%\">Disconnection Time</th> " +
				 " <th width=\"17%\">Usage Time</th> " +
				" <th width=\"17%\">Machine Name</th> " +
				" <th  width=\"17%\">Pool Name</th> </tr> ";
				tbody.append(headtr);
				var loading =  " <tr class=\"loadingrow\"> <td/><td/><td/><td/><td/><td/> </tr>"; 
				tbody.append(loading);
				$.ajax({
					url: './usage/connection',
					type: "GET",
					data:{user:$("#user").val(),days:days},
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
									+"<td>" + data[i].machineName + "</td>" 
									+"<td>" + data[i].poolName + "</td>" +"</tr>";
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
