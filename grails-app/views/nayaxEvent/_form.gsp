<%@ page import="nayax.NayaxEvent" %>



<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="nayaxEvent.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${nayaxEventInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'city', 'error')} required">
	<label for="city">
		<g:message code="nayaxEvent.city.label" default="City" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="city" required="" value="${nayaxEventInstance?.city}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="nayaxEvent.description.label" default="Description" />
		
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="5000" value="${nayaxEventInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'organizer', 'error')} required">
	<label for="organizer">
		<g:message code="nayaxEvent.organizer.label" default="Organizer" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="organizer" name="organizer.id" from="${nayax.NayaxUser.list()}" optionKey="id" required="" value="${nayaxEventInstance?.organizer?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'venue', 'error')} ">
	<label for="venue">
		<g:message code="nayaxEvent.venue.label" default="Venue" />
		
	</label>
	<g:textField name="venue" value="${nayaxEventInstance?.venue}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'startDate', 'error')} required">
	<label for="startDate">
		<g:message code="nayaxEvent.startDate.label" default="Start Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="startDate" precision="day"  value="${nayaxEventInstance?.startDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'endDate', 'error')} required">
	<label for="endDate">
		<g:message code="nayaxEvent.endDate.label" default="End Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="endDate" precision="day"  value="${nayaxEventInstance?.endDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'volunteers', 'error')} ">
	<label for="volunteers">
		<g:message code="nayaxEvent.volunteers.label" default="Volunteers" />
		
	</label>
	<g:select name="volunteers" from="${nayax.NayaxUser.list()}" multiple="multiple" optionKey="id" size="5" value="${nayaxEventInstance?.volunteers*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'tasks', 'error')} ">
	<label for="tasks">
		<g:message code="nayaxEvent.tasks.label" default="Tasks" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${nayaxEventInstance?.tasks?}" var="t">
    <li><g:link controller="task" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="task" action="create" params="['nayaxEvent.id': nayaxEventInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'task.label', default: 'Task')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'messages', 'error')} ">
	<label for="messages">
		<g:message code="nayaxEvent.messages.label" default="Messages" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${nayaxEventInstance?.messages?}" var="m">
    <li><g:link controller="message" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="message" action="create" params="['nayaxEvent.id': nayaxEventInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'message.label', default: 'Message')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: nayaxEventInstance, field: 'respondents', 'error')} ">
	<label for="respondents">
		<g:message code="nayaxEvent.respondents.label" default="Respondents" />
		
	</label>
	
</div>

