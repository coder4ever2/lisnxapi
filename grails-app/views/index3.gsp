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
<meta property="og:title"
	content="Know what your Facebook and LinkedIn profiles convey about you" />
<meta property="og:image" content="${image}" />
<meta property="og:locale" content="en_US" />
<meta property="og:description" content="${description}" />

<!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'bootstrap.min.css')}"
	type="text/css">
<!-- Custom CSS -->
<link rel="stylesheet" href="${resource(dir: 'css', file: 'reset.css')}"
	type="text/css">

<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'template.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'lisnx.css')}"
	type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'jquery.bxslider.css')}"
	type="text/css">

<g:javascript library="jquery" plugin="jquery" />

<!-- Custom Fonts -->
<link
	href='http://fonts.googleapis.com/css?family=Lato:400,300,700,700italic,400italic,300italic'
	rel='stylesheet' type='text/css'>

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
	<div id="bodyBackground"></div>

	<!-- Navigation -->
	<nav class="navbar navbar-default navbar-fixed-top">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header page-scroll">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#bs-example-navbar-collapse-1">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#page-top"> <g:img dir="images"
					file="logo.png" alt="logo" base="http://www.lisnx.com/mobile" /></a>

		</div>

		<!-- Collect the nav links, forms, and other content for toggling -->
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav navbar-left">
				<li class="hidden"><a
					href="https://www.lisnx.com/index.html#page-top"></a></li>
				<li class="page-scroll"><a
					href="https://www.lisnx.com/index.html#explore">Explore</a></li>
				<li class="page-scroll"><a
					href="https://www.lisnx.com/index.html#blog">Blog</a></li>
				<li class="page-scroll"><a
					href="https://www.lisnx.com/index.html#about">About</a></li>
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
					<div>
						<div class="PromoPart">
								<h2>Know what your Facebook friends &amp; LinkedIn
									connections convey about your personality</h2>
								<div class="tcenter">
									<div id="facebook">
										<a href="#"> <g:img dir="images" file="fbButton.png"
												alt="facebook" onclick="onFacebookLogin(); return false;" />
											<h3>
												step <span>1</span> of 2
											</h3>
										</a>
									</div>
									<div id="linkedIn">
										<a href="#"> <g:img dir="images" file="linkedInBtn.png"
												alt="linkedinn" onclick="onLinkedInLogin(); return false;" />
											<h3>
												step <span>2</span> of 2
											</h3>
										</a>
									</div>
								</div>
								<div class="mobile">
									<div class="iphone">
										<h3 class="type4">Which one are you?</h3>
										<ul>
											<li class="type1">Socialite</li>
											<li class="type2">Professional Guru</li>
											<li class="type3">Boundary Spanner</li>
											<li class="type4">Organized</li>
										</ul>
										<div class="picCont">
											<g:img dir="images" file="avatar.png" alt="name"
												style="padding-left:18px; padding-top:10px; width:90px; height:90px;" />
											<g:img dir="images" file="picTop.png" class="picBG" alt="" />
										</div>
									</div>
								</div>
								<div class="clear" style="height: 20px;"></div>
								<div class="clear" style="height: 20px;"></div>
							</div>
						</div>
						<div>
							<h1>Trending</h1>
							<div>
								<p>Srinivas Jaini</p>

								<p>Praveena Jaini</p>
							</div>
						</div>

					</div>
					</div>
					<div>

						<h3>
							<span>
								${soFarCount}
							</span>, so far have found about them.
						</h3>
						<div class="clear"></div>
						<div>
							<!-- Blog Section -->
							<section id="blog">
								<div class="container">
									<div class="row">
										<div class="blog">

											<h3>
												<b> Win $10 gift card each week!</b>
											</h3>
											<p>Participate and share with friends to win gifts
												weekly. We appreciate your participation and are giving out
												$10 gift cards to top 5 trending sharers each week.</p>
											<h3>
												<b> Nominate your charity!</b>
											</h3>
											<p>
												We believe doing good can never hurt. We are excited to
												provide 10% of each weeks gift cards to a charity and are
												looking for nominations. <br /> <b>Have a favorite
													charity?</b> Nominate by sending email to <b><a
													href="mailto:info@lisnx.com?Subject=Nominate my charity!"
													target="_top"> info@lisnx.com</a></b>
											</p>
											<h3>
												<b> Feedback</b>
											</h3>
											<p>Have a comment or a suggestion on what you like or
												what we can do better. Take a quick minute and send out a
												note. We will be happy to review and get back to you.</p>

										</div>
									</div>
								</div>
							</section>

						</div>
					</div>
				</div>
			</div>
		</div>
	</header>
	<p class="copy">
		&copy; 2014 LISNx Inc. All Rights Reserved. &nbsp; &nbsp; <a
			href="privacy.html" target="_self">Privacy</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a
			href="#explore">Explore</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a
			href="#blog">Blog</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;<a
			href="#about">About</a>
	</p>

	<input type="hidden" id="skipLI" value="false" />
	<div id="spinner" class="spinner" style="display: none;">
		<img id="img-spinner"
			src="${resource(dir:'images',file:'spinnerImage.gif')}"
			alt="${message(code:'spinner.alt',default:'Loading...')}" />
	</div>
	<div id="chart_div"
		style="width: 400; height: 300; top: 170px; left: 175px; display: none"></div>

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

	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<script type="text/javascript"
		src="https://platform.linkedin.com/in.js">
  api_key: ${grailsApplication.config.linkedin.api.key}
  authorize: true
  credentials_cookie: true
