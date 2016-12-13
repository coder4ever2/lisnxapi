package nayax

import java.util.Date;

import nayax.Enum.INVITATION_CHANNEL;

class LISNxInvitation {
	
	NayaxUser user
	String targetUserId
	INVITATION_CHANNEL channel
	String invitationMessage
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	
	static constraints = {
		channel(nullable: true )
		user(nullable:false)
		targetUserId(nullable:false)
		dateCreated(nullable: true)
		lastUpdated(nullable: true)
	}
	
	static mapping = {
		channel enumType:"string" 
		invitationMessage sqlType:"text"
	}

}
