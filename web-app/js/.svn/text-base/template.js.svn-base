/*!
 * Start Bootstrap - Freelancer Bootstrap Theme (http://startbootstrap.com)
 * Code licensed under the Apache License v2.0.
 * For details, see http://www.apache.org/licenses/LICENSE-2.0.
 */

// jQuery for page scrolling feature - requires jQuery Easing plugin
$(function() {
    $('.page-scroll a, p.copy a, .positionCircle a').bind('click', function(event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: $($anchor.attr('href')).offset().top
        }, 1500, 'easeInOutExpo');
        event.preventDefault();
    });
});

// Floating label headings for the contact form
$(function() {
    $("body").on("input propertychange", ".floating-label-form-group", function(e) {
        $(this).toggleClass("floating-label-form-group-with-value", !! $(e.target).val());
    }).on("focus", ".floating-label-form-group", function() {
        $(this).addClass("floating-label-form-group-with-focus");
    }).on("blur", ".floating-label-form-group", function() {
        $(this).removeClass("floating-label-form-group-with-focus");
    });
});

// Highlight the top nav as scrolling occurs
$('body').scrollspy({
    target: '.positionCircle'
})

// Closes the Responsive Menu on Menu Item Click
$('.navbar-collapse ul li a').click(function() {
    $('.navbar-toggle:visible').click();
});
/*var parallax = document.getElementById("bodyBackground");
var speed = -10;

window.onscroll = function() {
  var yOffset = window.pageYOffset;
  parallax.style.backgroundPosition = "0px " + (yOffset / speed) + "px";
}*/
$(document).ready(function(){
  	$('.bxslider1').bxSlider({
	  	auto: true,
  		autoControls: true,
		controls: false
	});
	$('.bxslider2').bxSlider({
	    pagerCustom: '#bx-pager',
	    auto: true,
  		autoControls: true,
		controls: false
	});
	var HOME_SCROLLPOINTS = [];
	var counter = 0;
	
	var topPart = $('.topPart').outerHeight();
		counter += topPart;
		HOME_SCROLLPOINTS.push(counter);
		
	var explore = $('.explore').outerHeight();
		counter += explore;
		HOME_SCROLLPOINTS.push(counter);
		
	var blog = $('.blog').outerHeight();
		counter += blog;
		HOME_SCROLLPOINTS.push(counter);
		
	var about = $('.about').outerHeight();
		counter += about;
		HOME_SCROLLPOINTS.push(counter);
	var scrollTop = $(window).scrollTop();
	
	if(scrollTop < HOME_SCROLLPOINTS[0]){
		$('.sohp-arrow-up, .sohp-ios, .sohp-android').hide();
	} else {
		$('.sohp-arrow-up, .sohp-ios, .sohp-android').show();
	};
	if (scrollTop >= HOME_SCROLLPOINTS[2]){
		$('.sohp-arrow-down').hide();
	} else {
		$('.sohp-arrow-down').show();
	}
	$(window).scroll(function() {
		var scrollTop = $(window).scrollTop();
		if(scrollTop < HOME_SCROLLPOINTS[0]){
			$('.sohp-arrow-up, .sohp-ios, .sohp-android').hide();
		} else {
			$('.sohp-arrow-up, .sohp-ios, .sohp-android').show();
		};
		if (scrollTop >= HOME_SCROLLPOINTS[2]){
			$('.sohp-arrow-down').hide();
		} else {
			$('.sohp-arrow-down').show();
		}
	});
	$('.sohp-arrow-up').on('click' , function (event) {
		var scrollTop = $(window).scrollTop();
		if(scrollTop < HOME_SCROLLPOINTS[1] && scrollTop >= HOME_SCROLLPOINTS[0]){
			$('html,body').animate({
				scrollTop: 0
			}, 1000);
		} else if(scrollTop < HOME_SCROLLPOINTS[2] && scrollTop >= HOME_SCROLLPOINTS[1]){
			$('html,body').animate({
				scrollTop: HOME_SCROLLPOINTS[0]
			}, 1000);
		} else if(scrollTop < HOME_SCROLLPOINTS[3] && scrollTop >= HOME_SCROLLPOINTS[2]){
			$('html,body').animate({
				scrollTop: HOME_SCROLLPOINTS[1]
			}, 1000);
		}
	});
	$('.sohp-arrow-down').on('click' , function (event) {
		var scrollTop = $(window).scrollTop();
		if(scrollTop < HOME_SCROLLPOINTS[0] && scrollTop >= 0){
			$('html,body').animate({
				scrollTop: HOME_SCROLLPOINTS[0]
			}, 1000);
		} else if(scrollTop < HOME_SCROLLPOINTS[1] && scrollTop >= HOME_SCROLLPOINTS[0]){
			$('html,body').animate({
				scrollTop: HOME_SCROLLPOINTS[1]
			}, 1000);
		} else if(scrollTop < HOME_SCROLLPOINTS[2] && scrollTop >= HOME_SCROLLPOINTS[1]){
			$('html,body').animate({
				scrollTop: HOME_SCROLLPOINTS[2]
			}, 1000);
		}
	});
});