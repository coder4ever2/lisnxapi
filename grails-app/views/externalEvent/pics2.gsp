<!doctype html>
<html>
    <head>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.js"></script>
        <script src="galleria/galleria-1.4.min.js"></script> 
        
         <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>LISNx - connect instantly with people you meet</title>

    <!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="css/reset.css" rel="stylesheet">
    <link href="css/template.css" rel="stylesheet">
    <link href="css/jquery.bxslider.css" rel="stylesheet">

    <!-- Custom Fonts -->
	<link href='http://fonts.googleapis.com/css?family=Lato:400,300,700,700italic,400italic,300italic' rel='stylesheet' type='text/css'>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-27274582-1', 'auto');
  ga('send', 'pageview');

</script>
    </head>
    <body>
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-left">
                    <li class="hidden">
                        <a href="#page-top"></a>
                    </li>
                    <li class="page-scroll">
                        <a href="#explore">Explore</a>
                    </li>
                    <li class="page-scroll">
                        <a href="#blog">Blog</a>
                    </li>
                    <li class="page-scroll">
                        <a href="#about">About</a>
                    </li><!--
                    <li class="page-scroll">
                        <a href="https://www.lisnx.com/mobile/promo">Promo</a>
                    </li>-->
                </ul>
            </div>
        <div class="galleria">
        
		<g:each in ="${pics}" var="pic">
				
				<img src="${pic}" width="400">
				<g:link action="shareOnFb" id="${pic}" 
					oncomplete="showSpinner(false);" onloading="showSpinner(true);" update="bookDetails">
					<g:img uri="http://theonefly.com/wp-content/uploads/2014/12/fbshare.png" width="50"/>
            	</g:link>    
            	<g:checkBox name="select to share"/>
            </g:each>
        </div> 
        <script>
            Galleria.loadTheme('galleria/themes/classic/galleria.classic.min.js');
            Galleria.run('.galleria');
        </script> 
    </body>
</html>