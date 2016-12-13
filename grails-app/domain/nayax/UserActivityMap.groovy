package nayax

class UserActivityMap {
    NayaxUser nayaxUser
    Activity activity
    Date activityTime
    double latitude
    double longitude

    static belongsTo = [nayaxUser: NayaxUser]

    /*static searchable = {
    nayaxUser component: true
    activity component: true
    only: ['activityTime', 'latitude', 'longitude']
    }*/

    static constraints = {
        nayaxUser(nullable: false)
        activity(nullable: true)
        activityTime(nullable: false)
        latitude(nullable: true)
        longitude(nullable: true)
    }
}
