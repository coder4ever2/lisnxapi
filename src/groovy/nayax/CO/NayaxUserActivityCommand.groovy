package nayax.CO

import nayax.NayaxUser

class NayaxUserActivityCommand {

    String name
    String duration
    String latitude
    String longitude
    Date expiry

    static constraints = {
        name(nullable: true)
        duration(nullable: true)
        latitude(nullable: true)
        longitude(nullable: true)
        expiry(nullable: true)
    }

    Date calculateExpiry(def duration) {
        Date expiry
        def splitValue = duration.split(" ")
        def numberValue
        switch (splitValue[1]) {
            case "Min":
                use(TimeCategory) {
                    numberValue = splitValue[0] as Integer
                    expiry = new Date() + numberValue.minutes
                }
                break;


            case "Hours":
                use(TimeCategory) {
                    numberValue = splitValue[0] as Integer
                    expiry = new Date() + numberValue.hours
                }
                break;

            case "Days":
                use(TimeCategory) {
                    numberValue = splitValue[0] as Integer
                    expiry = new Date() + numberValue.days
                }
                break;
            default: println "## could not convert ##"
        }
        return expiry
    }
}
