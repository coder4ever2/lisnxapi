package nayax

import java.util.Date;

/**
 * Captures the information that a user shared his/her profile with another LISNx user or a LISN
 * @author sjaini
 *
 */
class ProfileShare {

	NayaxUser user
	NayaxUser receiver
	LISN lisn
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	static belongsTo = [user: NayaxUser]
	

    static constraints = {
		user(nullable:false)
		receiver(nullable:true)
		lisn(nullable:true)
    }

}
