
// usage: log('inside coolFunc', this, arguments);
// paulirish.com/2009/log-a-lightweight-wrapper-for-consolelog/
window.log = function(){
  log.history = log.history || [];   // store logs to an array for reference
  log.history.push(arguments);
  if(this.console) {
    arguments.callee = arguments.callee.caller;
    var newarr = [].slice.call(arguments);
    (typeof console.log === 'object' ? log.apply.call(console.log, console, newarr) : console.log.apply(console, newarr));
  }
};

// make it safe to use console.log always
(function(b){function c(){}for(var d="assert,count,debug,dir,dirxml,error,exception,group,groupCollapsed,groupEnd,info,log,timeStamp,profile,profileEnd,time,timeEnd,trace,warn".split(","),a;a=d.pop();){b[a]=b[a]||c}})((function(){try
{console.log();return window.console;}catch(err){return window.console={};}})());


// place any jQuery/helper plugins in here, instead of separate, slower script files.

if (!window.console) {
	console = {
		log : function() {
		}
	};
}


function relayout() {
	if ($("#bar").is(":visible")) {
		$("#console").height(
				$(window).height() - $("#bar").outerHeight());
	} else {
		$("#console").height($(window).height());
	}
	$("#spinner").css("margin-left",
			$("#console").width() / 2 - $("#spinner").width());
	_wmks.wmks("rescale");
}

function getURLParameter(name) {
	return decodeURIComponent((RegExp(name + '=' + '(.+?)(&|$)').exec(
			location.search) || [ , null ])[1]);
}

$(document)
		.ready(
				function() {
					_wmks = $("<div/>")
							.wmks({
								"useVNCHandshake" : false
							})
							.bind(
									"wmksconnecting",
									function() {
										console
												.log("The console is connecting");
										$("#bar").slideUp("slow",
												relayout);
									})
							.bind(
									"wmksconnected",
									function() {
										console
												.log("The console has been connected");
										$("#spinner").removeClass(
												"spinner");
										$("#bar").slideDown("fast",
												relayout);
									})
							.bind(
									"wmksdisconnected",
									function(evt, info) {
										console
												.log("The console has been disconnected");
										console.log(evt, info);
										if($("#powerstate").text().toLowerCase().indexOf("off")>=0){
											//powering off
											$('#console .wmks')
											.html(
													"The VM is powered off.");
								
										}else if($("#powerstate").text().toLowerCase().indexOf("susp")>=0){
											//powering off
											$('#console .wmks')
											.html(
													"The VM is suspended.");
								
										}else{
											$('#console .wmks')
											.html(
													"The console has been disconnected. Close this window and re-launch the console to reconnect.");
										}
											$('#console .wmks').css(
												'text-align', 'center');
										$('#console .wmks').css(
												'color', 'white');
										$("#bar").slideDown("fast",
												relayout);
										$("#spinner").removeClass(
												"spinner");
									})
							.bind(
									"wmkserror",
									function(evt, errObj) {
										console.log("Error!");
										console.log(evt, errObj);
										var idx = errObj.error
												.lastIndexOf(".") + 1;
										alert(errObj.error.substr(idx)
												+ " - " + errObj.msg);
									})
							.bind(
									"wmksresolutionchanged",
									function(canvas) {
										console
												.log("Resolution has changed!");
										$('#console .wmks canvas').css(
												'position', 'absolute');
										$('#console .wmks canvas')
												.css(
														'margin-left',
														-1
																* (canvas.target.childNodes[0].clientWidth)
																/ 2
																+ 'px');
										$('#console .wmks canvas')
												.css(
														'margin-top',
														-1
																* (canvas.target.childNodes[0].clientHeight)
																/ 2
																+ 'px');
										$('#console .wmks canvas').css(
												'top', '50%');
										$('#console .wmks canvas').css(
												'left', '50%');
									}).appendTo("#console");
					relayout();

					//listen for resize events
					$(window).resize(function() {
						relayout();
					});

					// if params are provided, no need to show chrome
					if (location.search) {
						$("#bar").hide();
						
						var vmurl = $(".vmurl").text();
						_wmks.wmks("connect", vmurl);
						$("#spinner").addClass("spinner");
					}

					document.title = $("#vmTitle").text();

					
						$("#vmrc").css({
							"visibility" : "hidden"
						});
					

					$("#sendCAD").click(
							function() {
								_wmks.wmks('sendKeyCodes', [
										17,
										18,
										46 ]);
							});

				});