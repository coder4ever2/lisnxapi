package nayax



import javax.servlet.http.*
import org.apache.commons.logging.LogFactory
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.web.context.request.RequestContextHolder
class LoggingSecurityEventListener implements
    ApplicationListener<AbstractAuthenticationEvent>, LogoutHandler {
    def config = ConfigurationHolder.config
    //private static final log = LogFactory.getLog(this)
    static def usersID=[];

    void onApplicationEvent(AbstractAuthenticationEvent event) {
        event.authentication.with {
            def username = principal.hasProperty('username')?.getProperty(principal) ?: principal
            def id=principal.hasProperty('id')?.getProperty(principal) ?: principal
            /*println "event=${event.class.simpleName} username=${username} " +
                "remoteAddress=${details.remoteAddress} sessionId=${details.sessionId}"*/
            def session=RequestContextHolder.currentRequestAttributes().getSession();
            session.loginUserId=id;
            //println "id---------------------------->>> : " + id;


        }
    }

    void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        authentication.with {
            def username = principal.hasProperty('username')?.getProperty(principal) ?: principal
           /* println "event=Logout username=${username} " +
                "remoteAddress=${details.remoteAddress} sessionId=${details.sessionId}"*/
        }
    }
}