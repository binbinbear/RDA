
$(document).ready(function () {
	$.ajax("./sessionMsg.list")
	.done(function( data ) {
		alert("Data: " + data);
	});
});
