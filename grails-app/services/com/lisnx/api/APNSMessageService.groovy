package com.lisnx.api

import groovy.lang.GroovyInterceptable;
import groovy.lang.MetaClass;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;

import nayax.apple.Device;

import com.notnoop.apns.*

class APNSMessageService implements InitializingBean, GroovyInterceptable {

	
	def static transactional = false
	def grailsApplication
    def setting
    ApnsService apnsService

	def sendMessageToDevices(List<Device> recipients, String messageKey, String... arguments) {
		recipients.each {Device device ->
			def payload = APNS.newPayload()
				.badge(device.messages.size())
				.localizedKey(messageKey)
				.localizedArguments(arguments)
				.sound("default")

			if (payload.isTooLong()) log.info("Message is too long: " + payload.length())
			try {
				EnhancedApnsNotification notification = new EnhancedApnsNotification(EnhancedApnsNotification.INCREMENT_ID() /* Next ID */,
					new Date().getTime() + 60 * 60 /* Expire in one hour */,
					device.token /* Device Token */,
					payload);
				
				apnsService.push(notification)
			} catch (Exception e) {
				log.error("Could not connect to APNs to send the notification")
			}
		}
	}
	@PostConstruct
	public void init(){
		log.info("APNS post CONSTRUCT");
	}
	
	@Override
	public MetaClass getMetaClass() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object getProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object invokeMethod(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMetaClass(MetaClass arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setProperty(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	 void afterPropertiesSet() {
        this.setting = grailsApplication.config.setting
    }

}
