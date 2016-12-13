import nayax.FacebookPalsCache
import nayax.NConnection
import nayax.NayaxUser
import org.codehaus.groovy.grails.web.json.JSONArray
import org.springframework.beans.factory.InitializingBean
import nayax.UserActivityMap
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

import nayax.NayaxUserActivityDescription
import nayax.ActivityAttendence
import nayax.NayaxUserActivityDescription as NUAD;
import nayax.Enum.NConnectionStatus
import nayax.LISN
import nayax.UserLISNMap
import nayax.Enum.ProfileShareType
import java.sql.Connection
import nayax.ConnectionSharingType
import nayax.Setting
import nayax.Enum.SettingType;




class UserService implements InitializingBean {
	
	def grailsApplication
	def setting
	def springSecurityService
	def facebookService
	def fbPalsCache = new FacebookPalsCache(100)
	//    def static transactional = false
	
	def boolean isUserLoggedIn() {
		return springSecurityService.isLoggedIn()
	}
	
	boolean addUserToJoiningList(long activityDescriptionId) {
		NUAD activityDescriptionInstance = NUAD.findById(activityDescriptionId)
		NayaxUser userAttendingEvent = getUser()
		if (userAttendingEvent) {
			nayax.ActivityAttendence attende = new ActivityAttendence(userAttendingEvent: userAttendingEvent, nayaxUserActivity: activityDescriptionInstance).save()
			if (attende) {
				return true
			}
			else {
				log.error("COULD NOT ADD DATA IN TABLE")
				return false
			}
		}
		else {
			log.debug "**** COULD NOT GET USER FROM SESSION ****"
			return false
		}
	}
	
	Date calculateExpiry(String duration) {
		Date expiry = null
		
		def splitValue = duration.split(" ")
		def numberValue = splitValue[0] as Integer
		def checkValue = splitValue[1].trim().toLowerCase()
		log.debug "## NUMBER VALUE IS ${numberValue}, split value1 is ${splitValue[1]} , check value is ${checkValue} and class is ${numberValue.class}"
		
		switch (checkValue) {
			case "min":
				use(TimeCategory) {
					expiry = new Date() + numberValue.minutes
				}
				break;
			
			
			case "hours":
				log.debug "in case hours"
				use(TimeCategory) {
					expiry = new Date() + numberValue.hours
				}
				break;
			
			case "hour":
				log.debug "in case hours"
				use(TimeCategory) {
					expiry = new Date() + numberValue.hours
				}
				break;
			
			case "Days":
				use(TimeCategory) {
					expiry = new Date() + numberValue.days
				}
				break;
			default: log.debug "## could not convert ##"
		}
		log.debug "%%% Expiry is ${expiry}"
		return expiry
	}
	
	List<NUAD> getUserActivities(NayaxUser nayaxUser) {
		//        NayaxUserActivityDescription.list().sort{ it.dateCreated}.reverse()
		//TODO: FIND ALL THE EVENTS THE CURRENT USER HAS JOINED AND FILTER THEM FROM THIS LIST TOO
		//        nayaxUser.nayaxUserActivities.attendies
		List<ActivityAttendence> activityAttendenceList = ActivityAttendence.findAllByUserAttendingEvent(nayaxUser)
		List<NUAD> activitiesUserAttendingList = []
		activityAttendenceList.each { activityAttending ->
			activitiesUserAttendingList += activityAttending.nayaxUserActivity
		}
		List<NUAD> nayaxUserActivityDescriptionList = NayaxUserActivityDescription.findAllByNayaxUserNotEqual(nayaxUser).sort { it.dateCreated}.reverse()
		List<NUAD> activitiesAvailableForAttending = nayaxUserActivityDescriptionList - activitiesUserAttendingList
		log.debug("ACTIVITIES AVALIABLE FOR ATTENDING FOR USER ${nayaxUser.fullName} ARE ${activitiesAvailableForAttending*.name}")
		return activitiesAvailableForAttending
	}
	
