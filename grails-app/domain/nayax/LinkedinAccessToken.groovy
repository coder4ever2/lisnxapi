package nayax

import java.util.Date;

class LinkedinAccessToken {
	
	String accessToken
	String accessTokenSec
	Date expirationDate
	String permissions
	Date dateCreated
	Date lastUpdated
	Date dateProcessed
	String oauthVersion
	
	
	static belongsTo = [linkedin: Linkedin]

	/*
	static searchable = true
	*/

	static constraints = {
		accessToken(nullable: false)
		accessTokenSec(nullable:true)
		expirationDate(nullable: true)
		permissions(nullable:false)
		linkedin(nullable: true)
		lastUpdated(nullable:true)
		dateCreated(nullable:true)
		dateProcessed(nullable:true)
		oauthVersion(nullable:true)
		
	}

}
