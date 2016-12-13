package lisnxapi

import java.util.Date;

class GoogleInvitee {
	
    String email
    Date invitedDate
    Date dateCreated
    Date lastUpdated
    
    static belongsTo = [host: GoogleInviteeHost]
   
    static constraints = {
        email(blank: false, unique: true, email: true)
        invitedDate(nullable: false)
    }
}
