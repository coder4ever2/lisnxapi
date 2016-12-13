<%@ page import="nayax.NayaxUser" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="dashBoard"/>
    <g:set var="entityName" value="${message(code: 'nayaxUser.label', default: 'NayaxUser')}"/>
    <title><g:message code="default.list.label"/></title>
    %{--<g:javascript library="prototype"/>--}%
    <gui:resources components="['dialog']"/>
  
<script type="text/javascript" src="https://platform.linkedin.com/in.js">
api_key: ndd55k2u1zem
authorize: true
lang:  en_US
</script>

</head>

<body>

<div class="mainPanel" style="padding-left:10px">
    <p tbodyfont>${title}</p>
    <g:if test="${flash.message}">
        <div class="fontColor">${flash.message}</div>
    </g:if>
    <div class="list">
        <table cellpadding="0" cellspacing="0">

            <tbody>
           <tr>
           <td>
           <script type="IN/Login"> 
<form action="/register.html"> 
<p>Your Name: <input type="text" name="name" value="<?js= firstName ?> <?js= lastName ?>" /></p>
<p>Your Password: <input type="password" name="password" /></p>
<input type="hidden" name="linkedin-id" value="<?js= id ?>" />
<input type="submit" name="submit" value="Sign Up"/>
</script>
           </td>
           </tr>
           
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
