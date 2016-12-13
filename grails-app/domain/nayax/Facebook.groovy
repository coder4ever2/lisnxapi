package nayax

class Facebook {
    String fid
    String facebookLink
    Date lastLogin
    Date dateCreated
    String email
	NayaxUser user
	FacebookAccessToken facebookAccessToken
	Integer friendsTotalCount
    static belongsTo = [user: NayaxUser]

/*
    static searchable = true
*/

    static constraints = {
        fid()
        facebookLink(nullable: true)
        email(nullable: true)
        lastLogin()
        dateCreated(nullable: true)
        user(nullable: false)
		facebookAccessToken(nullable:true)
		friendsTotalCount(nullable:true)
    }
}

