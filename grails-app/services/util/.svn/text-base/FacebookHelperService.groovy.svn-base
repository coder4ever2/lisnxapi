package util

import nayax.Facebook
import nayax.SecRole
import nayax.NayaxUser
import nayax.SecRoleConstants
import nayax.SecUserSecRole

class FacebookHelperService {

    def springSecurityService

    static transactional = true

    def populateDataFromJson(def user) {
        boolean newUser

        // HANDLE CASES OF CREATING ROLES FOR USER AND REAUTHENTICATE THEM DONE
        NayaxUser existingNayaxUser = NayaxUser.findByUsername(user.email)
        Facebook existingProfile = Facebook.findByFid(user.id)
        if (existingNayaxUser) {
            log.debug("# User already present in the database , either he is coming after he has registered with us through WEBTABLEAU Or hitting the facebook button second time #")
            newUser = false
            if (existingProfile) {
                if (!existingProfile.user)
                    existingProfile.setUser(existingNayaxUser)
            }

            else {
                log.debug "#############GLAD YOU DECIDED TO REGISTER WITH WEBTABLEAU , BUT YOU SHOULD COME THROUGH US"
                // FETCH EXISTING USER OF WEBTABLEAU CREDENTIALS AND REAUTHENTICATE USER
                if (existingNayaxUser.enabled) {
                    log.debug "## YOUR PASSWORD IS ENABLED"
                }
                else {
                    log.debug "## YOUR PASSWORD IS NOT ENABLED"
                }
                Facebook facebookObj = new Facebook(fid: user.id, lastLogin: new Date()).save(flush: true)
                updateExistingNayaxUser(existingNayaxUser, facebookObj, user)
                springSecurityService.reauthenticate(existingNayaxUser.username)
                existingNayaxUser.merge(flush: true)
                return [existingNayaxUser, newUser]
            }
            // CREATE ROLE AND REAUTHENTICATE USER
            SecRole secRole1 = SecRole.findByAuthority(SecRoleConstants.ROLE_USER)

            // REAUTHENTICATE USER
            springSecurityService.reauthenticate(existingProfile.user.username)
            existingProfile.user.merge(flush: true)
            return [existingProfile.user, newUser]
        }
        else {
            log.debug("# User NOT already present in the database , but facebook data is present #")
            newUser = true
            Facebook facebookObj = new Facebook(fid: user.id, lastLogin: new Date()).save(flush: true)
            NayaxUser nayaxUser = new NayaxUser(fullName: user.name, facebook: facebookObj, username: user.email,
                    password: springSecurityService.encodePassword("pleaseChangeMe"), enabled: true)
            if (nayaxUser.save(flush: true)) {
                log.debug("# WORK DONE , saved user and save the user in session #")
                facebookObj.setUser(nayaxUser)
                springSecurityService.reauthenticate(nayaxUser.username)
                return [nayaxUser, newUser]
            }
            else {
                log.error("## ERROR IN VALIDATING DATA So NOT SETTING THE USER IN SESSION ##")
                nayaxUser.errors.allErrors.each { error ->
                    log.error("### ERROR IS ${error} ####")
                }
                return [nayaxUser, newUser]
            }
        }
    }

    def updateExistingNayaxUser(NayaxUser nayaxUserInstance, Facebook facebookInstance, def userFacebookJson) {
        nayaxUserInstance.facebookUrl = userFacebookJson.link
        nayaxUserInstance.facebook = facebookInstance
        nayaxUserInstance.save()
    }
}
