package com.lisn.api

import static grails.async.Promises.*
import grails.async.Promise
import grails.converters.JSON
import grails.transaction.Transactional

import java.util.regex.Matcher
import java.util.regex.Pattern

import nayax.Facebook;
import nayax.Linkedin
import nayax.LinkedinAccessToken
import nayax.LinkedinConnection
import nayax.NayaxUser

import org.apache.commons.lang.RandomStringUtils;
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.LinkedInApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Token
import org.scribe.model.Verb
import org.scribe.oauth.OAuthService

@Transactional
class AdminService {
	
	def grailsApplication
	def springSecurityService
	OAuthService service
	
	def getOAuth1AccessToken(def jsAccessToken, def memberId, def fid) {
		
		String AuthTokenFromJS = jsAccessToken
		
		service = new ServiceBuilder()
								.provider(LinkedInApi.class)
								.apiKey(grailsApplication.config.linkedin.api.key)
								.apiSecret(grailsApplication.config.linkedin.api.secret)
								.build()
		try{
			AuthTokenFromJS = URLEncoder.encode(AuthTokenFromJS, "UTF-8")
		}catch (Exception e){
			log.info "YOU MAY BE IN TROUBLE BUT IT STILL MIGHT WORK"
		}

		//since we are just making a two-legged call we don't need a member token to sign the token exchange request
		//because we are only identifying an app and not the member
		Token token = new Token("","")
		OAuthRequest request = new OAuthRequest(Verb.POST, "${grailsApplication.config.linkedin.api.url}?xoauth_oauth2_access_token=${AuthTokenFromJS}")
		service.signRequest(token, request)
		Response response = request.send()
		String responseBody = response.getBody()
		
		log.info "This should be our new OAuth token in here: ${responseBody}"
		/*
		 Some code you write to parse our the new token and secret
		 */
		
		String tokenRegex = "oauth_token=(.*?)&oauth_token_secret"
		String secretRegex = "oauth_token_secret=(.*?)&oauth_expires_in"
		
		Pattern p = Pattern.compile(tokenRegex)
		Matcher m = p.matcher(responseBody)
		String oauth1Token
		if (m.find()) {
			oauth1Token = m.group(1)
		}
		
		p = Pattern.compile(secretRegex)
		m = p.matcher(responseBody)
		String oauth1Screct
		if (m.find()) {
			oauth1Screct = m.group(1)
		}
		
		String oAuthJSONString = "{access_token: ${oauth1Token}, access_token_secret: ${oauth1Screct}}"
		loginWithOauthLinkedIn(oAuthJSONString, memberId, fid)
		
		return oAuthJSONString;
	}
	
