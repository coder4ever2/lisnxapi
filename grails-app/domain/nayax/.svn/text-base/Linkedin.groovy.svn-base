package nayax

import java.util.Date;

/**
 * @author Srinivas Jaini
 * @date Sep 21, 2011
 *
 */
class Linkedin {
    String thumbnailURL
    String profilePictureURL
    String firstName
    String lastName
    String loginProviderUID
    String profileURL
    String email
    Date lastLogin
    Date dateCreated
    Date lastUpdated
    Date lastConnsAPICalledDate
    NayaxUser user
    LinkedinAccessToken linkedinAccessToken
    static belongsTo = [user: NayaxUser]
    /*    static searchable = true*/

    static constraints = {
        loginProviderUID()
        thumbnailURL(nullable: true)
        profilePictureURL(nullable: true)
        email(nullable: true)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        firstName(nullable: true)
        lastName(nullable: true)
        profileURL(nullable: true)
        lastLogin()
        lastConnsAPICalledDate(nullable: true)
        user(nullable: false)
        linkedinAccessToken(nullable:true)
    }
}
