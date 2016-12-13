/**
 *
 */
package nayax

import nayax.Enum.ProfileShareType

/**
 * I think this should have been named LISNMember
 * @author Srinivas
 * @date Aug 12, 2011
 * Aug 12, 2011
 * Srinivas
 */
class UserLISNMap {
    NayaxUser user
    Date dateCreated
    Date lastUpdated
    Date lastViewed
    LISN lisn
    String lastViewedMessageId

    /*
     By default assigning ALL value to this. As soon as the functionality is implemented in the web then we can
     remove the default value.For mobile client the API needs to be refactored. While joining a LISN they
     need to send the profileShareType as well.
    */

    ProfileShareType profileShareType = ProfileShareType.ALL
    static belongsTo = [LISN]

    static constraints = {
        lisn(nullable: false)
        user(nullable: false)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        lastViewed(nullable: true)
        lastViewedMessageId(blank: true, nullable: true)
    }
	
	static mapping = {
		profileShareType enumType:"string"
	}
}
