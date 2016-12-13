package nayax

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import grails.util.Environment
import org.codehaus.groovy.grails.plugins.springsecurity.ui.RegistrationCode

import grails.plugin.asyncmail.AsynchronousMailService

class NayaxMailerService {
	
	static transactional = true
	def grailsApplication
	def userService
	def mailService
	AsynchronousMailService asynchronousMailService
	
	Boolean sendConnectionRequestMail(NConnection nConnection) {
		String subjectLine = "Invitation to connect with ${nConnection.owner.fullName}"
		asynchronousMailService.sendAsynchronousMail {
			to nConnection.connection.email
			from "notifications@lisnx.com"
			subject subjectLine
			body(view: '/email/connectionRequest', model: [nConnection: nConnection])
		}
		return true
	}
	
	Boolean sendConnectionAcceptedMail(NConnection nConnection) {
		String subjectLine = "You are now connected with ${nConnection.connection.fullName}"
		asynchronousMailService.sendAsynchronousMail {
			to nConnection.owner.email
			from "notifications@lisnx.com"
			subject subjectLine
			body(view: '/email/connectionAccepted', model: [nConnection: nConnection])
		}
		return true
	}
	
	Boolean sendRegistrationMail(NayaxUser nayaxUser) {
		def registrationCode = RegistrationCode.findByUsername(nayaxUser.username)
		registrationCode = registrationCode ? registrationCode : new RegistrationCode(username: nayaxUser.username).save()
		def conf = SpringSecurityUtils.securityConfig
		asynchronousMailService.sendAsynchronousMail {
			to nayaxUser.username
			from "notifications@lisnx.com"
			subject 'Your registration with LISNX'
			body(view: "/email/welcome_email", model: [user: nayaxUser, registrationCode: registrationCode])
		}
		sendAccountCreatedAlertEmail(nayaxUser)
		return true
	}
	
	
	Boolean sendAccountActivationMail(NayaxUser nayaxUser) {
		def registrationCode = RegistrationCode.findByUsername(nayaxUser.username)
		if(!registrationCode) {
			registrationCode = registrationCode ? registrationCode : new RegistrationCode(username: nayaxUser.username).save()
			def conf = SpringSecurityUtils.securityConfig
			asynchronousMailService.sendAsynchronousMail {
				to nayaxUser.username
				from "notifications@lisnx.com"
				subject 'Activate your LISNX account'
				body(view: "/email/accountActivation", model: [nayaxUser: nayaxUser, registrationCode: registrationCode])
			}
			sendAccountCreatedAlertEmail(nayaxUser)
			return true
		}
		return true
	}
	
	//TODO  why we are using this sendEmail...
	public void sendEmail(def toEmails, NayaxLiveEvent event, NayaxUser originator, def invitationUrl) {
		asynchronousMailService.sendAsynchronousMail {
			to toEmails
			from "notifications@lisnx.com"
			subject 'Invited to ' + event.description
			body(
					view: "/email/welcome_email",
					model: [url: invitationUrl, user: originator]
					)
		}
	}
	
	Boolean sendMessageToLISNers(def id, String message, fromUser = userService.getUser()) {
		LISN lisn = LISN.read(id)
		lisn.joins.each {UserLISNMap userLISNMap ->
			if (userLISNMap.user.id != fromUser.id) {
				String email = userLISNMap.user.email
				asynchronousMailService.sendAsynchronousMail {
					to email
					from "notifications@lisnx.com"
					subject 'Message from ' + fromUser.fullName
					body(
							view: "/email/messageToLISN",
							model: [from: fromUser, to: userLISNMap.user, message: message]
							)
				}
			}
		}
		return true
	}
	
	Boolean sendMessageToLISNMember(NayaxUser nayaxUser, String message,String id, fromUser = userService.getUser()) {
		String email = nayaxUser.email
		asynchronousMailService.sendAsynchronousMail {
			to email
			from "notifications@lisnx.com"
			subject 'Message from ' + fromUser.fullName
			body(
					view: "/email/messageToLISN",
					model: [from: fromUser, to: nayaxUser, message: message,id: id]
					)
		}
		return true
	}
	// TODO what is use of testintroEmail
	def testIntroEmail(NayaxUser nayaxUser) {
		def registrationCode = new RegistrationCode()
		registrationCode.token = "token"
		def url = "url"
		def nConnection = new NConnection();
		nConnection.owner = nayaxUser
		nConnection.connection = nayaxUser
		def to1 = nayaxUser
		def from1 = nayaxUser
		def message1 = "Message..."
		asynchronousMailService.sendAsynchronousMail {
			to "srinivasjaini@gmail.com"
			from "notifications@lisnx.com"
			subject 'Reset Password successfully.'
			body(view: "/email/welcome_email", model: [registrationCode: registrationCode, nayaxUser: nayaxUser, nConnection: nConnection, to1: to1, from1: from1, message1: message1])
		}
	}
	
	
	def sendAccountCreatedAlertEmail(NayaxUser nayaxUser) {
		if (Environment.current == Environment.PRODUCTION) {
			asynchronousMailService.sendAsynchronousMail {
				def toMail = grailsApplication.config.registration.alertmail
				toMail.each {
					log.debug "mail to  "  + it.toString()
					to it.toString()
					from "notifications@lisnx.com"
					subject 'New User Created'
					html "New user registered : <br/> Username : ${nayaxUser.username} <br/> Name : ${nayaxUser.fullName}"
				}
			}
		}
	}
	def sendExternalConnectionRequestEmail(NayaxUser nayaxUser, NayaxUser targetUser, String socialNetwork) {
		
		def link = null
		switch(socialNetwork){
			case "facebook":
				link= "http://www.facebook.com/people/@/" + nayaxUser.facebook.fid
				break
			case "linkedin":
				link= nayaxUser.linkedin.profileURL
				break
			default:
				break
		}
		asynchronousMailService.sendAsynchronousMail {
			def toMail = targetUser.username
			
			toMail.each {
				log.debug "mail to  "  + "srinivasjaini@gmail.com"//it.toString()
				to toMail
				from "notifications@lisnx.com"
				subject 'LISNx: new connection request'
                body(view: "/email/externalConnectionRequestEmail", model: [nayaxUser: nayaxUser, socialNetwork: socialNetwork, link: link])
			}
		}
	}
}
