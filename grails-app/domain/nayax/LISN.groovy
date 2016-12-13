package nayax

import nayax.Enum.NConnectionStatus
import nayax.Enum.SettingType
import org.codehaus.groovy.grails.commons.ConfigurationHolder

//Local Instant Social Network

class LISN {
	
    def grailsApplication
	
    Date dateCreated
    Date lastUpdated
    NayaxUser creator
    LocationCoordinate locationCoordinate
    String name
    String description
    String venue
    Date startDate
    Date endDate    	
	
    static mapping = {
        description type: "text"
    }
    static hasMany = [joins: UserLISNMap, messages:LISNMessage, lisnInvitations:LISNInvitation,
        phoneInvitations:PhoneInvitation]
	
    static constraints = {
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        creator(nullable: false)
        locationCoordinate(nullable: false)
        name(nullable:true)
        description(nullable: true)
        venue(nullable: true)
        startDate(nullable: true)
        endDate(nullable: true)
    }
	
    /*static searchable = {
    only: ['dateCreated', 'lastUpdated', 'creator', 'coordinates', 'name', 'description']
    }*/
	
    static namedQueries = {
        lisnsNearBy {LocationCoordinate coordinate ->
            double latitudeMin = coordinate.latitude - this.getRangeValue()
            double latitudeMax = coordinate.latitude + this.getRangeValue()
            double longitudeMin = coordinate.longitude - this.getRangeValue()
            double longitudeMax = coordinate.longitude + this.getRangeValue()
            locationCoordinate {
                between("latitude", latitudeMin, latitudeMax)
                between("longitude", longitudeMin, longitudeMax)
            }
            gt("id", 470L) //ids before 470 are old data//TODO: Remove Hard-coded 
            order("id", "desc")
        }
        lisnsNearByNow {LocationCoordinate coordinate ->
            double latitudeMin = coordinate.latitude - this.getRangeValue()
            double latitudeMax = coordinate.latitude + this.getRangeValue()
            double longitudeMin = coordinate.longitude - this.getRangeValue()
            double longitudeMax = coordinate.longitude + this.getRangeValue()
            locationCoordinate {
                between("latitude", latitudeMin, latitudeMax)
                between("longitude", longitudeMin, longitudeMax)
            }
            gt("id", 470L) //ids before 470 are old data
            gt("dateCreated", new Date().minus(1))
            order("id", "desc")
        }
    }
	
    def getDisplayString() {
        String lisnDetail = this.name + getDisplayStringWithoutName()
        return lisnDetail
    }
	
    def getDisplayStringWithoutName() {
        def lisn = grailsApplication.mainContext.getBean("nayax.LisnTagLib")
        String lisnDetail = this.venue ? (" at " + this.venue + " ") : " "
        lisnDetail += lisn.formatDateTimeStamp(date: this.startDate)
        return lisnDetail
    }
	
    def getTotalMembers() {
        List<UserLISNMap> userLISNMaps = UserLISNMap.findAllByLisn(this)
        return userLISNMaps ? userLISNMaps.size() : 0
    }
	
