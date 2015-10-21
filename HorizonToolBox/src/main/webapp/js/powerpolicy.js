$.extend(true, $.hik.jtable.prototype.options, {
	//columnResizable: false,
	//animationsEnabled: false,
	jqueryuiTheme: true,
	ajaxSettings: {
		type: 'GET'
	}
	
});
if (!window.ToolBox){
	window.ToolBox= {};
}

var dialog = $('#alarm-dialog').parent(),
    alarm_set = $('#alarm-set'),
    alarm_clear = $('#alarm-clear'),
  
 // Handle setting and clearing alamrs

    
$('.alarm-button').click(function(){
    // Show the dialog
    dialog.trigger('show');
});
 
dialog.find('.close').click(function(){
    dialog.trigger('hide')
});
 
dialog.click(function(e){
    // When the overlay is clicked, 
    // hide the dialog.
    if($(e.target).is('.overlay')){
        // This check is need to prevent
        // bubbled up events from hiding the dialog
        dialog.trigger('hide');
    }
});
 
alarm_set.click(function(){
    var valid = true, after = 0,
        to_seconds = [3600, 60, 1];
    dialog.find('input').each(function(i){
        // Using the validity property in HTML5-enabled browsers:
        if(this.validity && !this.validity.valid){
            // The input field contains something other than a digit,
            // or a number less than the min value
            valid = false;
            this.focus();
            return false;
        }
        after += to_seconds[i] * parseInt(parseInt(this.value));
    });
    if(!valid){
        alert('Please enter a valid number!');
        return;
    }
    if(after < 1){
        alert('Please choose a time in the future!');
        return; 
    }
    alarm_counter = after;
    dialog.trigger('hide');
});
 
alarm_clear.click(function(){
    alarm_counter = -1;
    dialog.trigger('hide');
});
 
// Custom events to keep the code clean
dialog.on('hide',function(){
    dialog.fadeOut();
}).on('show',function(){
 
    // Calculate how much time is left for the alarm to go off.
 

 
    // Update the input fields
    //dialog.find('input').eq(0).val(hours).end().eq(1).val(minutes).end().eq(2).val(seconds);
 
    //dialog.fadeIn();
 
});