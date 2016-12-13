package nayax

import grails.converters.JSON;

import javax.servlet.http.Cookie;

import com.google.gson.JsonElement;

class AdminController {
	
	def adminService
	def apiService
	def grailsApplication
	
	def beforeInterceptor = [action: this.&adminBeforeInterceptor]
	
	def adminBeforeInterceptor = {
		log.warn "Admin beforeInterceptor with params : ${params}"
	}
	def afterInterceptor = { model ->
		log.trace "Tracing in afterInterceptor of Admin: ${actionUri}, ${params}"
	}

    def index() { 
		// render the view with the specified model
		log.info "In index method."
		def userList = NayaxUser.findAll()
		render(view:"list",model:[users:userList, usersCount:userList.size()])
	}
	
	def connectWithUser(def userId){
		render "done"
	}
	
	def linkedin(){
		log.info "In Linkedin method...."
		render (view:'linkedin')
		
	}
	
	def linkedinLogin(){
		log.info "In Linked in login method."
		Cookie [] cookies = request.getCookies()
		String cookieName = "linkedin_oauth_${grailsApplication.config.linkedin.api.key}"
		String jsAccessToken
		String memberId
	
		
		for (cookie in cookies) {
			if (cookieName.equals(cookie?.getName())) {
				String decodeCookieValue = URLDecoder.decode(cookie.getValue(), "UTF-8")
				def element = JSON.parse(decodeCookieValue)
				jsAccessToken = element.access_token
				memberId = element.member_id
			}
		}
		String accessToken = adminService.getOAuth1AccessToken(jsAccessToken, memberId, params.fid)
		
		def accessTokenJson = JSON.parse(accessToken)
		
		session.setAttribute("LinkedInAccessToken", accessTokenJson.access_token)
		session.setAttribute("LinkedInAccessTokenSecret", accessTokenJson.access_token_secret)
		
	}
	
	def index2(){
		/*https://www.linkedin.com/uas/oauth2/accessToken?grant_type=authorization_code
		 &code=AUTHORIZATION_CODE
		 &redirect_uri=YOUR_REDIRECT_URI
		 &client_id=YOUR_API_KEY
		 &client_secret=YOUR_SECRET_KEY*/
		 log.info "In index2 method."
		 render (view:'index2')
		
	}
	
	
}
