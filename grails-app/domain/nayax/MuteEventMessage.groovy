package nayax

class MuteEventMessage extends MuteMessage{

    ExternalEvent externalEvent
    
    static constraints = {
        externalEvent(nullable: false)
    }
}
