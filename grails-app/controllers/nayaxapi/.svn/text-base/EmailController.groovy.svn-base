package nayaxapi

import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import grails.converters.JSON
import nayax.MobileAuthToken;
import nayax.NayaxUser;
import nayax.Picture;

class EmailController {
	def mailService
	def apiService
	def imageService
	def grailsApplication
	def facebookService

   
	
	def welcome() {
		//sendEmail(NayaxUser.get(1), "Welcome to LISNx!", "email")
		render (view: "email", model:[firstName:"Sri"]) 
	}
	def simple() {
		//sendEmail(NayaxUser.get(1), "Welcome to LISNx!", "simpleEmail")
		render (view: "simpleEmail", model:[firstName:"Sri"])
	}
	def happyNewYear(){
		//sendEmail(NayaxUser.get(1), "Welcome to LISNx!", "happyNewYearEmail")
		render (view: "happyNewYearEmail", model:[firstName:"Sri"])
	}
	
	def happyValentine(){
		if(params.id && params.id!="ALL"){
			NayaxUser user = NayaxUser.get(params.id)
			sendEmail(user, "Happy Valentines!", "HappyValentines")
			render (view: "HappyValentines", model:[firstName:user.fullName])
		}else if(params.id=="ALL" && params.max && params.min){
			
			def max = 0
			def min =1000
			if(params.max)
			 	max = params.long('max')
			if(params.min)
				 min = params.long('min')
			def allUsers = NayaxUser.createCriteria().list{
				between ("id",min, max)
			}
			allUsers.each{user ->
					sendEmail(user, "Happy Valentines!", "HappyValentines")
					log.info("Sending email to "+ user.fullName + " as "+user.fullName.split(" ")[0])
					render (view: "HappyValentines", model:[firstName:user.fullName])
					
			}
		}
	}
	def sendUpdate2_6(){
		def result = [:]
		result.status = 'success'
		result.message = [:]
		NayaxUser sender = NayaxUser.get(133)
		def content = 'Get the latest app update and check out the new features!'
		def token = "593b13cb-40ad-45d7-8c7c-fac70f791484";
		
		if(params.id && params.id!="ALL"){
			NayaxUser user = NayaxUser.get(params.id)
			def userFirstName = user.fullName.split(" ")[0]
			sendEmail(user, userFirstName+ ", get the latest LISNx app now!", "update2_6")
			//apiService.sendPrivateMessageV2("", token, user.id, content, null)
			//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
			result.message[user.id]=[id:user.id, email:user.username, name:user.fullName]
			render (view: "update2_6", model:[firstName:user.fullName])
		}else if(params.id=="ALL" && params.max && params.min){
			
			def max = 0
			def min =1000
			if(params.max)
				 max = params.long('max')
			if(params.min)
				 min = params.long('min')
			def allUsers = NayaxUser.createCriteria().list{
				between ("id",min, max)
			}
			allUsers.each{user ->
					def userFirstName = user.fullName.split(" ")[0]
					sendEmail(user, userFirstName+ ", get the latest LISNx app!", "update2_6")
					log.info("Sending email to "+ user.fullName + " as "+user.fullName.split(" ")[0])
					result.message[user.id]=[id:user.id, email:user.username, name:user.fullName]
			}
			render result as JSON
		}
		
	}
	
