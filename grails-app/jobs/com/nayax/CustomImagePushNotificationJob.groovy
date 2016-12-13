
package com.nayax

import nayax.Facebook;
import nayax.FacebookAccessToken;
import nayax.FacebookConnection;
import nayax.LISNxPushNotification;
import nayax.MobileAuthToken;
import nayax.NayaxUser;
import nayax.PrivateMessage;

import org.springframework.beans.factory.InitializingBean;

import com.lisn.api.ApiService;

import grails.plugin.asyncmail.AsynchronousMailService;
import groovy.json.JsonSlurper;
import groovy.lang.GroovyInterceptable;

class CustomImagePushNotificationJob {
	
	def concurrent = false
	def requestsRecovery = true
	def durability = true
	//def cronExpression = "30 * * * * ? " // FIRE THE EMAIL AT 10.15 AM EVERYDAY TO THOSE PEOPLE WHO HAVE NOT RECEIEVED WELCOME EMAIL

	def userService
	def mailService
	AsynchronousMailService asynchronousMailService
	def grailsApplication
	def facebookService
	ApiService apiService
	
	static triggers = {
		cron name: 'pushTrigger', cronExpression: "40 * * * * ?"
	  }
	 def group = "PushGroup"
	 def description = "Push notificitions Trigger"
	
	

	def execute() {
		NayaxUser sender = NayaxUser.findByFullName('LISNx Team')
		def token = MobileAuthToken.findByNayaxUser(sender)?.token
		if(token){
			def pushNotifications = LISNxPushNotification.createCriteria().list{
				// Get all tokens that are not processed yet and the validity is true
				and{
					isNull("dateProcessed")
					or{
						isNotNull('content')
						isNotNull('picture')
					}
					eq('isActive', true)
				}
			}
			if(pushNotifications?.size()==0){
				log.info('no notifications...')
			}
			pushNotifications.each{pushNotification ->
				def content = 	pushNotification.content
				def picture =	pushNotification.picture
				def receivers = []
				if(pushNotification.targetAll){
					receivers = NayaxUser.all
				}else{
					NayaxUser receiver = NayaxUser.get(1)
					receivers.add(receiver)
				}
				receivers.each{receiver ->
					apiService.sendPrivateMessageV2(null, token, receiver.id, content, picture)
				}
				pushNotification.dateProcessed = new Date()
				apiService.saveObject(pushNotification)
			}
		}
		
	}

}
