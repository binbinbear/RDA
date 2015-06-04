
$(document).ready(function () {

	$.get("/toolbox/remoteassist/list", null, function(result) {
		
		$("#ra_table").html(result);
		
	}, "text");
	
	$.get("/toolbox/remoteassist/sessions", null, function(result) {
		$("#session_table").html(result);
	}, "text");

});

