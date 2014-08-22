// scrolling-to-top button
jQuery(document).ready(function() {
	var duration = 500;
	jQuery(window).scroll(function() {
		if (jQuery(this).scrollTop() > 0) {
			jQuery('#scroll-to-top').fadeIn(duration);
		} else {
			jQuery('#scroll-to-top').fadeOut(duration);
		}
	});
				
	jQuery('#scroll-to-top').click(function(event) {
		event.preventDefault();
		jQuery('html, body').animate({scrollTop: 0}, duration);
		return false;
	});
});
