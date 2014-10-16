if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Usage || !ToolBox.Usage.init){
	ToolBox.Usage = {

			getViewPools: function(){
				$.ajax({
					url: './policy/viewpools',
					type: "GET",
					success: function (data) {
						 $(".loadingrow").remove();
						 var poolbody = $("#poolListTable tbody");
						 for (var i= 0; i< data.length; i++){
								var dSD = data[i];
								var tr = "<tr><td>" + "<input type=radio name=pool value=\""+dSD.desktopSummaryData.name+"\">" + dSD.desktopSummaryData.name + "</td></tr>";
							 	poolbody.append(tr);
							}
					}
				});
			},

			init: function(){
				$("#submitBtn").click(ToolBox.Usage.updatePlicies);			
				ToolBox.Usage.getViewPools();
			},

			updatePlicies: function (){
				$.ajax({
					url: './policy/updatepolicies',
					type: "GET",
					data:{pool:$("#pool").val(),clipboard:$("#clipboard").val()},
					success: function (data) {
						
					}
				}); 
			}
	};
}


$(document).ready(function(){  
	ToolBox.Usage.init();
});
/*
  <script>
  $(function() {
    $( "#dialog" ).dialog();
  });
  </script>
</head>
<body>
 
<div id="dialog" title="Basic dialog">
  <p>This is the default dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p>
</div>
*/