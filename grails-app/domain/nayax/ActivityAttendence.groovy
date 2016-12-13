package nayax

class ActivityAttendence {

    boolean emailSent = false
    NayaxUser userAttendingEvent

    static constraints = {
    }

    static belongsTo = [nayaxUserActivity: NayaxUserActivityDescription]
}
