import nayax.NayaxEvent;

import nayax.Activity;
import nayax.NConnection;
import nayax.NayaxLiveEvent
import nayax.SecRole;
import nayax.NayaxUser
import nayax.SecRoleConstants
import grails.converters.JSON;
import grails.util.Environment
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import nayax.Enum.NConnectionStatus
import nayax.SecUserSecRole
import nayax.Setting
import nayax.Enum.SettingType
import nayax.LISN
import nayax.LocationCoordinate
import nayax.UserLISNMap;

class BootStrap {

	public static final String SERVER_LISNX_COM = 'server@lisnx.com'
    def springSecurityService
    def mailService
    def grailsApplication
    def lisnService
	def asynchronousMailService

    def init = { servletContext ->
        TimeZone.setDefault(TimeZone.getTimeZone('GMT'))
        Map defaultSaveArgs = [flush: true, failOnError: true]
        def userRole = SecRole.findByAuthority(SecRoleConstants.ROLE_USER) ?: new SecRole(authority: SecRoleConstants.ROLE_USER).save(defaultSaveArgs)
        def adminRole = SecRole.findByAuthority(SecRoleConstants.ROLE_ADMIN) ?: new SecRole(authority: SecRoleConstants.ROLE_ADMIN).save(defaultSaveArgs)
        Date now = new Date()

		//testFacebookFriendsUrl()

		def user = NayaxUser.findByUsername('srinivasjaini@gmail.com')
        if (!user) {
            user = new NayaxUser(fullName: 'Srinivas Jaini',
                    password: springSecurityService.encodePassword('COMMENTED_OUT'),
                    enabled: true,
                    username: 'srinivasjaini@gmail.com',
                    website: 'sjaini.ulitzer.com',
                    bio: '''Srinivas is cool!''',
                    emailSent: true)
            use(groovy.time.TimeCategory) {
                user.dateOfBirth = now - 30.years  //G, who said I'm 40??
            }
            saveObject(user)
            SecUserSecRole.create(user, adminRole)
        }
		user = NayaxUser.findByUsername(SERVER_LISNX_COM)
		if (!user) {
			user = new NayaxUser(fullName: 'LISNx Team',
					password: springSecurityService.encodePassword('COMMENTED_OUT'),
					enabled: true,
					username: SERVER_LISNX_COM,
                    emailSent: true)
            use(groovy.time.TimeCategory) {
                user.dateOfBirth = now - 30.years
            }
            saveObject(user)
        }

        Setting setting = Setting.findBySettingType(SettingType.LATLONGRANGE)
        if (!setting) {
            new Setting(settingType: SettingType.LATLONGRANGE, value: "0.2").save(flush: true, failOnError: true)
        }
		setting = Setting.findBySettingType(SettingType.PIC_WIDTH)
		if (!setting) {
			new Setting(settingType: SettingType.PIC_WIDTH, value: "400").save(flush: true, failOnError: true)
		}

        setting = Setting.findBySettingType(SettingType.TIME_WINDOW)
        if (!setting) {
            new Setting(settingType: SettingType.TIME_WINDOW, value: 10 * 60 * 1000).save(flush: true, failOnError: true)
        }
		setting = Setting.findBySettingType(SettingType.CURRENT_ANDROID_BUILD)
		if (!setting) {
			new Setting(settingType: SettingType.CURRENT_ANDROID_BUILD, value: 1).save(flush: true, failOnError: true)
		}
		setting = Setting.findBySettingType(SettingType.CURRENT_IOS_BUILD)
		if (!setting) {
			new Setting(settingType: SettingType.CURRENT_IOS_BUILD, value: 1).save(flush: true, failOnError: true)
		}

        if (Environment.current.name.equals("qa")) {

            def aUser = NayaxUser.findByUsername('a@t.com')
            if (!aUser) {
                aUser = new NayaxUser(fullName: 'A T',
                        password: springSecurityService.encodePassword('COMMENTED_OUT2'),
                        enabled: true,
                        username: 'a@t.com',
                        website: 'a.t.com',
                        bio: '''A is cool!''',
                        emailSent: true)
                use(TimeCategory) {
                    aUser.dateOfBirth = now - 42.years
                }
                saveObject(aUser)
                SecUserSecRole.create(aUser, userRole)
            }
            def bUser = NayaxUser.findByUsername('b@t.com')
            if (!bUser) {
                bUser = new NayaxUser(fullName: 'B T',
                        password: springSecurityService.encodePassword('COMMENTED_OUT2'),
                        enabled: true,
                        username: 'b@t.com',
                        website: 'b.t.com',
                        bio: '''B is cool!''',
                        emailSent: true)
                use(groovy.time.TimeCategory) {
                    bUser.dateOfBirth = now - 34.years
                }
                saveObject(bUser);
                SecUserSecRole.create(bUser, userRole)
            }
            def cUser = NayaxUser.findByUsername('c@t.com')
            if (!cUser) {
                cUser = new NayaxUser(fullName: 'C T',
                        password: springSecurityService.encodePassword('COMMENTED_OUT3'),
                        enabled: true,
                        username: 'c@t.com',
                        website: 'c.t.com',
                        bio: '''c is cool!''',
                        emailSent: true)
                use(groovy.time.TimeCategory) {
                    cUser.dateOfBirth = now - 34.years
                }
                saveObject(cUser);
                SecUserSecRole.create(cUser, userRole)
            }
            def dUser = NayaxUser.findByUsername('d@t.com')
            if (!dUser) {
                dUser = new NayaxUser(fullName: 'D T',
                        password: springSecurityService.encodePassword('COMMENTED_OUT4'),
                        enabled: true,
                        username: 'd@t.com',
                        website: 'd.t.com',
                        bio: '''d is cool!''',
                        emailSent: true)
                use(groovy.time.TimeCategory) {
                    dUser.dateOfBirth = now - 34.years
                }
                saveObject(dUser);
                SecUserSecRole.create(dUser, userRole)
            }
            def userLaddoo = NayaxUser.findByUsername('reachpvd@gmail.com')
            if (!userLaddoo) {
                userLaddoo = new NayaxUser(fullName: 'Laddoo Jaini',
                        enabled: true,
                        password: springSecurityService.encodePassword('COMMENTED_OUT5'),
                        username: 'reachpvd@gmail.com',
                        website: 'dhanalakota.com',
                        bio: '''Laddoo is cool!''',
                        emailSent: true)

                use(groovy.time.TimeCategory) {
                    userLaddoo.dateOfBirth = now - 32.years
                }

                def activity = new Activity(name: 'Login')
                saveObject(activity)
                saveObject(userLaddoo)
                SecUserSecRole.create(userLaddoo, userRole)
            }

            def user2 = NayaxUser.findByUsername('srinivasjaini@yahoo.com')
            if (!user2) {
                user2 = new NayaxUser(fullName: 'S J',
                        password: springSecurityService.encodePassword('COMMENTED_OUT5'),
                        enabled: true,
                        username: 'srinivasjaini@yahoo.com',
                        website: 's.ulitzer.com',
                        bio: '''S is cool!''',
                        emailSent: true)

                use(groovy.time.TimeCategory) {
                    user2.dateOfBirth = now - 40.years
                }
                saveObject(user2)
                SecUserSecRole.create(user2, userRole)
            }
        }
        // Create Mock lisns

//        lisnService.loadingMockData()

    }

