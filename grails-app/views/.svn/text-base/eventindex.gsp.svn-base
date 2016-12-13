<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<title>LISNx events page</title>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
 <meta property="fb:app_id" content="164116363623152" />
<!--<meta property="og:site_name" content="LISNx" />
<meta property="og:type" content="article" />
<meta property="og:title" content="Know what your Facebook and LinkedIn profiles convey about you" />
<meta property="og:image" content="${image}" />
<meta property="og:locale" content="en_US" />
<meta property="og:description" content="${description}" />-->

<!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
    <!-- Custom CSS -->
<link rel="stylesheet" href="${resource(dir: 'css', file: 'reset.css')}" type="text/css">
    
<link rel="stylesheet" href="${resource(dir: 'css', file: 'template.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'lisnx.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.bxslider.css')}" type="text/css">

<g:javascript library="jquery" plugin="jquery" />

<!-- Custom Fonts -->

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
                <a href="https://www.lisnx.com/index.html#explore">My Events</a>
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
                    <div class="tcenter">
                    	<div id="facebook">
                    		<a href="#">
                    		<g:img dir="images" file="fbButton.png" alt="facebook" onclick="onFacebookLogin(); return false;"/>                    		
                    		</a>
                    	</div>
                    	
                    </div>
                   
                        <div class="clear" style="height:20px;"></div>
                    <div class="clear" style="height:20px;"></div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>
</header>
<input type="hidden" id="skipLI" value="false" />
<div id="spinner" class="spinner" style="display:none;">
    <img id="img-spinner" src="${resource(dir:'images',file:'spinnerImage.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
</div>
<div id="chart_div" style="width:400; height:300; top:170px; left:175px; display:none"></div>

<!-- jQuery Version 1.11.0 -->
<script src="${resource(dir: 'js', file: 'jquery-1.11.0.js')}"></script>

<!-- Bootstrap Core JavaScript -->
<script src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>

<!-- Plugin JavaScript -->
   <script src="${resource(dir: 'js', file: 'jquery.easing.1.3.js')}"></script>
   <script src="${resource(dir: 'js', file: 'classie.js')}"></script>
<script src="${resource(dir: 'js', file: 'jquery.bxslider.min.js')}"></script>
   <!-- Custom Theme JavaScript -->
<script src="${resource(dir: 'js', file: 'template.js')}"></script>

<script type="text/javascript">
	var $loading = $('#spinner').hide();
	 $(document)
	   .ajaxStart(function () {
	     $loading.show();
	   })
	   .ajaxStop(function () {
	     $loading.hide();
	   });
</script>
<script>
	var fbId, liId, fbAccessToken, fbAccessTokenExpiresIn, fbConnections, liConnections, skipLinkedIn = false, fbLogged = false;

	window.fbAsyncInit = function() {
		
	    FB.init({
               appId      : '164116363623152',
	       xfbml      : true,
               version    : 'v2.3'
	    });
	
	    FB.getLoginStatus(function(response) {
	      statusChangeCallback(response);
	    });
   };

  (function(d, s, id){
     var js, fjs = d.getElementsByTagName(s)[0];
     if (d.getElementById(id)) {return;}
     js = d.createElement(s); js.id = id;
     js.src = "//connect.facebook.net/en_US/sdk.js";
     fjs.parentNode.insertBefore(js, fjs);
     
   }(document, 'script', 'facebook-jssdk'));
   
   
  function onFacebookLogin() {
    FB.login(function() {
    		checkLoginState();
	    },
	    {scope: "public_profile,email,user_friends"}
    );
  }
  
  
  function statusChangeCallback(response) {
    console.log('statusChangeCallback');
    console.log(response);
    $("#fbSection").hide();
    if (response.status === 'connected') {
      $("#facebook").hide();
      fbLoginSuccess(response.authResponse);
      if (response.authResponse != null || response.authResponse != undefined) {
          fbId = response.authResponse.userID;
      }
      skipLinkedIn = false;
      fbLogged = true;
  	  getStats(1);
    } else if (response.status === 'not_authorized') {
      
    } else {
      
      fbLogged = false;
      
    }
  }

  function checkLoginState() {
    FB.getLoginStatus(function(response) {
      statusChangeCallback(response);
    });
  }
  
  function fbLoginSuccess(data) {
    console.log('Welcome!  Fetching your information.... ');
   
    FB.api('/me', function(response) {
      console.log('Successful login for: ' + response.name);
      loginWithFacebook(data);
    });
  }

  function getContextPath() {
	  var loc = new String(window.location), locLength, lastChar, endIndex;
	  endIndex = loc.indexOf("event");
	  if (endIndex > 0) {
	  	  loc = loc.substr(0, endIndex);
	  		
	  }
	  locLength = loc.length;
	  lastChar = loc.substr(locLength-1);
	  if (!(lastChar === "/")) {
		  loc = loc+"/";
	  }
	  return loc;
  }

  function loginWithFacebook(resValue) {
	  fbId = resValue.userID;
	  fbAccessToken = resValue.accessToken;
	  fbAccessTokenExpiresIn = resValue.expiresIn;
	  
      $(".fb_iframe_widget").hide();
      console.log("test: "+window.location.pathname+"***"+window.location);
      $.get(getContextPath()+"api/getLoginToken"
      	,function(data, status) {
      	}
      );

    $.post(getContextPath()+"api/loginWithFacebook"
	      ,{ fid: fbId
	   	    ,ACCESS_TOKEN: fbAccessToken
	       	,TOKEN_EXPIRATION_DATE: "1398579830000"
            ,PERMISSIONS: "public_profile,email,user_friends"
	   	   }
	      ,function (data, status) {}
      );

  }

  function getStats(d) {
	console.log("called from: " + d);
        
        window.location.href = getContextPath()+"externalEvent/pics?eventId=19";
  }

  function logout() {
	  	var loggedout = false;
	    FB.getLoginStatus(function(response){
		     if (response.status == "connected") {
				FB.logout(function (response) {
					if (response && !response.error) {
						console.log ("Logged out from Facebook");
						if (!IN.User.isAuthorized()) {
							window.location.reload(true);
						}
					} else {
						console.log ("Unable to logout from Facebook");
					}
				});
		     }
	    });   

  }

</script>
  

</body>
</html>