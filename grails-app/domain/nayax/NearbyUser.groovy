package nayax

import java.util.Date;

import nayax.ExternalEvent;
import nayax.NayaxUser;

class NearbyUser {
	
	NayaxUser user
	NayaxUser nearbyUser
	Date dateCreated
	Date lastUpdated
	LocationCoordinate userLocationCoordinate
	LocationCoordinate nearbyUserLocationCoordinate
	String distance
	
	static constraints = {
		user(nullable:false)
		nearbyUser(nullable:false)
		userLocationCoordinate(nullable: false)
		nearbyUserLocationCoordinate(nullable: false)
		dateCreated(nullable: true)
		lastUpdated(nullable: true)
		distance(nullable:false)
	}
}