
package nayax

import java.util.Date;

/**
 * @author Srinivas
 * @date May 1, 2012
 * 
 */
class LISNMessage {
	NayaxUser user
	LISN lisn
	Date dateCreated
	Date lastUpdated
	String content
	Picture picture
	MessageType messageType = MessageType.MESSAGE
	enum MessageType {
		CREATED, INVITED, JOINED, IGNORED, MESSAGE, CONTACT_INFO
	}
	
	static belongsTo = [LISN]
	
	static hasMany =[messageEvents: LISNMessageEvent]
	
	static constraints = {
		dateCreated(nullable: true)
		lastUpdated(nullable:true)
		user(nullable:false)
		lisn(nullable:true)
		content(nullable:true)
		picture(nullable:true, blank:true)
		messageType(nullable:true)
	}

    static mapping = {
        content(type: 'text')
    }
	def getLikes(){
		return MessageLike.findAllWhere(lisnMessage: this, unliked:false)
	}
}
