
$(document).ready(function () {

	$.get("/toolbox/remoteassist/list", null, function(result) {
		
		$("#ra_table").html(result);
	}, "text");
	
	$.get("/toolbox/remoteassist/sessions", null, function(result) {
		$("#session_table").html(result);
		registerEvents();
	}, "text");
	
	$( "#ratabs" ).tabs();
	$( "#radialog" ).dialog({
	     autoOpen: false,
	     modal: true,
	     width: 600,
	     show: {
	       effect: "blind",
	       duration: 1000
	     },
	     hide: {
	       effect: "explode",
	       duration: 1000
	     }
	   });
});

function registerEvents() {
	$( "a.radesktopsession" ).click(function(event) {
		event.preventDefault();
		
		$.get($(this).attr("href"), null, function(result) {
			if(result.indexOf("ticketlink") == -1) {
				var msg = "<p>Create RA ticket failed!</p> <p> Description:  " + result + "</p>";
				msg = msg + "<p align='center'><button id='closedlg'>OK</button></p>";
				$( "#raprogressbar" ).hide();
				$("#shadowmsg").html(msg);
				$("#closedlg").click(function() {
					$( "#radialog" ).dialog( "close" );
				});
			} else {
				ticketDownloadViaUser(result);
			}
		}, "text");
		
		 resetTicketDlg();
	  });
};

function ticketDownloadViaUser(result) {
	$( "#raprogressbar" ).hide();
	var msg = "<p>Create RA ticket successfully! Please click the button start this assist.</p>";
	msg = msg + "<p align='center'>" + result + "</p>";
	$("#shadowmsg").html(msg);
	$("#ticketlink").click(function() {
		$( "#radialog" ).dialog( "close" );
	});
};


function ticketAutoDownload(result) {
	
}


function resetTicketDlg() {
	$( "#radialog" ).dialog( "open" );
	$( "#raprogressbar" ).progressbar({
	    value: false
	  });
	 $( "#raprogressbar" ).show();	 
	 $("#shadowmsg").html("<p>Shadow operation is running, please wait a moment.</p>");
}
