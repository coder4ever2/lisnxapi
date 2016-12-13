<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<title>LISNx promo page</title>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
 <meta property="fb:app_id" content="164116363623152" />
<meta property="og:site_name" content="LISNx" />
<meta property="og:type" content="article" />
<meta property="og:title" content="Know what your Facebook and LinkedIn profiles convey about you" />
<meta property="og:image" content="${image}" />
<meta property="og:locale" content="en_US" />
<meta property="og:description" content="${description}" />

<!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
    <!-- Custom CSS -->
<link rel="stylesheet" href="${resource(dir: 'css', file: 'reset.css')}" type="text/css">
    
<link rel="stylesheet" href="${resource(dir: 'css', file: 'template.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'lisnx.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.bxslider.css')}" type="text/css">

<g:javascript library="jquery" plugin="jquery" />

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

<body id="promo-page" class="index">
<div id="bodyBackground">
</div>

<!-- Navigation -->
<nav class="navbar navbar-default navbar-fixed-top">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header page-scroll">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
         <a class="navbar-brand" href="#page-top">
        		<g:img dir="images" file="logo.png" alt="logo" base="http://www.lisnx.com/mobile"/></a>
    
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
        <ul class="nav navbar-nav navbar-left">
            <li class="hidden">
                <a href="https://www.lisnx.com/index.html#page-top"></a>
            </li>
            <li class="page-scroll">
                <a href="https://www.lisnx.com/index.html#explore">Explore</a>
            </li>
            <li class="page-scroll">
                <a href="https://www.lisnx.com/index.html#blog">Blog</a>
            </li>
            <li class="page-scroll">
                <a href="https://www.lisnx.com/index.html#about">About</a>
            </li>
        </ul>
    </div>
    <!-- /.navbar-collapse -->
<!-- /.container-fluid -->
</nav>

<!-- Header or home page-->
<header>
    <div class="container">
        <div class="row">
            <div class="col-lg-12">
                <div class="PromoPart">
                    <img width="100" height="100" src="https://media.licdn.com/mpr/mpr/shrinknp_400_400/p/4/000/133/021/368d692.jpg"/>
                    <a>Connect with Srinivas</a>
                    <div class="tcenter">
                    	<div id="facebook">
                    		<a href="#">
                    		<g:img dir="images" file="fbButton.png" alt="facebook" onclick="onFacebookLogin(); return false;"/>
                    		<h3>step <span>1</span> of 2</h3>
                    		</a>
                    	</div>
                    	<div id="linkedIn">
                    		<a href="#">
                    		<g:img dir="images" file="linkedInBtn.png" alt="linkedinn" onclick="onLinkedInLogin(); return false;"/>
                    		<h3>step <span>2</span> of 2</h3>
                    		</a>
                        </div>
                    </div>
               
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>
</header>


</body>
</html>