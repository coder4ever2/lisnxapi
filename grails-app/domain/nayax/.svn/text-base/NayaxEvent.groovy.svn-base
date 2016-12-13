package nayax


import java.util.Date;
import nayax.NayaxUser;

class NayaxEvent {
	String city
    String name
    NayaxUser organizer
    String venue
    Date startDate
    Date endDate
    String description

    String toString(){
        "$name, $city"
    }
	//static hasMany = [volunteers:NayaxUser]
/*	static searchable = true*/

	 static hasMany = [volunteers:NayaxUser, 
                      respondents:String, 
                     // sponsorships:Sponsorship,
                      tasks:Task, 
                      messages:Message
					  ]
    
    static constraints = {
        name(blank:false)
        city(blank:false)
        description(maxSize : 5000)
        organizer(nullable:false)
        venue()
        startDate()
        endDate()
        volunteers(nullable : true)
       // sponsorships(nullable : true)
        tasks(nullable : true)
        messages(nullable : true)
    } 
}
