package nayax

import java.util.Date;

/**
 * Using Twitter's Fabric framework to create and authenticate user by phone number.
 *
 * https://docs.fabric.io/#
 *
 */
class PhoneUser {	
	
    String userId
    String phoneNumber
    String thumbnailURL
    String profilePictureURL
    String firstName
    String lastName
    String userName
    String email
    Date lastLogin
    Date dateCreated
    Date lastUpdated
    NayaxUser user
    static belongsTo = [user: NayaxUser]
	
    static constraints = {
        userId()
        phoneNumber(nullable: true)
        thumbnailURL(nullable: true)
        profilePictureURL(nullable: true)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        firstName(nullable: true)
        lastName(nullable: true)
        userName(nullable: true)
        lastLogin()
        user(nullable: false)
    }
}
