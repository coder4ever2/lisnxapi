package nayax

import java.util.Date;

class FacebookAccessToken {
	
	String accessToken
	Date expirationDate
	String permissions
	Date dateCreated
	Date lastUpdated
	Date dateProcessed
	boolean invalid = false
	
	
	static belongsTo = [facebook: Facebook]

	/*
    static searchable = true
	*/

    static constraints = {
        accessToken(nullable: false)
        expirationDate(nullable: true)
        permissions(nullable:false)
        facebook(nullable: false)
		lastUpdated(nullable:true)
		dateCreated(nullable:true)
		dateProcessed(nullable:true)
		invalid(nullable:true)
    }
	static mapping = {
		permissions type: 'text'
	 }

}
