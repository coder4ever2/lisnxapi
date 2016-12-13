package nayax

import java.util.Date;

import com.lisn.api.ApiService;

class ExternalEvent {
	
	EventSource eventSource = EventSource.Unknown
	String eventId
	LocationCoordinate locationCoordinate
	String eventUrl
	String logoUrl
	Date dateCreated
	Date lastUpdated
	String eventName
	String description
	String cityState
	String city
	String state
	String country
	Date startDate
	Date endDate
	Boolean isGlobal = false
	Boolean isActive = true
	
	
	enum EventSource {
		Eventbrite, Unknown, LISNx
	}

    static constraints = {
		eventSource(nullable:false)
		dateCreated(nullable: true)
		lastUpdated(nullable: true)
		eventId(nullable:false)
		eventName(nullable:true)
		description (nullable:true)
		locationCoordinate(nullable:true)
		cityState(nullable:true)
		eventUrl(nullable:true)
		logoUrl(nullable:true)
		city(nullable:true)
		state(nullable:true)
		startDate(nullable:true)
		endDate(nullable:true)
		country(nullable:true)
		isGlobal(nullable:true)
		isActive(nullable:true)
		
    }
	static mapping = {
		description(type: 'text')
		eventSource enumType:"string"
	}
	def getEventGoers(){
		def goers = EventGoing.findAllWhere(event:this, notGoing:false)?.collect { it.goer } as Set
		return goers
	}
	
	def getEventDetails(timeStamp){
		    def eventInfo = [:]
			eventInfo.eventId = this.id
			eventInfo.eventSource = this.eventSource.toString()
			eventInfo.eventUrl= this.eventUrl
			eventInfo.logoUrl = this.logoUrl
			eventInfo.city = this.city
			eventInfo.state = this.state
			eventInfo.cityState = this.cityState
			eventInfo.country = this.country
			eventInfo.startDate = ApiService.formatDateTimeStampForApi(timeStamp, this.startDate)
			eventInfo.endDate = ApiService.formatDateTimeStampForApi(timeStamp, this.endDate)
			eventInfo.externalEventId = this.eventId
			eventInfo.eventName = this.eventName
			eventInfo.description = this.description
			
			
			
			def eventGoers =[:]
			def externalEventGoers = this.getEventGoers()
			externalEventGoers?.each{goer ->
				eventGoers[goer.id] = [id:goer.id,
										fid:goer.facebook?.fid,
										full_name:goer.fullName]
			}
			eventInfo.goerCount = externalEventGoers?.size()
			eventInfo.goers = eventGoers
			def eventMessageCount = EventMessage.findAllByExternalEvent(this)?.size()
			eventInfo.eventMessageCount=eventMessageCount
			
			return eventInfo
			
	}
	def nearbyEvents(latitude, longitude){
		
	}
	
}
