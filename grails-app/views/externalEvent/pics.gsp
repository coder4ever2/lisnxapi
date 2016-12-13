<!doctype html>
<html>
    <head>
        <script src="${resource(dir: 'js', file: 'jquery-1.11.3.min.js')}"></script>
        <script src="${resource(dir: 'js', file: 'unitegallery.js')}"></script>

        <link rel="stylesheet" href="${resource(dir: 'css', file: 'unite-gallery.css')}" type="text/css">
        <script src="${resource(dir: 'themes/default', file: 'ug-theme-default.js')}"></script>
        <link rel="stylesheet" href="${resource(dir: 'themes/default', file: 'ug-theme-default.css')}" type="text/css">
        
        <!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
            <!-- Custom CSS -->
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'reset.css')}" type="text/css">

        <link rel="stylesheet" href="${resource(dir: 'css', file: 'template.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'lisnx.css')}" type="text/css">


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
                        <a href="#">My Events</a>
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
                            <div class="clear" style="height:20px;"></div>
                            <div class="tcenter">
                                <div id="gallery" style="display:none;">

                                    <g:each in ="${pics}" var="pic">
                                        <img alt="Image 1 Title" src="${pic}" data-image="${pic}">
                                    </g:each>
                                </div>

                            </div>

                            <div class="clear" style="height:20px;"></div>
                            <div class="clear"></div>
                        </div>
                    </div>
                </div>
            </div>
        </header>

        

        <script type="text/javascript"> 
            
            window.fbAsyncInit = function() {
                FB.init({
                    appId      : '${grailsApplication.config.facebook.api.key}',
                    xfbml      : true,
                    version    : 'v2.3'
                });
            };

            (function(d, s, id){
                var js, fjs = d.getElementsByTagName(s)[0];
                if (d.getElementById(id)) {return;}
                js = d.createElement(s); js.id = id;
                js.src = "//connect.facebook.net/en_US/sdk.js";
                fjs.parentNode.insertBefore(js, fjs);
            }(document, 'script', 'facebook-jssdk'));

            jQuery(document).ready(function(){ 
                jQuery("#gallery").unitegallery({
                    theme_enable_text_panel: false
                }); 
            }); 

        </script>

    </body>
</html>