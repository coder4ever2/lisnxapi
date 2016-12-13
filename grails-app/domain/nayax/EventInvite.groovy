package nayax

import java.util.Date;

import nayax.ExternalEvent;
import nayax.NayaxUser;

class EventInvite {
	
    NayaxUser sender
    NayaxUser receiver
    Date dateCreated
    Date lastUpdated
    ExternalEvent externalEvent
	
    static constraints = {
        sender(nullable:false)
        receiver(nullable:false)
        externalEvent(nullable: false)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }
}
