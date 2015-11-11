/**
 * 
 */

!function(){
	var Donut3D={};
	
	function pieTop(d, rx, ry, ir ){
		if(d.endAngle - d.startAngle == 0 ) return "M 0 0";
		var sx = rx*Math.cos(d.startAngle),
			sy = ry*Math.sin(d.startAngle),
			ex = rx*Math.cos(d.endAngle),
			ey = ry*Math.sin(d.endAngle);
			
		var ret =[];
		ret.push("M",sx,sy,"A",rx,ry,"0",(d.endAngle-d.startAngle > Math.PI? 1: 0),"1",ex,ey,"L",ir*ex,ir*ey);
		ret.push("A",ir*rx,ir*ry,"0",(d.endAngle-d.startAngle > Math.PI? 1: 0), "0",ir*sx,ir*sy,"z");
		return ret.join(" ");
	}

	function pieOuter(d, rx, ry, h ){
		var startAngle = (d.startAngle > Math.PI ? Math.PI : d.startAngle);
		var endAngle = (d.endAngle > Math.PI ? Math.PI : d.endAngle);
		
		var sx = rx*Math.cos(startAngle),
			sy = ry*Math.sin(startAngle),
			ex = rx*Math.cos(endAngle),
			ey = ry*Math.sin(endAngle);
			
			var ret =[];
			ret.push("M",sx,h+sy,"A",rx,ry,"0 0 1",ex,h+ey,"L",ex,ey,"A",rx,ry,"0 0 0",sx,sy,"z");
			return ret.join(" ");
	}

	function pieInner(d, rx, ry, h, ir ){
		var startAngle = (d.startAngle < Math.PI ? Math.PI : d.startAngle);
		var endAngle = (d.endAngle < Math.PI ? Math.PI : d.endAngle);
		
		var sx = ir*rx*Math.cos(startAngle),
			sy = ir*ry*Math.sin(startAngle),
			ex = ir*rx*Math.cos(endAngle),
			ey = ir*ry*Math.sin(endAngle);

			var ret =[];
			ret.push("M",sx, sy,"A",ir*rx,ir*ry,"0 0 1",ex,ey, "L",ex,h+ey,"A",ir*rx, ir*ry,"0 0 0",sx,h+sy,"z");
			return ret.join(" ");
	}

	function getPercent(d){
		var ret= (d.endAngle-d.startAngle > 0.2 ? 
				  Math.round(1000*(d.endAngle-d.startAngle)/(Math.PI*2))/10+'%' : '');
		if (d.data && d.data.label){
			return d.data.label + ': '+  ret;
		}
		return ret;
	}	
	
	Donut3D.transition = function(id, data, rx, ry, h, ir){
		function arcTweenInner(a) {
		  var i = d3.interpolate(this._current, a);
		  this._current = i(0);
		  return function(t) { return pieInner(i(t), rx+0.5, ry+0.5, h, ir);  };
		}
		function arcTweenTop(a) {
		  var i = d3.interpolate(this._current, a);
		  this._current = i(0);
		  return function(t) { return pieTop(i(t), rx, ry, ir);  };
		}
		function arcTweenOuter(a) {
		  var i = d3.interpolate(this._current, a);
		  this._current = i(0);
		  return function(t) { return pieOuter(i(t), rx-.5, ry-.5, h);  };
		}
		function textTweenX(a) {
		  var i = d3.interpolate(this._current, a);
		  this._current = i(0);
		  return function(t) { return 0.6*rx*Math.cos(0.5*(i(t).startAngle+i(t).endAngle));  };
		}
		function textTweenY(a) {
		  var i = d3.interpolate(this._current, a);
		  this._current = i(0);
		  return function(t) { return 0.6*rx*Math.sin(0.5*(i(t).startAngle+i(t).endAngle));  };
		}
		
		var _data = d3.layout.pie().sort(null).value(function(d) {return d.value;})(data);
		
		d3.select("#"+id).selectAll(".innerSlice").data(_data)
			.transition().duration(750).attrTween("d", arcTweenInner); 
			
		d3.select("#"+id).selectAll(".topSlice").data(_data)
			.transition().duration(750).attrTween("d", arcTweenTop); 
			
		d3.select("#"+id).selectAll(".outerSlice").data(_data)
			.transition().duration(750).attrTween("d", arcTweenOuter); 	
			
		d3.select("#"+id).selectAll(".percent").data(_data).transition().duration(750)
			.attrTween("x",textTweenX).attrTween("y",textTweenY).text(getPercent); 	
	}
	
	Donut3D.draw=function(id, data, x /*center x*/, y/*center y*/, 
			rx/*radius x*/, ry/*radius y*/, h/*height*/, ir/*inner radius*/){
	
		var _data = d3.layout.pie().sort(null).value(function(d) {return d.value;})(data);
		
		var slices = d3.select("#"+id).append("g").attr("transform", "translate(" + x + "," + y + ")")
			.attr("class", "slices");
			
		slices.selectAll(".innerSlice").data(_data).enter().append("path").attr("class", "innerSlice")
			.style("fill", function(d) { return d3.hsl(d.data.color).darker(0.7); })
			.attr("d",function(d){ return pieInner(d, rx+0.5,ry+0.5, h, ir);})
			.each(function(d){this._current=d;});
		
		slices.selectAll(".topSlice").data(_data).enter().append("path").attr("class", "topSlice")
			.style("fill", function(d) { return d.data.color; })
			.style("stroke", function(d) { return d.data.color; })
			.attr("d",function(d){ return pieTop(d, rx, ry, ir);})
			.each(function(d){this._current=d;});
		
		slices.selectAll(".outerSlice").data(_data).enter().append("path").attr("class", "outerSlice")
			.style("fill", function(d) { return d3.hsl(d.data.color).darker(0.7); })
			.attr("d",function(d){ return pieOuter(d, rx-.5,ry-.5, h);})
			.each(function(d){this._current=d;});

		slices.selectAll(".percent").data(_data).enter().append("text").attr("class", "percent")
			.attr("x",function(d){ return 0.6*rx*Math.cos(0.5*(d.startAngle+d.endAngle));})
			.attr("y",function(d){ return 0.6*ry*Math.sin(0.5*(d.startAngle+d.endAngle));})
			.text(getPercent).each(function(d){this._current=d;});				
	}
	
	this.Donut3D = Donut3D;
}();


