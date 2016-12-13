<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<title>Facebook Login JavaScript Example</title>
<meta charset="UTF-8">

    <!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      var social=1, boundary_spanner=0, pro_guru=91, organized=77;
    
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
        ['Social', social],
        ['Boundary', boundary_spanner],
        ['Pro Guru', pro_guru], 
        ['Organized', organized]
      ]);

      // Set chart options
      var options = {'title':'',
    		         'legend': 'none',
    	             'pieSliceText': 'percentage',
                     'width':650,
                     'pieHole': 0.6,
                     'backgroundColor': 'transparent',
                     'height':400};

      // Instantiate and draw our chart, passing in some options.
      var chart = new google.visualization.PieChart(document.getElementById('chart_div'));

   // Wait for the chart to finish drawing before calling the getImageURI() method.
      google.visualization.events.addListener(chart, 'ready', function () {
        chart_div.innerHTML = '<img src="' + chart.getImageURI() + '">';
        console.log(chart_div.innerHTML);
        document.getElementById('imageUrl').innerHTML = chart.getImageURI();
      });
      
      chart.draw(data, options);
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
	    top: 165px;   /* chartArea top */
	    left: 142px; /* chartArea left */
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
  <p> Testing google charts</p>
<!--Div that will hold the pie chart-->
    <div id="chart_div" style="width:1000; height:850"></div>
    <div class="overlay-text">
 </div>
 <p style="top:200;">Google charts image url <p id="imageUrl">URL</p> </p> 
  </body>
</html>