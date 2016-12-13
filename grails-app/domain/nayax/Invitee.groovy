package nayax

class Invitee {
	String email
	String facebookId
	Integer nayaxId
	InvitationStatus status
	
	enum InvitationStatus {
		SEND_FAILURE, SENT, ACCEPTED, UNKNOWN
	}
	
	
	static constraints = {
		email(email:true, nullable:true)
		facebookId(nullable:true)
		nayaxId(nullable:true)
		status(nullable:true)
	}
	static mapping = {
		status enumType:"string"
	}
}