	def loginWithOauthLinkedIn(def line, String memberId, def fid) {
		if (service == null) {
			service = new ServiceBuilder()
								.provider(LinkedInApi.class)
								.apiKey(grailsApplication.config.linkedin.api.key)
								.apiSecret(grailsApplication.config.linkedin.api.secret)
								.build()
		}
		line = JSON.parse(line);
		
		log.info "oAuth1.0 Access token: ${line?.access_token}"
		
		Token oauth1_0aToken = new Token(line?.access_token, line?.access_token_secret)
		Linkedin linkedIn
		
		String url = "http://api.linkedin.com/v1/people/id=${memberId}:(first-name,last-name,public-profile-url,email-address)?format=json"
		OAuthRequest request = new OAuthRequest(Verb.GET, url)
		service.signRequest(oauth1_0aToken, request)
		Response response = request.send()

		def li = JSON.parse(response.getBody())
			
	//	Promise saveAccessTokenPromise = LinkedinAccessToken.async.task {
	//		withTransaction {
				def lats = null
				try {
					lats = LinkedinAccessToken.findByAccessToken(line?.access_token)
				} catch(Exception e) {
					log.info "*************"+e.getMessage()
				}
				if (lats == null) {
					LinkedinAccessToken lat = new LinkedinAccessToken()
					lat.accessToken = line.access_token
					lat.accessTokenSec = line.access_token_secret != null ? line.access_token_secret : ""
					lat.permissions = ""
					
					if (li != null) {
						NayaxUser user = NayaxUser.findByUsername(li.emailAddress)
						Facebook facebook = Facebook.findByFid(fid);
						if (facebook != null) {
							user = facebook.user
						}
						
						if (user == null) {
							user = new NayaxUser(username:li.emailAddress, fullName:li.firstName, enabled: true, emailSent: false,
								password: springSecurityService.encodePassword(RandomStringUtils.random(6)))
							if(!user.save(flush:true)) {
								user.errors.allErrors.each {log.error it }
								user.discard()
							}
						} 
						log.info "LinkedIn e-mail id: ${li.emailAddress}"
						if (li.emailAddress != null) {
							linkedIn = Linkedin.findByEmail(li.emailAddress)
						}
						if (linkedIn == null) {
							linkedIn = new Linkedin (firstName:li.firstName, lastName:li.lastName, loginProviderUID:memberId,
								profileURL:li.publicProfileUrl, lastLogin:new Date(), email:li.emailAddress, user:user, 
								linkedinAccessToken:lat)
							
						} else {
							linkedIn.user = user
						}
						if (!linkedIn.save(flush:true)) {
								linkedIn.errors.allErrors.each { log.error it }
								linkedIn.discard()
						}
					}
				}
	//		}
		//}
		
		
		//def getConnectionsAndSavePromise = task {
			Linkedin linkedInObj
			if (li.emailAddress != null) {
				linkedInObj = Linkedin.findByEmail(li.emailAddress)
				log.info "User: ${linkedInObj.user}"
			}
			
			String connectionUrl
			
			if (linkedInObj != null && linkedInObj.lastConnsAPICalledDate != null) {
				long dateInMilliSec = linkedInObj.lastConnsAPICalledDate.getTime()
				connectionUrl = "http://api.linkedin.com/v1/people/~/connections?modified=updated&modified-since=${dateInMilliSec}&format=json"
			} else {
				connectionUrl = "http://api.linkedin.com/v1/people/~/connections?format=json"
			}
			
			log.info "LinkedIn get connections url: ${connectionUrl}"
			OAuthRequest connRequest = new OAuthRequest(Verb.GET, connectionUrl)
			service.signRequest(oauth1_0aToken, connRequest)
			Response connResponse = connRequest.send()

			def connections = JSON.parse(connResponse.getBody())
			//LinkedinConnection.executeUpdate("delete from LinkedinConnection lc where lc.userLinkedinId = :id", [id: memberId]);
			
			connections.values.each() { con ->
				LinkedinConnection.withNewSession {
					LinkedinConnection linkedinConnection = LinkedinConnection.findWhere(connectionLinkedinId: con.id, userLinkedinId: memberId)
					
					if (linkedinConnection == null) {
						//log.info "New LinkedIn connection"
						linkedinConnection = new LinkedinConnection(connectionLinkedinId: con.id,
							firstName: con.firstName,
							lastName: con.lastName,
							pictureUrl: con.pictureUrl,
							headline: con.headline,
							industry: con.industry,
							locationName: con.location?.name,
							url: con.siteStandardProfileRequest?.url,
							userLinkedinId: memberId
							)
					} else {
						//log.info "Update LinkedIn connection"
						linkedinConnection.firstName = con.firstName
						linkedinConnection.lastName = con.lastName
						linkedinConnection.pictureUrl = con.pictureUrl
						linkedinConnection.headline = con.headline
						linkedinConnection.industry = con.industry
						linkedinConnection.locationName = con.location?.name
						linkedinConnection.url = con.siteStandardProfileRequest?.url
					}
				   if (!linkedinConnection.save(flush:true)) {
					   linkedinConnection.errors.allErrors.each { log.error it }
					   linkedinConnection.discard()
				   }
				}
			}
			Linkedin.withTransaction {
				linkedInObj.lastConnsAPICalledDate = new Date()
				if(!linkedInObj.save(flush:true)){
					linkedInObj.errors.allErrors.each { log.error it }
					linkedInObj.discard()
				}
			}
		//}
		
		/*waitAll(saveAccessTokenPromise, getConnectionsAndSavePromise)
		
		onComplete([saveAccessTokenPromise, getConnectionsAndSavePromise]) {
			List results ->
			assert [true, true] == results
			
		}
		
		onError([saveAccessTokenPromise, getConnectionsAndSavePromise]) {
			Throwable t ->
			log.error "An error occurred during async promise processing ${t.message}"
		}*/
	}
}