$(function () {
    $( "#dialog-confirm" ).dialog({
	      resizable: false,
	      autoOpen: false,
	        height: "auto",
	        width: "400px",
	      modal: true,
	      buttons: {
	        "Accept": function() {
	        	ToolBox.Client.enableCEIP();
	          $( this ).dialog( "close" );
	        },
	        Cancel: function() {
	          $( this ).dialog( "close" );
	        }
	      }
	    });
    

});


if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Client || !ToolBox.Client.init){
	
	function  tableController($scope, ngTableParams) {

		$scope.days = "7";
		 $scope.$watch("days", function () {
		        $scope.reloadData();
		        
		  });
		 $scope.BrokerTable=window.l10Ntable;
		 $scope.data = [];
		 $scope.reloadData = function(){
			 if($("#loading2").length == 0){			    	// $('#accumulatedUsing').append("<div class=\"loadingdiv\">Loading</div>"); 
					$('#BrokeSessionTable').append("<tr id=\"loading2\" class=\"loadingrow\"><td class=\"desktopName \" ></td>"
							+"<td class=\"desktopName \" ></td><td class=\"desktopName \" ></td><td class=\"desktopName \" ></td>"
							+ "<td class=\"desktopName \" ></tr>");
			     }
	        var thisRequestData = {user:$("#user").val(),days:$scope.days};
	        
			 $.ajax({
					url: './client/brokersessionlist',
					type: "GET",
					data:thisRequestData,
					success: function (data) {
						$(".updateDate").text("");
						$(".loadingrow").remove();
						if(data){	
							for(var i = 0; i < data.length; i++){
								// peter: how to handle the exception time range
								data[i].loggedInTime = new Date(data[i].disconnectionTime).toLocaleString();
								data[i].loggedOutTime = new Date(data[i].connectionTime).toLocaleString();
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
	
	ToolBox.Client = {
			app:  ToolBox.NgApp.controller('BrokerSeessionCtrl', tableController),
			osdata: [],
			versiondata: [],
			colorMap: {
				"Windows 7": "#3366CC",
				"Windows 8": "#DC3912",
				"Windows XP": "#FF9900",
				"iOS": "#109618",
				"Android": "#990099",
				"OS X":"#A5A5A5",
				
				"3.0": "#3366CC",
				"2.3": "#DC3912",
				"2.2": "#FF9900",
				"2.1": "#109618",
				"1.x": "#990099",
				
				"Others":"#0277aa",
			},
			
			refreshModel: function(){
				$.ajax({
					url: './client/report',
					type: "GET",
					success: function (data) {
						 if (data.updatedDate){
							 var date = new Date(data.updatedDate);
							 $(".updateDate").text("Updated on "+ date.toLocaleString());
						 }else{
							 $(".updateDate").text("");
						 }
						 ToolBox.Client.osdata = [];
						 ToolBox.Client.versiondata = [];
						var osMap = data.osMap;
						var versionMap = data.versionMap;
						//label, color, value
						var colorMap = ToolBox.Client.colorMap;
						for (var label in osMap){
							var color = colorMap[label];
							
							var os = { label: label, color: color, value: osMap[label]}
							
							ToolBox.Client.osdata.push(os);
						}
						
						for (var label in versionMap){
							var color = colorMap[label];
							
							var version = { label: label, color: color, value: versionMap[label]}
							
							ToolBox.Client.versiondata.push(version);
						}
					       
					      var type = $("#viewType").val();
					      if (type == "pie"){
					    	  ToolBox.Client.showPieView();
					      }else{
					    	  ToolBox.Client.showTableView();
					      }

					}
				});    
				
			},
			
			
			showPieView: function(){
				var table = $(".activetable");
				if (table){
					table.css("display", "none");
				}
				var svg = $(".activesvg");
				if (!svg){
					   return;
				}
				svg.css("display","block");
				var size = 180;
				var height = 20;
				Donut3D.draw("osdata",ToolBox.Client.osdata, size+height, size+height, size, size-height, height, 0.4);
				Donut3D.draw("versiondata",ToolBox.Client.versiondata, size+height, size+height, size, size-height, height, 0.4);  
				
			},
			
			showTableView: function(){
				var table = $(".activetable");
				if (!table){
					   return;
				}
				var osdata = ToolBox.Client.osdata;
				var osbody = $("#clientostable tbody");
				osbody.empty();
				var headtr = "<tr> <th width=\"23%\">Client OS</th>		<th width=\"23%\">Number</th> </tr>"
				osbody.append(headtr);
				for (var i= 0; i< osdata.length; i++){
					var os = osdata[i];
					var tr = "<tr><td>" + os.label + "</td><td>"  +   os.value + "</td></tr>";
					osbody.append(tr);
				}
				
				var versionbody = $("#clientversionTable tbody");
				versionbody.empty();
				var versiondata = ToolBox.Client.versiondata;
				headtr = "<tr> <th width=\"23%\">Client Version</th>		<th width=\"23%\">Number</th> </tr>";
				versionbody.append(headtr);
				for (var i = 0;i<versiondata.length;i++)
				{
					var version = versiondata[i];
					var tr = "<tr><td>" + version.label + "</td><td>"  +   version.value + "</td></tr>";
					versionbody.append(tr);
				}
				
				table.css("display","block");
				var svg = $(".activesvg");
				if (svg){
					svg.css("display", "none");
				}
			},
			
			popEnableDialog: function(){
				$( "#dialog-confirm" ).dialog("open");
			},
			
			
			enableCEIP: function(){
				window.location.href = "./enableCEIP";
			},
			
			init: function(){
				ToolBox.Client.refreshModel();
				$("#viewType").change(ToolBox.Client.refreshModel);
				$(".enableButton").click(ToolBox.Client.popEnableDialog);
			}
	}
}


$(window).load(ToolBox.Client.init);