	def sendUpdate2_6Push(){
		def result = [:]
		result.status = 'success'
		result.message = [:]
		NayaxUser sender = NayaxUser.get(133)
		def content = 'Get the latest app update and check out the new features!'
		def token = "593b13cb-40ad-45d7-8c7c-fac70f791484";
		
		if(params.id && params.id!="ALL"){
			NayaxUser user = NayaxUser.get(params.id)
			def userFirstName = user.fullName.split(" ")[0]
			apiService.sendPrivateMessageV2("", token, user.id, content, null)
			//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
			result.message[user.id]=[id:user.id, name:user.fullName]
			render (view: "update2_6", model:[firstName:user.fullName])
		}else if(params.id=="ALL" && params.max && params.min){
			
			def max = 0
			def min =1000
			if(params.max)
				 max = params.long('max')
			if(params.min)
				 min = params.long('min')
			def allUsers = NayaxUser.createCriteria().list{
				between ("id",min, max)
			}
			allUsers.each{user ->
					def userFirstName = user.fullName.split(" ")[0]
					apiService.sendPrivateMessageV2("", token, user.id, content, null)
					log.info("Sending email to "+ user.fullName + " as "+user.fullName.split(" ")[0])
					result.message[user.id]=[id:user.id, name:user.fullName]
			}
			render result as JSON
		}
		
	}
	def sendJulyFourthPush(){
		def result = [:]
		result.status = 'success'
		result.message = [:]
		def bgImageName = "July4_bg.png"
		def imageBaseUrl = "http://www.lisnx.com/mobile/images/july4th/"
		
		NayaxUser sender = NayaxUser.findByFullName('LISNx Team')
		def token = MobileAuthToken.findByNayaxUser(sender)?.token
		def content = 'Happy fourth of July! - Meeting new and old friends? Connect and stay in touch with LISNx. - LISNx Team.'
		if(token){
			if(params.id && params.id!="ALL"){
				NayaxUser user = NayaxUser.get(params.id)
				if(user.facebook){
					def userFirstName = user.fullName.split(" ")[0]
					def imageServiceResponse = imageService.getCustomImage(user.facebook.fid, bgImageName, imageBaseUrl);
					
					File file = new File(imageServiceResponse.filePath);
					FileInputStream input = new FileInputStream(file);
					MultipartFile multipartFile = new MockMultipartFile("file",
							file.getName(), "image/png", IOUtils.toByteArray(input));
					
					apiService.sendPrivateMessageWithMultipartInParam("", token, user.id, content, imageServiceResponse.picUrl, multipartFile)
					//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
					result.message[user.id]=[id:user.id, name:user.fullName]
					render result as JSON
				}
			}else if(params.id=="ALL" && params.max && params.min){
				
				def max = 0
				def min =1000
				if(params.max)
					 max = params.long('max')
				if(params.min)
					 min = params.long('min')
				def allUsers = NayaxUser.createCriteria().list{
					between ("id",min, max)
				}
				allUsers.each{user ->
						if(user.facebook){
							if (facebookService.hasProfilePic(user.facebook.fid)){
								def imageServiceResponse = imageService.getCustomImage(user.facebook.fid);
								File file = new File(imageServiceResponse.filePath);
								FileInputStream input = new FileInputStream(file);
								MultipartFile multipartFile = new MockMultipartFile("file",
										file.getName(), "image/png", IOUtils.toByteArray(input));
								apiService.sendPrivateMessageWithMultipartInParam("", token, user.id, content, imageServiceResponse.picUrl, multipartFile)
								//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
								result.message[user.id]=[id:user.id, name:user.fullName]
							}else {
								apiService.sendPrivateMessageWithDefaultImage("", token, user.id, content)
								//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
								result.message[user.id]=[id:user.id, name:user.fullName]
							}
						}
				}
				render result as JSON
			}else if(params.in){
				
				def inList = []
					params.in.split(",").each{
						inList.add(it.toString().toLong())
					}
				def allUsers = NayaxUser.createCriteria().list{
						"in" ("id", inList)
					}
				allUsers.each{user ->
						if(user.facebook){
							if (params.hasFbImage){
								def imageServiceResponse = imageService.getCustomImage(user.facebook.fid);
								File file = new File(imageServiceResponse.filePath);
								FileInputStream input = new FileInputStream(file);
								MultipartFile multipartFile = new MockMultipartFile("file",
										file.getName(), "image/png", IOUtils.toByteArray(input));
								apiService.sendPrivateMessageWithMultipartInParam("", token, user.id, content, imageServiceResponse.picUrl, multipartFile)
								//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
								result.message[user.id]=[id:user.id, name:user.fullName]
							}else {
								apiService.sendPrivateMessageWithDefaultImage("", token, user.id, content)
								//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
								result.message[user.id]=[id:user.id, name:user.fullName]
							}
						}
				}
				render result as JSON
			}
		}
	}
	def sendAug15Push(){
		def result = [:]
		result.status = 'success'
		result.message = [:]
		def bgImageName = "bg3.png"
		def imageBaseUrl = "http://www.lisnx.com/mobile/images/aug15th/"
		
		NayaxUser sender = NayaxUser.findByFullName('LISNx Team')
		def token = MobileAuthToken.findByNayaxUser(sender)?.token
		def content = 'Happy independence day India! - Share your patriotism and love for mother land - LISNx Team.'
		if(token){
			if(params.id && params.id!="ALL"){
				NayaxUser user = NayaxUser.get(params.id)
				if(user.facebook){
					def userFirstName = user.fullName.split(" ")[0]
					def imageServiceResponse = imageService.getCustomSquareImage(user.facebook.fid, bgImageName, imageBaseUrl);
					
					File file = new File(imageServiceResponse.filePath);
					FileInputStream input = new FileInputStream(file);
					MultipartFile multipartFile = new MockMultipartFile("file",
							file.getName(), "image/png", IOUtils.toByteArray(input));
					
					apiService.sendPrivateMessageWithMultipartInParam("", token, user.id, content, imageServiceResponse.picUrl, multipartFile)
					//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
					result.message[user.id]=[id:user.id, name:user.fullName]
					render result as JSON
				}
			}else if(params.id=="ALL" && params.max && params.min){
				
				def max = 0
				def min =1000
				if(params.max)
					 max = params.long('max')
				if(params.min)
					 min = params.long('min')
				def allUsers = NayaxUser.createCriteria().list{
					between ("id",min, max)
				}
				allUsers.each{user ->
						if(user.facebook){
							if (facebookService.hasProfilePic(user.facebook.fid)){
								def imageServiceResponse = imageService.getCustomSquareImage(user.facebook.fid, bgImageName, imageBaseUrl);
								File file = new File(imageServiceResponse.filePath);
								FileInputStream input = new FileInputStream(file);
								MultipartFile multipartFile = new MockMultipartFile("file",
										file.getName(), "image/png", IOUtils.toByteArray(input));
								apiService.sendPrivateMessageWithMultipartInParam("", token, user.id, content, imageServiceResponse.picUrl, multipartFile)
								//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
								result.message[user.id]=[id:user.id, name:user.fullName]
							}else {
								apiService.sendPrivateMessageWithDefaultImage("", token, user.id, content)
								//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
								result.message[user.id]=[id:user.id, name:user.fullName]
							}
						}
				}
				render result as JSON
			}else if(params.in){
				
				def inList = []
					params.in.split(",").each{
						inList.add(it.toString().toLong())
					}
				def allUsers = NayaxUser.createCriteria().list{
						"in" ("id", inList)
					}
				allUsers.each{user ->
						if(user.facebook){
							if (params.hasFbImage){
								def imageServiceResponse = imageService.getCustomSquareImage(user.facebook.fid, bgImageName, imageBaseUrl);
								File file = new File(imageServiceResponse.filePath);
								FileInputStream input = new FileInputStream(file);
								MultipartFile multipartFile = new MockMultipartFile("file",
										file.getName(), "image/png", IOUtils.toByteArray(input));
								apiService.sendPrivateMessageWithMultipartInParam("", token, user.id, content, imageServiceResponse.picUrl, multipartFile)
								//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
								result.message[user.id]=[id:user.id, name:user.fullName]
							}else {
								apiService.sendPrivateMessageWithDefaultImage("", token, user.id, content)
								//apiService.sendMessageToDevice(sender, user, "DIRECT_MESSAGE", result, "${sender.fullName}: ${content}")
								result.message[user.id]=[id:user.id, name:user.fullName]
							}
						}
				}
				render result as JSON
			}
		}
	}
	
	def updateEmail() {
		//sendEmail(NayaxUser.get(1), "LISNx update!", "updateEmail")
		render (view: "updateEmail", model:[firstName:"Srinivas"])
	}
	def promoEmail() {
		sendEmail(NayaxUser.get(1), "1. Facebook, 2. LinkedIn, 3. Find out what you are on LISNx!", "promoEmail")
		render (view: "promoEmail", model:[firstName:"Srinivas"])
	}
	def inviteMore() {
		render (view:'inviteMore')
	}
	def sendEmail(def user, def emailSubject, def emailView){ 
		def mailSendingStatus
		try {
				mailService.sendMail {
					//to "${username}"
					to user.username
					//bcc "srinivas@lisnx.com"
					//to "srinivas@lisnx.com"
					subject emailSubject
					body(view: emailView, model: [firstName: user.fullName.split()[0]])
					from "LISNx Team<team@lisnx.com>"
				}
				mailSendingStatus = "success"
			} catch (Exception e) {
				log.error(e);
				mailSendingStatus = "fail"
			}
	}

}
