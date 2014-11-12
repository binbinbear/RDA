if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Email || !ToolBox.Email.init){
	ToolBox.Email = {
		init: function(){
			$('#emailTabs a').click(function (e) {
				  e.preventDefault();
				  $(this).tab('show');
				});
		},


	};
}
$(document).ready(function(){  
	ToolBox.Email.init();
});