	def getUserActivitiesFromPassedUser(NayaxUser nayaxUser) {
		log.debug "PASSED USER IS ${nayaxUser.fullName}"
		NayaxUser currentUser = getUser()
		log.debug "CURRENT USER IS ${currentUser.fullName}"
		def result = []
		//todo : step1 find all activities created by passed user L1
		//todo: step2 find all activities which the current user is attending L2
		//todo : step3 now find intersection of L1 and L2  l3
		//todo : result = L3 - L1
		
		def activitiesCreatedByPassesUser = NUAD.findAllByNayaxUser(nayaxUser)
		log.debug "activities created by passed user ${activitiesCreatedByPassesUser*.name}"
		def activitiesAttendedByCurrentUser = []
		def activitiesAttendenceList = ActivityAttendence.findAllByUserAttendingEvent(currentUser)
		log.debug "user ${currentUser.fullName} is not attending ${activitiesAttendenceList}"
		activitiesAttendenceList.each { activitiesAttendedByCurrentUser += it.nayaxUserActivity }
		result = activitiesCreatedByPassesUser - activitiesCreatedByPassesUser.intersect(activitiesAttendedByCurrentUser)
		log.debug "after intersection result is ${result}"
		return result
	}
	
	public NayaxUser getUser() {
		def nayaxUser = springSecurityService.getCurrentUser().id
		NayaxUser nayaxUserInstance = NayaxUser.read(nayaxUser.toString().toLong()).refresh()
		return nayaxUserInstance;
	}
	
	def isSameUser(def user) {
		return getUser().id == user.id
	}
	
	def getUserConnections(def offset = 0) {
		def thisUser = getUser()
		def connections
		if (thisUser) {
			connections = NConnection.createCriteria().list([max: 20, offset: offset]) {
				and {
					eq("nConnectionStatus", NConnectionStatus.CONNECTED)
					or {
						connection {
							eq('id', thisUser.id)
						}
						owner {
							eq('id', thisUser.id)
						}
					}
				}
			}
		}
		return connections
	}
	
	def getFriends(NayaxUser nayaxUser, NayaxUser userOne) {
		def connections
		def friends = []
		if (nayaxUser) {
			connections = NConnection.createCriteria().list() {
				and {
					eq("nConnectionStatus", NConnectionStatus.CONNECTED)
					or {
						connection {
							eq('id', nayaxUser.id)
						}
						owner {
							eq('id', nayaxUser.id)
						}
					}
				}
			}
		}
		for (NConnection connection : connections) {
			if (connection.owner.id == userOne.id) {
				friends.add(connection.connection)
			}
			else {
				friends.add(connection.owner)
			}
		}
		return friends?.minus(nayaxUser)
	}
	
	def getCommonFriends(NayaxUser userOne, NayaxUser userTwo) {
		def userOneFriends = getFriends(userOne, userOne)
		def userTwoFriends = getFriends(userTwo, userTwo)
		return userOneFriends?.intersect(userTwoFriends)
	}
	
	
	
	
	void afterPropertiesSet() {
		this.setting = grailsApplication.config.setting
	}
	
	public boolean isUserConnected(def otherUser) {
		NayaxUser currentUser = getUser()
		
		def connections = NConnection.createCriteria().list {
			or {
				and {
					connection {
						eq('id', otherUser.id)
					}
					owner {
						eq('id', currentUser.id)
					}
					eq("nConnectionStatus", NConnectionStatus.CONNECTED)
				}
				and {
					connection {
						eq('id', currentUser.id)
					}
					owner {
						eq('id', otherUser.id)
					}
					eq("nConnectionStatus", NConnectionStatus.CONNECTED)
				}
			}
		}
		for (e in connections) {
			log.debug("${e.owner.fullName},${e.connection.fullName} ")
		}
		log.debug("Connections not null " + (connections != null))
		return ((connections != null) && (connections.size() != 0))
	}
	
	public boolean isUserConnectedForApi(def otherUser, def currentUser) {
		//        NayaxUser currentUser = getUser()
		
		def connections = NConnection.createCriteria().list {
			or {
				and {
					connection {
						eq('id', otherUser.id)
					}
					owner {
						eq('id', currentUser.id)
					}
					eq("nConnectionStatus", NConnectionStatus.CONNECTED)
				}
				and {
					connection {
						eq('id', currentUser.id)
					}
					owner {
						eq('id', otherUser.id)
					}
					eq("nConnectionStatus", NConnectionStatus.CONNECTED)
				}
			}
		}
		for (e in connections) {
			log.debug("${e.owner.fullName},${e.connection.fullName} ")
		}
		log.debug("Connections not null " + (connections != null))
		return ((connections != null) && (connections.size() != 0))
	}
	
