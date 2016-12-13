<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<title>Facebook Login JavaScript Example</title>
<meta charset="UTF-8">

    <!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      window.fbAsyncInit = function() {
    	 	 
        FB.init({
          appId      : '${grailsApplication.config.facebook.api.key}',
          cookie	 : true,
          xfbml      : true,
          version    : 'v2.1'
        });

      };

      (function(d, s, id){
         var js, fjs = d.getElementsByTagName(s)[0];
         if (d.getElementById(id)) {return;}
         js = d.createElement(s); js.id = id;
         js.src = "//connect.facebook.net/en_US/sdk.js";
         fjs.parentNode.insertBefore(js, fjs);
       }(document, 'script', 'facebook-jssdk'));
      
    
      // Load the Visualization API and the piechart package.
      google.load('visualization', '1.0', {'packages':['corechart']});
      
      // Set a callback to run when the Google Visualization API is loaded.
      google.setOnLoadCallback(drawChart);

      // Callback that creates and populates a data table, 
      // instantiates the pie chart, passes in the data and
      // draws it.
      function drawChart() {

      // Create the data table.
      var data = new google.visualization.DataTable();
      data.addColumn('string', 'Topping');
      data.addColumn('number', 'Slices');
      data.addRows([
        ['Social', ${status.facebook_count}],
        ['Boundary Spanner', ${status.overlap}],
        ['Pro Guru', ${status.linkedin_count}], 
        ['Organized', ${status.disjoint}]
      ]);

      // Set chart options
      var options = {'title':'What are you? ',
                     'width':300,
                     'pieHole': 0.5,
                     'backgroundColor': 'transparent',
                     'height':200};

      // Instantiate and draw our chart, passing in some options.
      var chart = new google.visualization.PieChart(document.getElementById('chart_div'));

      google.visualization.events.addListener(chart, 'ready', function () {
        });
      
      chart.draw(data, options);
    }
      
    function postOnFB() {
      FB.ui({
		  method: 'share',
		  href: 'http://www.lisnx.com/mobile/api/getSocialStatus?id=1',
		}, function(response){});
    }
      
    </script>
      <style>
    .chartWithMarkerOverlay {
    	position: relative;
   	 	width: 700px;
	}
	.overlay-text {
	    width: 200px;
	    height: 200px;
	    position: absolute;
	    top: 300px;   /* chartArea top  */
	    left: 100px; /* chartArea left */
	}
	.overlay-marker {
	    width: 50px;
	    height: 50px;
	    position: absolute;
	    top: 252px;   /* chartArea top */
	    left: 280px; /* chartArea left */
	}
   </style>
   <script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-27274582-1', 'auto');
  ga('send', 'pageview');

</script>
  </head>

  <body background="/images/promo-bg.png" style="background-repeat:no-repeat">
<!--Div that will hold the pie chart-->
    <div id="chart_div" style="width:400; height:300; top:170px; left:175px"></div>
    <div class="overlay-text">
   <div style="font-family:'Arial Black'; font-size: 32px;">88</div>
   <div style="color: #b44; font-family:'Arial Black'; font-size: 32px; letter-spacing: .21em; margin-top:-23px; margin-left:5px;">zombie</div>
   <div style="color: #444; font-family:'Arial Black'; font-size: 32px; letter-spacing: .15em; margin-top:-10px; margin-left:5px;">attacks</div>
 </div>
     <div class="overlay-marker">
    <img src="https://developers.google.com/chart/interactive/images/zombie_150.png" height="50">
 </div>
 <div id="fbSection">
		<div id="fbStatus"></div>
		<img src="/images/facebook_share_button.png" onclick="postOnFB" style="padding-top:100px">
		
	</div>
  </body>
</html>