$.extend(true, $.hik.jtable.prototype.options, {
	//columnResizable: false,
	//animationsEnabled: false,
	jqueryuiTheme: true,
	ajaxSettings: {
		type: 'GET'
	}
});

function showConfirmDialog(title, msg, onOK) {
	var dynamicDialog = $('<div id="MyDialog">\
		<P>' + msg + '</P>\
		</div>');
		
	dynamicDialog.dialog({ title: title,
		modal: true,
		buttons: [{ 
			text: "OK", 
			click: function() {
				$(this).dialog("close").remove(); 
				onOK();
			}
		}, { 
			text: "Cancel", 
			click: function () {
				$(this).dialog("close").remove(); 
			} 
		}]
	});
}

/*
function showProgressDialog(title, msg) {
}
*/

function sendAddToWhitelist(recordId) {

/*
	var params = {recordId: recordId};
	$.ajax("./deviceFilter/addToWhitelist",
		params,
		function(data) {
		},
		'json'
	)
	.done(function( data ) {
		alert( "Data Loaded: " + data );
	})
	.fail(function(data) {
		alert( "error: " + data );
	})
	.always(function() {
		alert( "finished" );
		dlg.dialog('destroy').remove();
	});
	*/
	
	self.location.href = './deviceFilter/addToWhitelist?recordId=' + recordId;
}

function addToWhitelist(recordId) {

	var addToWhitelistProcess = function() {
		//var dlg = showProgressDialog();
		sendAddToWhitelist(recordId);
	}
	
	showConfirmDialog("Add device to whitelist", "Do you want to add the device to white list?", addToWhitelistProcess);
}

$(document).ready(function () {

	//Prepare jtable plugin
	$('#WhitelistContainer').jtable({
		title: 'Device Whitelist',
		paging: false,
		sorting: false,
		defaultSorting: 'lastAccessTime DESC',
		selecting: false, //Enable selecting
		multiselect: false, //Allow multiple selecting
		selectingCheckboxes: false, //Show checkboxes on first column
		//selectOnRowClick: false, //Enable this to only select using checkboxes
		actions: {
			listAction: './deviceFilter/whitelist',
			deleteAction: './deviceFilter/delete'
			//updateAction: '/deviceFilter/update',
			//createAction: '/deviceFilter/create'
		},
		fields: {
			recordId: {
				key: true,
				create: false,
				edit: false,
				list: false
			},		
			clientId: {
				title: 'Device ID',
				width: '23%',
				type: 'text',
				create: false,
				edit: false
			},
			clientType: {
				title: 'Type',
				width: '12%',
				type: 'text',
				create: false,
				edit: false
			},
			userName: {
				title: 'Last User',
				width: '12%',
				type: 'text',
				create: false,
				edit: false
			},
			userDnsDomain: {
				title: 'Domain',
				width: '12%',
				type: 'text',
				create: false,
				edit: false
			},
			lastAccessTime: {
				title: 'Last Access',
				width: '70px',
				type: 'text',
				create: false,
				edit: false,
				sorting: false
			}
		}
	});

	$('#AccessLogContainer').jtable({
		title: 'Access Log',
		paging: false,
		sorting: false,
		defaultSorting: 'lastAccessTime DESC',
		selecting: false, //Enable selecting
		multiselect: false, //Allow multiple selecting
		selectingCheckboxes: false, //Show checkboxes on first column
		//selectOnRowClick: false, //Enable this to only select using checkboxes
		actions: {
			listAction: './deviceFilter/accessLog'
			//deleteAction: '/deviceFilter/delete',
			//updateAction: './deviceFilter/addToWhitelist',
			//createAction: '/deviceFilter/create'
		},
		fields: {
			recordId: {
				key: true,
				create: false,
				edit: false,
				list: false
			},		
			clientId: {
				title: 'Device ID',
				width: '23%',
				type: 'text',
				create: false,
				edit: false
			},
			clientType: {
				title: 'Type',
				width: '12%',
				type: 'text',
				create: false,
				edit: false
			},
			userName: {
				title: 'Last User',
				width: '12%',
				type: 'text',
				create: false,
				edit: false
			},
			userDnsDomain: {
				title: 'Domain',
				width: '12%',
				type: 'text',
				create: false,
				edit: false
			},
			time: {
				title: 'Last Access',
				width: '50px',
				type: 'text',
				create: false,
				edit: false,
			},
			status: {
				title: 'Status',
				width: '15%',
				type: 'text',
				create: false,
				edit: false
			},
			Action: {
				title: 'Allow',
				width: '40%',
				edit: false,
				display: function(data) {
					if (data.record.status == "BLOCKED") {
						return '<button class="jtable-my-add-action" onclick="addToWhitelist('
							+ data.record.recordId
							+ ')" title="Add to whitelist"></button>';
					}
					return "";
				}
			}
		}
	});

	$('#WhitelistContainer').jtable('load');
	$('#AccessLogContainer').jtable('load');
});

