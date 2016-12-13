
<%@ page import="nayax.ExternalEvent" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'externalEvent.label', default: 'ExternalEvent')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-externalEvent" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-externalEvent" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="eventSource" title="${message(code: 'externalEvent.eventSource.label', default: 'Event Source')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'externalEvent.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'externalEvent.lastUpdated.label', default: 'Last Updated')}" />
					
						<g:sortableColumn property="eventId" title="${message(code: 'externalEvent.eventId.label', default: 'Event Id')}" />
					
						<g:sortableColumn property="eventName" title="${message(code: 'externalEvent.eventName.label', default: 'Event Name')}" />
					
						<g:sortableColumn property="description" title="${message(code: 'externalEvent.description.label', default: 'Description')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${externalEventInstanceList}" status="i" var="externalEventInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${externalEventInstance.id}">${fieldValue(bean: externalEventInstance, field: "eventSource")}</g:link></td>
					
						<td><g:formatDate date="${externalEventInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${externalEventInstance.lastUpdated}" /></td>
					
						<td>${fieldValue(bean: externalEventInstance, field: "eventId")}</td>
					
						<td>${fieldValue(bean: externalEventInstance, field: "eventName")}</td>
					
						<td>${fieldValue(bean: externalEventInstance, field: "description")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${externalEventInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
