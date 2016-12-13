package nayax

import java.util.Date;

class LISNInvitation {

	NayaxUser sender
	NayaxUser receiver
	String phoneNumber
	LISN lisn
	Boolean invitationSent = false
	Boolean joined = false
	static belongsTo = [LISN]
	Date invitationAccepted
	Date invitationIgnored
	
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	

	static constraints = {
		sender(nullable:false)
		receiver(nullable:true)
		phoneNumber(nullable:true)
		lisn(nullable:false)
		invitationAccepted(nullable:true)
		invitationIgnored(nullable:true)
		
	}

}
