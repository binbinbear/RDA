$(document).ready(function() {

    $.get("/toolbox/remoteassist/list", null, function(result) {

        $("#ra_table").html(result);
    }, "text");

    $.get("/toolbox/remoteassist/sessions", null, function(result) {
        $("#session_table").html(result);
        registerEvents();
    }, "text");
});

$("#ratabs").tabs();
$("#radialog").dialog({
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


var waitTime = 300; // 300s
var curTime = waitTime;
var tipId;
var originHref;

function registerEvents() {
    $("a.radesktopsession").click(function(event) {
        if (curTime < waitTime) {
            return;
        }
        event.preventDefault();
        originHref = $("a.radesktopsession").attr('href');
        $.get($("a.radesktopsession").attr("href"), null, function(result) {
            if (result.indexOf("ticketlink") == -1) {
                var msg = "<p>Cannot communicate with remote desktop.</p> <p> Please check the user guide and confirm that there is no msra.exe in the process list of remote desktop.</p>";
                msg = msg + "<p>Please wait 5 minutes and try again.</p>"
                msg = msg + "<p align='center'><button id='closedlg'>OK</button></p>";
                $("#raprogressbar").hide();
                $("#shadowmsg").html(msg);
                $("#closedlg").click(function() {
                    $("#radialog").dialog("close");
                });
            } else {
                ticketDownloadViaUser(result);
            }
        }, "text");

        resetTicketDlg();

       // tipId = window.setInterval("processEventes()", 1000); //  call processEventes method every onesecond

    });
};


function processEventes() {

    if (curTime > 0) {
    	var text = "shadow";
    	if (curTime < 30) {
    		text = text + "(" + curTime + "s)";
    	} else {
    		var left = curTime%60==0?curTime/60:curTime/60+1;
    		text = text + "(" + parseInt(left) + "m)";
    	}
        
        $("a.radesktopsession").text(text);
        $("a.radesktopsession").removeAttr('href');
        curTime--;
    } else {
        curTime = waitTime;
        $("a.radesktopsession").text('shadow');
        $("a.radesktopsession").attr('href', originHref);
        window.clearInterval(tipId);
    }


}

function ticketDownloadViaUser(result) {
    $("#raprogressbar").hide();
    var msg = "<p>Please click the button to start this assist.</p>";
    msg = msg + "<p align='center'>" + result + "</p>";
    $("#shadowmsg").html(msg);
    $("#ticketlink").click(function() {
        $("#radialog").dialog("close");
    });
};


function ticketAutoDownload(result) {

}


function resetTicketDlg() {
    $("#radialog").dialog("open");
    $("#raprogressbar").progressbar({
        value: false
    });
    $("#raprogressbar").show();
    $("#shadowmsg").html("<p>Try to communicate with remote desktop, please wait a moment.</p>");
}
