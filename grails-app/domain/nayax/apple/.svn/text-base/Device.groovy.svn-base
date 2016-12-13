package nayax.apple

import nayax.NayaxUser;

class Device {
	
	String token
	List<String> messages
	int badgeCount
	String deviceType
	NayaxUser user
	String userStatus
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	static belongsTo = [user: NayaxUser]
	

    static constraints = {
		user(nullable:false)
		deviceType(nullable:true)
		userStatus(nullable:true)
    }
}
