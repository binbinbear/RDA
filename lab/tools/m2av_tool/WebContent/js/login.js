$(function() {
	$("#login_btn").click(function(event) {
		volume_host = $('#host_address').val();
		volume_name = $('#user_account_name').val();
		volume_password = $('#user_password').val();
		if (volume_host == ""|| volume_name == "" || volume_password == "") {
					$("#flash_messages").html("<font color='red'>All these parameters are required<front>");
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
			$("#flash_messages").html("<font color='red'>invalid user name or password<front>");
			return false;
		}
	});
});