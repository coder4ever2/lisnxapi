package nayax

import java.util.Date;
import nayax.LISNMessage;
import nayax.NayaxUser;

class DirectMessageLike {
		
		PrivateMessage privateMessage 
		NayaxUser user
		Date dateCreated // grails will auto timestamp
		Date lastUpdated // grails will auto timestamp
		Boolean unliked = false
		
	    static constraints = {
			privateMessage(nullable:false)
			user(nullable:false)
	    }
		
}

