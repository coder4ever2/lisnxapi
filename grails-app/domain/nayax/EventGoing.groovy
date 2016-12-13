package nayax

import java.util.Date;

class EventGoing {
	NayaxUser goer
	Date dateCreated
	Date lastUpdated
	ExternalEvent event
	Boolean notGoing = false

    static constraints = {
		goer(nullable:false)
		event(nullable:false)
    }
}
