if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Email || !ToolBox.Email.init){
	ToolBox.Email = {
		init: function(){
			var validate = function(){
				var host = $("#mailHost").val();
				if(!host){
					return "host";
				}
				var port = $("#serverPort").val();
				if (!port){
					return "port";
				}
				var to = $("#toAddress").val();
				if (!to){
					return "toAddress";
				}
				return true;
			}
			$("#emailForm").submit(function(e){
				var result = validate();
				if (result===true){
					
				}else{
					e.preventDefault();
				    alert("Invalid Arguments "+result);
				    return;
				}
				
				
			});
		},


	};
}
$(document).ready(function(){  
	ToolBox.Email.init();
});