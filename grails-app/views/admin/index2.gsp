<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

        <title>LISNx</title>
        <meta name="layout" content="main" />
<title>Facebook Login JavaScript Example</title>
<meta charset="UTF-8">
<g:javascript library="jquery" plugin="jquery"/>
<script type="text/javascript" src="http://platform.linkedin.com/in.js">
  api_key: ndd55k2u1zem
  authorize: false
</script>
</head>
<body>

<script>
  // This is called with the results from from FB.getLoginStatus().
  function statusChangeCallback(response) {
    console.log('statusChangeCallback');
    console.log(response);
    // The response object is returned with a status field that lets the
    // app know the current login status of the person.
    // Full docs on the response object can be found in the documentation
    // for FB.getLoginStatus().
    if (response.status === 'connected') {
      // Logged into your app and Facebook.
      testAPI(response.authResponse);
    } else if (response.status === 'not_authorized') {
      // The person is logged into Facebook, but not your app.
      document.getElementById('status').innerHTML = 'Please log ' +
        'into this app.';
    } else {
      // The person is not logged into Facebook, so we're not sure if
      // they are logged into this app or not.
      document.getElementById('status').innerHTML = 'Please log ' +
        'into Facebook.';
    }
  }

  // This function is called when someone finishes with the Login
  // Button.  See the onlogin handler attached to it in the sample
  // code below.
  function checkLoginState() {
    FB.getLoginStatus(function(response) {
      statusChangeCallback(response);
    });
  }

  window.fbAsyncInit = function() {
  $("#linkedInId").show();
  FB.init({
    appId      : '844486615570890',
    cookie     : true,  // enable cookies to allow the server to access 
                        // the session
    xfbml      : true,  // parse social plugins on this page
    version    : 'v2.1' // use version 2.1
  });

  // Now that we've initialized the JavaScript SDK, we call 
  // FB.getLoginStatus().  This function gets the state of the
  // person visiting this page and can return one of three states to
  // the callback you provide.  They can be:
  //
  // 1. Logged into your app ('connected')
  // 2. Logged into Facebook, but not your app ('not_authorized')
  // 3. Not logged into Facebook and can't tell if they are logged into
  //    your app or not.
  //
  // These three cases are handled in the callback function.

  FB.getLoginStatus(function(response) {
    statusChangeCallback(response);
  });

  };

  // Load the SDK asynchronously
  (function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
  }(document, 'script', 'facebook-jssdk'));

  // Here we run a very simple test of the Graph API after login is
  // successful.  See statusChangeCallback() for when this call is made.
  function testAPI(data) {
    console.log('Welcome!  Fetching your information.... ');
   
    FB.api('/me', function(response) {
      console.log('Successful login for: ' + response.name);
      document.getElementById('status').innerHTML =
        'Thanks for logging in, ' + response.name + '!';

      t(data);
    });
  }

  function t(resValue){
	  $("#linkedInId").show();
      $(".fb_iframe_widget").hide();
      $.get("/api/getLoginToken"
      	,function(data, status) {
      		alert ("Token value: " + data.message + "----" + resValue.userID);
      	}
      );

     $.post("/api/loginWithFacebook"
    	      ,{fid: resValue.userID
        	    ,ACCESS_TOKEN: resValue.accessToken
            	,TOKEN_EXPIRATION_DATE: "1398579830000"
                ,PERMISSIONS:"public_profile"
        	      }
    	      ,function(data, status) {
    	    	  $.post("/api/getStats"
    	        	      ,{fid: resValue.userID
    	            	      }
    	        	      ,function(data, status) {
    	            	      alert("Total facebook friends: " + status + " -- " + data.message.totalFacebookFriends);
    	            	      }
    	        	      );
        	      alert("Total status: " + "--" +data.message.username + data.status + " - " + resValue.accessToken + " - " + resValue.expiresIn);
        	      }
    	      );
     
  }

  function onLinkedInAuth() {
	  alert("Logged in with linked in!");
	  var randomText = '';
      var possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

      for(var i=0; i < 21; i++)
      {
    	  randomText += possible.charAt(Math.floor(Math.random() * possible.length));
      }
	  console.log("Random text: " + randomText);

      $.get("/api/getLinkedInToken"
    	      	,function(data, status) {
    	      		alert ("Token value: " + data + "----" + status);
    	      	}
    	      );
      
	 /* $.get("https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=75z6qmt6hemzyy"
			  +"&scope=r_basicprofile&state="+randomText+"&redirect_uri=http://localhost:9990/api/linkedInRedirect"
      	,function(data, status) {
      		alert ("Token value: " + data + "----" + status);
      	}
      );*/
  }
</script>

<!--
  Below we include the Login Button social plugin. This button uses
  the JavaScript SDK to present a graphical Login button that triggers
  the FB.login() function when clicked.
-->
<!-- 
<fb:login-button scope="public_profile,email" onlogin="checkLoginState();">
</fb:login-button>-->
<div id="linkedInId">
<script type="IN/Login" data-onAuth="onLinkedInAuth">
Hello <?js= firstName ?>
</script>
</div>

<div id="status">
</div>

</body>
</html>