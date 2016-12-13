package nayax

import java.text.ParseException

import org.springframework.beans.factory.InitializingBean
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import nayax.Enum.ProfileShareType
import nayax.Enum.SettingType
import grails.util.Environment

class LisnService implements InitializingBean {
    static transactional = true

    def grailsApplication
    def setting
    def springSecurityService
    def userService

    void afterPropertiesSet() {
        this.setting = grailsApplication.config.setting
    }

    LocationCoordinate saveLocationCoordinate(double longitude, double latitude) {
        LocationCoordinate locationCoordinate = new LocationCoordinate(longitude: longitude, latitude: latitude)
        if (locationCoordinate.validate()) {
            locationCoordinate.save()
        } else {
            locationCoordinate.discard()
        }
        return locationCoordinate
    }

    def nearbyLisns(def session, def offset = 0) {
        def rangeValue = getRangeValue()
        def lisnList
        if (session && session['position']) {
            def position = session['position']
            LocationCoordinate coordinate = new LocationCoordinate(longitude: position?.coords?.longitude, latitude: position?.coords?.latitude)
            double latitudeMin = coordinate.latitude - rangeValue
            double latitudeMax = coordinate.latitude + rangeValue
            double longitudeMin = coordinate.longitude - rangeValue
            double longitudeMax = coordinate.longitude + rangeValue
            lisnList = LISN.createCriteria().list([max: 10, offset: offset]) {
                gt("endDate", new Date())
                and {
                    locationCoordinate {
                        between("latitude", latitudeMin, latitudeMax)
                        between("longitude", longitudeMin, longitudeMax)
                    }
                    order("startDate", "desc")
                }
            }
        }
        return lisnList
    }

    LISN saveLisn(lisn) {
        if (lisn.validate()) {
            lisn.save()
        } else {
            lisn.errors.allErrors.each { log.error it }
            lisn.discard()
        }
        return lisn
    }

    Boolean joinLisn(LISN lisn) {
        return saveLisn(lisn, userService.getUser())
    }



    Boolean joinLisnWithoutLogin(LISN lisn, NayaxUser user) {
        return saveLisn(lisn, user)
    }

    Boolean saveLisn(LISN lisn, NayaxUser user) {
        Boolean status = true
        if (!UserLISNMap.countByLisnAndUser(lisn, user)) {
            UserLISNMap userLISNMap = new UserLISNMap(user: user, lisn: lisn)
            if (userLISNMap.validate()) {
                userLISNMap.save()
            } else {
                userLISNMap.errors.allErrors.each { log.error it }
                userLISNMap.discard()
                status = false
            }
        }
        return status
    }

    Boolean joinLisnWithProfileShareType(LISN lisn, NayaxUser user, ProfileShareType profileShareType) {
        Boolean status = true
        if (!UserLISNMap.countByLisnAndUser(lisn, user)) {
            UserLISNMap userLISNMap = new UserLISNMap(user: user, lisn: lisn, profileShareType: profileShareType)
            if (userLISNMap.validate()) {
                userLISNMap.save()
            } else {
                userLISNMap.errors.allErrors.each { log.error it }
                userLISNMap.discard()
                status = false
            }
        }
        return status
    }
	Boolean isUserALisnMember(LISN lisn, NayaxUser user){
		if(lisn.creator.equals(user))
			return true
		if(!UserLISNMap.countByLisnAndUser(lisn, user))
			return false
		else return true
		
	}

    List<NayaxUser> getMembersByPriority(List<NayaxUser> lisnMembers, int max) {
        List sortedMembers = []
        NayaxUser loggedInUser = springSecurityService.getCurrentUser()
        def friends = lisnMembers.findAll {loggedInUser.isConnectedToUser(it)}
        sortedMembers = friends
        if (sortedMembers.size() < 6) {
            def strangers = lisnMembers - friends
            def peopleWithPicture = strangers.findAll {it.facebook}
            sortedMembers += peopleWithPicture
            if (sortedMembers.size() < 6) {
                sortedMembers += strangers - peopleWithPicture
            }
        }
        if (sortedMembers.size() > max) {
            sortedMembers = sortedMembers.subList(0, max)
        }
        return sortedMembers
    }


    def getPastUserLisnMap(NayaxUser nayaxUser, def offset) {
        def userLisns = UserLISNMap.createCriteria().list([max: 10, offset: offset]) {
            eq('user', nayaxUser)
            lisn {
                lt('endDate', new Date())
            }
        }
        return userLisns
    }

    def getCommonLisns(NayaxUser user, NayaxUser otherUser) {
        def commonLisns = []
        def userLisns = UserLISNMap.findAllWhere(user: user)
        def otherUserLisns = UserLISNMap.findAllWhere(user: otherUser)
        userLisns.each() {
            if (otherUserLisns*.lisn.contains(it.lisn))
                commonLisns.add(it)
        }
        return commonLisns
    }

    Boolean canVisitLisn(LISN lisn, def session) {
        boolean canVisit = false
        NayaxUser nayaxUserInstance = springSecurityService.currentUser;
        if (nayaxUserInstance.isMember(lisn)) {
            canVisit = true
        }
        if (!canVisit) {
            List<UserLISNMap> lisnMapList = UserLISNMap.findAllByLisn(lisn)
            lisnMapList.each {UserLISNMap lisnMap ->
                if (nayaxUserInstance.isConnectedToUser(lisnMap.user)) {
                    canVisit = true;
                }
            }
        }
        if (!canVisit) {
            def offset = 0;
            def lisnList = nearbyLisns(session, offset)
            lisnList.each { lisnlist ->
                if (lisnlist.id.toString().equalsIgnoreCase(lisn.id.toString())) {
                    canVisit = true;
                }
            }
        }
        return canVisit;
    }


    def getRangeValue() {
        Setting setting = Setting.findBySettingType(SettingType.LATLONGRANGE)
        return setting.value.toFloat()
    }

    def loadingMockData() {

        def aUser = NayaxUser.findByUsername('a@t.com')
        def picture = Picture.findByFilename("dummyPic") ?: new Picture(filename: "dummyPic", filePath: '../images/', fileSize: 1000, originalFilename: "mockUser", mimeType: ".jpg").save(flush: true)
        def locationCoordinate = LocationCoordinate.findByLatitude(01.663628) ?: new LocationCoordinate(latitude: 01.663628, longitude: 01.663628).save(flush: true)
        def creator = NayaxUser.findByUsername("a@t.com")

        def mockLisn = LISN.findByName("Dummy Lisn") ?: new LISN(name: "Dummy Lisn", startDate: new Date(), endDate: new Date() + 100, locationCoordinate: locationCoordinate, creator: creator).save(flush: true)

        def i = 0
        while (i < 100) {

            def userInstance = NayaxUser.findByUsername("test${i}@t.com") ?: new NayaxUser(fullName: "TEST${i}",
                    password: springSecurityService.encodePassword('p'),
                    enabled: true,
                    username: "test${i}@t.com",
                    website: 's.ulitzer.com',
                    bio: '''S is cool!''',
                    emailSent: false)

            userInstance.picture = picture
            userInstance.save(flush: true)
            def userLisnMap = new UserLISNMap(user: userInstance, lisn: mockLisn)
            def result = joinLisnWithProfileShareType(mockLisn, userInstance, ProfileShareType.PROFESSIONAL)
            i++;
        }
    }
}
