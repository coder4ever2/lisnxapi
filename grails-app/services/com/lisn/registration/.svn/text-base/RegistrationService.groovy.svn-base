package com.lisn.registration

import nayax.NayaxUser
import nayax.Facebook
import nayax.Linkedin

class RegistrationService {

    static transactional = true
    def nayaxMailerService

    Boolean handleGigyaLoginUsingProvider(NayaxUser nayaxUser, def providerObject, Boolean sendAccountActivationMail) {
        Boolean status = false;
        log.trace "Inside Registration Service : handleGigyaLoginUsingProvider"
        if (saveObject(nayaxUser)) {
            log.trace "Inside Registration Service : handleGigyaLoginUsingProvider : userInstanceSaved"
            providerObject.user = nayaxUser
            if (saveObject(providerObject)) {
                log.trace "Inside Registration Service : handleGigyaLoginUsingProvider : providerInstanceSaved"
                if (providerObject instanceof Facebook) {
                    log.trace "Inside Registration Service : handleGigyaLoginUsingProvider : facebook instance"
                    nayaxUser.facebook = providerObject
                } else if (providerObject instanceof Linkedin) {
                    log.trace "Inside Registration Service : handleGigyaLoginUsingProvider : linkedin instance"
                    nayaxUser.linkedin = providerObject
                }
                if (saveObject(nayaxUser)) {
                    if (sendAccountActivationMail) {
                        log.trace "Inside Registration Service : handleGigyaLoginUsingProvider : userInstanceSavedAgain"
                        status = nayaxMailerService.sendAccountActivationMail(nayaxUser)
                        log.trace "Inside Registration Service : handleGigyaLoginUsingProvider : registration mail sent"
                    } else {
                        status = true
                    }
                }
            }
        }
        return status;
    }

    private Boolean saveObject(def obj) {
        log.trace "Inside saveObject for ${obj}"
        if (obj.validate()) {
            log.trace "Inside saveObject : if true"
            obj.errors.allErrors.each { log.error it }
            obj.save()
            return true
        } else {
            log.trace "Inside saveObject : else true"
            obj.errors.allErrors.each { log.error it }
            obj.discard()
            return false
        }
    }
}
