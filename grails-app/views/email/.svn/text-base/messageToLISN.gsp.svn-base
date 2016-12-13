<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" contentType="text/html" %>
%{--<html>
<head>
    <title>LISNX</title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css', absolute: true)}"/>
</head>

<body>
<div id="north1" style='border: 1pt gray solid;'>
    <div id="appLogo">
        <g:link url="/">
            <img src="${resource(dir: 'images', file: 'top_logo.png', absolute: true)}" width="207" height="98"/>
        </g:link>
    </div>

    <div id="pageBody"
         style="font-family:georgia;padding-left:0%;padding-right:10%;height:500px;background-image:  url(${resource(dir: 'images', file: 'main_bg.jpg', absolute: true)})">
        <div id="include">
            Hi ${to.fullName}, <br/><br/>
            ${from.fullName} has sent the following message to you.
            <g:set var="url"
                   value="${createLink(absolute: true, controller: 'lisn', action: 'show', params: ['id': id])}"/>
            Click <a href="${url}">here</a> to see lisn.
        </div>

        <div style="font-size:12px;padding:10px;">
            <p>
                ${message}
            </p>
        </div>
        <g:render template="/template/emailFooter" absolute="true"/>
    </div>
</div>
</body>
</html>--}%

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<head>

</head>
<body style="font-family: 'Arial'; font-size: 12px" >

<div style="width:700px;margin:auto;background-image:url(http://www.lisnx.com/mobile/images/mailer.jpg);min-height:500px">
    <div style="padding-top:200px; padding-left: 75px; ">

        <div id="pageBody"
             style="font-family:georgia;padding-left:0%;padding-right:10%;height:500px;background-image:  url(${resource(dir: 'images', file: 'main_bg.jpg', absolute: true)})">
            <div id="include">
                Hi ${to.fullName}, <br/><br/>
                ${from.fullName} has sent the following message to you.
                <g:set var="url"
                       value="${createLink(absolute: true, controller: 'lisn', action: 'show', params: ['id': id])}"/>
                Click <a href="${url}">here</a> to see lisn.
            </div>

            <div style="font-size:12px;padding:10px;">
                <p>
                    ${message}
                </p>
            </div>

        </div>
    </div>


</div>

</body>
</html>