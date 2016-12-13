package nayax

import java.util.Date;


class EventMessage {
	NayaxUser user
	ExternalEvent externalEvent
	Date dateCreated
	Date lastUpdated
	String content
	Picture picture
	MessageType messageType = MessageType.MESSAGE
	enum MessageType {
		CREATED, INVITED, JOINED, IGNORED, MESSAGE, CONTACT_INFO
	}
	
	static belongsTo = [ExternalEvent]
	
	static hasMany =[lastViewedEventMessages: LastViewedEventMessage]
	
	static constraints = {
		user(nullable:false)
		externalEvent(nullable:false)
		content(nullable:true)
		picture(nullable:true, blank:true)
		messageType(nullable:true)
	}

	static mapping = {
		content(type: 'text')
	}
	def getLikes(){
		return EventMessageLike.findAllWhere(eventMessage: this, unliked:false)
	}

}
