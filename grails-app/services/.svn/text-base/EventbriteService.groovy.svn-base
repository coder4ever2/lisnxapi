import groovy.lang.GroovyInterceptable;
import groovy.lang.MetaClass;

import nayax.ExternalEvent;
import nayax.LocationCoordinate
import nayax.ExternalEvent.EventSource;

import org.springframework.beans.factory.InitializingBean;

import com.lisn.api.ApiService;

import java.net.URL;
import java.text.SimpleDateFormat;



import grails.transaction.Transactional
import groovy.json.JsonSlurper;

class EventbriteService implements InitializingBean, GroovyInterceptable {
	


	def static transactional = false
	def grailsApplication
	def setting
	
	
	static final String EVENTBRITE_BASE_API = 'https://www.eventbriteapi.com/v3/events/'

	static final String FORMAT_AND_TOKEN = '?format=json&token=EFJZLBQ64Y5PL5ZFTR2S'
	
	
	
	static def String API_SEARCH_URL=EVENTBRITE_BASE_API+'search/'+FORMAT_AND_TOKEN
			//&location.latitude=37.4&location.longitude=-122.0'
	
	//static def String API_EVENT_DETAILS_URL=EVENTBRITE_BASE_API+'15795513807/'+FORMAT_AND_TOKEN

	
	
	def getNearbyEvents(def latitude, def longitude, def locationWithin) {
		log.info("In eventbrite service")
		URL apiUrl = new URL(API_SEARCH_URL+'&location.latitude='+latitude+'&location.longitude='+longitude +'&location.within='+locationWithin)
		def response = getResponseFromUrl(apiUrl)
		return new JsonSlurper().parseText(response)
    }
	
	def getEventDetails(def eventId){
		URL apiUrl = new URL(EVENTBRITE_BASE_API+eventId+'/'+FORMAT_AND_TOKEN)
		def response = getResponseFromUrl(apiUrl)
		return new JsonSlurper().parseText(response)
		
	}
	
	String getResponseFromUrl(URL url) {
		String response = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			int respCode = conn.responseCode
			if (respCode == 400) {
				log.error("COULD NOT MAKE CONNECTION")
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				def jsonResp = JSON.parse(br.text)
			} else {
				response = conn.getInputStream().getText()
			}
		} finally {
			conn.disconnect()
		}
		return response;
	}
	
	def populateEventDetails(def eventDetails){
		ExternalEvent externalEvent = new ExternalEvent()
		externalEvent.eventId = eventDetails.id
		externalEvent.eventSource = EventSource.Eventbrite
		externalEvent.description=eventDetails.description.text
		externalEvent.eventName = eventDetails.name.text
		externalEvent.eventUrl = eventDetails.url
		externalEvent.logoUrl = eventDetails.logo_url
		
		SimpleDateFormat utcFormat  = new java.text.SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" )
		def startDate = utcFormat.parse(eventDetails.start.utc)
		def endDate = utcFormat.parse(eventDetails.end.utc)
		
		externalEvent.startDate=startDate
		externalEvent.endDate = endDate
		
		
		def eventAddress = eventDetails.venue?.address
		if(eventAddress?.latitude && eventAddress?.longitude)
			externalEvent.locationCoordinate = new LocationCoordinate(
														latitude: eventAddress.latitude,
														longitude:eventAddress.longitude)
		if(eventAddress?.city){
			externalEvent.city = eventAddress.city
			
			if(eventAddress.region){
				externalEvent.cityState = eventAddress.city+', '+
						eventDetails.region
				externalEvent.state = eventAddress.region
			}
			else if(eventAddress.country){
				externalEvent.cityState = eventAddress.city+', '+
						eventDetails.country
				externalEvent.country=eventAddress.country
			}
		}
		return externalEvent
	}

	

	void afterPropertiesSet() {
		this.setting = grailsApplication.config.setting
	}
}
