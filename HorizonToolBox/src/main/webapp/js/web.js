var Auditing = {};

Auditing.submit = function(){
	//check user name, password and domain
	var username = $("#username").val();
	var password = $("#password").val();
	if (!username || !password ){
		alert("Username and password can't be null!");
		return;
	}
	$("#loginForm").submit();
};


var keyPress = function(event){
	if (event.which == 13){
		//enter
		Auditing.submit();
	}
	
};

var init = function(){
	$("#submitButton").click(Auditing.submit);
	$("#loginForm").keypress(keyPress);
};

$(window).load(init);



