package lisnxapi

import java.util.Date;

import nayax.NayaxUser;

class GoogleInviteeHost {
	
    String email
    Date dateCreated
    Date lastUpdated	
	
    static belongsTo = [user: NayaxUser]
    static hasMany = [invitees: GoogleInvitee]

    static constraints = {
        email(blank: false, unique: true, email: true)
    }
}
