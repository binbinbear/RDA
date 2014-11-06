if (!window.ToolBox){
	window.ToolBox= {};
}
if (!ToolBox.Policy || !ToolBox.Policy.init){
	ToolBox.Policy = {

			getViewPools: function(){
				$.ajax({
					url: './common/desktoppools',
					type: "GET",
					success: function (data) {
						 var poolbody = $("#desktoppool");
						 for (var i= 0; i< data.length; i++){
								var dSD = data[i];
								var option = "<option value=\""   +  dSD.name + "\"> "+ dSD.name+"</option>";
							 	poolbody.append(option);
							}
					}, 
					error: function(data) {
					}
				});
			},

			init: function(){
				$('#myTab a').click(function (e) {
					  e.preventDefault()
					  $(this).tab('show')
					})
				$("#submitBtn").click(ToolBox.Policy.updatePlicies);			
				ToolBox.Policy.getViewPools();
			},

			updatePlicies: function (){
				$( "#message" ).empty();
		    	$("#message").append("<div class=\"loadingdiv\"></div>");
		    	
				$.ajax({
					url: './policy/updatepolicies',
					type: "GET",
					//做一个判断，传有值得参数。for e: if $("#desktoppool").val() == "" | $("#desktoppool").val().equal(null)),不加入data
					data:{pool:$("#desktoppool").val(), clipboard:$("#clipboard").val(), 
						  device:$("#device").val(), productionLogs:$("#productionLogs").val(), 
						  debugLogs:$("#debugLogs").val(), logSize:$("#logSize").val(), 
						  logDirectory:$("#logDirectory").val(), 
						  sendLogs:$("#sendLogs").val(), interval:$("#interval").val(),
						  overallCPU:$("#overallCPU").val(), overallMemory:$("#overallMemory").val(),
						  processCPU:$("#processCPU").val(), processMemory:$("#processMemory").val(),
						  processCheck:$("#processCheck").val(), certificateRevocation:$("#certificateRevocation").val(),
						  cachedRevocation:$("#cachedRevocation").val(), checkTimeout:$("#checkTimeout").val(),
						  //
						  lossless:$("#lossless").val(), maximum:$("#maximum").val(),
						  MTU:$("#MTU").val(), floor:$("#floor").val(),
						  enDisAudio:$("#enDisAudio").val(), limit:$("#limit").val(),
						  SSL:$("#SSL").val(), encryption:$("#encryption").val(),
						  USB:$("#USB").val(), TCP:$("#TCP").val(),
						  UDP:$("#UDP").val(), channels:$("#channels").val(),
						  image:$("#image").val(), FIPS:$("#FIPS").val(),
						  vSphere:$("#vSphere").val(), synchronization:$("#synchronization").val(),
						  alternate:$("#alternate").val(), CAD:$("#CAD").val(),
						  transport:$("#transport").val(), verbosity:$("#verbosity").val(),
						  timeindays:$("#timeindays").val(), sizeinmb:$("#sizeinmb").val(),
						  //
						  exclude:$("#exclude").val(), splitdevice:$("#splitdevice").val(),
						  other:$("#other").val(), HID:$("#HID").val(),
						  inputdevices:$("#inputdevices").val(), outputdevices:$("#outputdevices").val(),
						  keyboard:$("#keyboard").val(), videodevices:$("#videodevices").val(),
						  smartcards:$("#smartcards").val(), autodevice:$("#autodevice").val(),
						  excludeVP:$("#excludeVP").val(), includeVP:$("#includeVP").val(),
						  excludeDF:$("#excludeDF").val(), includeDF:$("#includeDF").val(),
						  exclludeAll:$("#exclludeAll").val(), MMR:$("#MMR").val(),
						  multimedia:$("#multimedia").val(), directRDP:$("#directRDP").val(),
						  singleSignon:$("#singleSignon").val(), timeout:$("#timeout").val(),
						  credentialFilter:$("#credentialFilter").val(), usingDNS:$("#usingDNS").val(),
						  disableTZName:$("#disableTZName").val(), toggle:$("#toggle").val(),
						  onConnect:$("#onConnect").val(), onReconnect:$("#onReconnect").val(),
						  onDisconnect:$("#onDisconnect").val(), showIcon:$("#showIcon").val(),
						  frameworkChannel:$("#frameworkChannel").val(), unity:$("#unity").val(),
						  maxFrames:$("#maxFrames").val(), maxImageHeight:$("#maxImageHeight").val(),
						  maxImageWidth:$("#maxImageWidth").val(), defaultImageHeight:$("#defaultImageHeight").val(),
						  defaultImageWidth:$("#defaultImageWidth").val(), disableRTAV:$("#disableRTAV").val(),
						  portNumber:$("#portNumber").val(), sessionTimeout:$("#sessionTimeout").val(),
						  disclaimerEnabled:$("#disclaimerEnabled").val(), disclaimerText:$("#disclaimerText").val(),
						  applictionsEnabled:$("#applictionsEnabled").val(), autoConnect:$("#autoConnect").val(),
						  alwaysConnect:$("#alwaysConnect").val(), screenSize:$("#screenSize").val(),
						  PCoIPport:$("#PCoIPport").val(), RDPport:$("#RDPport").val(),
						  blastPort:$("#blastPort").val(), IPaddress:$("#IPaddress").val(),
						  channelPort:$("#channelPort").val(), USBenabled:$("#USBenabled").val(),
						  MMRenabled:$("#MMRenabled").val(), resetEnabled:$("#resetEnabled").val(),
						  USBautoConnect:$("#USBautoConnect").val(), cacheTimeout:$("#cacheTimeout").val(),
						  DisSessionTimeout:$("#DisSessionTimeout").val() },
					success: function (data) {						
						var loadingDiv = $(".loadingdiv");
				    	loadingDiv.removeClass("loadingdiv");
				    	$(function(){
							var tr="<div class='ui-state-highlight ui-corner-all' style='padding: 0 .7em; display:none;'><p><span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;'></span>Successful !</p></div>";
							$( "#message" ).empty();
							$( "#message" ).append( tr );
							$("div.ui-state-highlight").slideDown(1500,function(){setTimeout(function(){$("div.ui-state-highlight").slideUp(1500);},"2000");});
							//setTimeout("$( '#message' ).empty();",2000);
							//$("div.ui-state-highlight").slideDown(1000,function(){$("div.ui-state-highlight").slideUp(2000);});
							//$("div.ui-state-highlight").slideUp(2000);
						});
				    	
					},
					error: function (){
						var loadingDiv = $(".loadingdiv");
				    	loadingDiv.removeClass("loadingdiv");
				    	$(function(){
							var tr="<div class='ui-state-error ui-corner-all' style='padding: 0 .7em; display:none;'><p><span class='ui-icon ui-icon-alert' style='float: left; margin-right: .3em;'></span>ERROR !</p></div>";
							$( "#message" ).empty();
							$( "#message" ).append( tr );
							//setTimeout("$( '#message' ).empty();",2000);
							$("div.ui-state-error").slideDown(1500,function(){setTimeout(function(){$("div.ui-state-error").slideUp(1500);},"2000");});
						});
					}
				}); 
			}
	};
}


$(document).ready(function(){  
	ToolBox.Policy.init();
});
/*
  <script>
  $(function() {
    $( "#dialog" ).dialog();
  });
  </script>
</head>
<body>
 
<div id="dialog" title="Basic dialog">
  <p>This is the default dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p>
</div>
*/