	def getFBFriends() {
		NayaxUser user = getUser()
		def fbFriendsJson = facebookService.getFriends()
		def returnList = []
		if (user?.facebook) {
			def fid = user.facebook.fid
			if (fbPalsCache.containsKey(fid)) {
				fbFriendsJson = fbPalsCache.get(user.facebook.fid);
			}
			else {
				fbFriendsJson = facebookService.getFriends()
				fbPalsCache.put(fid, fbFriendsJson);
			}
			
			if (fbFriendsJson?.data) {
				def palsList = new JSONArray(fbFriendsJson.data.toString())
				for (e in palsList) {
					returnList.add([id: e.id, name: e.name])
				}
			}
		}
		return returnList
	}
	
	def usersNearby(def session) {
		def nayaxUserInstanceList = userActivityNearby(session, false)
		log.info "Inside UserService method: usersNearBy value to be returned : ${nayaxUserInstanceList}"
		nayaxUserInstanceList
	}
	
	def usersNearbyOfGivenUser(NayaxUser nayaxUser) {
		def nayaxUserInstanceList = userActivityNearby(nayaxUser, false)
		nayaxUserInstanceList
	}
	
	
	def getUserIDsForgivenSession(def session, boolean isConnected, boolean lisn) {
		def nayaxUserInstanceList = getUserIDsForPush(session, isConnected, lisn);
		nayaxUserInstanceList
	}
	
	def usersNotNearby(def session) {
		def nayaxUserInstanceList = userActivityNotNearby(session, false)
		nayaxUserInstanceList
	}
	
	
	
	def friendsNearby(def session) {
		def nayaxUserInstanceList = userActivityNearby(session, true)
		nayaxUserInstanceList
	}
	
	def friendsNotNearby(def session) {
		def nayaxUserInstanceList = userActivityNotNearby(session, true)
		nayaxUserInstanceList
	}
	
	//    Searches for either users either nearby or not which are not present in the same
	//     geographical region if isConnected is true it searches for friends otherwise all users
	
	def userActivityNotNearby(def session, boolean isConnected) {
		def rangeValue = getRangeValue()
		Set nayaxUserInstanceList = new HashSet()
		def position = session.position
		if (position) {
			def latitude = position.coords.latitude
			//      def latitudeMin = latitude + 90 - CH.config.grails.rangeValue
			def latitudeMin = latitude - rangeValue
			//      def latitudeMax = latitude + 90 + CH.config.grails.rangeValue
			def latitudeMax = latitude + rangeValue
			log.debug("IN PEOPLE NOT NEARBY rint")
			def longitude = position.coords.longitude
			
			//      def longitudeMin = longitude + 180 - CH.config.grails.rangeValue
			def longitudeMin = longitude - rangeValue
			//      def longitudeMax = longitude + 180 + CH.config.grails.rangeValue
			def longitudeMax = longitude + rangeValue
			
			
			UserActivityMap.list().each {
				log.debug "latitiue :" + it.latitude + "longitude :" + it.longitude + "name : " + it.nayaxUser.fullName
			}
			
			// TODO: CHECK FOR COMMENTED OUT FUNCTIONALITY
			//            def userActivityList = UserActivityMap.list()
			def notUserActivityList = UserActivityMap.createCriteria().list {
				not {
					between("latitude", latitudeMin, latitudeMax)
					between("longitude", longitudeMin, longitudeMax)
				}
			}
			
			for (e in notUserActivityList) {
				def otherUser = NayaxUser.get(e.nayaxUser.id)
				//              CONNECTED PEOPLE NOT NEARBY
				if (isConnected && isUserConnected(otherUser)) {
					nayaxUserInstanceList.add(otherUser)
				}
				//              NOT CONNECTED PEOPLE NEARBY
				else if (!isConnected && !isUserConnected(otherUser)) {
					nayaxUserInstanceList.add(otherUser)
				}
			}
			nayaxUserInstanceList.toArray()
		}
	}
	
	
	
