<%@ page import="nayax.NayaxUser" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Welcome to LISNx</title>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'nayaxUser.label', default: 'NayaxUser')}"/>
    <title><g:message code="default.list.label"/></title>
    %{--<g:javascript library="prototype"/>--}%
    <gui:resources components="['dialog']"/>

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
            <g:each in="${users}" status="i" var="nayaxUserInstance">
                <tr class="${(i % 2) == 0 ? 'alternateOne' : 'alternateSec'}">

                    <td class="tbodyfont" style="vertical-align:middle;height:60px;width:100%">
                        <table>
                            <tr>
                                <td>${fieldValue(bean: nayaxUserInstance, field: "username")} | ${fieldValue(bean: nayaxUserInstance, field: "fullName")}</td>
                                <td><g:actionSubmit value="Connect" action="connectWithUser" userId="{nayaxUserInstance.id}" /></td>
                            </tr>
                        </table>
                    <td>
                  
                    
                    </td>

                </tr>
            </g:each>
            <g:if test="${usersCount>20}">
                <tr>
                    <td>
                        <div class="paginateButtons"> 
                            <g:paginate controller="nayaxUser" action="connections" total="${usersCount}"/>
                        </div>
                    </td>
                </tr>
            </g:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
