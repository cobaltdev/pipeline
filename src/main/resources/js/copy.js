window.onload = function() {
	//copy to clipboard function
	ZeroClipboard.config({moviePath: 'http://cdnjs.cloudflare.com/ajax/libs/zeroclipboard/1.3.5/ZeroClipboard.swf', forceHandCursor: true});
				 
	var client = new ZeroClipboard(jQuery("#copy-button"));
	
	client.on('load', function(client) {

	    client.on('datarequested', function(client) {
	    	var text = window.location.href;
	    	client.setText(text);
	  	});
	 
	  	client.on('complete', function(client, args) {
	  		jQuery("#copyAlertStyle").fadeIn(500).delay(1500).fadeOut(2000);
	    	console.log("copied to clipboard: \n" + args.text );
		});
	});
	 
	client.on( 'wrongflash noflash', function() {
		ZeroClipboard.destroy();
	});
};

function selectText(textField) {
	textField.focus();
	textField.select();
}