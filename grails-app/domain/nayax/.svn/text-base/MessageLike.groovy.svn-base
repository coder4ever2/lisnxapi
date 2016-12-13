package nayax

import java.util.Date;
import nayax.LISNMessage;

class MessageLike {
	
	LISNMessage lisnMessage
	NayaxUser user
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	Boolean unliked = false
	
    static constraints = {
		lisnMessage(nullable:false)
		user(nullable:false)
    }
}
