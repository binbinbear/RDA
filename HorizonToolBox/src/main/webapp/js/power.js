
$(document)
.ready(
		function() {
			var _hasEvent = false;
			if (!window.reportHACAEvent){
				window.reportHACAEvent =function(){
					console.log("report event in console");
					_hasEvent = true;
				};
			}
			
			var disableClass="ui-state-disabled";
			function isPowerOn(){
				return $("#powerstate").text().toLowerCase().indexOf("on")>=0;
			}
			
			function isPowerOff(){
				return $("#powerstate").text().toLowerCase().indexOf("off")>=0; 
			}
			
			function isSuspended(){
				return $("#powerstate").text().toLowerCase().indexOf("susp")>=0; 
			}
			
			function enable(buttonid){
				$("#"+buttonid).removeClass(disableClass).removeAttr('disabled');
			}
			function disable(buttonid){
				$("#"+buttonid).addClass(disableClass).attr('disabled','disabled');
			}
			
			function isDisabled(buttonid){
				return $("#"+buttonid).hasClass(disableClass);
			};
			
			function getctoken(){
				return  $(".csrftoken").text();
			}
			
			function disableAll(){
				disable("poweron");
				disable("poweroff");
				disable("reset");
				disable("suspend");
			//	disable("sendCAD");
			}
			
			function setState(){
				disableAll();
				
				if (isPowerOn()){
					enable("poweroff");
					enable("reset");
					enable("suspend");
					enable("sendCAD");
					
				}else if (isPowerOff()){
					enable("poweron");
					
				}else if (isSuspended()){
					//power on, power off is enabled
					enable("poweron");
					enable("poweroff");
					
				}
			}
			
			setState();
			
			function getvmid(){
				var search= window.location.search;

				if (!search){
					return "";
				}
				var start = search.indexOf('?');
				if (start<0){
					return "";
				}
				search = search.substr(start+1).split("&");
				if (!search || search.length<1){
					return "";
				}
				for (var i=0;i<search.length;i++){
					var pair = search[i].split("=");
					if (pair && pair[0] && pair[0]=="vmPatternId"){
						return pair[1];
					}
				}
				return "";
			}
			var vmId = getvmid();
			
			
			$( "#power-process" ).dialog({
				resizable: false,
				autoOpen: false,
				height: "auto",
				width: "400px",
				modal: true
			});
			
			
			function heartbeat(){
				if (!_hasEvent){
					return;
				}
				_hasEvent = false;
				var url="../heartbeat?action=heartbeat&vmid="+vmId;
				$.ajax({
					type: "GET",
					url: url,
					success: function(msg){
						// do nothing
						
					},
					error:function(XMLHttpRequest, textStatus, errorThrown){
						//reload when heartbeat failed
						window.location.reload();
					}
				});
			}
			
			function vmAction(action, vmId){
				var url="../vmAction";
				disableAll();
				if (action=="poweron" || action == "reset"){
					$("#powerstate").text("poweron");
				}else{
					$("#powerstate").text(action);
				}
				$( "#power-process" ).dialog( "open" );
				
				$.ajax({
					type: "POST",
					url: url,
					data: {action: action, vmid: vmId, csrftoken: getctoken()},
					success: function(msg){
						$( "#power-process" ).dialog( "close" );
						
						console
						.log("Power action successful:"+action+" msg:"+msg);
						// reload this console page
						if (action=="poweron" || action == "reset"){
							window.location.reload();
						}
						setState();
						
					},
					error:function(XMLHttpRequest, textStatus, errorThrown){
						$( "#power-process" ).dialog( "close" );
						
						console
						.log("Power action failed!"+ textStatus+" "+ errorThrown);
						alert("Power action failed!");
						window.location.reload();
					}
				});
			}


			$( "#confirm-off" ).dialog({
				resizable: false,
				autoOpen: false,
				height: "auto",
				width: "400px",
				modal: true,
				buttons: {
					"Yes": function() {
						vmAction("poweroff", vmId);
						$( this ).dialog( "close" );
					},
					"No": function() {
						$( this ).dialog( "close" );
					}
				}
			});

			$( "#confirm-suspend" ).dialog({
				resizable: false,
				autoOpen: false,
				height: "auto",
				width: "400px",
				modal: true,
				buttons: {
					"Yes": function() {
						vmAction("suspend", vmId);
						$( this ).dialog( "close" );
					},
					"No": function() {
						$( this ).dialog( "close" );
					}
				}
			});

			$( "#confirm-reset" ).dialog({
				resizable: false,
				autoOpen: false,
				height: "auto",
				width: "400px",
				modal: true,
				buttons: {
					"Yes": function() {
						vmAction("reset", vmId);
						$( this ).dialog( "close" );
					},
					"No": function() {
						$( this ).dialog( "close" );
					}
				}
			});

			$("#poweron").click(
					function() {
						if (isDisabled("poweron")){
							return;
						}
						vmAction("poweron", vmId);
					});

			$("#poweroff").click(
					function() {
						if (isDisabled("poweroff")){
							return;
						}
						$( "#confirm-off" ).dialog("open");

					});

			$("#suspend").click(
					function() {
						if (isDisabled("suspend")){
							return;
						}
						$( "#confirm-suspend" ).dialog("open");
					});
			
			$("#reset").click(
					function() {
						if (isDisabled("reset")){
							return;
						}
						$( "#confirm-reset" ).dialog("open");
					});
			
			setInterval(heartbeat,30000);
			
			
//////////////////////////////////////////////////////////////////////
			//	handle fullscreen & touchie (hide banner)
			//////////////////////////////////////////////////////////////////////
			
			var Fullscreen = {
				isFullscreen: function() {
					return document.fullscreenElement
							|| document.mozFullScreen
							|| document.mozFullScreenElement
							|| document.webkitFullscreenElement
							|| document.MSFullscreenElement
				},
				enter: function(target) {
					if (!target)
						target = document.documentElement;
					if (target.requestFullscreen) {
						target.requestFullscreen();
					} else if (target.webkitRequestFullscreen) {
						target.webkitRequestFullscreen();
					} else if (target.mozRequestFullScreen) {
						target.mozRequestFullScreen();
					} else if (target.msRequestFullscreen) {
						target.msRequestFullscreen();
					}
				},
				exit: function() {
					if (document.exitFullscreen)
						document.exitFullscreen();
					else if (document.msExitFullscreen)
						document.msExitFullscreen();
					else if (document.mozCancelFullScreen)
						document.mozCancelFullScreen();
					else if (document.webkitCancelFullScreen)
						document.webkitCancelFullScreen();
				},
				isSupported: function() {
					return document.fullscreenEnabled || 
						document.webkitFullscreenEnabled || 
						document.mozFullScreenEnabled ||
						document.msFullscreenEnabled;
				}
			};
			/*
			function onFullscreen() {
			}

			document.addEventListener("fullscreenchange", onFullscreen);
			document.addEventListener("fullscreenchange", onFullscreen);
			document.addEventListener("webkitfullscreenchange", onFullscreen);
			document.addEventListener("mozfullscreenchange", onFullscreen);
			document.addEventListener("MSFullscreenChange", onFullscreen);
			*/
			
			$('#fullscreen').click(function() {
				
				if (!Fullscreen.isSupported()) {
					alert("Fullscreen is not supported by the current browser.");
					return;
				}
				
				//var target = document.getElementById("console");
					
				if (Fullscreen.isFullscreen()) {
					Fullscreen.exit();
					toggleBar(true);
				} else {
					// go full-screen
					Fullscreen.enter();
					toggleBar(false);
				}
			});
			
			function toggleBar(show) {
				var bar = $('#bar');
				var consoleDiv = $('#console');
				
				var shouldShow = bar.css('display') == 'none';
				if (!(show === true || show === false))
					show = shouldShow;
				else if (show != shouldShow)
					return;
				
				if (show) {
					bar.css('display', 'block');
					consoleDiv.height(consoleDiv.height() - bar.height());
				} else {
					bar.css('display', 'none');
					consoleDiv.height(consoleDiv.height() + bar.height());
				}
			}
			$('#touchy').click(toggleBar);
			function repositionTouchy() {
				var touchy = $('#touchy');
				touchy.css('left', ($('#bar').width() - touchy.width()) / 2);
			}
			$(window).resize(repositionTouchy);
			repositionTouchy();
			$('#touchy').css('display', 'inline');
			

		});