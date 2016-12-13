package nayax

class InvitationCode {
	NayaxUser host
	NayaxLiveEvent event
	String email
	String code
	public String toString(){
		return "${host.fullName}"+"${event.description}"+"${email}"
	}
	
	
	static constraints = {
		email(nullable:true)
		code(nullable : true)
	}
}
