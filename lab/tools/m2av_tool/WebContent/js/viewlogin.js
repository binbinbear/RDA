$(function() {
	$("#login_btn").click(function(event) {
		view_IP = $('#view_IP').val();
		view_name = $('#view_name').val();
		view_password = $('#view_password').val();
		view_domain = $('#view_domain').val();
		if (view_name == "" || view_password == ""|| view_domain == "") {
					$("#msg").html("<font color='red'>All these parameters are required<front>");
							return false;
						}
					});

	var getRequest = function() {
		var url = location.search;
		var request = new Object();
		if (url.indexOf("?") != -1) {
			var str = url.substr(1);
			strs = str.split("&");
			for (var i = 0; i < strs.length; i++) {
				request[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
			}
		}
		return request;
	}

	$(document).ready(function() {
		var request = new Object();
		request = getRequest();
		if (request["msg"] == undefined) {
			return true;
		} else if (request["msg"] == "loginFailed") {
			$("#msg").html("<font color='red'>invalid user name or password<front>");
			return false;
		}
	});
});