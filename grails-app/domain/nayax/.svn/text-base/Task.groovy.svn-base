package nayax

import nayax.NayaxUser;
import nayax.NayaxEvent;

import java.util.Date;

class Task {

    String title
    String notes
    NayaxUser assignedTo
    Date dueDate
    NayaxEvent event
	Boolean completed
    
    static constraints = {
        title(blank:false)
        notes(blank:true, nullable:true, maxSize:5000)
        assignedTo(nullable:true)
        dueDate(nullable:true)
		completed(nullable:true)
    }
    
    static belongsTo = nayax.NayaxEvent
	
	
		
		
		
		String toString(){
			title
		}
	
}
