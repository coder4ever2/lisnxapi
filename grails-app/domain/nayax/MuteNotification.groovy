package nayax

import java.util.Date;

class MuteNotification {
	
	NayaxUser user
	TargetType targetType = TargetType.UNKNOWN
	String targetId
	String duration
	Date endDate
	Date dateCreated
	Date lastUpdated
	enum TargetType {
		USER, LISN, EXTERNAL_EVENT, UNKNOWN
	}

    static constraints = {
		user(nullable:false)
		targetType(nullable:true)
		targetId(nullable:true)
		duration(nullable:true)
		dateCreated(nullable: true)
		lastUpdated(nullable: true)
		endDate(nullable:true)
    }
}
