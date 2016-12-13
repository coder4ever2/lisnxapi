package nayax

import nayax.CO.NayaxUserActivityCommand

class NayaxUserActivityDescription {

//    def userService
    String name
    String duration
    String latitude
    String longitude
    Date dateCreated
    Date lastUpdated
    Date expiry

    static constraints = {
        duration(nullable: true)
        latitude(nullable: true)
        longitude(nullable: true)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }

//    static transients = ["duration"]

    static belongsTo = [nayaxUser: NayaxUser]

    static hasMany = [attendies: ActivityAttendence]

    NayaxUserActivityDescription() {

    }



    NayaxUserActivityDescription(NayaxUserActivityCommand nayaxUserActivityCommand) {
        name = nayaxUserActivityCommand.name
        duration = nayaxUserActivityCommand?.duration
//        nayaxUser = userService.getUser()

        //todo: store coordinates
    }
}