	def getUserIDsForPush(def session, boolean isConnected, boolean lisn) {
		def rangeValue = getRangeValue()
		log.debug "isConnected  : " + isConnected
		log.debug "************Start method userActivityNearby inside UserService*******************"
		Set nayaxUserInstanceList = new HashSet()
		def position = session.position
		log.debug "Position in Session ${position}"
		if (position) {
			def latitude = position.coords.latitude
			log.debug "Range value from config is ${CH.config.grails.rangeValue}"
			def latitudeMin = latitude + 90 - rangeValue
			def latitudeMax = latitude + 90 + rangeValue
			log.debug "Logged in User: ${getUser().fullName} , latitude: ${latitude}"
			def longitude = position.coords.longitude
			def longitudeMin = longitude + 180 - rangeValue
			def longitudeMax = longitude + 180 + rangeValue
			Date dateWindow = new Date(System.currentTimeMillis() - getTimeWindow())
			def userActivityList = UserActivityMap.createCriteria().list {
				between("latitude", latitudeMin, latitudeMax)
				between("longitude", longitudeMin, longitudeMax)
				gt("activityTime", dateWindow)
			}
			log.debug "Fetched UserActivityMap for logged in  user ${getUser().fullName} is ${userActivityList}"
			for (e in userActivityList) {
				def otherUser = NayaxUser.get(e.nayaxUser.id)
				def nayaxUser = getUser();
				if (otherUser.id != nayaxUser.id) {
					if (isConnected && !lisn) {             // if friend
						if (isUserConnected(otherUser))
							nayaxUserInstanceList.add(otherUser.id)
					} else if (!isConnected && !lisn) {        // if not friend and not lisn checking
						if (!isUserConnected(otherUser)) {
							log.debug "User : ${getUser().fullName} is not connected to ${otherUser.fullName}"
							nayaxUserInstanceList.add(otherUser.id)
						}
					} else if (lisn && !isConnected) {
						nayaxUserInstanceList.add(otherUser.id)
					}
				}
			}
		}
		log.debug "Returning the user set  : ${nayaxUserInstanceList.toArray()}"
		log.debug "************End method userActivityNearby inside UserService*******************"
		return nayaxUserInstanceList.toArray()
	}
	
	/*def userActivityNearby(NayaxUser nayaxUser, boolean isConnected) {
	 def rangeValue = getRangeValue()
	 Set nayaxUserInstanceList = new HashSet()
	 log.debug("NAYAX USER IS ${nayaxUser.fullName}")
	 def userActivityMaps = nayaxUser?.activityMaps
	 if (userActivityMaps) {
	 def latitude = nayaxUser.activityMaps.latitude[0]
	 def latitudeMin = latitude - rangeValue
	 def latitudeMax = latitude + rangeValue
	 log.debug("latmin ${latitudeMin} and latmax is ${latitudeMax}")
	 def longitude = nayaxUser.activityMaps.longitude[0]
	 log.debug("LONGITUDE IS ${longitude}")
	 def longitudeMin = longitude - rangeValue
	 def longitudeMax = longitude + rangeValue
	 log.debug("Longmin ${longitudeMin} and longmax is ${longitudeMax} for USER ${nayaxUser.fullName}")
	 log.debug "longitueMin" + longitudeMin + "longitudeMax :" + longitudeMax
	 log.debug "latitudeMax" + latitudeMax + "latitudeMin :" + latitudeMin
	 UserActivityMap.list().each {
	 log.debug "latitiue :" + it.latitude + "longitude :" + it.longitude + "name : " + it.nayaxUser.fullName
	 }
	 Date dateWindow = new Date(System.currentTimeMillis() - getTimeWindow())
	 def userActivityList = UserActivityMap.createCriteria().list {
	 between("latitude", latitudeMin, latitudeMax)
	 between("longitude", longitudeMin, longitudeMax)
	 gt("activityTime", dateWindow)
	 }
	 log.debug("USER LIST BETWEEN VALUE FOR ${nayaxUser.fullName} IS ${userActivityList}")
	 for (e in userActivityList) {
	 def otherUser = NayaxUser.get(e.nayaxUser.id)
	 if (otherUser.id != nayaxUser.id) {
	 // THIS MEANS THAT isConneted parameter is true and we also check
	 // that there should be atleast one user connection
	 if (isConnected) {
	 nayaxUserInstanceList.add(otherUser)
	 } else if (!isUserConnectedForApi(otherUser, nayaxUser)) {
	 nayaxUserInstanceList.add(otherUser)
	 log.debug("id----------- : : ${otherUser.id}")
	 }
	 }
	 }
	 log.debug("USERS ARE ${nayaxUserInstanceList} which are nearby ${nayaxUser} user ***********")
	 nayaxUserInstanceList.toArray()
	 }
	 }*/
	
