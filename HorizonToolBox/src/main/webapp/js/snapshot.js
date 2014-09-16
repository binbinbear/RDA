$(function () {
    
    $( "#dialog" ).dialog({
        autoOpen: false,
        height: "auto",
        width: "auto"
    });
    

});

if (!window.AuditingApp){
	window.AuditingApp= {};
}
if (!AuditingApp.Snapshot || !AuditingApp.Snapshot.init){
	AuditingApp.Snapshot = {
			templates: [],
			vms: [],
			
			
			refreshModel: function(){
				$.ajax({
					url: './snapshot/report',
					type: "GET",
					success: function (data) {
						 AuditingApp.Snapshot.templates = data.templates;
						 AuditingApp.Snapshot.vms = data.vms;
						 AuditingApp.Snapshot.updateListView();
						 if (data.updatedDate){
							 var date = new Date(data.updatedDate);
							 $(".updateDate").text("Updated on "+ date.toLocaleString());
						 }else{
							 $(".updateDate").text("");
						 }
					}
				});    
				
			},
			
			renderSnapShot: function(snapshot){
				var li =  "<li><span>" ;
				if (snapshot.notInUse){
					li = "<li><span class = \"not-in-use\">"
				}
				 li = li + "<i class=\"glyphicon plus\"/><i	class=\"glyphicon glyphicon-camera\"></i> SnapShot</span> <b>"
					+snapshot.name + "</b><ul>" ;
				
				var pools = snapshot.linkedClonePools;
				for (var i=0;i<pools.length;i++){
					var pool = pools[i];
					li = li +  "<li><span><i class=\"glyphicon plus\"/><i	class=\"glyphicon glyphicon-cloud\"></i> Pool </span> <a class=\"hiddenDetail\">" +
					 pool.name + 		
					"</a><span	class=\"hidden\" hidden=\"hidden\">" +
					pool.information +
					"</span></li>";
				}
				
				var childrenSnapShots = snapshot.childrenSnapShots;
				for (var i=0;i<childrenSnapShots.length;i++){
					var childsnapshot = childrenSnapShots[i];
					li = li + AuditingApp.Snapshot.renderSnapShot(childsnapshot);
				}
				li = li + "</ul></li>";
				return li;
			},

			
			updateListView: function(){

				var vmDiv = $(".vms");
				var vms = AuditingApp.Snapshot.vms;
				if (vmDiv){
					vmDiv.empty();
					var ul = "<ul> ";
					for (var i=0;i<vms.length;i++){
						var vm = vms[i];
						ul = ul + "<li><span><i class=\"glyphicon plus\"/><i class=\"glyphicon glyphicon-folder-open\"></i> Parent VM</span> <b> " 
						+ vm.fullName
						+"</b> 	<ul> ";
						
						var children = vm.children;
						for (var j=0;j<children.length;j++){
							ul = ul + AuditingApp.Snapshot.renderSnapShot(children[j]);
						}
						
						ul = ul + " </ul> </li> ";
					}
					ul = ul+"</ul>";
					vmDiv.append(ul);
				}
				
				
				var templatesDiv = $(".templates");
				var templates = AuditingApp.Snapshot.templates;
				if (templatesDiv){
					templatesDiv.empty();
					var ul = "<ul> ";
					for (var i=0;i<templates.length;i++){
						var template = templates[i];
						ul = ul + "<li><span><i class=\"glyphicon plus\"/><i class=\"glyphicon glyphicon-folder-open\"></i> Template</span> <b> " 
						+ template.path
						+"</b> 	<ul> ";
						
						var fullClonePools = template.fullClonePools;
						for (var j=0;j<fullClonePools.length;j++){
							var fullClonePool = fullClonePools[j];
							ul = ul + "<li><span><i class=\"glyphicon plus\"/><i	class=\"glyphicon glyphicon-cloud\"></i> Pool</span> <a class=\"hiddenDetail\" >"
							+fullClonePool.name + "</a><span class=\"hidden\" hidden=\"hidden\">"  + fullClonePool.information
							+"</span></li>";
						}
						
						ul = ul + " </ul> </li> ";
					}
					ul = ul+"</ul>";
					templatesDiv.append(ul);
				}
				
			    $('a.hiddenDetail').on('click', function (e) {
			    	
			        var child = $(this).parent('li').find(' > span.hidden');
			        $("#dialog").html(child.html());
			        $( "#dialog" ).dialog( "open" );
			    });
			    
			    $('.tree li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');
			    $('.tree li.parent_li > span').on('click', function (e) {
			        var children = $(this).parent('li.parent_li').find(' > ul > li');
			        var icon = $(this).find('.plus');
			        if (children.is(":visible")) {
			            children.hide('fast');
			            $(this).attr('title', 'Expand this branch');
			            icon.addClass('glyphicon-plus');
			        } else {
			            children.show('fast');
			            $(this).attr('title', 'Collapse this branch');
			            icon.removeClass('glyphicon-plus');
			        }
			        e.stopPropagation();
			    });
				
			},
			
			init: function(){
				AuditingApp.Snapshot.refreshModel();
			}
	};
}



AuditingApp.Snapshot.init();

