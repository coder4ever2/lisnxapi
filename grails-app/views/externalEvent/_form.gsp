<%@ page import="nayax.ExternalEvent" %>



<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'eventSource', 'error')} required">
	<label for="eventSource">
		<g:message code="externalEvent.eventSource.label" default="Event Source" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="eventSource" from="${nayax.ExternalEvent$EventSource?.values()}" keys="${nayax.ExternalEvent$EventSource.values()*.name()}" required="" value="${externalEventInstance?.eventSource?.name()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'eventId', 'error')} ">
	<label for="eventId">
		<g:message code="externalEvent.eventId.label" default="Event Id" />
		
	</label>
	<g:textField name="eventId" value="${externalEventInstance?.eventId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'eventName', 'error')} ">
	<label for="eventName">
		<g:message code="externalEvent.eventName.label" default="Event Name" />
		
	</label>
	<g:textField name="eventName" value="${externalEventInstance?.eventName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="externalEvent.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${externalEventInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'locationCoordinate', 'error')} ">
	<label for="locationCoordinate">
		<g:message code="externalEvent.locationCoordinate.label" default="Location Coordinate" />
		
	</label>
	<g:select id="locationCoordinate" name="locationCoordinate.id" from="${nayax.LocationCoordinate.list()}" optionKey="id" value="${externalEventInstance?.locationCoordinate?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'cityState', 'error')} ">
	<label for="cityState">
		<g:message code="externalEvent.cityState.label" default="City State" />
		
	</label>
	<g:textField name="cityState" value="${externalEventInstance?.cityState}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'eventUrl', 'error')} ">
	<label for="eventUrl">
		<g:message code="externalEvent.eventUrl.label" default="Event Url" />
		
	</label>
	<g:textField name="eventUrl" value="${externalEventInstance?.eventUrl}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'logoUrl', 'error')} ">
	<label for="logoUrl">
		<g:message code="externalEvent.logoUrl.label" default="Logo Url" />
		
	</label>
	<g:textField name="logoUrl" value="${externalEventInstance?.logoUrl}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'city', 'error')} ">
	<label for="city">
		<g:message code="externalEvent.city.label" default="City" />
		
	</label>
	<g:textField name="city" value="${externalEventInstance?.city}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'state', 'error')} ">
	<label for="state">
		<g:message code="externalEvent.state.label" default="State" />
		
	</label>
	<g:textField name="state" value="${externalEventInstance?.state}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'startDate', 'error')} ">
	<label for="startDate">
		<g:message code="externalEvent.startDate.label" default="Start Date" />
		
	</label>
	<g:datePicker name="startDate" precision="day"  value="${externalEventInstance?.startDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'endDate', 'error')} ">
	<label for="endDate">
		<g:message code="externalEvent.endDate.label" default="End Date" />
		
	</label>
	<g:datePicker name="endDate" precision="day"  value="${externalEventInstance?.endDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'country', 'error')} ">
	<label for="country">
		<g:message code="externalEvent.country.label" default="Country" />
		
	</label>
	<g:textField name="country" value="${externalEventInstance?.country}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: externalEventInstance, field: 'isGlobal', 'error')} ">
	<label for="isGlobal">
		<g:message code="externalEvent.isGlobal.label" default="Is Global" />
		
	</label>
	<g:checkBox name="isGlobal" value="${externalEventInstance?.isGlobal}" />
</div>