	def userActivityNearby(NayaxUser nayaxUser, boolean isConnected) {
		def rangeValue = getRangeValue()
		def nayaxUserInstanceList = new HashSet()
		log.debug("NAYAX USER IS ${nayaxUser.fullName}")
		def userActivityMaps = nayaxUser?.activityMaps
		if (userActivityMaps) {
			
			def latestUserActivity = nayaxUser.activityMaps.sort {it.id}.last()
			
			
			def latitude = latestUserActivity.latitude
			def latitudeMin = latitude - rangeValue
			def latitudeMax = latitude + rangeValue
			
			log.debug("latmin ${latitudeMin} and latmax is ${latitudeMax}")
			
			def longitude = latestUserActivity.longitude
			def longitudeMin = longitude - rangeValue
			def longitudeMax = longitude + rangeValue
			log.debug("Longmin:${longitudeMin} longmax:${longitudeMax}")
			log.debug("latitude:${latitude} and longitude:${longitude}")
			
			Date dateWindow = new Date(System.currentTimeMillis() - getTimeWindow())
			def userActivityList = UserActivityMap.createCriteria().list {
				between("latitude", latitudeMin, latitudeMax)
				between("longitude", longitudeMin, longitudeMax)
				gt("activityTime", dateWindow)
				ne("nayaxUser", nayaxUser)
			}
			
			
			userActivityList.each {e ->
				def otherUser = e.nayaxUser
				nayaxUserInstanceList.add(otherUser)
			}
			//nayaxUserInstanceList.add(NayaxUser.findByUsername('server@lisnx.com'))
			log.debug("Users near ${nayaxUser} are :${nayaxUserInstanceList} *******")
			nayaxUserInstanceList as List
		}
		
		return nayaxUserInstanceList
	}
	
	
	
	def userActivityNearby(def session, boolean isConnected) {
		def rangeValue = getRangeValue()
		Set nayaxUserInstanceList = new HashSet()
		def position = session.position
		if (position) {
			def latitude = position.coords.latitude
			def latitudeMin = latitude + 90 - rangeValue
			def latitudeMax = latitude + 90 + rangeValue
			def longitude = position.coords.longitude
			def longitudeMin = longitude + 180 - rangeValue
			def longitudeMax = longitude + 180 + rangeValue
			Date dateWindow = new Date(System.currentTimeMillis() - getTimeWindow())
			def userActivityList = UserActivityMap.createCriteria().list {
				between("latitude", latitudeMin, latitudeMax)
				between("longitude", longitudeMin, longitudeMax)
				gt("activityTime", dateWindow)
			}
			for (e in userActivityList) {
				def otherUser = NayaxUser.get(e.nayaxUser.id)
				if (isConnected) {
					if (isUserConnected(otherUser))
						nayaxUserInstanceList.add(otherUser)
				}
				
				else if (!isUserConnected(otherUser)) {
					nayaxUserInstanceList.add(otherUser)
				}
			}
		}
		return nayaxUserInstanceList.toArray()
	}
	
	Boolean canVisitProfile(NayaxUser nayaxUser, def session) {
		boolean canVisit = false;
		def currentUser = getUser()
		//        NayaxUser currentUser=NayaxUser.get(springSecurityService.currentUser);
		//def currentUser=springSecurityService.currentUser
		if (nayaxUser.equals(currentUser)) {
			canVisit = true;
		}
		if (!canVisit) {
			if (nayaxUser.isConnectedToUser(currentUser)) {
				canVisit = true;
				log.info "user can see in isConnectedToUser-------->> : " + canVisit;
			}
		}
		if (!canVisit) {
			def listofPeopleNearBy = getUserIDsForPush(session, false, false);
			listofPeopleNearBy.each {
				if (nayaxUser.id.toString().equalsIgnoreCase(it.toString())) {
					canVisit = true;
				}
			}
		}
		
		return canVisit
	}
	