</script>

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

	if ("${image}" != "") {
		window.location.href = "https://www.lisnx.com/mobile/promo";
	}
	window.fbAsyncInit = function() {
		$("#linkedIn").hide();
	 	$("#fbSection").hide();
	 	$("#liSection").hide();
		 	 
	    FB.init({
	      appId      : '${grailsApplication.config.facebook.api.key}
		',
				cookie : true,
				xfbml : true,
				version : 'v2.1'
			});

			FB.getLoginStatus(function(response) {
				statusChangeCallback(response);
			});
		};

		(function(d, s, id) {
			var js, fjs = d.getElementsByTagName(s)[0];
			$("#linkedIn").hide();
			if (d.getElementById(id)) {
				return;
			}
			js = d.createElement(s);
			js.id = id;
			js.src = "//connect.facebook.net/en_US/sdk.js";
			fjs.parentNode.insertBefore(js, fjs);

		}(document, 'script', 'facebook-jssdk'));

		function onFacebookLogin() {
			FB.login(function() {
				checkLoginState();
			}, {
				scope : "public_profile,email,user_friends"
			});
		}
		function onLinkedInLogin() {
			IN.User.authorize(function() {
				setTimeout(onLinkedInAuth(), 2000);
			});
		}

		function statusChangeCallback(response) {
			console.log('statusChangeCallback');
			console.log(response);
			$("#linkedIn").hide();
			$("#fbSection").hide();
			$("#liSection").hide();
			if (response.status === 'connected') {
				$("#facebook").hide();
				fbLoginSuccess(response.authResponse);
				if (response.authResponse != null
						|| response.authResponse != undefined) {
					fbId = response.authResponse.userID;
				}
				skipLinkedIn = false;
				fbLogged = true;
				getStats(1);
			} else if (response.status === 'not_authorized') {

			} else {

				$("#linkedIn").hide();
				$("#liSection").hide();
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
			endIndex = loc.indexOf("promo");
			if (endIndex > 0) {
				loc = loc.substr(0, endIndex);

			}
			locLength = loc.length;
			lastChar = loc.substr(locLength - 1);
			if (!(lastChar === "/")) {
				loc = loc + "/";
			}
			return loc;
		}

		function loginWithFacebook(resValue) {
			fbId = resValue.userID;
			fbAccessToken = resValue.accessToken;
			fbAccessTokenExpiresIn = resValue.expiresIn;

			$(".fb_iframe_widget").hide();
			console.log("test: " + window.location.pathname + "***"
					+ window.location);
			$.get(getContextPath() + "api/getLoginToken",
					function(data, status) {
					});

			$.post(getContextPath() + "api/loginWithFacebook", {
				fid : fbId,
				ACCESS_TOKEN : fbAccessToken,
				TOKEN_EXPIRATION_DATE : "1398579830000",
				PERMISSIONS : "public_profile,email,user_friends"
			}, function(data, status) {
			});

			if (IN.User.isAuthorized()) {
				$("#linkedIn").hide();
			} else {
				$("#linkedIn").show();
			}
		}

		function onLinkedInAuth() {
			if (fbLogged) {
				if (!IN.User.isAuthorized()) {
					IN.User.authorize();
				}
				$.post(getContextPath() + "admin/linkedinLogin", function(data,
						status) {
					console.log("Status: " + status);
					if (status == "success") {
						skipLinkedIn = false;

					} else {
						skipLinkedIn = true;
					}
					getStats(2);
				});
			}
		}

		function skipLinkedInLogin() {
			$("#linkedIn").hide();
			$("#liSection").hide();
			skipLinkedIn = true;
			$("#skipLI").val("true");
			getStats(3);
		}

		function getStats(d) {
			console.log("called from: " + d);
			$("#linkedIn").hide();

			console.log("skiplinkedIn: " + skipLinkedIn);

			if (!skipLinkedIn) {

				if (IN.User.isAuthorized()) {
					IN.API.Profile("me").result(function(me) {
						liId = me.values[0].id;
						$.get(getContextPath() + "api/getCampaignStats", {
							fid : fbId,
							lid : liId
						}, function(data, status) {
							console.log("Status: " + status);
							if (status == "success") {
								drawChart(data, fbId, liId);
							}
						});

					}).error(function(r) {
						console.log(r);
					});

				} else {
					skipLinkedIn = true;
				}
			}
		}

		function logout() {
			var loggedout = false;
			FB.getLoginStatus(function(response) {
				if (response.status == "connected") {
					FB.logout(function(response) {
						if (response && !response.error) {
							console.log("Logged out from Facebook");
							if (!IN.User.isAuthorized()) {
								window.location.reload(true);
							}
						} else {
							console.log("Unable to logout from Facebook");
						}
					});
				}
			});

			if (IN.User.isAuthorized()) {
				IN.User.logout(function(response) {
					if (response) {
						console.log("Logged out from LinkedIn");
						window.location.reload(true);
					} else {
						console.log("Unable to logout from LinkedIn");
					}
				});
			}

		}
	</script>

	<script type="text/javascript">
  // Load the Visualization API and the piechart package.
  google.load('visualization', '1.0', {'packages':['corechart']});
  
  // Set a callback to run when the Google Visualization API is loaded.
  //google.setOnLoadCallback(drawChart);

  // Callback that creates and populates a data table, 
  // instantiates the pie chart, passes in the data and
  // draws it.
  function drawChart(status, fid, lid) {

  // Create the data table.
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Topping');
  data.addColumn('number', 'Slices');
  data.addRows([
    ['Social', status.facebook_count],
    ['Boundary Spanner', status.overlap],
    ['Pro Guru', status.linkedin_count], 
    ['Organized', status.disjoint]
    ]);

    // Set chart options
    var options = {'title':' ',
                   'width':600,
                   'pieHole': 0.6,
                   'backgroundColor': 'transparent',
                   'legend': { 'textStyle': { 'color': 'white' } },
                   'height':450};

    // Instantiate and draw our chart, passing in some options.
    var chart = new google.visualization.PieChart(document.getElementById('chart_div'));

    google.visualization.events.addListener(chart, 'ready', function () {
        var url = chart.getImageURI();
        $.post(getContextPath()+"api/getFinalUrl",
	  			  {	imageUrl: url,
		        	fid: fid,
		        	lid: lid 
	  		  	  }, 
	  			  function (data, status){
		  			  if(status == "success")
		  				window.location.href = getContextPath()+"api/getPromoShare?imageUrl="+data+"&fid="+fid+"&lid="+lid;
	  			  }
	  			 );
      });
    
    chart.draw(data, options);
  }
    
</script>
</body>
</html>