$(document).ready(function() {
	
	reloadRemoteassistList();
	reloadActiveDesktopSessions();
	reloadRaHists(); 
});

$("#ratabs").tabs({
  activate: function( event, ui ) {
	  switch(ui.newTab.index()) {
	  case 0:
		  {
		  	reloadActiveDesktopSessions();
		  }
		  break;
	  case 1:
		  {
		  	reloadRaHists();
		  }
		  break;
	  case 2:
		  {
		  reloadRemoteassistList();
		  }
		  break;
	  }
  }
});

function reloadActiveDesktopSessions() {
	 $("#searchSessionInput").text("Results limited to top 100");
	$.ajax({
		url: '/toolbox/remoteassist/sessions',
		type: "GET",
		dataType: 'text',
		success: function (result) {
			if(result.indexOf("No active session now") != -1) {
				window.location.reload();	
				return;
			}
			 $("#session_table").html(result);
		        registerEvents();
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
			if (("timeout" == sessionstatus) || (null == sessionstatus)){
				window.location.reload();	
				return;
			}
	    }
	}); 
};

function reloadRaHists() {
	$.ajax({
		url: '/toolbox/remoteassist/raHists',
		type: "GET",
		dataType: 'text',
		success: function (result) {
			 $("#rahist_table").html(result);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
			if (("timeout" == sessionstatus) || (null == sessionstatus)){
				window.location.reload();	
				return;
			}
	    }
	}); 
};

function reloadRemoteassistList() {
	$.ajax({
		url: '/toolbox/remoteassist/list',
		type: "GET",
		dataType: 'text',
		success: function (result) {
			$("#ra_table").html(result);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
			if (("timeout" == sessionstatus) || (null == sessionstatus)){
				window.location.reload();	
				return;
			}
	    }
	}); 
};

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
        console.log("start to call ticket request");
        $.ajax({
    		url: $(this).attr("href") +"?&date="+new Date().getTime(),
    		type: "GET",
    		dataType: 'text',
    		success: function (result) {
    			if(result.indexOf("Cannot get related session") != -1) {
    				window.location.reload();	
    				return;
    			}
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
                };
    		},
    		error:function(XMLHttpRequest, textStatus, errorThrown){
    			console.log("result error:"+textStatus);
    			var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
    			if (("timeout" == sessionstatus) || (null == sessionstatus)){
    				window.location.reload();	
    				return;
    			}
    	    }
    	}); 
        console.log("ticket request is sent");
        resetTicketDlg();
        console.log("reset tick dlg");
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
                }).error(function(data, status, headers, config) {
                	if ("timeout" == status){
        				window.location.reload();	
        				return;
        			}
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