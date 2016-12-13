import java.net.URL;

import groovy.json.JsonSlurper;
import groovy.lang.GroovyInterceptable;

import nayax.FacebookPalsCache
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.request.RequestContextHolder
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONElement



class LinkedinService implements InitializingBean, GroovyInterceptable {
	/*
	 * http://api.linkedin.com/v1/people/~/connections
       http://api.linkedin.com/v1/people/id=12345/connections

http://api.linkedin.com/v1/people/~/connections:(headline,first-name,last-name)
http://api.linkedin.com/v1/people/url=http%3A%2F%2Fwww.linkedin.com%2Fin%2Flbeebe/connections


	 */


	def static transactional = false
	def facebookGraphService
	def grailsApplication
	def setting
	def facebookPalsCache
	def static String linkedinURL = "https://api.linkedin.com/v1/people/~:(%s)?format=json"
	def static String linkedinMemberURL = "https://api.linkedin.com/v1/people/~:(first_name,last_name,positions,public-profile-url)?format=json"
	
	
	void afterPropertiesSet() {
		this.setting = grailsApplication.config.setting
	}
	
	def getUserFriends(def userToken, def tokenSecret){
		
		String apiKey =grailsApplication.config.linkedin.applicationId;
		String apiSecret =grailsApplication.config.linkedin.applicationId; 
		userToken = "93fba0b5-5f67-4f30-9c2a-f8c2b7a3f2e4";
		tokenSecret = "e74e7e7d-b1ba-49f5-97df-160ca1847507";
		LinkedInTest linkedInTest = new LinkedInTest();
		 //linkedInTest.getConnections();
		def response = linkedInTest.getConnections(apiKey, apiSecret, userToken, tokenSecret);
		def results = new JsonSlurper().parseText(response);
		results.values.each{ member ->
			log.info(member.apiStandardProfileRequest?.url)
			log.info(member.firstName)
			log.info(member.lastName)
			log.info(member.pictureUrl)
			log.info(member.id)
			log.info(member.headline)
			log.info(member.industry)
			log.info(member.location?.name)
		}
		
		log.info("response is "+ response)
		return response
	}
	
	def getUserCardInfo(def linkedInAccessToken){
		def response =  getResponseFromUrl(new URL(linkedinMemberURL+'&oauth2_access_token='+linkedInAccessToken), linkedInAccessToken)
		log.info('AccessToken:'+linkedInAccessToken)
		def linkedinUserCardInfo = new JsonSlurper().parseText(response);
		return linkedinUserCardInfo
	}

	def getLinkedInUserDetails(def accessToken){
		def fields="id,formatted-name,num-connections,email-address,picture-url,picture-urls::(original),public-profile-url"
		def linkedInUrl = String.format(linkedinURL, fields) + "&oauth2_access_token=${accessToken}"
		log.info("User's linkedIn details url initial login "+ linkedInUrl)
		return getResponseFromUrl(new URL(linkedInUrl), accessToken)
	}
	
	String getResponseFromUrl(URL url, def authToken) {
		String response = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//conn.setRequestProperty("Authorization", "Bearer " + authToken);
		try {
			int respCode = conn.responseCode
			if (respCode == 400) {
				log.error("COULD NOT MAKE CONNECTION")
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				def jsonResp = JSON.parse(br.text)
			} else {
				response = conn.getInputStream().getText()
			}
		} finally {
			conn.disconnect()
		}
		log.info("RETURNING RESPONSE")
		return response;
	}

}
