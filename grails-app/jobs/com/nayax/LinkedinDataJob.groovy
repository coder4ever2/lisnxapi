package com.nayax

import org.springframework.beans.factory.InitializingBean;

import groovy.json.JsonSlurper;
import groovy.lang.GroovyInterceptable;

import nayax.LinkedinAccessToken;
import nayax.Linkedin

class LinkedinDataJob {
	
	def concurrent = false
	def requestsRecovery = true
	def durability = true
        static triggers = {
            cron name: 'linkedinTrigger', cronExpression: "0 30 * * * ?"  //Trigger job for every 30 minutes
	}
	
	def grailsApplication
	def linkedinService	
	

	def execute() {
		String apiKey =grailsApplication.config.linkedin.applicationId;
		String apiSecret =grailsApplication.config.linkedin.applicationId;
		
                log.info "In execute method of LinkedInDataJob"
		def linkedinAccessTokens = LinkedinAccessToken.findAllByExpirationDateGreaterThanEquals(new Date())
		linkedinAccessTokens.each{linkedinAccessToken ->
                    
                    // Update profile picture url of all active tokens
                    log.info "Profile picture url update started for ${linkedinAccessToken?.accessToken}"
                    try {
                        def lnResponse = linkedinService.getLinkedInUserDetails(linkedinAccessToken?.accessToken)
                        def lnResultJson = new JsonSlurper().parseText(lnResponse);

                        if (lnResultJson) {
                                Linkedin linkedIn = Linkedin.findByLoginProviderUID(lnResultJson.id)

                                String profilePictureUrl = null
                                if (lnResultJson?.pictureUrls?._total > 0) {
                                    profilePictureUrl = lnResultJson.pictureUrls.values[0]
                                }

                                if(linkedIn){

                                    linkedIn.thumbnailURL = lnResultJson.pictureUrl
                                    linkedIn.profilePictureURL = profilePictureUrl
                                    
                                    linkedIn.save(flush:true, failOnError:true)
                                }
                        }
                    } catch (Exception e) {
                        log.error  "error occured while fetching/saving LinkedIn user details: ${e.getMessage()}"
                    }
                    
                    // Profile picture url update finished
            
			/*def response = linkedinService.getUserFriends(linkedinAccessToken.accessToken, linkedinAccessToken.accessTokenSec)
			def results = new JsonSlurper().parseText(response);
			def connectionsProcessed = 0
			results.values.each{ member ->
				
				LinkedinConnection linkedinConnection = new LinkedinConnection
				(
				userLinkedinId: linkedinAccessToken.linkedin?.loginProviderUID==null?'unknown':linkedinAccessToken.linkedin?.loginProviderUID,
				url: member.apiStandardProfileRequest?.url,
				firstName: member.firstName,
				lastName: member.lastName,
				pictureUrl: member.pictureUrl,
				connectionLinkedinId: member.id,
				headline: member.headline,
				industry: member.industry,
				locationName: member.location?.name,
				)
				
				if (linkedinConnection.validate()) {
					linkedinConnection.save(flush: true, failOnError: true)
					connectionsProcessed++
				} else {
					log.error "Error in LinkedinDataJob while saving ${linkedinConnection}"
					linkedinConnection.errors.allErrors.each { log.error it }
				}
				
				
			}
			if(connectionsProcessed == results.values.size()) {
				linkedinAccessToken.dateProcessed = new Date()
				linkedinAccessToken.save(flush:true, failOnError:true)
			}else {
				log.error("Connections received: "+ results.values.size())
				log.error("Processed: " + connectionsProcessed)
			}*/
			
		}
		
		
		/*String userToken = "93fba0b5-5f67-4f30-9c2a-f8c2b7a3f2e4";
		String tokenSecret = "e74e7e7d-b1ba-49f5-97df-160ca1847507";
		def response = linkedinService.getUserFriends("", "");
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
		}*/
		
	}

}
