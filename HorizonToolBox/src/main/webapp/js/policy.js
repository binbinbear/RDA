if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Policy || !ToolBox.Policy.init){
	ToolBox.Policy = {

			getViewPools: function(){
				$.ajax({
					url: './policy/viewpools',
					type: "GET",
					success: function (data) {
						 var poolbody = $("#desktoppool");
						 for (var i= 0; i< data.length; i++){
								var dSD = data[i];
								var option = "<option value=\""   +  dSD.desktopSummaryData.name + "\"> "+ dSD.desktopSummaryData.name+"</option>";
							 	poolbody.append(option);
							}
					}, 
					error: function(data) {
					}
				});
			},

			init: function(){
				$('#myTab a').click(function (e) {
					  e.preventDefault()
					  $(this).tab('show')
					})
				$("#submitBtn").click(ToolBox.Policy.updatePlicies);			
				ToolBox.Policy.getViewPools();
			},

			updatePlicies: function (){
				$.ajax({
					url: './policy/updatepolicies',
					type: "GET",
					data:{pool:$("#desktoppool").val(),clipboard:$("#clipboard").val()},
					success: function (data) {
						
					}
				}); 
			}
	};
}


$(document).ready(function(){  
	ToolBox.Policy.init();
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