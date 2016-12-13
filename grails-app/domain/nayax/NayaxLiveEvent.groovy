package nayax

import java.util.Date;

class NayaxLiveEvent{
	Date startDate
	Date endDate
	String description
	NayaxUser host
	double latitude
	double longitude
	def fbPals
	def connections
	
	static transients = [ "fbPals", "connections" ]
	
	
	String toString(){
		"$description, $startDate"
	}
	
	/*static searchable = {

		only: ['startDate', 'endDate','description', 'latitude', 'longitude']
	}*/
	static hasMany =[attendees:NayaxUser, 
	invitees:Invitee,
	tags: String,
	intendees:NayaxUser,
	userMaps:UserEventMap]
	
	
	
	static constraints = {
		
		startDate()
		endDate()
		host()
		description(blank:false)
		attendees(nullable : true)
		userMaps(nullable:true)
		intendees(nullable : true)
		invitees(nullable : true)
		latitude(nullable:true)
		longitude(nullable:true)
		tags(nullable:true)
	}
}
