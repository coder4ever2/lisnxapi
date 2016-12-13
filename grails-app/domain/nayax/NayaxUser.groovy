package nayax

import java.util.Date

import nayax.Enum.NConnectionStatus
import nayax.apple.Device;
import nayax.FacebookConnection;

import java.text.SimpleDateFormat
import java.text.DateFormat

import lisnxapi.GoogleInviteeHost;

class NayaxUser {
    Long id
    String fullName
    String website
    String bio
    Facebook facebook
    Linkedin linkedin
    PhoneUser phoneUser
    Date dateOfBirth
    String username
    String password
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    Picture picture
    //    ADDED FOR EMAIL
    boolean emailSent = false
    static transients = ['email']
    Boolean isResetPassword=true
    Device iosDevice
    Phone userPhone
    Profile profile
    UserActivityMap latestLocationActivity
    String currentAppVersion
    Date dateCreated // grails will auto timestamp
    Date lastUpdated // grails will auto timestamp
	

    static mapping = {
        password column: '`password`'
        table "sec_user"
        id column: '`id`'
    }

    String getEmail() {
        this.username
    }

    String getFullName() {
        this.fullName
    }

    String toString() {
        this.fullName
    }

    /* static searchable = {
    only: ['fullName', 'bio']
    }*/
    static hasMany = [nayaxUserActivities: NayaxUserActivityDescription,
        eventMaps: UserEventMap, activityMaps: UserActivityMap,
        connections: NConnection, googleInviteeHosts: GoogleInviteeHost]

    static mappedBy = [connections: "owner", activityMaps: "nayaxUser"]


    static constraints = {
        username blank: false, unique: true, email: true
        fullName(blank: false)
        website(nullable: true)
        currentAppVersion(nullable:true)
        bio(nullable: true, maxSize: 5000)
        facebook(nullable: true)
        linkedin(nullable: true)
        phoneUser(nullable: true)
        picture(nullable: true)
        iosDevice(nullable:true)
        userPhone(nullable:true)
        profile(nullable:true)
        latestLocationActivity(nullable:true)
        dateOfBirth(nullable: true, validator: {val, obj ->
                if (val) {
                    def now = new Date();
                    String date = val.toString();
                    String dob
                    if (!val.toString().contains("GMT")) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(df.parse(date));
                        SimpleDateFormat sdFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                        dob = sdFormat.format(cal.getTime());
                    } else {
                        dob = val.toString();
                    }
                    def dateOfbirth = new Date(dob);
                    Double yearDiff = (now + 1 - dateOfbirth) / 365.25;
                    if (yearDiff >= 105) {
                        return 'ageMoreThan105Yrs'
                    }
                    if (yearDiff < 18) {
                        return 'agelessThan18Yrs'
                    }
                    if (val > now) {
                        return 'dobGreaterThanCurrentDate'
                    }
                }
            })
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }

    Boolean isMember(LISN lisn) {
        return UserLISNMap.findByUserAndLisn(this, lisn) ? true : false
    }
    def getFullNameIfNotConnected(){
        def returnName = this.fullName
        def name = this.fullName;
        def fullNameSplit = name.split()
        if(fullNameSplit.size()>1){
            returnName = fullNameSplit[0] +' '+fullNameSplit[fullNameSplit.size()-1].getAt(0) +'.'
        }
        return returnName

    }		

    Boolean isConnectedToUser(NayaxUser nayaxUser) {
        Boolean isConnected = NConnection.findWhere(owner: this, connection: nayaxUser, nConnectionStatus: NConnectionStatus.CONNECTED) ? true : false
        if (!isConnected) {
            isConnected = NConnection.findWhere(owner: nayaxUser, connection: this, nConnectionStatus: NConnectionStatus.CONNECTED) ? true : false
        }
        return isConnected
    }

    Boolean isNotConnectedToUser(NayaxUser nayaxUser) {
        Boolean isNotConnected = NConnection.findWhere(owner: this, connection: nayaxUser, nConnectionStatus: NConnectionStatus.CONNECTED) ? false : true
        if (isNotConnected) {
            isNotConnected = NConnection.findWhere(owner: nayaxUser, connection: this, nConnectionStatus: NConnectionStatus.CONNECTED) ? false : true
        }
        return isNotConnected
    }

    Boolean isConnectionRequestPendingWithUser(NayaxUser nayaxUser) {
        Boolean isRequestPending = NConnection.findWhere(owner: this, connection: nayaxUser, nConnectionStatus: NConnectionStatus.PENDING) ? true : false
        if (!isRequestPending) {
            isRequestPending = NConnection.findWhere(connection: this, owner: nayaxUser, nConnectionStatus: NConnectionStatus.PENDING) ? true : false
        }
        return isRequestPending
    }

    Boolean isConnectionRequestIgnoredByUser(NayaxUser nayaxUser) {
        Boolean isRequestIgnored = NConnection.findWhere(owner: this, connection: nayaxUser, nConnectionStatus: NConnectionStatus.IGNORED) ? true : false
        if (!isRequestIgnored) {
            isRequestIgnored = NConnection.findWhere(connection: this, owner: nayaxUser, nConnectionStatus: NConnectionStatus.IGNORED) ? true : false
        }
        return isRequestIgnored
    }
    Boolean isConnectedOnFacebook(NayaxUser targetUser){
        if (this.facebook?.fid == null) {
            return false
        }
        return FacebookConnection.findByUserFacebookIdAndConnectionFacebookId(this.facebook?.fid, targetUser.facebook?.fid)!=null
    }

    List<NConnection> getPendingConnectionRequests() {
        return NConnection.findAllWhere(connection: this, nConnectionStatus: NConnectionStatus.PENDING)
    }	

    List<NConnection> getFriends() {
        List<NConnection> friends = NConnection.createCriteria().list() {
            eq('nConnectionStatus', NConnectionStatus.CONNECTED)
            and {
                or {
                    eq('connection', this)
                    eq('owner', this)
                }
            }
        }
        return friends
    }

    Set<SecRole> getAuthorities() {
        SecUserSecRole.findAllByNayaxUser(this).collect { it.secRole } as Set
    }
    def getFid(){
        return this.facebook?.fid
    }
	
    def getEventsUserParticipatedIn (){
        def user = this
        def eventsUserParticipatedInMap =[:]
        def eventsUserisGoingTo = EventGoing.findAllWhere(goer:user, notGoing:false)?.collect { it.event } as Set
        eventsUserisGoingTo?.each{
            eventsUserParticipatedInMap[it.id]= it
        }
        def eventsWithUserLikes = EventMessageLike.findAllWhere(user:user)?.collect{it.eventMessage.externalEvent} as Set
        eventsWithUserLikes?.each{
            eventsUserParticipatedInMap[it.id]= it
        }
        def eventsThatUserCommentedOn = EventMessage.findAllWhere(user:user)?.collect{it.externalEvent} as Set
        eventsThatUserCommentedOn?.each{
            eventsUserParticipatedInMap[it.id]= it
        }
                
        return eventsUserParticipatedInMap.values()
		
    }
}
