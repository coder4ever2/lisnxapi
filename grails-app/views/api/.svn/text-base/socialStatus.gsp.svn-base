<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<title>Social Status</title>
<meta property="fb:app_id" content="164116363623152" />
<meta property="og:site_name" content="LISNx" />
<meta property="og:type" content="article" />
<meta property="og:title" content="Know what your Facebook friends & LinkedIn connections convey about your personality" />
<meta property="og:image" content="${image}" />
<meta property="og:locale" content="en_US" />
<meta property="og:description" content="${description}" />

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
  		  href: 'http://www.lisnx.com/mobile/api/getSocialStatus?imageUrl=${image}',
  		}, function(response){});
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
</head>
<body>
<p>
Note to the user..
</p>

<g:img uri="${image}" />
      
<div id="fbSection">
	<input type="image" src="/mobile/images/facebook_share_button.png" onclick="postOnFB(); return false;" style="padding-top:100px">
</div>
</body>

</html>