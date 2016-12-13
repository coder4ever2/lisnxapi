package nayax

import java.util.Date;

class PhoneInvitation {

	NayaxUser sender
	String phoneNumber
	LISN lisn
	Boolean invitationSent = false
	
	static belongsTo = [LISN]
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	

	static constraints = {
		sender(nullable:false)
		phoneNumber(nullable:false)
		lisn(nullable:true)
		
	}

}
