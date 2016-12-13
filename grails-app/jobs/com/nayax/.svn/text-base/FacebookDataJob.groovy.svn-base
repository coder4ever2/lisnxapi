package com.nayax

import nayax.Facebook;
import nayax.FacebookAccessToken;
import nayax.FacebookConnection;
import nayax.PrivateMessage;

import org.springframework.beans.factory.InitializingBean;

import grails.plugin.asyncmail.AsynchronousMailService;
import groovy.json.JsonSlurper;
import groovy.lang.GroovyInterceptable;

class FacebookDataJob {
	
	def concurrent = false
	def requestsRecovery = true
	def durability = true
	//def cronExpression = "30 * * * * ? " // FIRE THE EMAIL AT 10.15 AM EVERYDAY TO THOSE PEOPLE WHO HAVE NOT RECEIEVED WELCOME EMAIL

	def userService
	def mailService
	AsynchronousMailService asynchronousMailService
	def grailsApplication
	def facebookService
	def apiService
	
	static triggers = {
		cron name: 'myTrigger', cronExpression: "30 * * * * ?"
	  }
	 def group = "MyGroup"
	 def description = "Example job with Cron Trigger"
	
	
	
	
	

	def execute() {
		
		def facebookAccessTokens = FacebookAccessToken.createCriteria().list{
			// Get all tokens that are not processed yet and the validity is true
			and{
				isNull("dateProcessed")
				or{
					isNull("invalid")
					eq("invalid", false)
				}
			}
		}
		facebookAccessTokens.each{facebookAccessToken ->
			def sameFbAccessTokens = FacebookAccessToken.findByAccessToken(facebookAccessToken.accessToken);
			/*if(sameFbAccessTokens.size()>1){
				def lastProcessedDate = sameFbAccessTokens.sort {it.dateProcessed}.last().dateProcessed
				def now = new Date()
				if(now - 30.days < lastProcessedDate){
					log.info("This facebook access token has been processed recently")
					return
				}
			}*/
			def facebookConnectionsAdded = []
			def response = facebookService.getUserFriends(facebookAccessToken?.accessToken, facebookAccessToken?.facebook?.fid)
			log.info("REsonponse" + response)
			if(response){
				def results = new JsonSlurper().parseText(response);
				log.info("results-data"+results.data)
				def connectionsProcessed = 0
				log.info("JSON: " + results)
				results.data.each{ member ->
					
					FacebookConnection facebookConnection = new FacebookConnection
					(
					userFacebookId: facebookAccessToken.facebook?.fid==null?'unknown':facebookAccessToken.facebook?.fid,
					//url: member.apiStandardProfileRequest?.url,
					firstName: member.first_name,
					lastName: member.last_name,
					pictureUrl: member.picture.data.url,
					pictureIsSilhouette: member.picture.data.is_silhouette,
					connectionFacebookId: member.id,
					appInstalled: member.installed==null?false:member.installed.equals("true")?true:false,
					)
					log.info("MEMBER"+member)
					
					if (facebookConnection.validate()) {
						facebookConnection.save(flush: true, failOnError: true)
						facebookConnectionsAdded.add(facebookConnection)
						connectionsProcessed++
					} else {
						log.error "Error in FacebookDataJob while saving ${facebookConnection}"
						facebookConnection.errors.allErrors.each { log.error it }
					}
					
					
				}
				if(connectionsProcessed == results.data.size()) {
					facebookAccessToken.dateProcessed = new Date()
					facebookAccessToken.save(flush:true, failOnError:true)
				}else {
					log.error("Connections received: "+ results.data.size())
					log.error("Processed: " + connectionsProcessed)
				}
				Facebook facebook = facebookAccessToken.facebook.refresh();
				apiService.updateFacebookTotalCount(facebook, results.summary.total_count)
				def result = [:]
				
				if(facebookConnectionsAdded.size()>0){
					
					def firstFacebookConnection = facebookConnectionsAdded.get(0)
					def facebookOfUser = Facebook.findByFid(firstFacebookConnection.userFacebookId)
					if(facebookOfUser.user){
							def thisUser = facebookOfUser.user
							facebookConnectionsAdded.each{facebookConnection ->
							def facebookOfConnection = Facebook.findByFid(facebookConnection.connectionFacebookId)
							if(facebookOfConnection){
								def lisnxAccountOfFacebookConnection = facebookOfConnection.user
								def notificationMessage = 'Your facebook friend '+thisUser.fullName +' joined LISNx.'
								if(lisnxAccountOfFacebookConnection){
									def privateMessageInstance = new PrivateMessage(sender: thisUser, receiver: lisnxAccountOfFacebookConnection, content: ' joined LISNx!')
									privateMessageInstance.save(flush: true)
								   
									apiService.sendMessageToDevice(thisUser, 
																	lisnxAccountOfFacebookConnection, 
																	"DIRECT_MESSAGE", 
																	result, 
																	notificationMessage , "",  "")
								}
							}
						}
					}
				}
				
				
			}else {
				facebookAccessToken.invalid= true
				facebookAccessToken.save(flush:true, failOnError:true)
				
			}
			
		}
		
		
	}

}