    def getLisnMessages() {
        List<LISNMessage> messages = LISNMessage.findAllByLisn(this, [sort:"dateCreated", order:"asc" ])
        return messages
    }
    def getLisnMembers(apiService, timeStamp){
        def lisnMembers = []
        def creator = this.creator
        def defaultThumbnailPicUrl = ConfigurationHolder.config.grails.serverURL + "/images/default_profile_pic_small.gif"
        def defaultProfilePicUrl = ConfigurationHolder.config.grails.serverURL + "/images/default_profile_pic_large.gif"
        def lisnCreator = [
            id:creator?.id,
            fid:creator?.facebook?.fid,
            fullName:creator?.fullName,
            profileThumbnailPicUrl: creator?.linkedin?.thumbnailURL != null ? creator?.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
            profilePictureUrl: creator?.linkedin?.profilePictureURL != null ? creator?.linkedin?.profilePictureURL : defaultProfilePicUrl,
            joined:true,
            status:'joined',
            date_created:apiService.formatDateTimeStampForApi(timeStamp, this.dateCreated)
        ]
        lisnMembers.add(lisnCreator)
		
        this.lisnInvitations.each{lisnInvitation->
            def member = lisnInvitation.receiver
            def lisnMember = [
                id:member?.id,
                fid:member?.facebook?.fid,
                fullName:member?.fullName,
                profileThumbnailPicUrl: member?.linkedin?.thumbnailURL != null ? member?.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                profilePictureUrl: member?.linkedin?.profilePictureURL != null ? member?.linkedin?.profilePictureURL : defaultProfilePicUrl,
                joined:lisnInvitation.invitationAccepted?true:false,
                status:lisnInvitation.invitationAccepted!=null?'joined':lisnInvitation.invitationIgnored!=null?'ignored':'invited',
                date_created:apiService.formatDateTimeStampForApi(timeStamp, lisnInvitation.lastUpdated)
            ]
            lisnMembers.add(lisnMember)
        }
        return lisnMembers
    }
    def getLISNSummary(timeStamp, apiService, user){
        def lisn = this
        def lisnMembers = []
        def members
        def membersJoined = lisn.joins.sort{it.id}
        def moreMemberCount = 0
        def isMuteEnabled = false;
        def defaultThumbnailPicUrl = ConfigurationHolder.config.grails.serverURL + "/images/default_profile_pic_small.gif"
        def defaultProfilePicUrl = ConfigurationHolder.config.grails.serverURL + "/images/default_profile_pic_large.gif"
		
        if(membersJoined.size()==1){
            lisnMembers.add(membersJoined[0].user)
            members= [[id:lisnMembers[0].id, fid: lisnMembers[0].facebook?.fid, fullName: lisnMembers[0].fullName,
                profileThumbnailPicUrl: lisnMembers[0].linkedin?.thumbnailURL != null ? lisnMembers[0].linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                profilePictureUrl: lisnMembers[0].linkedin?.profilePictureURL != null ? lisnMembers[0].linkedin?.profilePictureURL : defaultProfilePicUrl]]
            moreMemberCount = 0		
        }else if(membersJoined.size()>1){
            lisnMembers.add(membersJoined[0].user)
            lisnMembers.add(membersJoined[1].user)
            members= [[id:lisnMembers[0].id, fid: lisnMembers[0].facebook?.fid, fullName: lisnMembers[0].fullName,
                profileThumbnailPicUrl: lisnMembers[0].linkedin?.thumbnailURL != null ? lisnMembers[0].linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                profilePictureUrl: lisnMembers[0].linkedin?.profilePictureURL != null ? lisnMembers[0].linkedin?.profilePictureURL : defaultProfilePicUrl],
                [id:lisnMembers[1].id, fid: lisnMembers[1].facebook?.fid,fullName: lisnMembers[1].fullName,
                profileThumbnailPicUrl: lisnMembers[1].linkedin?.thumbnailURL != null ? lisnMembers[1].linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                profilePictureUrl: lisnMembers[1].linkedin?.profilePictureURL != null ? lisnMembers[1].linkedin?.profilePictureURL : defaultProfilePicUrl]]
            moreMemberCount = lisn.joins.size() - 2
        }
		
        def unviewedMessageCount =0
        UserLISNMap lisnMember = UserLISNMap.findWhere(user:user, lisn:lisn)
        if(lisnMember?.lastViewedMessageId){
            unviewedMessageCount = LISNMessage.createCriteria().list {
                eq("lisn", lisn)
                ne("user", user)
                gt("id", lisnMember.lastViewedMessageId as long)
                //not {'in' ("messageType", [LISNMessage.MessageType.CREATED, LISNMessage.MessageType.INVITED, LISNMessage.MessageType.JOINED])}
                or {
                    isNull("messageType")
                    eq("messageType", LISNMessage.MessageType.MESSAGE)
                }				
            }?.size()
        }
	
        def muteLisnMessages = MuteLisnMessage.withCriteria {
            and {
                eq "user", user
                eq "lisn", lisn
                eq "isCancelled", false
                ge "endDate", new Date()
            }
        }

        MuteLisnMessage muteLisnMessage = null
        if (muteLisnMessages?.size() > 0) {
            log.info "Total MuteLisnMessage records found: " + muteLisnMessages.size()
            muteLisnMessage = muteLisnMessages.get(0)
        }
        
        if (muteLisnMessage) {
            isMuteEnabled = true
        }
		
        def lisnSummary = ["members": members,
			"id":lisn.id,
			"name":lisn.name,
                        "messageType": "LISN",
                        moreMemberCount: moreMemberCount,
                        "unviewedMessageCount":unviewedMessageCount,
                        isMuteEnabled: isMuteEnabled,
                        "date_created":apiService.formatDateTimeStampForApi(timeStamp, lisn.getLisnMessages().get(lisn.getLisnMessages().size()-1).dateCreated)]
        return lisnSummary
    }
	
    def getLISNDetail(timeStamp, apiService, user){
        def result =[:]
        result.message =[:]
        result.message.lisnDetail = getLISNSummary(timeStamp, apiService, user)
        return result
		
    }


	
    def getFriends(NayaxUser nayaxUser) {
        List<NayaxUser> nayaxUsers = []
        List<NConnection> connectionsSent = NConnection.findAllByOwnerAndNConnectionStatus(nayaxUser, NConnectionStatus.CONNECTED)
        nayaxUsers = connectionsSent ? connectionsSent*.connection : []
        List<NConnection> connectionsReceived = NConnection.findAllByConnectionAndNConnectionStatus(nayaxUser, NConnectionStatus.CONNECTED)
        nayaxUsers += connectionsReceived ? connectionsReceived*.owner : []
        Long totalFriendsAttendingEvent = 0
        if (nayaxUsers) {
            totalFriendsAttendingEvent = UserLISNMap.createCriteria().get() {
                projections { countDistinct "id" }
                eq('lisn', this)
				'in'('user', nayaxUsers)
            }
        }
        return totalFriendsAttendingEvent
    }
	
    public static List<LISN> getPastLisnsByUser(NayaxUser nayaxUser) {
        List<LISN> lisnsAttended = []
        List<UserLISNMap> userLisns = UserLISNMap.findAllWhere(user: nayaxUser)
        if (userLisns) {
            userLisns = userLisns.findAll {it.lisn.endDate.before(new Date())}
            lisnsAttended = userLisns.size() > 0 ? userLisns*.lisn : []
        }
        return lisnsAttended.sort{it.startDate}.reverse()
    }
    def isMember(NayaxUser user){
        def isMember = false
        if(this.creator.equals(user))
        return true;
        else {
            this.joins.each{lisnMember ->
                if(lisnMember.user.equals(user)){
                    isMember = true
                }
            }
        }
        return isMember;
    }
	
    def isExpired() {
        return this.endDate < new Date()
    }
	
	
    def static getRangeValue() {
        Setting setting = Setting.findBySettingType(SettingType.LATLONGRANGE)
        return setting.value.toFloat()
    }
}