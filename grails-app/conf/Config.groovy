// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// content field in PrivateMessage was auto-trimmed, causing user input strings with ending space trimmed resulting in bugs.
grails.databinding.trimStrings = false
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

grails.rangeValue = 0.2
grails.timeRangeValueForUserActivity = 10 * 60 * 1000
devEnv = true
// set per-environment serverURL stem for creating absolute links
environments {
    production {
        //grails.serverURL = "http://www.webtableau.com"
        //TODO: This will break if the application is deployed as root. In case app is deployed as root then remove the appName part.
        grails.serverURL = "https://www.lisnx.com/mobile"
        facebook.applicationSecret = 'COMMENTED_OUTFASECRET'
        facebook.applicationId = 'COMMENTED_OUTFAID'
		webImagePath ="http://www.lisnx.com/user_images/"
    }

    qa {
        grails.serverURL = "http://qa.lisnx.com"
        facebook.applicationSecret = ''
        facebook.applicationId = ''
    }

    development {
        grails.serverURL = "http://localhost:9997"
        facebook.applicationSecret = ''
        facebook.applicationId = ''
		webImagePath ="http://localhost:9997/user_images/"
    }

    test { grails.serverURL = "http://localhost:8080" }
}
//254519357898002&app_id=254519357898002
// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    debug 'nayax'
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d{MM-dd-yy HH:mm:ss,SSS} %5p %c{1} - %m%n')
    }
    debug 'grails'
    info 'grails'
    trace 'grails'
    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'

    warn 'org.mortbay.log'
	environments {
		production {
			// Override previous setting for 'grails.app.controller'
			info 'grails'
		}
	}
}

/*
log4j = {

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'

    warn 'org.mortbay.log'


}
*/


grails {
    mail {
        host = "smtp.gmail.com"
        port = "465"
        ssl = "on"
        username = "team@lisnx.com" // Please Change This To Some Valid Value
        password = "COMMENTED_OUT"       // Please Change This Corresponding To The Above Value
        props = ["mail.smtp.auth": "true",
                "mail.smtp.socketFactory.port": "465",
                "mail.smtp.socketFactory.class": "javax.net.ssl.SSLSocketFactory",
                "mail.smtp.socketFactory.fallback": "false"]

    }
}

/*grails {
    mail {
        host = "smtp.gmail.com"
        port = 465
        username = "lisnx123@gmail.com"
        password = "COMMENTED_OUT"
        props = ["mail.smtp.auth": "true",
                "mail.smtp.socketFactory.port": "465",
                "mail.smtp.socketFactory.class": "javax.net.ssl.SSLSocketFactory",
                "mail.smtp.socketFactory.fallback": "false"]

    }
}*/

//grails.sendMail.from = ['notifications@lisnx.com']


facebook.applicationSecret = 'COMMENTED_OUTFASECRET'
facebook.applicationId = 'COMMENTED_OUTFAID'

linkedin.applicationSecret = 'COMMENTED_OUTLASECRET'
linkedin.applicationId = 'COMMENTED_OUTLAID'


// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'nayax.NayaxUser'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'nayax.SecUserSecRole'
grails.plugins.springsecurity.authority.className = 'nayax.SecRole'

grails.plugins.springsecurity.securityConfigType = grails.plugins.springsecurity.SecurityConfigType.InterceptUrlMap
grails.plugins.springsecurity.interceptUrlMap = [
        '/buildInfo/**': ['ROLE_ADMIN'],
        '/nayaxUser/**': ['IS_AUTHENTICATED_REMEMBERED'],
        '/lisn/fillProfile/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/lisn/**': ['IS_AUTHENTICATED_REMEMBERED'],
        '/nayaxLiveEvent/**': ['IS_AUTHENTICATED_FULLY'],
        '/js/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/css/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/images/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/login/auth/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/lisn/getLisnsAroundMe/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/lisn/emailPopUp/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/lisn/joinLisnWithoutLogin/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/includes/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/register/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/email/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**': ['IS_AUTHENTICATED_ANONYMOUSLY']
]
grails.plugins.springsecurity.auth.loginFormUrl = '/'
grails.plugins.springsecurity.failureHandler.defaultFailureHandler = '/'
grails.plugins.springsecurity.successHandler.defaultTargetUrl = "/lisn/index"
//grails.views.javascript.library = "dojo"

grails.views.default.codec = 'html';

//Config settings specific to breadcrumbs
breadcrumbs.crumbs.max = 10
breadcrumbs.crumbs.showDuplicates = true;
breadcrumbs.selector = "title"

grails.app.context = "/"
grails.plugins.springsecurity.useSecurityEventListener = true
grails.plugins.springsecurity.logout.handlerNames =
    ['rememberMeServices',
            'securityContextLogoutHandler',
            'securityEventListener']

base.url = "www.lisnx.com";

registration.alertmail = ["srinivasjaini@gmail.com"]


pictureLocation = "/var/lib/tomcat7/webapps/user_images/"
promoImageLocation ='/var/lib/tomcat7/webapps/promo-images/'
mashape.authorization = "Basic bGlzbng6NDJERTdCRkYtQTVGMi01MzBGLUJBMDEtNUZBMjQ2MjY1OTBC"
mashape.key = "mbHoUsZf8Bmsh2feCblfQKoKTMSxp1Az0K7jsnP32ZGXcGdhDj"
thumbnailWidth=100
environments {
    development {
        pictureLocation = "/tmp/"
		promoImageLocation='/tmp/'
    }
}

environments {
	development {
		apns {
			//	pathToCertificate = "/opt/certificates/Certificates_Distribution_Lisnx.p12"
	        //pathToCertificate = "C:/SomeStuff/LISNx/LisnxRepo/samyaa_api/Certificates_Distribution_Lisnx.p12"
			pathToCertificate = "/opt/certificates/iOSDistributionCertificate.p12"
	        password = "COMMENTED_OUT"

			environment = "production"
		}
	}
	test {
		apns {
		//	pathToCertificate = "/opt/certificates/Certificates_Distribution_Lisnx.p12"
                        pathToCertificate = "/opt/certificates/Certificates_Dev_Lisnx_2014.p12"
                        password = "COMMENTED_OUT"
			environment = "sandbox"
		}
	}

	production {
		apns {
		//	pathToCertificate = "/opt/certificates/Certificates_Distribution_Lisnx.p12"
                        pathToCertificate = "/opt/certificates/iOSDistributionCertificate.p12"
                        password = "COMMENTED_OUT"
			environment = "production"
        //                environment = "sandbox"
		}
	}
}

environments {
    development {
		linkedin {
			api {
			 key = "COMMENTED_OUTLAID"
			 secret = "COMMENTED_OUTLASECRET"
			 url = "https://api.linkedin.com/uas/oauth/accessToken"
			}
		   }

		facebook {
			api {
			 key = "844486615570890"
			}
		}
   }
	production {
		linkedin {
			api {
				key = "COMMENTED_OUTLAID"
				secret = "COMMENTED_OUTLASECRET"
				url = "https://api.linkedin.com/uas/oauth/accessToken"
			}
		}

		facebook {
			api {
				key = "164116363623152"
			}
		}
	}
}
