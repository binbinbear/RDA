
$(document).ready(function () {

	$.get("/toolbox/remoteassist/list", null, function(result) {
		
		$("#ra_table").html(result);
		
	}, "text");
});

