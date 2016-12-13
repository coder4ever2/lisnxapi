package nayax

import nayax.Enum.ProfileShareType

class ConnectionSharingType {

    NayaxUser sourceUser
    NayaxUser targetUser
    ProfileShareType profileShareType = ProfileShareType.ALL

    static constraints = {
        sourceUser(nullable: false)
        targetUser(nullable: false)
    }
	
	static mapping = {
		profileShareType enumType:"string"
	}
	
}
