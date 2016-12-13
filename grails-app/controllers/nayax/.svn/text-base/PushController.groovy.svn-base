package nayax

import org.apache.jasper.compiler.Node.ParamsAction;

import grails.converters.JSON;

class PushController {
	
	def apiService

    def index() { 
		def result = [:]
		
		NayaxUser sender = NayaxUser.findByFullName('LISNx Team')
		def token = MobileAuthToken.findByNayaxUser(sender)?.token
		if(token){
			def content = 	params.content
			
			def picture 
			if(params.picId){
				picture=	Picture.get(params.picId)
			}
			def receivers = []
			if(params.targetAll){
				receivers = NayaxUser.all
			}else{
				NayaxUser receiver = NayaxUser.get(1)
				receivers.add(receiver)
			}
			receivers.each{receiver ->
				apiService.sendPrivateMessageV2(null, token, receiver.id, content, picture)
			}
			
		}
		result['status'] ='success'
		render result as JSON
	}
	
	
}