	NayaxUser registerUser(NayaxUser nayaxUser) {
		save(nayaxUser)
		return nayaxUser
	}
	
	private Object save(Object object) {
		validateAndPrintErrors(object)
		Object result = object.save(flush: true)
		return result
	}
	
	private void validateAndPrintErrors(Object object) {
		if (!object.validate()) {
			log.error "Error saving object in UserService"
			object.errors.allErrors.each {error -> log.error error }
		}
	}
	
	Boolean setProfileShareType(NayaxUser sourceUser, NayaxUser targetUser, ProfileShareType profileShareType) {
		boolean status = true
		def isProfileShared = ConnectionSharingType.findBySourceUserAndTargetUser(sourceUser, targetUser);
		if (isProfileShared) {
			isProfileShared.profileShareType = profileShareType
			if (isProfileShared.validate()) {
				isProfileShared.save()
				status = true
			} else {
				isProfileShared.errors.allErrors.each { log.error it }
				isProfileShared.discard()
				status = false
			}
		} else {
			ConnectionSharingType connectionSharingType = new ConnectionSharingType(sourceUser: sourceUser, targetUser: targetUser, profileShareType: profileShareType);
			if (connectionSharingType.validate()) {
				connectionSharingType.save()
				status = true
			} else {
				connectionSharingType.errors.allErrors.each { log.error it }
				connectionSharingType.discard()
				status = false
			}
		}
		return status
	}
	
	NConnection sendConnectionRequestToUser(NayaxUser receiver, NayaxUser sender) {
		log.info "nConnectionService : sendConnectionRequestToUser : Entry"
		NConnection nConnection
		if (!sender.isConnectionRequestPendingWithUser(receiver)) {
			nConnection = new NConnection(owner: sender, connection: receiver)
			//nConnection.connectionType = NConnection.ConnectionType.NAYAX_EVENT
		}
		else {
			nConnection = NConnection.findByOwnerAndConnection(sender, receiver)
			nConnection.timesResent == null ? nConnection.timesResent = 1 : nConnection.timesResent++
		}
		save(nConnection)
		log.info "nConnectionService : sendConnectionRequestToUser : Exit"
		return nConnection
	}
	
	NConnection acceptConnectionRequestOfUser(NayaxUser receiver, NayaxUser sender) {
		log.info "nConnectionService : sendConnectionRequestToUser : Entry"
		def nConnectionList = NConnection.createCriteria().list {
			eq("owner", receiver)
			eq("connection", sender)
			//            or {
			//               eq("nConnectionStatus", NConnectionStatus.PENDING)
			//                eq("nConnectionStatus", NConnectionStatus.IGNORED)
			//            }
		}
		NConnection nConnection;
		if (nConnectionList) {
			nConnectionList.each {
				nConnection = it
				it.nConnectionStatus = NConnectionStatus.CONNECTED;
				it.isNotified = true
				it.save(flush: true);
			}
		}
		
		return nConnection
	}
	
	NConnection ignoreConnectionRequestOfUser(NayaxUser receiver, NayaxUser sender) {
		log.info "nConnectionService : sendConnectionRequestToUser : Entry"
		NConnection nConnection = NConnection.createCriteria().get {
			eq('connection', sender)
			eq('owner', receiver)
			eq('nConnectionStatus',NConnectionStatus.PENDING)
		}
		nConnection.nConnectionStatus = NConnectionStatus.IGNORED;
		nConnection.save(flush: true);
		return nConnection
	}
	
	def getRangeValue() {
		Setting setting = Setting.findBySettingType(SettingType.LATLONGRANGE)
		return setting.value.toFloat()
	}
	
	def getTimeWindow() {
		Setting setting = Setting.findBySettingType(SettingType.TIME_WINDOW)
		log.debug "TIME SETTING " + setting.value.toLong()
		return setting.value.toLong()
	}
}
