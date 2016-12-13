<%@ page contentType="text/html;charset=UTF-8"%>
<html>

<head>
<title>LISNx Promo</title>
    
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
<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.bxslider.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'new.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css">
 <style>
     .ui-widget-header,.ui-state-default, ui-button{
        background:#0090d4;
        border: 1px solid #b9cd6d;
        color: #FFFFFF;
        font-weight: bold;
     }
  </style>
    <!-- Custom Fonts -->
	<link href='https://fonts.googleapis.com/css?family=Lato:400,300,700,700italic,400italic,300italic' rel='stylesheet' type='text/css'>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

<script type="text/javascript" src="https://platform.linkedin.com/in.js">
  api_key: ${grailsApplication.config.linkedin.api.key}
  authorize: true
  credentials_cookie: true
  scope: r_basicprofile r_emailaddress r_network rw_nus
</script>
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

    function postOnFB() {
        FB.ui({
  		  method: 'share',
  		  href: 'https://www.lisnx.com/mobile/api/getSocialStatus?imageUrl=${image}&referred=facebook&fid=${fid}&lid=${lid}',
    	}, function(response){});
     }

    function postOnLI() {
  	  IN.API.Raw("/people/~/shares")
  	    .method("POST")
  	    .body( JSON.stringify( {
  	        "content": {
  	          "submitted-url": "https://www.lisnx.com/mobile/promo",
  	          "title": "Know what your Facebook friends & LinkedIn connections convey about your personality",
  	          "description": "${linkedInDescription}",
  	          "submitted-image-url": "${image}"
  	        },
  	        "visibility": {
  	          "code": "anyone"
  	        },
  	        "comment": "${description}"
  	      })
  	    )
  	    .result(function(r) { 
  	    	$("#linkedInPostSuccess").dialog({
  	    		//autoOpen: false,
                buttons: {
                   OK: function() {$(this).dialog("close");}
                }
  	  	    });
  	    })
  	    .error(function(r) {
  	    	$("#linkedInPostFailure").dialog({
  	    		//autoOpen: false,
                buttons: {
                   OK: function() {$(this).dialog("close");}
                }
  	  	    });
  	    });
    }
</script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-27274582-1', 'auto');
  ga('send', 'pageview');

</script>
<script type="text/javascript" src="https://platform.linkedin.com/in.js">
  api_key: ${grailsApplication.config.linkedin.api.key}
  authorize: true
  credentials_cookie: true
</script>
</head>

<body id="page-top" class="index">
<div id="bodyBackground"> </div>
<!-- Navigation --> <nav
 class="navbar navbar-default navbar-fixed-top">
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
<!-- /.navbar-collapse --><!-- /.container-fluid -->
</nav>
<!-- Header or home page-->
<header></header>

 <!-- Header or home page-->
<div class="container">
	<div class="row">
		<div class="col-lg-12">
			<div class="PromoShare">
				<div class="clear" style="height:80px;"></div>
		    	<h3>${personalMessage} <b>${champion}</b>${personalMessage2}</h3>
		        <div style="text-align:center">
			        <div style="float:left;width:100px">&nbsp;</div>
			        <a class="button icon FB" style="text-align:center" href="#" onclick="postOnFB(); return false;" ><span>Share on Facebook</span></a>
					<a class="button icon LINKEDIN" href="#" onclick="postOnLI(); return false;"><span>Share on LinkedIn</span></a>
				</div>
			    <div class="clear" style="height:20px;"></div>
			    <div class="mobile">
					<div class="iphone">
						<g:img uri="${image}" />
					</div>
				</div>
			    <div class="clear" style="height:20px;"></div>
			    <h3><span>${soFarCount}</span>, so far have found about them.</h3>
			    <div class="clear"></div>
			</div>
		</div>
	</div>
</div>
<div id="linkedInPostSuccess" title="LinkedIn Post Status" style="display:none">Successfully posted to LinkedIn</div>
<div id="linkedInPostFailure" title="LinkedIn Post Status" style="display:none">Failed to posted to LinkedIn. Please contact Administrator.</div>
    
    <!-- jQuery Version 1.11.0 -->
<script src="${resource(dir: 'js', file: 'jquery-1.11.0.js')}"></script>
<script src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>

<!-- Bootstrap Core JavaScript -->
<script src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>

<!-- Plugin JavaScript -->
   <script src="${resource(dir: 'js', file: 'jquery.easing.1.3.js')}"></script>
   <script src="${resource(dir: 'js', file: 'classie.js')}"></script>
<script src="${resource(dir: 'js', file: 'jquery.bxslider.min.js')}"></script>
   <!-- Custom Theme JavaScript -->
<script src="${resource(dir: 'js', file: 'template.js')}"></script>

</body>

</html>
