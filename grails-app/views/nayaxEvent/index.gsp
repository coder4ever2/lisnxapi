
<%@ page import="nayax.NayaxEvent" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'nayaxEvent.label', default: 'NayaxEvent')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-nayaxEvent" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-nayaxEvent" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'nayaxEvent.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="city" title="${message(code: 'nayaxEvent.city.label', default: 'City')}" />
					
						<g:sortableColumn property="description" title="${message(code: 'nayaxEvent.description.label', default: 'Description')}" />
					
						<th><g:message code="nayaxEvent.organizer.label" default="Organizer" /></th>
					
						<g:sortableColumn property="venue" title="${message(code: 'nayaxEvent.venue.label', default: 'Venue')}" />
					
						<g:sortableColumn property="startDate" title="${message(code: 'nayaxEvent.startDate.label', default: 'Start Date')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${nayaxEventInstanceList}" status="i" var="nayaxEventInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${nayaxEventInstance.id}">${fieldValue(bean: nayaxEventInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: nayaxEventInstance, field: "city")}</td>
					
						<td>${fieldValue(bean: nayaxEventInstance, field: "description")}</td>
					
						<td>${fieldValue(bean: nayaxEventInstance, field: "organizer")}</td>
					
						<td>${fieldValue(bean: nayaxEventInstance, field: "venue")}</td>
					
						<td><g:formatDate date="${nayaxEventInstance.startDate}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${nayaxEventInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
