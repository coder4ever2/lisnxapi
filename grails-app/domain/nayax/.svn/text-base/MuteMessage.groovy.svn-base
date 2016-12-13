package nayax

class MuteMessage {

    NayaxUser user
    Date startDate
    Date endDate
    Integer duration
    Boolean showNotification
    Boolean isCancelled
    DurationType durationType
    Date dateCreated
    Date lastUpdated
    
    enum DurationType {
        HOUR, DAY, WEEK, MONTH
    }
    
    static mapping = {
        showNotification defaultValue: true
        isCancelled defaultValue: false
        durationType enumType: "string"
        tablePerHierarchy false
        autoTimestamp true
    }
    
    static constraints = {
        user(nullable: false)
        showNotification(nullable: false)
    }
}
