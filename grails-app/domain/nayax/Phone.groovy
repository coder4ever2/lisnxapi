package nayax

import java.util.Date;
import java.util.List;

class Phone {

    NayaxUser user
	String phoneNumber
	String countryCode
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	static belongsTo = [user: NayaxUser]
	

    static constraints = {
		user(nullable:false)
		phoneNumber(nullable:false)
		countryCode(nullable:true)
		
    }
	def getPhoneNumberFormatted(){
		return countryCode+phoneNumber
	}
}
