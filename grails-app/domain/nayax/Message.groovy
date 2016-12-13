package nayax

import nayax.NayaxEvent;
import nayax.NayaxUser;

class Message {

     String subject
    String content
    Message parent
    NayaxEvent event
    NayaxUser author
    
    static constraints = {
        subject(blank:false)
        content(blank:false, maxSize:2000)
        parent(nullable:true)
        author(nullable:false)
    }
    
    static belongsTo = NayaxEvent
}
