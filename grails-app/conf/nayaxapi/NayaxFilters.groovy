package nayaxapi

import nayax.*;
import org.springframework.web.context.request.RequestContextHolder as RCH
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import java.util.logging.Logger

class NayaxFilters {
    def springSecurityService
    def mailService
    def grailsApplication
    def userService
	//public static final log = org.apache.commons.logging.LogFactory.getLog(this)
    def filters = {
        
        saveObjectsInSession(controller: "*", action: "*") {
            before = {
                log.info "Params : ${params}"
				log.info 'JSON: '+request.JSON
            }
            after = {
                if (params.token) {
                    def token = MobileAuthToken.findByToken(params.token)
                    def latitude = params.latitude?.toString()?.toDouble()
                    def longitude = params.longitude?.toString()?.toDouble()
                    if (MobileAuthToken.isTokenValid(params.token) && longitude && latitude) {
                        def nayaxUser = token.nayaxUser
                        def recentEventActivity = nayax.UserActivityMap.findByNayaxUser(nayaxUser, [sort: "activityTime", order: "desc"])
                        def currentDate = new Date()
                        if (!recentEventActivity || (recentEventActivity.activityTime.time + (0.5 * 60 * 1000)) < System.currentTimeMillis()) {
                            def searchEventActivity = new nayax.UserActivityMap()
                            searchEventActivity.nayaxUser = nayaxUser
                            searchEventActivity.activity = Activity.findByName('Login')
                            searchEventActivity.activityTime = new Date()
                            searchEventActivity.latitude = latitude + 90
                            searchEventActivity.longitude = longitude + 180
                            if (!searchEventActivity.merge()) {
                                searchEventActivity.errors.allErrors.each {error ->
                                    log.info " Inside NayaxFilter. An error occurred with USERActivity :${error}"
                                }
                            }
                        }
                        else {
                            log.info "Inside NayaxFilter. Not recording user activity."
                        }
                    }
                }else{
                    log.info " Inside NayaxFilter. No token found in params."
                }
            }
            return true
        }
    }
}
