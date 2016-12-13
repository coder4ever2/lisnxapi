package nayax

import java.util.Date;

class LISNMessageEvent {
	LISNMessage lisnMessage
	NayaxUser user
	Date dateCreated
	Date lastUpdated
	EventType eventType
	
	enum EventType {
		VIEWED_MESSAGE
	}
	
	static mapping = {
		eventType enumType:"string"
	}
	static constraints = {
		dateCreated(nullable: true)
		lastUpdated(nullable: true)
		
	}
	
}