	private testFacebookFriendsUrl() {
		URL facebookUrl = new URL("https://graph.facebook.com/1273617912/friends?fields=id,first_name,last_name,bio,email,picture,devices,installed,security_settings,birthday&access_token=CAACVQ1GOOvABAGbF6SAjg86upyDABLnb4wxNwftpLaSh0492K2zWZBXasgK0EZCJThKSHQGjiQ1l1o9NF5ZCtJkk3gBp72rysQqsYD2gAWV0t8jPZCxnpH1KgrKJViICcfSQq2eLz5jK7nzS5EvfF3ZBLiMXWZCDIZCBW4DkXHAiqiUZBDuf8uxGLkelZC47pwN4ZD")
		String response = null;
		HttpURLConnection conn = (HttpURLConnection) facebookUrl.openConnection();
		try {
			int respCode = conn.responseCode
			if (respCode == 400) {
				log.error("COULD NOT MAKE CONNECTION")
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				log.info(br.text)
				def jsonResp = JSON.parse(br.text)
			} else {
				response = conn.getInputStream().getText()
				log.info("Response is")
				def jsonResp = JSON.parse(response)

				log.info(response)
			}
		} finally {
			conn.disconnect()
		}
		log.info("RETURNING RESPONSE")
	}
	def testLinkedInProfileUrl() {

		/*
		 * 10-22 19:01:56.066: I/com.lisnx.activity.LinkedInActivity(824): {
		 * TOKEN_EXPIRATION_DATE=1387666914377,
		 * lid=Xekey08vqc,
		 * linkedinProfileURL=http://www.linkedin.com/in/srinivasjaini,
		 * token=582d6b16-80be-4f0f-8fc5-c3015a9366de,
		 * LINKEDIN_ACCESS_TOKEN_SEC=e74e7e7d-b1ba-49f5-97df-160ca1847507,
		 * LINKEDIN_ACCESS_TOKEN=93fba0b5-5f67-4f30-9c2a-f8c2b7a3f2e4}

		 */

		String linkedinUrl = "http://api.linkedin.com/v1/people/id=Xekey08vqc/connections?access_token=93fba0b5-5f67-4f30-9c2a-f8c2b7a3f2e4"
		//http://api.linkedin.com/v1/people/id=12345/connections
		URL facebookUrl = new URL(linkedinUrl)
		String response = null;
		HttpURLConnection conn = (HttpURLConnection) facebookUrl.openConnection();
		try {
			int respCode = conn.responseCode
			if (respCode == 400) {
				log.error("COULD NOT MAKE CONNECTION")
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				log.info(br.text)
				def jsonResp = JSON.parse(br.text)
			} else {
				response = conn.getInputStream().getText()
				log.info("Response is")
				def jsonResp = JSON.parse(response)

				log.info(response)
			}
		} finally {
			conn.disconnect()
		}
		log.info("RETURNING RESPONSE")
	}


    def saveObject(def obj) {
        if (obj.validate()) {
            obj.save(flush: true, failOnError: true)
        } else {
            log.error "Error in bootstrap while saving ${obj}"
            obj.errors.allErrors.each { log.error it }
        }
    }

    def destroy = {
    }
}
