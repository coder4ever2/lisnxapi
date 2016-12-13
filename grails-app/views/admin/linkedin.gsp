<html>
<head>
 <title>LISNx</title>
        <meta name="layout" content="main" />
      
      <script type="text/javascript" src="https://platform.linkedin.com/in.js">
  api_key: 75z6qmt6hemzyy
  authorize: true
</script>
</head>
<body>

<script type="text/javascript">
function onLinkedInAuth() {
  IN.API.Profile("me")
    .result( function(me) {
      var id = me.values[0].id;
      alert (JSON.stringify(me));
      alert (id);
      
      // AJAX call to pass back id to your server
    });
}
</script>
<p>Welcome to linkedin login</p>
<script type="IN/Login" data-onAuth="onLinkedInAuth">
Hello <?js= firstName ?>
</script>
</body>
</html>

