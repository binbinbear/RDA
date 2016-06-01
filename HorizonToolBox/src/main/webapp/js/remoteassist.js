$(document).ready(function() {

    $.get("/toolbox/remoteassist/list", null, function(result) {

        $("#ra_table").html(result);
    }, "text");

    $("#searchSessionInput").Text("Results limited to top 100");
    $.get("/toolbox/remoteassist/sessions", null, function(result) {
        $("#session_table").html(result);
        registerEvents();
    }, "text");
    
    $.get("/toolbox/remoteassist/raHists", null, function(result) {

        $("#rahist_table").html(result);
    }, "text");
});

$("#ratabs").tabs({
  activate: function( event, ui ) {
	  switch(ui.newTab.index()) {
	  case 0:
		  {
		  $("#searchSessionInput").Text("Results limited to top 100");
			  $.get("/toolbox/remoteassist/sessions", null, function(result) {
			        $("#session_table").html(result);
			        registerEvents();
			    }, "text");
		  }
		  break;
	  case 1:
		  {
			  $.get("/toolbox/remoteassist/raHists", null, function(result) {
	
			        $("#rahist_table").html(result);
			    }, "text");
		  }
		  break;
	  case 2:
		  {
			  $.get("/toolbox/remoteassist/list", null, function(result) {
	
			        $("#ra_table").html(result);
			    }, "text");

		  }
		  break;
	  }
  }
});

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
        $.get($(this).attr("href"), null, function(result) {
            if (result.indexOf("ticketlink") == -1) {
                var msg = "<p>Cannot communicate with remote desktop.</p> <p> Check the desktop environment referring to user guide or wait 5 minutes and try again.</p>";
                msg = msg + "<p align='center'><button id='closedlg'>OK</button></p>";
                $("#raprogressbar").hide();
                $("#shadowmsg").html(msg);
                $("#closedlg").click(function() {
                    $("#radialog").dialog("close");
                });
            } else {
               // ticketDownloadViaUser(result);
            	ticketAutoDownload(result);
            }
        }, "text");

        resetTicketDlg();
        setTimeout(function(){
        	var isVisible = $("#raprogressbar").is(":visible");
        	if(isVisible) {
            	var msg = "This operation timeout and please check the environment.";
                $("#raprogressbar").hide();
                $("#shadowmsg").html(msg);
                $("#closedlg").click(function() {
                    $("#radialog").dialog("close");
                });
        	}
        }, 300000); 

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
	var a = document.createElement('a');
    arr = result.split("'");
    a.href = arr[1];
    a.target = '_blank';
    a.download = arr[1];

    document.body.appendChild(a);
    a.click();
    $("#radialog").dialog("close");
    a=null;
}


function resetTicketDlg() {
    $("#radialog").dialog("open");
    $("#raprogressbar").progressbar({
        value: false
    });
    $("#raprogressbar").show();
    $("#shadowmsg").html("<p>Try to communicate with remote desktop, please wait a moment.</p>");
}


if (!window.ToolBox){
    window.ToolBox= {};
}
if (!ToolBox.RASession) {
    function activeDesktopController($scope, $http){
        
        $scope.reloadData = function(){
            $(".loadingrow").attr("style","");

            var key=$scope.key;
            if (!key){
                key="";
            }
             $http.get('/toolbox/remoteassist/sessions?key='+key).success(function(result){
                        $(".loadingrow").attr("style","display:none");
                        $("#session_table").html(result);
                        registerEvents();
                });
         };

         $("#searchSessionInput").keypress(function(event){
                if (event.keyCode == 13 || event.keyCode == 3){
                    // if regular Enter key or Enter on Mac numeric keypad,
                    // submit the search
                    $scope.reloadData();
                }
            });
            
         $scope.reloadData();
    }
    



    ToolBox.RASession = {
        controller: ToolBox.NgApp.controller('activeDesktopCtrl', activeDesktopController)

    };
    
    ToolBox.RASession.init = function() {


    };
}

$(window).load(ToolBox.RASession.init);