package nayax

import nayax.Enum.NConnectionStatus

class NConnection {
	NayaxUser owner
	NayaxUser connection
	Date dateCreated
	Date lastUpdated
	ConnectionType connectionType
	NayaxLiveEvent nayaxEvent
	Boolean nayaxLocal
	Boolean facebook
	Boolean linkedin
	Integer timesResent
	NConnectionStatus nConnectionStatus = NConnectionStatus.PENDING
    Boolean isNotified=false
	static belongsTo = [NayaxUser]
	enum ConnectionType {
		NAYAX_EVENT, FACEBOOK, LINKEDIN, NAYAX_LOCAL
	}
	
	
	static constraints = {
        isNotified(nullable: true)
		owner(nullable: false)
		connection(nullable: false)
		dateCreated(nullable: true)
		lastUpdated(nullable: true)
		connectionType(nullable: true)
		nayaxEvent(nullable: true)
		nayaxLocal(nullable: true)
		facebook(nullable: true)
		linkedin(nullable: true)
		timesResent(nullable:true)
		nConnectionStatus(nullable: false)
	}
	
	static mapping = {
		connectionType enumType:"string"
		nConnectionStatus enumType:"string"
	}
}
