package nayax

class UserEventMap {
	NayaxLiveEvent event
	NayaxUser user
	boolean shareFb
	boolean shareLi
	String eventSpecificTitle
	UserState userState
	Date creationDate
	Date lastModifiedDate
	enum UserState {
		INVITED, INTERESTED, VOLUNTEERED, ACCEPTED, DENIED
	}
	
	static constraints = {
		event(nullable:false)
		user(nullable:false)
		eventSpecificTitle(nullable:true)
		userState(nullable:true)
		creationDate(nullable:false)
		lastModifiedDate(nullable:false)
	}
	static mapping = {
		userState enumType:"string"
	}
}
