package nayax

import nayax.LISNMessage.MessageType;

class PrivateMessage {

    def apiService
    NayaxUser receiver
    NayaxUser sender
    Date dateCreated
    Date lastUpdated
    String content
    String isViewed="false"
    Picture picture
    MessageType messageType = MessageType.MESSAGE
    
    def updateTrigger
    
    enum MessageType {
	MESSAGE, CONTACT_INFO
    }
    
    static constraints = {
        content(nullable:  true, blank: true)
        picture(nullable:true, blank:true)
        messageType(nullable:true)
        updateTrigger(nullable:true)
    }

    static mapping = {
        content(type: 'text')
    }
    
    def likeMessage(def user){
        if(!DirectMessageLike.findWhere(privateMessage:this, user:user) ){
            DirectMessageLike messageLike = new DirectMessageLike(user:user, privateMessage:this)
            apiService.saveObject(messageLike)
        }else if(DirectMessageLike.findWhere(lisnMessage:lisnMessage, user:user, unliked:true)){
            DirectMessageLike messageLike = DirectMessageLike.findWhere(privateMessage:this, user:user, unliked:true)
            messageLike.setUnliked(false)
            apiService.saveObject(messageLike)
        }
		
    }
    
    def getLikes(){
        return DirectMessageLike.findAllWhere(privateMessage: this, unliked:false)
    }
    
    def touch() {
        if (!updateTrigger) {
            updateTrigger = 0
        }
        updateTrigger++
        this
    }
}
