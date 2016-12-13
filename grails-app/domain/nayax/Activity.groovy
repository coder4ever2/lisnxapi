package nayax

class Activity {
	
	String name
	/*static searchable = true*/
	
	static hasMany = [ activityMaps:UserActivityMap]

    static constraints = {
		name(nullable:false)
    }
}
