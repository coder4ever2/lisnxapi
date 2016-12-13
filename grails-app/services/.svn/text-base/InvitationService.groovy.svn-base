

import nayax.InvitationCode;
import nayax.*;
import org.springframework.beans.factory.InitializingBean;
class InvitationService implements InitializingBean {
	def grailsApplication
	def setting
	private AES aes = new AES()
	
	void afterPropertiesSet() {
		this.setting = grailsApplication.config.setting
	}
	
	public InvitationCode createInvitationCode(NayaxUser user, NayaxLiveEvent event, Invitee invitee){
		InvitationCode invitationCode = new InvitationCode() 
		invitationCode.setHost(user)
		if(event)
			invitationCode.setEvent(event)
		invitationCode.setEmail(invitee.email)
		def code =  aes.encrypt (invitationCode.toString())
		invitationCode.code = code;
		if(!invitationCode.save()){
			invitationCode.errors.allErrors.each{error -> log.debug " An error occurred with user :${error}" }
		}else{
			log.debug "invitation code saved: ${code}"
		}
		return invitationCode
	}
}
