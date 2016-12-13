package nayax

class LISNxPushNotification {
	
	String content
	Boolean targetAll = true
	Date dateProcessed
	Picture picture
	Boolean isActive = false

    static constraints = {
		content(nullable:  true, blank: true)
		picture(nullable:true, blank:true)
		dateProcessed(nullable:true)
    }
	static mapping = {
		content(type: 'text')
	}
}
