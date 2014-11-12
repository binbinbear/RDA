$.extend(true, $.hik.jtable.prototype.options, {
	//columnResizable: false,
	//animationsEnabled: false,
	jqueryuiTheme: true,
	ajaxSettings: {
		type: 'GET'
	}
});


$(document).ready(function () {

	//Prepare jtable plugin
	$('#TableContainer').jtable({
		title: 'Application Pool Usage and Limit',
		paging: false,
		sorting: false,
		//defaultSorting: 'lastAccessTime DESC',
		selecting: false, //Enable selecting
		multiselect: false, //Allow multiple selecting
		selectingCheckboxes: false, //Show checkboxes on first column
		//selectOnRowClick: false, //Enable this to only select using checkboxes
		actions: {
			listAction: './limit/list',
			//deleteAction: './limit/delete'
			updateAction: './limit/update'
			//createAction: '/limit/create'
		},
		fields: {
			appId: {
				title: 'Application Pool',
				width: '50%',
				key: true,
				create: false,
				edit: false,
				list: true
			},		
			concurrency: {
				title: 'Sessions',
				width: '20%',
				type: 'text',
				create: false,
				edit: false
			},
			limit: {
				title: 'Limit',
				width: '20%',
				type: 'text',
				create: false,
				edit: true
			}
		}
	});

	$('#TableContainer').jtable('load');
});

