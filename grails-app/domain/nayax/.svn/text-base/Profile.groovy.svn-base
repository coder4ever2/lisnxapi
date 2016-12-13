package nayax

import java.util.Date;

class Profile {

    NayaxUser user
	String professionalTitle
	String email
	String company
	String linkedInUrl
	
	Date dateCreated // grails will auto timestamp
	Date lastUpdated // grails will auto timestamp
	
	static belongsTo = [user: NayaxUser]
	

    static constraints = {
		user(nullable:false)
		professionalTitle(nullable:true)
		email(nullable:true)
		company(nullable:true)
		linkedInUrl(nullable:true)
    }
	String getEmail(){
		if(email)
			return email
		else return user.username
	}
	String getLinkedInUrl(){
		if(linkedInUrl)
			return linkedInUrl
		else return user.linkedin?.profileURL
	}
}
