package com.lisn.api

import static grails.async.Promises.*
import grails.converters.JSON
import groovy.json.JsonSlurper

import java.awt.image.BufferedImage
import java.text.DateFormat
import java.text.SimpleDateFormat

import javax.imageio.ImageIO
import lisnxapi.GoogleInvitee
import lisnxapi.GoogleInviteeHost

import nayax.Activity
import nayax.ConnectionSharingType
import nayax.DateParser
import nayax.DateUtil
import nayax.DirectMessageLike
import nayax.EventGoing
import nayax.EventInvite
import nayax.EventMessage
import nayax.EventMessageLike
import nayax.ExternalEvent
import nayax.Facebook
import nayax.FacebookAccessToken
import nayax.FacebookConnection
import nayax.GeoUtil
import nayax.LISN
import nayax.LISNInvitation
import nayax.LISNMessage
import nayax.LISNxInvitation
import nayax.LastViewedEventMessage
import nayax.Linkedin
import nayax.LinkedinAccessToken
import nayax.LinkedinConnection
import nayax.LocationCoordinate
import nayax.MessageLike
import nayax.MobileAuthToken
import nayax.MuteEventMessage
import nayax.MuteLisnMessage
import nayax.MuteMessage
import nayax.MuteNotification
import nayax.NConnection
import nayax.NayaxUser
import nayax.NearbyUser;
import nayax.ParseUtil
import nayax.Phone
import nayax.PhoneUser;
import nayax.Picture
import nayax.PrivateMessage
import nayax.Profile
import nayax.ProfileShare
import nayax.Setting
import nayax.UserActivityMap;
import nayax.UserLISNMap
import nayax.Enum.INVITATION_CHANNEL
import nayax.Enum.NConnectionStatus
import nayax.Enum.ProfileShareType
import nayax.Enum.SettingType
import nayax.ExternalEvent.EventSource
import nayax.MuteMessage.DurationType
import nayax.MuteNotification.TargetType
import nayax.NConnection.ConnectionType
import nayax.PrivateMessage.MessageType
import nayax.apple.Device
import nayaxapi.PromoInfo

import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.validator.GenericValidator
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.NullSaltSource
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.grails.plugins.imagetools.ImageTool
import org.imgscalr.Scalr

import com.flickr4java.flickr.Flickr
import com.flickr4java.flickr.REST
import com.flickr4java.flickr.photos.Photo
import com.flickr4java.flickr.photos.PhotoList
import com.flickr4java.flickr.photos.PhotosInterface
import com.flickr4java.flickr.photos.SearchParameters
import com.lisnx.api.APNSMessageService
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import com.notnoop.apns.APNS
import com.notnoop.apns.ApnsNotification
import com.notnoop.apns.ApnsService
import com.notnoop.apns.SimpleApnsNotification

class ApiService extends grails.plugins.springsecurity.ui.RegisterController {

    def springSecurityService
    def lisnService
    def userService
    def nayaxMailerService;
    def registrationService;
    static transactional = false
    def messageSource
    def mailService
    def facebookService
    def linkedinService
    def imageService
    def eventbriteService
    def flickrService
    def googleService
	
    def defaultThumbnailPicUrl = ConfigurationHolder.config.grails.serverURL + "/images/default_profile_pic_small.gif"
    def defaultProfilePicUrl = ConfigurationHolder.config.grails.serverURL + "/images/default_profile_pic_large.gif"
                
	
    APNSMessageService apnsMessageService
    ApnsService apnsService
    //def androidGcmService
	
    public static final String SERVER_LISNX_COM = 'server@lisnx.com'
    static final String CONNECTED = "Connected"
    def flickrApiKey = "33334f44d8930d17a60ea9bac5a20ba2";
    def flickrSharedSecret = "789ce72935c2d550";
    static String flickrServer = "www.flickr.com";

	
    /**
     * Search Images method returns image urls for the text provided
     * @param params.text
     * @return
     */
    def searchImages(def params){
        log.info("In searchImages method of FlickrService")
        def imageResults =[]
        REST rest;
        try {
            rest = new REST();
            rest.setHost(flickrServer);

            Flickr flickr = new Flickr(flickrApiKey,flickrSharedSecret, rest);
            Flickr.debugStream = false;

            SearchParameters searchParams = new SearchParameters();
            searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);

            // enter search keywords
            //String[] tags = {"qr code"};
            //searchParams.setTags(tags);
            searchParams.setText(params.text);

            PhotosInterface photosInterface = flickr.getPhotosInterface();
            PhotoList photoList;
            // change the number of results per page
            final int perPage = 30;
            final int maxResults = 30;
            // this is just to find out the total results and pages
            photoList = photosInterface.search(searchParams, perPage, 0);
            int totalPages = photoList.getPages();
            int totalResults = photoList.getTotal();
            log.info('Found ' + totalResults +' in FLICKR search')
            for (int y = 0; y < photoList.size(); y++) {
                try {
                    Photo photo = (Photo)photoList.get(y);
                    //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
                    def constructedPhotoUrl = 'https://farm'+photo.farm+'.staticflickr.com/'+photo.server+'/'+photo.id+'_'+photo.secret+'.jpg'
                    //imageResults.add([url:photo.photoUrl]) 
                    imageResults.add([url:constructedPhotoUrl])
                    //imageResults.add " url: " + photo.getUrl());
					
                } catch (Exception e) {
                    log.error("Exception in FlickrService:", e)
                }
            }
            /*for (int x = 0; x < totalPages; x++) {
            if (photoList != null){
            // retrieve each page
            photoList = photosInterface.search(searchParams, perPage, x);
            if (photoList != null) {
            for (int y = 0; y < photoList.size(); y++) {
            try {
            Photo photo = (Photo)photoList.get(y);
            log.info((x * perPage) + y + "/" + totalResults + " url: " + photo.getUrl());
								
            } catch (Exception e) {
            log.error("Exception in FlickrService:", e)
            }
            }
            }
            }
            }*/

        } catch (Exception e) {
            log.error("Exception in FlickrService:", e)
        }
        return imageResults
		
    }

    String generateLoginToken() {
        MobileAuthToken mobileAuthToken = new MobileAuthToken()
        MobileAuthToken.withTransaction { status ->
            try {
                mobileAuthToken.save(flush: true)
            }
            catch (Exception e) {
                status.setRollbackOnly()
            }
        }
        return mobileAuthToken.token
    }
    /**
     * Get's nearby events
     * @param latitude
     * @param longitude
     * @param within
     * @param token
     * @param timeStamp
     * @return
     */
    def getNearbyEvents(def latitude, def longitude, def within, def token, def timeStamp){
        def result = [:]
        result.status='success'
        result.message=[:]
        result.message.events =[]
        def rangeValue = 0.6
        def offset =10
		
        LocationCoordinate coordinate = new LocationCoordinate(longitude: longitude, latitude: latitude)
        double latitudeMin = coordinate.latitude - rangeValue
        double latitudeMax = coordinate.latitude + rangeValue
        double longitudeMin = coordinate.longitude - rangeValue
        double longitudeMax = coordinate.longitude + rangeValue
        def eventList = ExternalEvent.findAllWhere(isGlobal:true, isActive:Boolean.TRUE)
		
        def nearbyEventList = ExternalEvent.createCriteria().list(){
            Date now = new Date()
            Date pastDate = new Date()
			
            use(groovy.time.TimeCategory) {
                pastDate = now - 30.days  //date 30 days before
            }
			
            gt("endDate", pastDate )
            and {
                locationCoordinate {
                    between("latitude", latitudeMin, latitudeMax)
                    between("longitude", longitudeMin, longitudeMax)
                }
                order("startDate", "asc")
            }
            eq("isActive", Boolean.TRUE)
			
        }
        eventList.addAll(nearbyEventList)
        //
        log.info('Logging LISNx events.................')
        log.info(eventList)
        def eventIdsAdded = []
        eventList.each{event->
            log.info("EVENT:"+event.eventName)
            def eventGoersMessage = getEventGoers(token, event.eventId, event.eventSource.toString())?.message
            def messagesCount = 0
            def externalEvent = ExternalEvent.findWhere(eventId:event.eventId)
            if (externalEvent) {
                messagesCount = EventMessage.findAllByExternalEvent(
                    externalEvent)?.size()
            }
			
            def eventInfo = [name:event.eventName,
                description:[text: event.description],
                start_date:formatDateTimeStampForApi(timeStamp, event.startDate),
                city:event.city,
                state:event.state,
                logo_url:event.logoUrl,
                event_url:event.eventUrl,
                unviewedMessageCount:0,
                date_created:formatDateTimeStampForApi(timeStamp, event.dateCreated),
                eventSource:event.eventSource.toString(),
                event_id: event.id,
                externalEventId:event.eventId,
                eventGoers:eventGoersMessage?.goers,
                isUserGoing:eventGoersMessage?.isUserGoing,
                messagesCount:messagesCount
            ]
			
			
            result.message.events.add(eventInfo)
            eventIdsAdded.add(eventInfo.externalEventId+'-'+eventInfo.eventSource)
			
        }
        //TODO: commenting out for Color Fun Fest 5K
        
        def eventbriteResponseJson = eventbriteService.getNearbyEvents(latitude, longitude, within)
        def nEvents = eventbriteResponseJson.events
        if(nEvents?.size()>10)
        {
            nEvents = nEvents[0..10]
            nEvents.each{event ->
                def eventGoersMessage = getEventGoers(token, event.id, 'Eventbrite')?.message
                def messagesCount = 0
                def externalEvent = ExternalEvent.findWhere(eventId:event.id)
                if (externalEvent) {
                    messagesCount = EventMessage.findAllByExternalEvent(
                        externalEvent)?.size()
                }
                
                SimpleDateFormat utcFormat  = new java.text.SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" )
                def startDate = formatDateTimeStampForApi(timeStamp, utcFormat.parse(event.start.utc))
                def eventInfo = [name: event.name.text, 
                    description:event.description,
                    start_date:startDate,
                    city:event.venue?.address?.city,
                    state:event.venue?.address?.region,
                    logo_url:event.logo_url,
                    event_url:event.url,
                    externalEventId:event.id,
                    eventSource:'Eventbrite',
                    eventGoers:eventGoersMessage?.goers,
                    isUserGoing:eventGoersMessage?.isUserGoing,
                    messagesCount:messagesCount
                ]
                if(!eventIdsAdded.contains(eventInfo.externalEventId+'-'+eventInfo.eventSource))
                result.message.events.add(eventInfo)		
                else {
                    log.info("Found duplicate; NOT ADDING"+ eventInfo)
                }			
            }
        }
        //result.message.fullResponse = eventbriteResponseJson
        
		
        return result
    }
	
    /**
     *
     * @param token
     * @param externalEventId
     * @param eventSource
     * @return
     */
    def getEventGoers(def token, def externalEventId, def eventSource){
        def result = [:]
        result.status='success'
        result.message=[:]
        ExternalEvent externalEvent = ExternalEvent.findWhere(eventId:externalEventId)
        def user = getUserFromToken(token)
		
        if(externalEvent){
            EventGoing eventGoing = EventGoing.findWhere(goer:user, event:externalEvent, notGoing:false)
            if(eventGoing){
                result.message.isUserGoing = true
            }else {
                result.message.isUserGoing = false
            }
			
            Set goers = externalEvent.getEventGoers()
            result.message.goers =[]
            goers.each{goer->
                def aGoer = [id:goer.id, fid:goer.facebook.fid, full_name:goer.fullName]
                result.message.goers.add(aGoer)
            }
        }
        return result
		
    }
	
    /**
     * 
     * @param token
     * @param externalEventId
     * @param eventSource
     * @return
     */
    def goingToEvent(def token, def externalEventId, def eventSource){
        def result = [:]
        result.status='success'
        result.message=[:]
        ExternalEvent externalEvent = ExternalEvent.findWhere(eventId:externalEventId)
        def user = getUserFromToken(token)
        if(externalEvent){
			
        }else {
            if(EventSource.Eventbrite.toString().equals(eventSource)){
                def eventDetails = eventbriteService.getEventDetails(externalEventId)
                if(eventDetails){
                    externalEvent = eventbriteService.populateEventDetails(eventDetails)
                }
            }else {
                externalEvent.eventSource = EventSource.Unknown
            }
            if(externalEvent.locationCoordinate)
            saveObject(externalEvent.locationCoordinate)
            saveObject(externalEvent)
			
        }
        EventGoing eventGoing = EventGoing.findWhere(goer:user, event:externalEvent)
        if(eventGoing){
            eventGoing.notGoing = false
        }else {
            eventGoing = new EventGoing(goer:user, event:externalEvent)
        }
        saveObject(eventGoing)
        result.message.isUserGoing = true
        Set goers = externalEvent.getEventGoers()
        result.message.goers =[]
        goers.each{goer->
            def aGoer = [id:goer.id, fid:goer.facebook.fid, full_name:goer.fullName]
            result.message.goers.add(aGoer)
        }
        return result
		
    }
    /**
     * 
     * @param token
     * @param externalEventId
     * @param eventSource
     * @return
     */
    def notGoingToEvent(def token, def externalEventId, def eventSource){
        def result = [:]
        result.status='success'
        result.message=[:]
        ExternalEvent externalEvent = ExternalEvent.findWhere(eventId:externalEventId)
        def user = getUserFromToken(token)
        EventGoing eventGoing = EventGoing.findWhere(goer:user, event:externalEvent)
        if(eventGoing){
            eventGoing.notGoing = true
            saveObject(eventGoing)
        }
        result.message.isUserGoing = false
        Set goers = externalEvent.getEventGoers()
        result.message.goers =[]
        goers.each{goer->
            def aGoer = [id:goer.id, fid:goer.facebook.fid, full_name:goer.fullName]
            result.message.goers.add(aGoer)
        }
        return result
    }
	
    /**
     * 
     * @param token
     * @param externalEventId
     * @param timeStamp
     * @param lastMessageId
     * @param eventSource
     * @return
     */
    def getEventMessages(token, externalEventId, timeStamp, lastMessageId, eventSource){
        def result = [:]
        def message = [:]
        def user = getUserFromToken(token);
        def externalEvent = ExternalEvent.findWhere(eventId:externalEventId)
        if (externalEvent) {
            //message.members = lisn.getLisnMembers()
            def eventMessages = EventMessage.findAllByExternalEventAndIdGreaterThan(
                externalEvent, lastMessageId)
            def currentLastMessageId = 0
            eventMessages.each {eventMessage ->
                def aMessage = ["sender": eventMessage.user.id,
                                "senderFid":eventMessage.user?.facebook?.fid,
                                "content": eventMessage.content,
                                "dateCreated": formatDateTimeStampForApi(timeStamp, eventMessage.dateCreated),
                                "fullName":eventMessage.user.fullName
                ]
                if(eventMessage.picture){
                    aMessage.isImage = true;
                    aMessage.imageUrl = ConfigurationHolder.config.webImagePath +eventMessage.picture.filename
                    aMessage.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +eventMessage.picture.filename
                    aMessage.imageId = eventMessage.picture.originalFilename
						
                    aMessage.likedByUser =  EventMessageLike.findWhere(
                        eventMessage:eventMessage, 
                        user:user, 
                        unliked:false)!=null
						
                    aMessage.likes = []
                    eventMessage.getLikes()?.each{messageLike ->
                        def messageLikedUser = messageLike.user
                        def thumbnailPicUrl = defaultThumbnailPicUrl
                        def profilePicUrl = defaultProfilePicUrl

                        if (messageLikedUser.linkedin?.thumbnailURL) {
                            thumbnailPicUrl = messageLikedUser.linkedin?.thumbnailURL
                        }
                        if (messageLikedUser.linkedin?.profilePictureURL) {
                            profilePicUrl = messageLikedUser.linkedin?.profilePictureURL
                        }
                
                        def likeDetails = ["id": messageLikedUser.id,
                                            "fid":messageLikedUser.fid,
                                            "profileThumbnailPicUrl": thumbnailPicUrl,
                                            "profilePictureUrl": profilePicUrl,
                                            "fullName":messageLikedUser.fullName ]
                        aMessage.likes.add(likeDetails)
                    }
                }
                if(eventMessage.messageType.equals(EventMessage.MessageType.CONTACT_INFO)){
                    aMessage.messageType= EventMessage.MessageType.CONTACT_INFO.toString()
                }
                else if(eventMessage.messageType && !eventMessage.messageType.equals(EventMessage.MessageType.MESSAGE)){
                    aMessage.messageType = 'INFO'
                    aMessage.content = eventMessage.messageType.toString().toLowerCase()
                    if(eventMessage.messageType.equals(EventMessage.MessageType.JOINED)){
                        //In case of joined, the lastUpdated date is the date of joining and date created is the date of invitation.
                        aMessage.dateCreated = formatDateTimeStampForApi(timeStamp, eventMessage.lastUpdated)
                    }
                }
                message[eventMessage.id] = aMessage
            }
            if(eventMessages.size()>0){
                def lastEventMessage = eventMessages.get(eventMessages.size()-1)
                if(lastEventMessage.messageType==null || lastEventMessage.messageType.equals(EventMessage.MessageType.MESSAGE)
                    ||lastEventMessage.messageType.equals(EventMessage.MessageType.CONTACT_INFO)){
                    currentLastMessageId = lastEventMessage.id
                }
            }
            if(currentLastMessageId!=0){
					
                LastViewedEventMessage lastViewedEventMessage = LastViewedEventMessage.findWhere(user:user,
                    event:externalEvent)
                if(lastViewedEventMessage){
                    lastViewedEventMessage.lastViewedId = currentLastMessageId
                }
                else {
                    lastViewedEventMessage = new LastViewedEventMessage(user:user, event:externalEvent, lastViewedId: currentLastMessageId)
                }
					
                saveObject(lastViewedEventMessage)
            }
				
				
	
            result.status = "success"
        } else {
            result.status = "error"
        }
		
        result["message"] = message
        return result
    }
	
    /**
     *
     * @param token
     * @param externalEventId
     * @param eventSource
     * @param timeStamp
     * @return
     */
    def getExternalEventDetails(def token, def externalEventId, def eventSource, def timeStamp){
        def result = [:]
        result.status='success'
        result.message=[:]
        ExternalEvent externalEvent = ExternalEvent.findWhere(eventId:externalEventId, eventSource:EventSource.valueOf(eventSource))
        def user = getUserFromToken(token)
		
        if(externalEvent){
            EventGoing eventGoing = EventGoing.findWhere(goer:user, event:externalEvent, notGoing:false)
            if(eventGoing){
                result.message.isUserGoing = true
            }else {
                result.message.isUserGoing = false
            }
			
            result.message.eventDetails = externalEvent.getEventDetails(timeStamp)
        }
        else{
            result.status = 'error'
            result.message = 'Couldnot find event with id:'+externalEventId
        }
        return result
		
    }
	
    /**
     * 
     * @param token
     * @param messageId
     * @return
     */
    def likeEventMessage(def token, def messageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
        def eventMessage = EventMessage.findById(messageId)
        if(!EventMessageLike.findWhere(eventMessage:eventMessage, user:user) ){
            EventMessageLike messageLike = new EventMessageLike(user:user, eventMessage:eventMessage)
            saveObject(messageLike)
        }else if(EventMessageLike.findWhere(eventMessage:eventMessage, user:user, unliked:true)){
            EventMessageLike messageLike = EventMessageLike.findWhere(eventMessage:eventMessage, user:user, unliked:true)
            messageLike.setUnliked(false)
            saveObject(messageLike)
        }
        def currentMessageLikes = EventMessageLike.createCriteria().list{
            and {
                eq('eventMessage', eventMessage)
                eq('unliked', false)
            }
        }
        def currentLikes = []
        currentMessageLikes.each{ like ->
            def userThatLiked = like.user
            def userInfo = [
                            "id":userThatLiked.id,
                            "fullName":userThatLiked.fullName,
                            "fid":userThatLiked.facebook?.fid]
							
            currentLikes.add(userInfo)
			
        }
        result.status = 'success'
        result.message.currentLikedCount = currentMessageLikes.size()
        result.message.currentLikes = currentLikes
        return result
    }
	
    /**
     *
     * @param token
     * @param imageId
     * @return
     */
    def likeEventMessageV2(def token, def imageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
        Picture picture = Picture.findByOriginalFilename(imageId)
        if(picture){
		
            def eventMessage = EventMessage.findByPicture(picture)
            if(!EventMessageLike.findWhere(eventMessage:eventMessage, user:user) ){
                EventMessageLike messageLike = new EventMessageLike(user:user, eventMessage:eventMessage)
                saveObject(messageLike)
            }else if(EventMessageLike.findWhere(eventMessage:eventMessage, user:user, unliked:true)){
                EventMessageLike messageLike = EventMessageLike.findWhere(eventMessage:eventMessage, user:user, unliked:true)
                messageLike.setUnliked(false)
                saveObject(messageLike)
            }
            def currentMessageLikes = EventMessageLike.createCriteria().list{
                and {
                    eq('eventMessage', eventMessage)
                    eq('unliked', false)
                }
            }
            def currentLikes = []
            currentMessageLikes.each{ like ->
                def userThatLiked = like.user
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (userThatLiked.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = userThatLiked.linkedin?.thumbnailURL
                }
                if (userThatLiked.linkedin?.profilePictureURL) {
                    profilePicUrl = userThatLiked.linkedin?.profilePictureURL
                }
                def userInfo = [
                                "id":userThatLiked.id,
                                "fullName":userThatLiked.fullName,
                                "fid":userThatLiked.facebook?.fid,
                                "profileThumbnailPicUrl": thumbnailPicUrl,
                                "profilePictureUrl": profilePicUrl]
								
                currentLikes.add(userInfo)
				
            }
            result.status = 'success'
            result.message.currentLikedCount = currentMessageLikes?.size()
            result.message.currentLikes = currentLikes
        }
        return result
    }
	
    /**
     *
     * @param token
     * @param imageId
     * @return
     */
    def unlikeEventMessageV2(def token, def imageId){
        def result = [:]
        result.message=[:]
        def user = getUserFromToken(token)
        Picture picture = Picture.findByOriginalFilename(imageId)
        if(picture){
		
            def eventMessage = EventMessage.findByPicture(picture)
            def eventMessageLike = EventMessageLike.findWhere(eventMessage:eventMessage, user:user)
            if(eventMessageLike ){
                eventMessageLike.unliked = true
                saveObject(eventMessageLike)
            }
            def currentMessageLikes = EventMessageLike.createCriteria().list{
                and {
                    eq('eventMessage', eventMessage)
                    eq('unliked', false)
                }
            }
            def currentLikes = []
            currentMessageLikes.each{ like ->
                def userThatLiked = like.user
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (userThatLiked.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = userThatLiked.linkedin?.thumbnailURL
                }
                if (userThatLiked.linkedin?.profilePictureURL) {
                    profilePicUrl = userThatLiked.linkedin?.profilePictureURL
                }
                def userInfo = [
                                "id":userThatLiked.id,
                                "fullName":userThatLiked.fullName,
                                "fid":userThatLiked.facebook?.fid,
                                "profileThumbnailPicUrl": thumbnailPicUrl,
                                "profilePictureUrl": profilePicUrl
                ]
								
                currentLikes.add(userInfo)
				
            }
            result.status = 'success'
            result.message.currentLikedCount = currentMessageLikes?.size()
            result.message.currentLikes = currentLikes
        }
        return result
    }
	
    /**
     * 
     * @param token
     * @param messageId
     * @return
     */
    def unlikeEventMessage(def token, def messageId){
        def result = [:]
        def user = getUserFromToken(token)
        def eventMessage = EventMessage.findById(messageId)
        def eventMessageLike = EventMessageLike.findWhere(eventMessage:eventMessage, user:user)
        if(eventMessageLike ){
            eventMessageLike.unliked = true
            saveObject(messageLike)
        }
        def currentMessageLikes = EventMessageLike.createCriteria().list{
            and {
                eq('eventMessage', eventMessage)
                eq('unliked', false)
            }
        }
        def currentLikes = []
        currentMessageLikes.each{ like ->
            def userThatLiked = like.user
            def userInfo = [
                            "id":userThatLiked.id,
                            "fullName":userThatLiked.fullName,
                            "fid":userThatLiked.facebook?.fid]
							
            currentLikes.add(userInfo)
			
        }
        result.status = 'success'
        result.message.currentLikedCount = currentMessageLikes.size()
        result.message.currentLikes = currentLikes
        return result
    }
	
	
    def getEventsUserParticipatedIn(def token, def timeStamp){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token);
		
        def eventsUserParticipatedIn = user.getEventsUserParticipatedIn()
        eventsUserParticipatedIn.each{event ->
            def eventDetails = [id: event.id, 
                externalEventId:event.eventId, 
                name:event.eventName,
                logo_url:event.logoUrl,
                event_url:event.eventUrl,
                eventSource:event.eventSource.toString()
								
            ]
			
            result.message[event.id]=eventDetails
        }
        result.status = 'success'
		
        return result
    }
	
    /**
     * TODO
     * @param token
     * @param externalEventId
     * @param eventSource
     * @return
     */
    def getEventTrends(def token, def externalEventId, def eventSource, def timeStamp){
        def result = [:]
        def message = [:]
        def user = getUserFromToken(token);
        def externalEvent = ExternalEvent.findWhere(eventId:externalEventId)
        //def eventMessages = EventMessage.findAllByExternalEventAndPictureIsNotNull(externalEvent, [ max:20])
        def eventMessageLikes = EventMessageLike.createCriteria().list(){
            projections{
                groupProperty "eventMessage"
                count "id",'mycount'
		
            }
            order('mycount','asc')
            eventMessage{
                eq("externalEvent", externalEvent)
            }
            //maxResults(20)
        }
        //TODO: hack for color fun fest 5k south florida event for iOS app.
        if(!timeStamp)
        timeStamp = "-4.00"
			 
			 
        eventMessageLikes.each{eventMessageLike->
            def eventMessage =eventMessageLike[0]
            def thumbnailPicUrl = defaultThumbnailPicUrl
            def profilePicUrl = defaultProfilePicUrl

            if (eventMessage.user?.linkedin?.thumbnailURL) {
                thumbnailPicUrl = eventMessage.user?.linkedin?.thumbnailURL
            }
            if (eventMessage.user?.linkedin?.profilePictureURL) {
                profilePicUrl = eventMessage.user?.linkedin?.profilePictureURL
            }
            def aMessage = ["sender": eventMessage.user.id,
                            "senderFid":eventMessage.user?.facebook?.fid,
                            "profileThumbnailPicUrl": thumbnailPicUrl,
                            "profilePictureUrl": profilePicUrl,
                            "content": eventMessage.content,
                            "dateCreated": formatDateTimeStampForApi(timeStamp, eventMessage.dateCreated),
                            "fullName":eventMessage.user.fullName
            ]
            if(eventMessage.picture){
                aMessage.isImage = true;
                aMessage.imageUrl = ConfigurationHolder.config.webImagePath +eventMessage.picture.filename
                aMessage.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +eventMessage.picture.filename
                aMessage.imageId = eventMessage.picture.originalFilename
				
                aMessage.likedByUser =  EventMessageLike.findWhere(
                    eventMessage:eventMessage,
                    user:user,
                    unliked:false)!=null
				
                aMessage.likes = []
                eventMessage.getLikes()?.each{messageLike ->
                    def messageLikedUser = messageLike.user
                    thumbnailPicUrl = defaultThumbnailPicUrl
                    profilePicUrl = defaultProfilePicUrl

                    if (messageLikedUser.linkedin?.thumbnailURL) {
                        thumbnailPicUrl = messageLikedUser.linkedin?.thumbnailURL
                    }
                    if (messageLikedUser.linkedin?.profilePictureURL) {
                        profilePicUrl = messageLikedUser.linkedin?.profilePictureURL
                    }
                    def likeDetails = ["id": messageLikedUser.id,
                                        "fid":messageLikedUser.fid,
                                        "profileThumbnailPicUrl": thumbnailPicUrl,
                                        "profilePictureUrl": profilePicUrl,
                                        "fullName":messageLikedUser.fullName ]
                    aMessage.likes.add(likeDetails)
                }
            }
            if(eventMessage.messageType.equals(EventMessage.MessageType.CONTACT_INFO)){
                aMessage.messageType= EventMessage.MessageType.CONTACT_INFO.toString()
            }
            else if(eventMessage.messageType && !eventMessage.messageType.equals(EventMessage.MessageType.MESSAGE)){
                aMessage.messageType = 'INFO'
                aMessage.content = eventMessage.messageType.toString().toLowerCase()
                if(eventMessage.messageType.equals(EventMessage.MessageType.JOINED)){
                    //In case of joined, the lastUpdated date is the date of joining and date created is the date of invitation.
                    aMessage.dateCreated = formatDateTimeStampForApi(timeStamp, eventMessage.lastUpdated)
                }
            }
            message[eventMessage.id] = aMessage
			
        }
		
        //result.eventMessageLikes = eventMessageLikes
        /*
		
        eventMessages.each{eventMessage ->
        def aMessage = ["sender": eventMessage.user.id,
        "senderFid":eventMessage.user?.facebook?.fid,
        "content": eventMessage.content,
        "dateCreated": formatDateTimeStampForApi(timeStamp, eventMessage.dateCreated),
        "fullName":eventMessage.user.fullName
        ]
        if(eventMessage.picture){
        aMessage.isImage = true;
        aMessage.imageUrl = ConfigurationHolder.config.webImagePath +eventMessage.picture.filename
        aMessage.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +eventMessage.picture.filename
        aMessage.imageId = eventMessage.picture.originalFilename
				
        aMessage.likedByUser =  EventMessageLike.findWhere(
        eventMessage:eventMessage,
        user:user,
        unliked:false)!=null
				
        aMessage.likes = []
        eventMessage.getLikes()?.each{messageLike ->
        def messageLikedUser = messageLike.user
        def likeDetails = ["id": messageLikedUser.id,
        "fid":messageLikedUser.fid,
        "fullName":messageLikedUser.fullName ]
        aMessage.likes.add(likeDetails)
        }
        }
        if(eventMessage.messageType.equals(EventMessage.MessageType.CONTACT_INFO)){
        aMessage.messageType= EventMessage.MessageType.CONTACT_INFO.toString()
        }
        else if(eventMessage.messageType && !eventMessage.messageType.equals(EventMessage.MessageType.MESSAGE)){
        aMessage.messageType = 'INFO'
        aMessage.content = eventMessage.messageType.toString().toLowerCase()
        if(eventMessage.messageType.equals(EventMessage.MessageType.JOINED)){
        //In case of joined, the lastUpdated date is the date of joining and date created is the date of invitation.
        aMessage.dateCreated = formatDateTimeStampForApi(timeStamp, eventMessage.lastUpdated)
        }
        }
        message[eventMessage.id] = aMessage
        }*/
        result.status = 'success';
        result.message = message
        return result
		
    }
    /**Details when users invite facebook friends
     * 
     * @return
     */
    def getAppInviteDetails(){
        def result=[:]
        result.status='success'
        result.message = [:]
        result.message.appLinkUrl='https://fb.me/958533244181456'
        //result.message.previewImageUrl='http://lisnx.com/images/explore-img2.jpg'
        //TODO- find proper image, until then..
        result.message.previewImageUrl='http://www.lisnx.com'
        return result
		
    }
	
    /**
     * 
     * @param token
     * @param targetId
     * @param duration - number 
     * @param targetType
     * @return
     */
    def muteNotifications(def token, def targetId, def duration, def targetType){
        def result = [:]
        result.status ='success'
        result.message = [:]
        def user = getUserFromToken(token)
        Date muteNotificationEndDate = new Date()
        Date now = new Date()
        if("1h".equals(duration)){
            use(groovy.time.TimeCategory) {
                muteNotificationEndDate = now + 1.hours 
            }
			
        }else if("1d".equals(duration)){
            use(groovy.time.TimeCategory) {
                muteNotificationEndDate = now + 1.days
            }
			
        }else if ("1w".equals(duration)){
            use(groovy.time.TimeCategory) {
                muteNotificationEndDate = now + 1.weeks
            }
        }
		
        MuteNotification.TargetType targetTypeEnum = TargetType.valueOf(targetType)
        MuteNotification muteNotification = new MuteNotification(user:user, 
            targetType:targetTypeEnum,
            targetId: targetId,
            duration:duration ,
            endDate: muteNotificationEndDate)
        saveObject(muteNotification)
        return result
    }
	
    /**
     * 
     * @param token
     * @param image
     * @param messageId
     * @param content
     * @param externalEventId
     * @return
     */
    def postEventMessage(def token, def image, def messageId, def content, def externalEventId){
        def result = [:]
        result.status='success'
        result.message=[:]
        ExternalEvent externalEvent = ExternalEvent.findWhere(eventId:externalEventId)
        def user = getUserFromToken(token)
        Picture picture
        if(!externalEvent){
            def eventDetails = eventbriteService.getEventDetails(externalEventId)
            if(eventDetails){
                externalEvent = eventbriteService.populateEventDetails(eventDetails)
            }
            if(externalEvent.locationCoordinate)
            saveObject(externalEvent.locationCoordinate)
            saveObject(externalEvent)
        } 
        EventMessage eventMessage = new EventMessage(user:user, externalEvent:externalEvent, content:content)
        
        log.info "*** Image: ${image}" 
        if(image){
            picture = saveImage(request)
            eventMessage.picture = picture
            saveObject(picture)
			
        }
		
        if(picture){
            result.message.isImage = true
            result.message.imageId=picture.originalFilename
            result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
            result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
            result.message.likedCount = 0
            result.message.likedByUser=false
            result.message.likes = []
			
        }
        if (eventMessage.save(flush: true)) {
            result.status = "success"
            def notificationMessage = "${user.fullName}: ${content}"
            if(picture)
            notificationMessage = "${user.fullName} shared an image with you."
				
            def messageInfoMap = [message_type:"EVENT_MESSAGE", 
                sender_id:user.id, 
                fid:user.facebook?.fid, 
                name:user.fullName]
			
            messageInfoMap.eventId = externalEvent.id
            messageInfoMap.externalEventId = externalEvent.eventId
            messageInfoMap.eventName = externalEvent.eventName
            def eventParticipantsMap =[:]
            def eventParticipants = [] as Set
            def externalEventGoers = externalEvent.getEventGoers()
            externalEventGoers?.each{goer ->
                eventParticipants.add(goer)
                eventParticipantsMap[goer.id] = [id:goer.id, 
                    fid:goer.facebook?.fid, 
                    full_name:goer.fullName]
            }
            messageInfoMap.goerCount = externalEventGoers?.size()
            def eventMessagers = []
            def eventMessages = EventMessage.findAllWhere(user:user)
            eventMessages.each{message ->
                def messager = message.user
                eventParticipants.add(messager)
                eventParticipantsMap[messager.id] = [id:messager.id,
                    fid:messager.facebook?.fid,
                    full_name:messager.fullName]
            }
			
            //messageInfoMap.eventParticipants = eventParticipantsMap
				
            eventParticipants.each{participant ->
                sendLISNMessageToDevice(user,
                    participant,
                    "EVENT_MESSAGE",
                    result,
                    messageInfoMap,
                    notificationMessage,
                    "", "")
				
            }
			
			
        } else {
            result.status = "error"
            result.message = "error saving message on server"
        }
        return result
    }
    
    /**
     * 
     * @param token
     * @param image
     * @param messageId
     * @param content
     * @param externalEventId
     * check whether to mute Event message notification
     * @return
     */
    def postEventMessageV2(def token, def image, def messageId, def content, def externalEventId){
        def result = [:]
        result.status='success'
        result.message=[:]
        ExternalEvent externalEvent = ExternalEvent.findWhere(eventId:externalEventId)
        def user = getUserFromToken(token)
        Picture picture
        if(!externalEvent){
            def eventDetails = eventbriteService.getEventDetails(externalEventId)
            if(eventDetails){
                externalEvent = eventbriteService.populateEventDetails(eventDetails)
            }
            if(externalEvent.locationCoordinate)
            saveObject(externalEvent.locationCoordinate)
            saveObject(externalEvent)
        } 
        EventMessage eventMessage = new EventMessage(user:user, externalEvent:externalEvent, content:content)
        
        log.info "*** Image: ${image}" 
        if(image){
            picture = saveImage(request)
            eventMessage.picture = picture
            saveObject(picture)
			
        }
		
        if(picture){
            result.message.isImage = true
            result.message.imageId=picture.originalFilename
            result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
            result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
            result.message.likedCount = 0
            result.message.likedByUser=false
            result.message.likes = []
			
        }
        if (eventMessage.save(flush: true)) {
            result.status = "success"
            def notificationMessage = "${user.fullName}: ${content}"
            if(picture)
            notificationMessage = "${user.fullName} shared an image with you."
				
            def messageInfoMap = [message_type:"EVENT_MESSAGE", 
                sender_id:user.id, 
                fid:user.facebook?.fid, 
                name:user.fullName]
			
            messageInfoMap.eventId = externalEvent.id
            messageInfoMap.externalEventId = externalEvent.eventId
            messageInfoMap.eventName = externalEvent.eventName
            def eventParticipantsMap =[:]
            def eventParticipants = [] as Set
            def externalEventGoers = externalEvent.getEventGoers()
            externalEventGoers?.each{goer ->
                eventParticipants.add(goer)
                eventParticipantsMap[goer.id] = [id:goer.id, 
                    fid:goer.facebook?.fid, 
                    full_name:goer.fullName]
            }
            messageInfoMap.goerCount = externalEventGoers?.size()
            def eventMessages = []
            def eventMessageCriteria = EventMessage.createCriteria()
            def users = eventMessageCriteria.list{
                eq "externalEvent", externalEvent
                projections {
                    distinct('user')
                }
            }
            eventMessages = EventMessage.withCriteria {
                'in'("user", users)
            }
            eventMessages.each{message ->
                def messager = message.user
                eventParticipants.add(messager)
                eventParticipantsMap[messager.id] = [id:messager.id,
                    fid:messager.facebook?.fid,
                    full_name:messager.fullName]
            }			
           			
            eventParticipants.each{participant ->
                
                def muteEventMessages = MuteEventMessage.withCriteria {
                    and {
                        eq "user", participant
                        eq "externalEvent", externalEvent

                        eq "isCancelled", false
                        ge "endDate", new Date()
                    }
                }
					
                MuteEventMessage muteEventMessage = null
                
                if (muteEventMessages?.size() > 0) {
                    log.info "Total MuteEventMessage records found: " + muteEventMessages.size()
                    muteEventMessage = muteEventMessages.get(0)
                }
                    
                sendLISNMessageToDeviceV2(user,
                    participant,
                    "EVENT_MESSAGE",
                    result,
                    messageInfoMap,
                    notificationMessage,
                    muteEventMessage,
                    "", "")
				
            }
			
			
        } else {
            result.status = "error"
            result.message = "error saving message on server"
        }
        return result
    }
    
    //This method is specifically created for IOS. Content is decoded message. content_push is non decoded message sent as push message.
    def postEventMessageV4(request){
        
        def json = request.JSON
        
        def token = json.token        
        def image = json.image
        def messageId = json.messageId
        def content = json.content
        def externalEventId = json.externalEventId
        def contentPush = json.content_push
        
        def result = [:]
        result.status='success'
        result.message=[:]
        ExternalEvent externalEvent = ExternalEvent.findWhere(eventId:externalEventId)
        def user = getUserFromToken(token)
        Picture picture
        if(!externalEvent){
            def eventDetails = eventbriteService.getEventDetails(externalEventId)
            if(eventDetails){
                externalEvent = eventbriteService.populateEventDetails(eventDetails)
            }
            if(externalEvent.locationCoordinate)
            saveObject(externalEvent.locationCoordinate)
            saveObject(externalEvent)
        } 
        EventMessage eventMessage = new EventMessage(user:user, externalEvent:externalEvent, content:content)
        if(image){
            picture = saveImage(request)
            eventMessage.picture = picture
            saveObject(picture)
			
        }
		
        if(picture){
            result.message.isImage = true
            result.message.imageId=picture.originalFilename
            result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
            result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
            result.message.likedCount = 0
            result.message.likedByUser=false
            result.message.likes = []
			
        }
        if (eventMessage.save(flush: true)) {
            result.status = "success"
            def notificationMessage = "${user.fullName}: ${content}"
            if(picture)
            notificationMessage = "${user.fullName} shared an image with you."
				
            def messageInfoMap = [message_type:"EVENT_MESSAGE", 
                sender_id:user.id, 
                fid:user.facebook?.fid, 
                name:user.fullName]
			
            messageInfoMap.eventId = externalEvent.id
            messageInfoMap.externalEventId = externalEvent.eventId
            messageInfoMap.eventName = externalEvent.eventName
            def eventParticipantsMap =[:]
            def eventParticipants = [] as Set
            def externalEventGoers = externalEvent.getEventGoers()
            externalEventGoers?.each{goer ->
                eventParticipants.add(goer)
                eventParticipantsMap[goer.id] = [id:goer.id, 
                    fid:goer.facebook?.fid, 
                    full_name:goer.fullName]
            }
            messageInfoMap.goerCount = externalEventGoers?.size()
            
            def eventMessages = []
            def eventMessageCriteria = EventMessage.createCriteria()
            def users = eventMessageCriteria.list{
                eq "externalEvent", externalEvent
                projections {
                    distinct('user')
                }
            }
            eventMessages = EventMessage.withCriteria {
                'in'("user", users)
            }
            eventMessages.each{message ->
                def messager = message.user
                eventParticipants.add(messager)
                eventParticipantsMap[messager.id] = [id:messager.id,
                    fid:messager.facebook?.fid,
                    full_name:messager.fullName]
            }
				
            eventParticipants.each{participant ->
                def muteEventMessages = MuteEventMessage.withCriteria {
                    and {
                        eq "user", participant
                        eq "externalEvent", externalEvent

                        eq "isCancelled", false
                        ge "endDate", new Date()
                    }
                }
					
                MuteEventMessage muteEventMessage = null
                
                if (muteEventMessages?.size() > 0) {
                    log.info "Total MuteEventMessage records found: " + muteEventMessages.size()
                    muteEventMessage = muteEventMessages.get(0)
                }
                    
                sendLISNMessageToDeviceV2(user,
                    participant,
                    "EVENT_MESSAGE",
                    result,
                    messageInfoMap,
                    notificationMessage,
                    muteEventMessage,
                    "", "")				
            }			
			
        } else {
            result.status = "error"
            result.message = "error saving message on server"
        }
        return result
        
    }
	
    /**
     * 
     * @param token
     * @param eventId
     * @param eventSource
     * @return
     */
    def externalEventDetails(def token, def eventId, def eventSource){
        def result = [:]
        result.status='success'
        result.message=[:]
        ExternalEvent event = ExternalEvent.findWhere(eventId:eventId)
        def user = getUserFromToken(token)
        def eventDetails = eventbriteService.getEventDetails(eventId)
		
        def eventInfo = [name: event.name.text, 
            description:event.description,
            start_date:event.start.utc,
            city:event.venue?.address?.city,
            state:event.venue?.address?.region,
            logo_url:event.logo_url,
            event_url:event.url,
            event_id:event.id
        ]
        result.message.eventDetails = eventInfo
        return result
		
    }
	
	
    def getDistance(double lat1, double long1, double lat2, double long2){
		
        double distance = GeoUtil.distance(lat1, long1, lat2, long2)
        log.info("Distance is:"+distance)
        return distance
    }
	
	
    def testGeoUtil(){
        double latitude = 37.709976;
        double longitude = -122.051964;
        //home : 37.709976, -122.051964
        double end_latitude = 37.9796906;
        double end_longitude = -122.0534592;
        double distance = GeoUtil.distance(latitude, longitude, end_latitude, end_longitude)
        log.info("Distance is:"+distance)
        return distance
    }
	
	
    def getPromoSoFarCount(){
        return PromoInfo.count();
    }

    Boolean authenticate(String username, String password) {
        Boolean isAuthenticated = false
        NayaxUser nayaxUser = NayaxUser.findByUsername(username)
        if (nayaxUser) {
            if (nayaxUser.password == springSecurityService.encodePassword(password)) {
                isAuthenticated = true
            }
        }
        return isAuthenticated
    }

    String generateTokenForUser(String username) {
        NayaxUser nayaxUser = NayaxUser.findByUsername(username).refresh()
        return MobileAuthToken.generateTokenForUser(nayaxUser)
    }

    def getLoginToken = {
        Date start = new Date()
        def result = [:]
        //testFacebookFriends()
        result['status'] = "success"
        result['message'] = generateLoginToken()
        result['fb_permissions']="email,user_friends,public_profile"
        log.trace "getLoginToken completed in: " + ((new Date().getTime() - start.getTime())/(1000*60))
        return result;
    }
    def getFirstNameByFid(def fid){
        Facebook facebook = Facebook.findByFid(fid)
        log.info(facebook?.user);
        def firstName = facebook?.user?.fullName?.split(" ")[0]
        return firstName
		
    }
	
    def testFacebookFriends = {
		
        URL facebookUrl = new URL("https://graph.facebook.com/1273617912/friends?access_token=CAACVQ1GOOvABAMvwRf5TcOoJkgvFSwlY0bYUJnZAypLdAheHVgKmSmOnBgt4KriarKDMZA1UtTGlZCj5hZBIV7guyloPzMC2Ykn5MwlN3mSXe8ZBlebeMOtJHvx90dGMEphPZCGVk2PeM0aLzEqZCZAO58N5Y7JlDP3SyFfbCmo7v4UsZCgcZCJvDLRZB2YTefZAN34ZD")
        String response = null;
        HttpURLConnection conn = (HttpURLConnection) facebookUrl.openConnection();
        try {
            int respCode = conn.responseCode
            if (respCode == 400) {
                log.error("COULD NOT MAKE CONNECTION")
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                log.info(br.text)
                def jsonResp = JSON.parse(br.text)
            } else {
                response = conn.getInputStream().getText()
                log.info("Response is")
                def jsonResp = new JsonSlurper().parseText(response)
                jsonResp.data.each { member ->
                    log.info(member.id)
                    log.info(member.name)
                }
				
                log.info(response)
            }
        } finally {
            conn.disconnect()
        }
        log.info("RETURNING RESPONSE")
    }
	
    def updateFacebookTotalCount(def facebook, def count){
        facebook.friendsTotalCount = count
        saveObject(facebook)
    }
	
    def loginWithFacebook(def params) {
        def fbResponse = facebookService.getFacebookUserDetailsForInitialLogin(params.ACCESS_TOKEN,params. fid)
        def fbResultJson = new JsonSlurper().parseText(fbResponse);
        def user
        Facebook facebook = Facebook.findByFid(params.fid)
        def response = facebookService.getUserFriends(params.ACCESS_TOKEN,params. fid)
        if(facebook){
            if(response){
                def results = new JsonSlurper().parseText(response);
                if(results.summary?.total_count)
                updateFacebookTotalCount(facebook, results.summary.total_count)
            }
            if(facebook.facebookAccessToken?.accessToken!=params.ACCESS_TOKEN){
                def facebookAccessToken = facebook.facebookAccessToken
                if(!facebookAccessToken){
                    facebookAccessToken = new FacebookAccessToken()
                    facebook.facebookAccessToken = facebookAccessToken;
					
                }
                //facebookAccessToken.refresh()
                facebookAccessToken.accessToken= params.ACCESS_TOKEN
                facebookAccessToken.expirationDate= new Date(Long.valueOf(params.TOKEN_EXPIRATION_DATE))
                facebookAccessToken.permissions= params.PERMISSIONS
                saveObject(facebookAccessToken)
                saveObject(facebook)
				
            }
        }
        if(facebook?.user){
            user = facebook.user
        }
        else {
            user = NayaxUser.findByUsername(fbResultJson.email)
            def newUserCreated = false
			
            if(!user){
                user = new NayaxUser(fullName: fbResultJson.name,
                    password: springSecurityService.encodePassword(RandomStringUtils.random(6)),
                    enabled: true,
                    username: fbResultJson.email,
                    emailSent: false)
                saveObject(user)
                sendWelcomeEmail(user)
                user.emailSent = true
                saveObject(user)
                newUserCreated = true
            }
			
            Facebook newFacebook = new Facebook(fid: params.fid, facebookLink: null,
                dateCreated: new Date(), lastLogin: new Date(), email: fbResultJson.email, user: user);
            FacebookAccessToken facebookAccessToken = new FacebookAccessToken(accessToken: params.ACCESS_TOKEN,
                expirationDate: new Date(Long.valueOf(params.TOKEN_EXPIRATION_DATE)), permissions: params.PERMISSIONS);
            newFacebook.facebookAccessToken = facebookAccessToken
            newFacebook.save(flush:true)
            newFacebook = Facebook.findByFid(params.fid)
            facebookAccessToken = newFacebook.facebookAccessToken
			
            if(response){
                def results = new JsonSlurper().parseText(response);
                log.info("results-data"+results.data)
                def connectionsProcessed = 0
                log.info("JSON: " + results)
                results.data.each{ member ->
					
                    FacebookConnection facebookConnection = new FacebookConnection
                    (
                        userFacebookId: facebookAccessToken.facebook?.fid==null?'unknown':facebookAccessToken.facebook?.fid,
                        //url: member.apiStandardProfileRequest?.url,
                        firstName: member.first_name,
                        lastName: member.last_name,
                        pictureUrl: member.picture.data.url,
                        pictureIsSilhouette: member.picture.data.is_silhouette,
                        connectionFacebookId: member.id,
                        appInstalled: member.installed==null?false:member.installed.equals("true")?true:false,
                    )
                    log.info("MEMBER"+member)
					
                    if (facebookConnection.validate()) {
                        facebookConnection.save(flush: true, failOnError: true)
                        connectionsProcessed++
                    } else {
                        log.error "Error in FacebookDataJob while saving ${facebookConnection}"
                        facebookConnection.errors.allErrors.each { log.error it }
                    }
					
					
                }
                if(connectionsProcessed == results.data.size()) {
                    facebookAccessToken.dateProcessed = new Date()
                    facebookAccessToken.save(flush:true, failOnError:true)
                }else {
                    log.error("Connections received: "+ results.data.size())
                    log.error("Processed: " + connectionsProcessed)
                }
                Facebook updatedFacebook = facebookAccessToken.facebook.refresh();
                if(results.summary?.total_count)
                updateFacebookTotalCount(updatedFacebook, results.summary.total_count)
				
            }else {
                facebookAccessToken.invalid= true
                facebookAccessToken.save(flush:true, failOnError:true)
				
            }
            if(newUserCreated){
                notifyInviteesAndFriends(user, params.token)
                /*sendPrivateMessageV2(null, MobileAuthToken.findWhere(nayaxUser: NayaxUser.get(83)).token,
                1, "${user.fullName} has just joined ", null)*/
                
                def localParams = [:]
                NayaxUser adminUser = NayaxUser.findByUsername("server@lisnx.com")
                MobileAuthToken mobileAuthToken = MobileAuthToken.findByNayaxUser(adminUser)
                localParams.token = mobileAuthToken.token
                localParams.id = "521" //Lisnx Team lisn id
                localParams.content = "${user.fullName} has just joined "
                postLisnMessageV3(localParams, request)                
            }
				
			
        }
        if(params.appVersion){
            if(params.appVersion!=user.currentAppVersion){
                user.currentAppVersion = params.appVersion
                saveObject(user)
                user = user.refresh()
            }
			
        }
        //def user = NayaxUser.get(1)
        def usingThirdPartyLogin = true
		
        def result =  login(user.username, user.password, generateLoginToken(), params.iosDeviceToken, params.parseInstallationId, usingThirdPartyLogin )
        result.message.updateRequired = isUpdateRequired(user, params.appVersion)
        result.message.loginAccountType = "FACEBOOK"
			
        return result
    }
    /**
     * @deprecated - as this is functionality is handled in {@link FacebookDataJob}
     * @param user
     * @param token
     * @return
     */
    def notifyInviteesAndFriends(def user, def token){
        def result = [:]
        def invitations = LISNxInvitation.findAllByTargetUserId('f'+user.facebook?.fid)
        invitations.each { invitation ->
            def userThatInvited = invitation.user
            if(!userThatInvited.isConnectedToUser(user)){
                NConnection nConnection = new NConnection(owner: userThatInvited, connection: user,
                    connectionType:ConnectionType.FACEBOOK,
                    nConnectionStatus: NConnectionStatus.CONNECTED)
                saveObject(nConnection)
            }
            def privateMessageInstance = new PrivateMessage(receiver: userThatInvited, sender: user, content: " joined LISNx!")
            saveObject(privateMessageInstance)
			
            sendMessageToDevice(user,
                userThatInvited,
                "DIRECT_MESSAGE",
                result,
                "${user.fullName} is on LISNx! Say hi :)")
        }
		
		
    }
	
	
    def getFBStats(def fid){
        def result =[:]
        Facebook facebook = Facebook.findWhere(fid:fid)
        //this.sleep(5000);
        if(facebook){
            def resultMap = [:]
			
            def fbConnections = FacebookConnection.findAllByUserFacebookId(fid)*.connectionFacebookId.unique().size()
            resultMap['totalFacebookFriends']=fbConnections
            resultMap['fid']=fid
            result['status'] = "success"
            result['message'] = resultMap
			
        }else{
            result['status'] = "error"
            result['message'] = "couldnot find facebook account"
		
        }
        return result
    }
	
    def getLIStats(def lid){
        def result =[:]
        def resultMap = [:]
        //this.sleep(5000);
        def liConnections = LinkedinConnection.findAllByUserLinkedinId(lid)*.connectionLinkedinId.unique().size()
        resultMap['totalLinkedInFriends']=liConnections
        resultMap['lid']=lid
        result['status'] = "success"
        result['message'] = resultMap
			
        return result
    }

    def addFacebookAccount(String token, def fid, def params) {
        def result = [:]
		
        if(!token && fid){
            def fbResponse = facebookService.getFacebookUserDetailsForInitialLogin(params.ACCESS_TOKEN, fid)
            def fbResultJson = new JsonSlurper().parseText(fbResponse);
            log.info("FBRES JSON" +fbResultJson)
            /*def user = new NayaxUser(fullName: fbResultJson.name,
            password: springSecurityService.encodePassword(RandomStringUtils.random(6)),
            enabled: true,
            //username: fbResultJson.email,
            username: 'sjaini@lisnx.com',
            emailSent: true)
			
            saveObject(user)
            sendWelcomeEmail(user)*/
            def user = NayaxUser.get(1)
			
            Facebook facebook = new Facebook(fid: fid, facebookLink: null,
                dateCreated: new Date(), lastLogin: new Date(), email: fbResultJson.email, user: user);
            FacebookAccessToken facebookAccessToken = new FacebookAccessToken(accessToken: params.ACCESS_TOKEN,
                expirationDate: new Date(Long.valueOf(params.TOKEN_EXPIRATION_DATE)), permissions: params.PERMISSIONS);
            facebook.facebookAccessToken = facebookAccessToken
            facebook.save(flush:true)
            result['status'] = "success"
            result['message'] = "Added facebook account."
            return result;
			
        }
        NayaxUser nayaxUser = getUserFromToken(token);
        if (nayaxUser.facebook?.fid) {
            result['status'] = "error"
            result['message'] = "Facebook account already linked with the user account."
        } else {
            Facebook facebook = new Facebook(fid: fid, facebookLink: null,
                dateCreated: new Date(), lastLogin: new Date(), email: nayaxUser.email, user: nayaxUser);
				
            boolean flag = saveObject(facebook);
            if(flag && params?.ACCESS_TOKEN){
                log.info("Fetching facebook access token from request.")
				
                /*params.put(Constants.FACEBOOK_PARAMS.ACCESS_TOKEN.toString(), session.getAccessToken());
                params.put(Constants.FACEBOOK_PARAMS.USER_LOCATION.toString(), user.getLocation().getCity()+", "+user.getLocation().getCity());
                params.put(Constants.FACEBOOK_PARAMS.TOKEN_EXPIRATION_DATE.toString(), session.getExpirationDate().toString());
                params.put(Constants.FACEBOOK_PARAMS.PERMISSIONS.toString(), session.getPermissions().toString());*/
				
                FacebookAccessToken facebookAccessToken = new FacebookAccessToken(accessToken: params.ACCESS_TOKEN,
                    expirationDate: new Date(Long.valueOf(params.TOKEN_EXPIRATION_DATE)), permissions: params.PERMISSIONS, facebook: facebook  );
				
                saveObject(facebookAccessToken);
            }
            if (!flag) {
                result['status'] = "error"
                result['message'] = "Error in adding facebook account. Please try again."
            } else {
                result['status'] = "success"
                result['message'] = "Added facebook account."
            }
        }
        return result;
    }
	
    /**
     *
     * @param token
     * @param lid - linkedin id
     * @param linkedinProfileURL - linkedin profile url
     * @param LINKEDIN_ACCESS_TOKEN
     * @param  LINKEDIN_ACCESS_TOKEN_SEC
     * @param TOKEN_EXPIRATION_DATE
     * @param PERMISSIONS
     * @return
     */
    def addLinkedInAccount(String token, def lid, def linkedinProfileURL, def params) {
        def result = [:]
        NayaxUser nayaxUser = getUserFromToken(token);
        if (nayaxUser.linkedin?.loginProviderUID) {
            result['status'] = "error"
            result['message'] = "LinkedIn account already linked with the user account."
        } else {
            Linkedin linkdin = new Linkedin(loginProviderUID: lid, dateCreated: new Date(), lastLogin: new Date(), email: nayaxUser.email, user: nayaxUser, profileURL: profileURL);
            boolean flag = saveObject(linkdin);
            if(flag && params?.LINKED_ACCESS_TOKEN){
                log.info("Fetching linkedin access token from request.")
                LinkedinAccessToken linkedinAccessToken = new LinkedinAccessToken(accessToken: params.LINKEDIN_ACCESS_TOKEN, accessTokenSec:params.LINKEDIN_ACCESS_TOKEN_SEC,
                    expirationDate: new Date(Long.valueOf(params.TOKEN_EXPIRATION_DATE)), permissions: params.PERMISSIONS, linkedin: linkdin );
                saveObject(linkedinAccessToken);
            }
            if (!flag) {
                result['status'] = "error"
                result['message'] = "Error in adding linkedIn account. Please try again."
            } else {
                result['status'] = "success"
                result['message'] = "Added linkedIn account."
            }
        }
        return result;
    }
	
    /**
     *
     * @param token
     * @param lid
     * @param linkedinProfileURL
     * @param expires_in
     * @param LINKEDIN_ACCESS_TOKEN
     * @return
     */
    def addLinkedInOauth2(String token, def lid, def linkedinProfileURL, def expires_in, def LINKEDIN_ACCESS_TOKEN, def permissions) {
        def result = [:]
        Linkedin linkedIn
        LinkedinAccessToken linkedinAccessToken
        NayaxUser nayaxUser = getUserFromToken(token);
        if (nayaxUser.linkedin?.loginProviderUID) {
            linkedIn = nayaxUser.linkedin
            if(nayaxUser.linkedin.loginProviderUID.equals(lid)){
                linkedIn = nayaxUser.linkedin
            }else {
                linkedIn= new Linkedin(loginProviderUID: lid,
                    lastLogin: new Date(),
                    profileURL:linkedinProfileURL,
                    user: nayaxUser);
                saveObject(linkedIn);
            }
			
        }else {
            linkedIn = new Linkedin(loginProviderUID: lid,
                lastLogin:new Date(),
                profileURL:linkedinProfileURL,
                user:nayaxUser)
            saveObject(linkedIn)
		
        }
        def expirationDate = new Date(new Date().getTime()+expires_in.toLong())
        if(linkedIn.linkedinAccessToken){
            linkedinAccessToken = linkedIn.linkedinAccessToken
            linkedinAccessToken.accessToken = LINKEDIN_ACCESS_TOKEN
            linkedinAccessToken.expirationDate = expirationDate
            linkedinAccessToken.oauthVersion ='2.0'
            saveObject(linkedinAccessToken)
        }else {
            linkedinAccessToken = new LinkedinAccessToken(linkedin: linkedIn,
                accessToken:LINKEDIN_ACCESS_TOKEN,
                expirationDate:expirationDate,
                oauthVersion:'2.0',
                permissions:permissions)
            saveObject(linkedinAccessToken)
        }

		
        result['status'] = "success"
        result.message = [:]
        def linkedinUserCardInfo = linkedinService.getUserCardInfo(linkedinAccessToken.accessToken)
        def company = null//linkedinUserCardInfo?.positions?.values[0]?.company?.name
        def linkedInProfileUrl = linkedinUserCardInfo?.publicProfileUrl
        result.message.company = company;
        result.message.linkedInProfileUrl = linkedInProfileUrl;
		
        return result;
    }
	
    def getInformationFromLinkedIn(def token){
        NayaxUser user = getUserFromToken(token)
        def result=[:]
        result.status='success'
        result.message=[:]
        def linkedinAccessToken = user.linkedin?.linkedinAccessToken
        if(linkedinAccessToken){
            def linkedinUserCardInfo = linkedinService.getUserCardInfo(linkedinAccessToken.accessToken)
            def company = linkedinUserCardInfo?.positions?.values[0]?.company?.name
            def linkedInProfileUrl = linkedinUserCardInfo?.publicProfileUrl
            result.message.company = company;
            result.message.linkedInProfileUrl = linkedInProfileUrl//linkedInProfileUrl;
        }
        return result;
    }
	
    def isUpdateAvailable(def token, def currentAppVersion){
        NayaxUser currentUser = getUserFromToken(token);
        boolean isUpdateAvailable = false;
        //TODO check ios or android
        if("android".equals(currentUser.iosDevice.deviceType)){
            isUpdateAvailable = currentAppVersion < Setting.findBySettingType(SettingType.CURRENT_ANDROID_BUILD).value
        }
        else if("ios".equals(currentUser.iosDevice.deviceType)){
            isUpdateAvailable = currentAppVersion < Setting.findBySettingType(SettingType.CURRENT_IOS_BUILD).value
        }
        return isUpdateAvailable;
    }
	
    def isUpdateRequired(def currentUser, def currentAppVersion){
        boolean isUpdateAvailable = false;
        //TODO check ios or android
        if("android".equals(currentUser.iosDevice?.deviceType)){
            isUpdateAvailable = currentAppVersion < Setting.findBySettingType(SettingType.CURRENT_ANDROID_BUILD).value
        }
        else if("ios".equals(currentUser.iosDevice?.deviceType)){
            isUpdateAvailable = currentAppVersion < Setting.findBySettingType(SettingType.CURRENT_IOS_BUILD).value
        }
        return isUpdateAvailable;
    }
	
    def login(def username, def password, def token, def iosDeviceToken, def parseInstallationId, def usingThirdPartyLogin) {
        //TODO: Can we handle it using interceptor ?
        Map result = [:]

        if (token) {
            MobileAuthToken mobileAuthToken = MobileAuthToken.findByToken(token)
            log.info "mobileAuthToken : " + mobileAuthToken
            if (username && password && (usingThirdPartyLogin || authenticate(username, password))) {
                MobileAuthToken.withTransaction { status ->
                    try {
                        mobileAuthToken.delete(flush: true);
                    }
                    catch (Exception e) {
                        status.setRollbackOnly()
                        result['status'] = "error"
                        result['message'] = "API encountered an error, please try again"
                    }
                }
                token = generateTokenForUser(username);
                log.debug "user token check =========> " + token
                if (token != "null") {
                    NayaxUser currentUser = getUserFromToken(token);
                    String id = currentUser.id.toString();
                    result['status'] = "success"

                    result['message'] = ["fullName": currentUser.fullName, "username": currentUser.username,
                                        "website": currentUser.website, "facebook": currentUser.facebook?.facebookLink,
                                        "linkedin": currentUser.linkedin?.profileURL,
                                        "dateOfBirth": currentUser.dateOfBirth ? parseDate(currentUser.dateOfBirth.toString()) : currentUser.dateOfBirth,
                                        "isImage": currentUser.picture ? "true" : "false",
                                        "token":token,
                                        "id":id,
                        recommendProfileSetup: currentUser.profile==null?true:false,
                                        "resetPassword":currentUser.isResetPassword?"true":"false"]
					
                    if(iosDeviceToken){
                        Device userIosDevice = currentUser.iosDevice;
                        if(userIosDevice){
                            userIosDevice.token = iosDeviceToken;
                            userIosDevice.deviceType = "ios"
                        }
                        else {
                            userIosDevice = new Device(user:currentUser)
                            userIosDevice.token = iosDeviceToken;
                            userIosDevice.deviceType = "ios"
                        }
                        userIosDevice.userStatus = "USER_LOGGED_IN"
                        saveObject(userIosDevice)
                    }
                    if(parseInstallationId){
                        Device androidDevice = currentUser.iosDevice;
                        if(androidDevice){
                            androidDevice.token = parseInstallationId;
                            androidDevice.deviceType = "android"
                        }
                        else {
                            androidDevice = new Device(user:currentUser)
                            androidDevice.token = parseInstallationId;
                            androidDevice.deviceType = "android"
                        }
                        androidDevice.userStatus = "USER_LOGGED_IN"
                        saveObject(androidDevice)
                    }
                    //log.info(userService.getFBFriends(currentUser));
                } else {
                    result['status'] = "error"
                    result['message'] = "API encountered an error, please try again"
                }
            } else {
                result['status'] = "error"
                result['message'] = "Invalid Login. Please try again."
            }
        } else {
            result['status'] = "error"
            result['message'] = "Please pass authentication token with your request."
        }


        return result
    }

    def login(def username, def password, def token, def iosDeviceToken, def parseInstallationId) {
        return login(username, password, token, iosDeviceToken, parseInstallationId, false)
    }
	
	
    def sendWelcomeEmail(def user){
        def mailSendingStatus
        try {
            mailService.sendMail {
                //to "${username}"
                to user.username
                bcc "info@lisnx.com", "srinivas@lisnx.com"
                subject "Welcome to LISNx!"
                body(view: "/email/email", model: [firstName: user.fullName])
                from "LISNx Team<team@lisnx.com>"
            }
            mailSendingStatus = "success"
        } catch (Exception e) {
            log.error(e);
            mailSendingStatus = "fail"
        }
    }
	

    def register(def fullName, def username, def password, def password2, dateOfBirth, def token) {
        log.trace "In Apiservice register method  ";
        Map result = [:]
        boolean flag = false;
        def ret = false;
        if (token) {
            flag = true;
            String salt = saltSource instanceof NullSaltSource ? null : username
            String savepassword = springSecurityService.encodePassword(password, salt)
            def user = new nayax.NayaxUser(username: username, fullName: fullName, password: savepassword, accountLocked: false, enabled: true)
            if (dateOfBirth) {
                ret = GenericValidator.isDate(dateOfBirth, "MM/dd/yyyy", false)
            } else {
                ret = true
            }
            if ((fullName == "") || (!fullName)) {
                result['status'] = 'error'
                result['message'] = "Please enter username."
                flag = false;
            } else if (username == "" || !username) {
                result['status'] = "error"
                result['message'] = "Email id can not be left blank."
                flag = false;
            } else if (password == "" || !password) {
                result['status'] = "error"
                result['message'] = "Password can not be left blank"
                flag = false;
            } else if (!ret) {
                result['status'] = "error"
                result['message'] = "Invalid date " + dateOfBirth
                flag = false;
                // return
            } else if (password != password2) {
                result['status'] = "error"
                result['message'] = "Password does not match"
                flag = false;
            }
            if (flag) {
                def field
                user.dateOfBirth = dateOfBirth ? Date.parse("MM/dd/yyyy", dateOfBirth) : null
                user.accountLocked = false;
                log.debug("#### THE USER VALUES ARE ${user} #### ")
                if (!user.validate() || !user.save()) {
                    user.errors.allErrors.each {
                        result['status'] = 'error';
                        result['message'] = messageSource.getMessage(it, null)
                        flag = false;
                    }
                }
                else {
                    result['status'] = "Success";
                    result["message"] = "You are successfully registered."
                    sendWelcomeEmail(user)
                    flag = true;
                }
            }
            if (!flag) {
                user.discard();
            }
        }
        else {
            result['status'] = "error";
            result["message"] = "Request is not generated please try again. "
            flag = false;
        }

        return result
    }
    /**
     * @deprecated
     * @see #createLISNv2()
     * @param lisn
     * @param latitude
     * @param longitude
     * @param token
     * @param endDate
     * @param timeStamp
     * @param android
     * @param profileShareType
     * @return
     */
    def createLisn(LISN lisn, String latitude, String longitude, def token, def endDate, def timeStamp, def android, def profileShareType) {
        log.trace "Inside ApiService:API ,Method:createLisn : with lisn : ${lisn} , latitude : ${latitude} , logintude : ${longitude} , token :  ${token} "
        Map result = [:]

        if (longitude && latitude) {
            log.trace "Inside Services:APIservice : method createLisn : location coordinates found"
            NayaxUser nayaxUser = getUserFromToken(token);
            if (nayaxUser) {
                if (profileShareType in ProfileShareType.list()) {
                    LocationCoordinate locationCoordinate = lisnService.saveLocationCoordinate(longitude.toDouble(), latitude.toDouble())
                    lisn.locationCoordinate = locationCoordinate
                    lisn.creator = nayaxUser
                    lisn.startDate = new Date()
                    lisn.endDate = setTimeOfCreateLisn(timeStamp, endDate)
                    if (lisn.endDate < lisn.startDate) {
                        lisn.discard();
                        result['status'] = "error";
                        result['message'] = messageSource.getMessage('nayax.LISN.endDate.lessThanCurrentDate', null, null);
                    } else {
                        lisn = lisnService.saveLisn(lisn)
                        if (lisn && lisn.id) {
                            log.trace "Inside Service:API Method:createLisn : LISN saved."
                            ProfileShareType profileSharedType = ProfileShareType.valueOf(profileShareType)
                            lisnService.joinLisnWithProfileShareType(lisn, nayaxUser, profileSharedType)
                            log.trace "Inside ApiService:API Method:createLisn : LISN joined."
                            result['status'] = "success"
                            if (android) {                        // change message for android app
                                result['message'] = [id: lisn.id, name: lisn.name, description: lisn.description]
                            } else {
                                result['message'] = "Successfully created LISN with id : " + lisn.id + " and name : " + lisn.name
                            }
                        } else {
                            lisn.discard();
                            log.trace "Inside ApiService:API Method:createLisn : Error saving LISN."
                            result['status'] = "error"
                            result['message'] = "There was an error in creating LISN. Please try again."
                        }
                    }
                } else {
                    log.trace "Inside services:API method:createLisn. profileShareType not found."
                    result['status'] = "error"
                    result['message'] = "Please pass profileShareType for creating the LISN."
                }
            } else {
                lisn.discard();
                result['status'] = "error"
                result['message'] = "No user found with the associated token."
            }
        } else {
            lisn.discard();
            log.trace "Inside ApiService:API Method:createLisn : location coordinates not found"
            result['status'] = "error"
            result['message'] = "You need to pass longitude and latitude to call this API."
        }


        return result
    }

    def getLisnsAroundMe(longitude, latitude, token, timeStamp) {
        log.trace "Inside Apisevice:API method:getLisnsAroundMe :longitude :" + longitude + "   latitude   :" + latitude
        LocationCoordinate locationCoordinate
        Map result = [:]
        Map message = [:]
        locationCoordinate = new LocationCoordinate(longitude: longitude, latitude: latitude)
        NayaxUser nayaxUser = getUserFromToken(token);
        def RSVP
        if (locationCoordinate && locationCoordinate.longitude && locationCoordinate.latitude) {
            //Shall we add paging support here ?
            List<LISN> lisnList = LISN.lisnsNearBy(locationCoordinate).list()
            //            if (Environment.current.name.equals('qa')) {
            //                lisnList.add(LISN.findByName("Dummy Lisn"))
            //            }
            log.trace "Inside ApiService:API Method:getLisnsAroundMe : returning the lisnList : ${lisnList}"
            result['status'] = "success"
            int countLisns = 0, countJoinedLisns = 0;
            if (lisnList) {
                lisnList.each {lisn ->
                    if (nayaxUser.isMember(lisn)) {
                        RSVP = "In"
                        countJoinedLisns++
                    }
                    else {
                        RSVP = "LisnIn"
                    }
                    countLisns++;
                    def messagesCount = LISNMessage.findAllByLisn(lisn).size()
                    message["${lisn.id}"] = ["name": lisn.name, "description": lisn.description, "startDate": formatDateTimeStampForApi(timeStamp, lisn.startDate), "endDate": formatDateTimeStampForApi(timeStamp, lisn.endDate), "venue": lisn.venue, "member": lisn.joins?.size(), "RSVP": RSVP, "Friends": lisn.getFriends(nayaxUser), "totalMessage": messagesCount]
                    // result['name']=lisnDetails;
                }
            }

            result['message'] = message;
            result["countLisns"] = countLisns;
            result["countJoinedLisns"] = countJoinedLisns
        } else {
            log.trace "Inside ApiService:API Method:getLisnsAroundMe : location coordinates not found"
            result['status'] = "error"
            result['message'] = "You need to pass longitude and latitude to call this API."
        }
        return result
    }

    def getPastLisns(token, timeStamp) {
        log.trace "Inside services:APIservice method:getPastLisns :"
        Map result = [:]

        Map message = [:]

        NayaxUser nayaxUser = getUserFromToken(token);
        def RSVP
        if (nayaxUser) {
            result['status'] = "success"
            int countLisns = 0, countJoinedLisns = 0;
            List<LISN> pastLisns = LISN.getPastLisnsByUser(nayaxUser)
            pastLisns.each {lisn ->
                if (nayaxUser.isMember(lisn)) {
                    RSVP = "In"
                    countJoinedLisns++
                }
                else {
                    RSVP = "None"
                }
                countLisns++;
                def messagesCount = LISNMessage.findAllByLisn(lisn).size()
                message["${lisn.id}"] = ["name": lisn.name, "description": lisn.description, "startDate": formatDateTimeStampForApi(timeStamp, lisn.startDate), "endDate": formatDateTimeStampForApi(timeStamp, lisn.endDate), "venue": lisn.venue, "member": lisn.joins.size(), "RSVP": RSVP, "Friends": lisn.getFriends(nayaxUser), "totalMessage": messagesCount]

                // result['name']=lisnDetails;
            }
            result['message'] = message;
            result["countLisns"] = countLisns;
            result["countJoinedLisns"] = countJoinedLisns
        } else {
            result['status'] = "error"
            result['message'] = "No user found with the associated token."
        }


        return result
    }

    def searchLisn(token, now, past, longitude, latitude, nameLike, timeStamp) {

        if (now == 'true') {
            getSearchLisnsAroundMe(longitude, latitude, token, nameLike, timeStamp)
        } else {
            getSearchPastLisns(longitude, latitude, token, nameLike, timeStamp)
        }
    }


    def getSearchPastLisns(longitude, latitude, token, nameLike, timeStamp) {
        log.trace "Inside services:APIservice method:getPastLisns :"
        Map result = [:]
        try {
            Map message = [:]

            NayaxUser nayaxUser = getUserFromToken(token);
            def RSVP
            if (nayaxUser) {
                result['status'] = "success"
                List<LISN> pastLisns = LISN.getPastLisnsByUser(nayaxUser)
                int countLisns = 0, countJoinedLisns = 0;
                List<LISN> lisnLike = pastLisns.findAll { li ->
                    li.name.toString().contains(nameLike.toString()) || li.description.toString().contains(nameLike.toString())
                }

                lisnLike.each {lisn ->
                    if (nayaxUser.isMember(lisn)) {
                        RSVP = "In"
                        countJoinedLisns++
                    }
                    else {
                        RSVP = "None"
                    }
                    countLisns++;
                    message["${lisn.id}"] = ["name": lisn.name, "description": lisn.description, "startDate": formatDateTimeStampForApi(timeStamp, lisn.startDate), "endDate": formatDateTimeStampForApi(timeStamp, lisn.endDate), "venue": lisn.venue, "member": lisn.joins.size(), "RSVP": RSVP, "Friends": lisn.getFriends(nayaxUser)]

                    // result['name']=lisnDetails;
                }
                result['message'] = message;
                result["countLisns"] = countLisns;
                result["countJoinedLisns"] = countJoinedLisns
            } else {
                result['status'] = "error"
                result['message'] = "No user found with the associated token."
            }
        } catch (Exception e) {
            log.info "In addFacebookAccount Exception is    " + e
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        return result
    }

    def getSearchLisnsAroundMe(longitude, latitude, token, nameLike, timeStamp) {
        log.debug "lisn list-------------->>>>>>>>> : " + nameLike
        log.trace "Inside Apisevice:API method:getLisnsAroundMe :"
        LocationCoordinate locationCoordinate
        Map result = [:]
        try {
            Map message = [:]
            locationCoordinate = new LocationCoordinate(longitude: longitude, latitude: latitude)
            NayaxUser nayaxUser = getUserFromToken(token);
            def RSVP
            if (locationCoordinate && locationCoordinate.longitude && locationCoordinate.latitude) {
                //Shall we add paging support here ?
                List<LISN> lisnList = LISN.lisnsNearBy(locationCoordinate).list()
                log.debug "lisn list-------------->>>>>>>>> : " + lisnList
                List<LISN> lisnLike = lisnList.findAll { li ->
                    li.name.toString().contains(nameLike.toString()) || li.description.toString().contains(nameLike.toString())
                }
                log.debug "lis ----lis ---------->>>>>>>>> : " + lisnLike
                int countLisns = 0, countJoinedLisns = 0;
                log.trace "Inside ApiService:API Method:getLisnsAroundMe : returning the lisnList : ${lisnList}"
                result['status'] = 'success'

                lisnLike.each {lisn ->
                    if (nayaxUser.isMember(lisn)) {
                        RSVP = "In"
                        countJoinedLisns++
                    }
                    else {
                        RSVP = "LisnIn"
                    }
                    countLisns++;
                    message["${lisn.id}"] = ["name": lisn.name, "description": lisn.description, "startDate": formatDateTimeStampForApi(timeStamp, lisn.startDate), "endDate": formatDateTimeStampForApi(timeStamp, lisn.endDate), "venue": lisn.venue, "member": lisn.joins.size(), "RSVP": RSVP, "Friends": lisn.getFriends(nayaxUser)]
                    // result['name']=lisnDetails;
                }
                result['message'] = message;
                result["countLisns"] = countLisns;
                result["countJoinedLisns"] = countJoinedLisns
            } else {
                log.trace "Inside ApiService:API Method:getLisnsAroundMe : location coordinates not found"
                result['status'] = "error"
                result['message'] = "You need to pass longitude and latitude to call this API."
            }
        } catch (Exception e) {
            log.info "In addFacebookAccount Exception is    " + e
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        return result
    }
    /**
     * @deprecated
     * @see #getLisnDetailsV2
     * @param id
     * @param token
     * @param timeStamp
     * @return
     */
    def getLisnDetails(String id, token, timeStamp) {
        log.trace "Inside services:API method:getLisnDetails : "
        LISN lisnlist = LISN.read(id.toLong())
        Map result = [:]
        try {
            Map join = [:]
            //            Map connectionStatus = [:]
            def lisn = LISN.read(id.toLong())
            NayaxUser nayaxUser = getUserFromToken(token);
            def RSVP
            if (id) {
                log.trace "Inside ApiService:API Method:getLisnDetails when id found."
                if (nayaxUser.isMember(lisnlist)) {
                    log.info "in joined"
                    RSVP = "In"
                }
                else {
                    log.info "in join"
                    RSVP = "LisnIn"
                }
                def targetUser
                log.info "after join & joined"
                result['status'] = "success"
                lisnlist.joins.each {
                    if (!it.user.id.toString().equalsIgnoreCase(nayaxUser.id.toString()))
                    targetUser = NayaxUser.get(it.user.id.toLong()).refresh()
                    if (it.user.id != nayaxUser.id) {
                        join[it.user.id] = ["id": it.user.id, "fullName": it.user.fullName, "connectionStatus": userConnectionStatus(targetUser, nayaxUser), "isImage": it.user?.picture ? "true" : "false"]
                    } else {
                        join[it.user.id] = ["id": it.user.id, "fullName": it.user.fullName, "connectionStatus": 'Not_Connected', "isImage": it.user?.picture ? "true" : "false"]
                    }
                }



                def userLisnMap = UserLISNMap.findByLisnAndUser(lisn, nayaxUser)
                def messagesCount = LISNMessage.findAllByLisnAndDateCreatedGreaterThan(lisn, userLisnMap?.lastViewed).size()
                result["message"] = ["id": lisnlist.id, "name": lisnlist.name, "description": lisnlist.description, "startDate": formatDateTimeStampForApi(timeStamp, lisn.startDate), "endDate": formatDateTimeStampForApi(timeStamp, lisn.endDate), "venue": lisnlist.venue, "member": lisnlist.getTotalMembers(), "joins": join, "RSVP": RSVP, "Friends": lisn.getFriends(nayaxUser), "messageCount": messagesCount]
            } else {
                log.trace "Inside ApiService:API Method:getLisnDetails ."
                result['status'] = "error"
                result['message'] = "Please pass lisn id to get the details."
            }
        } catch (Exception e) {
            log.info "In addFacebookAccount Exception is    " + e
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        return result
    }
	
    /**
     * Get LISN details such as creator and created time, members, place
     * @param token
     * @param id
     * @param timeStamp
     * @return
     */
    def getLisnDetailsV2(id, token, timeStamp){
        LISN lisn = LISN.findById(id)
        NayaxUser user = getUserFromToken(token);
        def result = [:]
        def lisnDetails = [:]
        lisnDetails.isCreator = lisn.creator.id==user.id
        lisnDetails.lisnName = lisn.name
        lisnDetails.lisnCreatedTime = formatDateTimeStampForApi(timeStamp, lisn.dateCreated)
        lisnDetails.lisnMembers = lisn.getLisnMembers(this, timeStamp)
        lisnDetails.lisnCreator = [id:lisn.creator.id,
            fid:lisn.creator.facebook?.fid,
            fullName:lisn.creator.fullName]
        def locationString = GeoUtil.locationInfo(lisn.locationCoordinate)
        lisnDetails.location = locationString
        if(locationString!='null, null')
        lisnDetails.createdAtMessage = 'Created at '+locationString  ;
		
        result.lisnDetails = lisnDetails
		
		
    }
    def errorResponse(Map result, Exception e){
        log.error('error', e)
        result.status = 'error'
        result.message = 'Error:'+e.getCause().getMessage()
    }

    /**
     * @deprecated
     * @see #lisnInvitationResponse()
     */
    def joinLisn(id, token, timeStamp) {
        LISN lisn
        def result = [:]
        result.message=[:]

        if (id) {
            lisn = LISN.read(id.toLong())
            if (lisn) {
                NayaxUser nayaxUser = getUserFromToken(token);
                if (nayaxUser) {
                    if (lisnService.joinLisnWithProfileShareType(lisn, nayaxUser, ProfileShareType.ALL)) {
					   
                        result.message.lisnDetail = lisn.getLISNDetail(timeStamp, this, nayaxUser)
					   
                        result.status = "success"
                        result.message.info = "Successfully joined LISN."
                    } else {
                        log.trace "Inside services:API method:joinLisn. Error in joining lisn."
                        result['status'] = "error"
                        result['message'] = "Error in joining LISN. Please try again."
                    }
					   
                } else {
                    result['status'] = "error"
                    result['message'] = "No user found with the associated token."
                }
            } else {
                result['status'] = "error"
                result['message'] = "No LISN found with lisn id : " + id
            }
        } else {
            result['status'] = "error"
            result['message'] = "Please pass lisn id to join."
        }


        return result
    }

    def getUserDetails(userId) {
        log.trace "Inside ApiService:API Method:getUserDetails"
        LISN lisn
        Map result = [:]

        if (userId) {
            log.trace "Inside ApiService:API Method:getUserDetails."
            NayaxUser targetUser = NayaxUser.read(userId.toLong()).refresh()

            if (targetUser) {
                result['status'] = "success"
                result['message'] = ["fullName": targetUser.fullName, "username": targetUser.username, "website": targetUser.website, "facebook": targetUser.facebook, "linkedin": targetUser.linkedin, "dateOfBirth": targetUser.dateOfBirth]
            } else {
                log.trace "Inside ApiService:API Method:getUserDetails. LISN not found."
                result['status'] = "error"
                result['message'] = "No User found with user id : " + userId
            }
        } else {
            result['status'] = "error"
            result['message'] = "Please pass user id get userDetails."
        }
        return result
    }

    def getLoginUserDetails(token) {
        log.trace "Inside services:API method:getUserDetails : "
        LISN lisn
        Map result = [:]
        try {
            NayaxUser nayaxUser = getUserFromToken(token);

            log.trace "Inside service:API method:getUserDetails. User id found in params"
            NayaxUser targetUser = NayaxUser.read(nayaxUser.id.toLong()).refresh()

            if (targetUser) {
                result['status'] = "success"
                result['message'] = ["fullName": targetUser.fullName, "username": targetUser.username, "website": targetUser.website, "facebook": targetUser.facebook?.facebookLink, "linkedin": targetUser.linkedin?.profileURL, "dateOfBirth": targetUser.dateOfBirth ? parseDate(targetUser.dateOfBirth.toString()) : targetUser.dateOfBirth, "isImage": targetUser.picture ? "true" : "false"]
            } else {
                log.trace "Inside ApiService:API Method:getUserDetails. LISN not found."
                result['status'] = "error"
                result['message'] = "No User found with user id : " + targetUser
            }
        } catch (Exception e) {
            log.info "In addFacebookAccount Exception is    " + e
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        return result
    }

    def updateUser(fullName, website, dateOfBirth, password, newPassword, confirmPassword, token) {
        Map result = [:]

        def nayaxUserInstance = MobileAuthToken.findByToken(token).refresh().nayaxUser;
        boolean flag = false;
        boolean ret = false;
        long id = nayaxUserInstance.id.toLong();
        if (id) {
            flag = true;
            if (dateOfBirth) {
                ret = GenericValidator.isDate(dateOfBirth, "MM/dd/yyyy", false)
                if (!ret) {
                    flag = false;
                    result['status'] = "error"
                    result['message'] = "Date of birth is not parsable."
                } else {
                    nayaxUserInstance.dateOfBirth = DateParser.parseDate(dateOfBirth, "MM/dd/yyyy");
                }
            }

            if (!fullName) {
                flag = false;
                result['status'] = "error"
                result['message'] = "Please Pass username."
            }
            if (flag) {

                if (website) {
                    nayaxUserInstance.website = website;
                }
                nayaxUserInstance.fullName = fullName;
                nayaxUserInstance.withTransaction {status ->
                    try {
                        if (nayaxUserInstance.validate() || nayaxUserInstance.save(flush: true)) {
                            result['status'] = "success"
                            result['message'] = "Your profile successfully updated."
                            flag = true;
                        }
                        else {
                            flag = false;
                            nayaxUserInstance.errors.allErrors.each {
                                result['status'] = "error"
                                result['message'] = messageSource.getMessage(it, null)
                            }
                        }
                    }
                    catch (Exception e) {
                        status.setRollbackOnly();
                        result['status'] = "error"
                        result['message'] = "API encountered an error, please try again"
                    }
                }
            }
        }
        else {
            flag = false;
            result['status'] = "error"
            result['message'] = "No user found with current user."
        }
        if (!flag) {
            nayaxUserInstance.discard();
        }
        return result
    }

    def sendMessageToLisners(id, message, token) {
        log.trace "Inside service:API Method:sendMessageToLisners"
        LISN lisn
        Map result = [:]

        if (id) {
            log.trace "Inside ApiService:API Method:sendMessageToLisners. LISN id found in params"
            lisn = LISN.read(id.toLong())
            if (lisn) {
                log.trace "Inside ApiService:API Method:sendMessageToLisners. LISN found."
                NayaxUser nayaxUser = MobileAuthToken.findByToken(token).nayaxUser
                if (nayaxUser) {
                    try {
                        nayaxMailerService.sendMessageToLISNers(lisn.id, message, nayaxUser)
                        result['status'] = "success"
                        result['message'] = "Message sent."
                    } catch (Exception e) {
                        e.printStackTrace()
                        log.trace "Inside ApiService:API Method:sendMessageToLisners. Error in joining lisn."
                        result['status'] = "error"
                        result['message'] = "Error in sending message. Please try again."
                    }
                } else {
                    result['status'] = "error"
                    result['message'] = "No user found with the associated token."
                }
            } else {
                log.trace "Inside ApiService:API Method:sendMessageToLisners. LISN not found."
                result['status'] = "error"
                result['message'] = "No LISN found with lisn id : " + id
            }
        } else {
            result['status'] = "error"
            result['message'] = "Please pass lisn id to send message."
        }

        return result
    }


    /**
     * @deprecated
     * @see #getFriendsV2()
     * @param token
     * @return
     */
    def getFriends(token) {
        log.trace "Inside ApiService:API Method:getFriends with params : "
        Map result = [:]

        Map friends = [:]
        NayaxUser nayaxUser = getUserFromToken(token);
        if (nayaxUser) {

            def friendList = nayaxUser.getFriends()
            friendList.each {friend ->
                if (friend.owner.id != nayaxUser.id) {
                    friends[friend.owner.id] = ["id": friend.owner.id, "fullName": friend.owner.fullName, "isImage": friend.owner.picture ? "true" : "false"]
                } else {
                    friends[friend.connection.id] = ["id": friend.connection.id, "fullName": friend.connection.fullName, "isImage": friend.connection.picture ? "true" : "false"]
                }
            }
            result['status'] = "success"
            result['message'] = friends
        } else {
            result['status'] = "error"
            result['message'] = "No user found with the associated token."
        }


        return result
    }
	
    def getFacebookFriends(params){
        Map result = [:]
        Map friends = [:]
        NayaxUser nayaxUser = getUserFromToken(params.token);
        if (nayaxUser) {
            if(nayaxUser.facebook?.facebookAccessToken){
                checkAndAddNewFBFriends(nayaxUser)
            }
			
            if(nayaxUser.facebook?.facebookAccessToken?.dateProcessed){
                def facebookConnections = FacebookConnection.findAllByUserFacebookId(nayaxUser.facebook.fid)
                facebookConnections.each { facebookConnection ->
                    def invitation = LISNxInvitation.findByUserAndTargetUserId(nayaxUser, 'f'+facebookConnection.connectionFacebookId)
                    def wasInvited = (invitation!=null)
                    //check if this facebook connection is LISNx user
                    Facebook facebook = Facebook.findByFid(facebookConnection.connectionFacebookId)
                    if(facebook){
                        def thisUser = NayaxUser.findByFacebook(facebook)
                        if(thisUser){
                            friends[thisUser.id]=["id":thisUser.id,
                                                    "fullName":thisUser.fullName,
                                                    "isImage": (!facebookConnection.pictureIsSilhouette).toString(),
                                                    "connectedOnFacebook":true,
                                                    "connectedOnLisnx":thisUser.isConnectedToUser(nayaxUser),
                                                    "isLisnxUser":true,
                                                    "wasInvited":wasInvited,
                                                    "fid":facebookConnection.connectionFacebookId]
                        }
                    }else {
                        friends['f'+facebookConnection.connectionFacebookId]=["id":'f'+facebookConnection.connectionFacebookId,
							"fullName":facebookConnection.firstName +" "+facebookConnection.lastName,
							"isImage": (!facebookConnection.pictureIsSilhouette).toString(),
							"connectedOnLisnx":false,
							"isLisnxUser":false,
							"connectedOnFacebook":true,
							"wasInvited":wasInvited ,
							"fid":facebookConnection.connectionFacebookId]
                    }
                }
            }

        }
        result['status'] = "success"
        result['message'] = friends
    }
	
    /**
     *
     * @param params should contain token
     * @return
     */
    def getFriendsV2(params) {
        log.trace "Inside ApiService:API Method:getFriends with params : "
        Map result = [:]
        HashSet<Long> usersWithFacebookAccountAddedToList = new HashSet<Long>();

        Map friends = [:]
        NayaxUser nayaxUser = getUserFromToken(params.token);
        if (nayaxUser) {
            if(nayaxUser.facebook?.facebookAccessToken){
                checkAndAddNewFBFriends(nayaxUser)
            }

            if(nayaxUser.facebook?.facebookAccessToken?.dateProcessed){
                def facebookConnections = FacebookConnection.findAllByUserFacebookId(nayaxUser.facebook.fid)
                facebookConnections.each { facebookConnection ->
                    def invitation = LISNxInvitation.findByUserAndTargetUserId(nayaxUser, 'f'+facebookConnection.connectionFacebookId)
                    def wasInvited = (invitation!=null)
                    //check if this facebook connection is LISNx user
                    Facebook facebook = Facebook.findByFid(facebookConnection.connectionFacebookId)
                    if(facebook){
                        def thisUser = NayaxUser.findByFacebook(facebook)
                        if(thisUser){
                            friends[thisUser.id]=["id":thisUser.id,
                                                    "fullName":thisUser.fullName,
                                                    "isImage": (!facebookConnection.pictureIsSilhouette).toString(),
                                                    "connectedOnFacebook":true,
                                                    "connectedOnLisnx":thisUser.isConnectedToUser(nayaxUser),
                                                    "isLisnxUser":true,
                                                    "wasInvited":wasInvited,
                                                    "fid":facebookConnection.connectionFacebookId]
                            usersWithFacebookAccountAddedToList.add(thisUser.id)
                        }
                    }else {
                        friends['f'+facebookConnection.connectionFacebookId]=["id":'f'+facebookConnection.connectionFacebookId,
							"fullName":facebookConnection.firstName +" "+facebookConnection.lastName,
							"isImage": (!facebookConnection.pictureIsSilhouette).toString(),
							"connectedOnLisnx":false,
							"isLisnxUser":false,
							"connectedOnFacebook":true,
							"wasInvited":wasInvited ,
							"fid":facebookConnection.connectionFacebookId]
                    }
                }
            }
            if(nayaxUser.linkedin?.linkedinAccessToken?.dateProcessed){
				
                def invitation = LISNxInvitation.findByUserAndTargetUserId(nayaxUser, 'l'+facebookConnection.connectionFacebookId)
                def wasInvited = (invitation!=null)
				
                def linkedinConnections = LinkedinConnection.findAllByUserLinkedinId(nayaxUser.linkedin.loginProviderUID)
                linkedinConnections.each { linkedinConnection ->
                    friends['l'+linkedinConnection.connec]=["id":'l'+linkedinConnection.connectionLinkedinId,
						"fullName":linkedinConnection.firstName +" "+linkedinConnection.lastName,
						"isImage": linkedinConnection.pictureUrl?'true':'false',
						"connectedOnLinkedin":true,
						"wasInvited":wasInvited]
                }
            }
            def friendList = nayaxUser.getFriends()
            friendList.each {friend ->
                if (friend.owner.id != nayaxUser.id) {
                    if(!usersWithFacebookAccountAddedToList.contains(friend.owner.id)){
                        def thisFriend = friend.owner;
                        friends[thisFriend.id] = ["id": thisFriend.id,
							"fullName": thisFriend.fullName,
							"isImage": thisFriend.picture ? "true" : "false",
							"connectedOnLisnx":true,
							"isLisnxUser":true,
							"fid":thisFriend.facebook?.fid,
                                                        "profileThumbnailPicUrl": thisFriend?.linkedin?.thumbnailURL != null ? thisFriend?.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                                        "profilePictureUrl": thisFriend?.linkedin?.profilePictureURL != null ? thisFriend?.linkedin?.profilePictureURL : defaultProfilePicUrl]
                    }
                } else {
                    if(!usersWithFacebookAccountAddedToList.contains(friend.connection.id))
                    friends[friend.connection.id] = ["id": friend.connection.id,
							"fullName": friend.connection.fullName,
							"isImage": friend.connection.picture ? "true" : "false",
							"connectedOnLisnx":true,
							"isLisnxUser":true,
							"fid":friend.connection.facebook?.fid,
                                                        "profileThumbnailPicUrl": friend.connection?.linkedin?.thumbnailURL != null ? friend.connection?.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                                        "profilePictureUrl": friend.connection?.linkedin?.profilePictureURL != null ? friend.connection?.linkedin?.profilePictureURL : defaultProfilePicUrl]
                }
            }
			
            friends = friends.sort{it.value.fullName}
			
            result['status'] = "success"
			
            result['message'] = friends
            log.info "Friends on LISNx with facebook configured" + usersWithFacebookAccountAddedToList
        } else {
            result['status'] = "error"
            result['message'] = "No user found with the associated token."
        }


        return result
    }
    def checkAndAddNewFBFriends(def nayaxUser){
        if (nayaxUser) {
            def currentFriendsFromFb
            def currentFbFriendIdsFromFb
            if (nayaxUser.facebook?.facebookAccessToken.dateProcessed){
                def facebookConnectionFIDsFromDb = FacebookConnection.findAllByUserFacebookId(nayaxUser.facebook.fid).collect{it.connectionFacebookId}
                def response = facebookService.getUserFriends(nayaxUser.facebook.facebookAccessToken.accessToken , nayaxUser.facebook.fid)
                //log.info("Response" + response)
                if(response){
                    def results = new JsonSlurper().parseText(response);
                    currentFriendsFromFb = results.data;
                    //log.info("results-data"+currentFriendsFromFb.data)
                    def connectionsProcessed = 0
                    //log.info("JSON: " + currentFriendsFromFb)
                    currentFbFriendIdsFromFb = currentFriendsFromFb.collect{it.id}
                    currentFbFriendIdsFromFb.removeAll(facebookConnectionFIDsFromDb)
                    log.info 'New Facebook Friend IDs:' + currentFbFriendIdsFromFb
                    currentFbFriendIdsFromFb.each{ newFBFriendId ->
                        def newFBFriend = currentFriendsFromFb.find{it.id==newFBFriendId}
                        FacebookConnection facebookConnection = new FacebookConnection
                        (
                            userFacebookId: nayaxUser.facebook.fid,
                            //url: member.apiStandardProfileRequest?.url,
                            firstName: newFBFriend.first_name,
                            lastName: newFBFriend.last_name,
                            pictureUrl: newFBFriend.picture.data.url,
                            pictureIsSilhouette: newFBFriend.picture.data.is_silhouette,
                            connectionFacebookId: newFBFriend.id,
                            appInstalled: newFBFriend.installed==null?false:newFBFriend.installed.equals("true")?true:false,
                        )
                        log.info("New FB Friend"+newFBFriend)
						
                        if (facebookConnection.validate()) {
                            facebookConnection.save(flush: true, failOnError: true)
                        } else {
                            log.error "Error while saving ${facebookConnection}"
                            facebookConnection.errors.allErrors.each { log.error it }
                        }
                        if(results.summary?.total_count)
                        updateFacebookTotalCount(nayaxUser.facebook, results.summary.total_count)
						
                    }
                }
            }
        }
    }
	
    def getFBPermissions(){
        Map result = [:]
        result['status'] = "success"
        result['message'] = "email,user_friends,public_profile"
        return result
    }
	
	
    /**
     * @deprecated
     * @see #getLisnMessagesV2()
     * @param token
     * @param id
     * @param timeStamp
     * @param lastMessageId
     * @return
     */
    def getLisnMessages(token, id, timeStamp, lastMessageId) {
        NayaxUser nayaxUser = getUserFromToken(token);
        log.trace "Inside service:API Method:getLisnMessages"
        LISN lisn
        Map result = [:]
        Map allMessagesWithCount = [:]

        Map newMessagesList = [:]
        Map oldMessagesList = [:]
        if (id) {
            lisn = LISN.findById(id)
            def userLisnMap = UserLISNMap.findByLisnAndUser(lisn, nayaxUser)


            if (userLisnMap) {
                println "userLisnMap?.lastViewed----------------" + userLisnMap?.lastViewed
                //                def messagesCount = LISNMessage.findAllByLisnAndDateCreatedGreaterThan(lisn, userLisnMap?.lastViewed).size()
                //                println "New messagesCount----------------" + messagesCount
                def oldMessages = []
                def newMessages = []

                if (lastMessageId.equals("0")) {
                    println "********************lastMessageId.equals********************"
                    if (userLisnMap.lastViewedMessageId == null) {
                        println "********************userLisnMap.lastViewedMessageId == null********************"
                        newMessages = lisn.getLisnMessages()
                        userLisnMap.lastViewedMessageId = newMessages.size()
                        userLisnMap.save()
                    } else {
                        println "Else********************userLisnMap.lastViewedMessageId == null********************"
                        newMessages = LISNMessage.findAllByLisnAndIdGreaterThan(lisn, userLisnMap.lastViewedMessageId, [sort: "dateCreated", order: "asc"])
                        oldMessages = LISNMessage.findAllByLisnAndIdLessThanEquals(lisn, userLisnMap.lastViewedMessageId, [sort: "dateCreated", order: "asc"])
                    }
                } else {
                    println "Else********************lastMessageId.equals********************"
                    newMessages = LISNMessage.findAllByLisnAndIdGreaterThan(lisn, lastMessageId, [sort: "dateCreated", order: "asc"])
                    oldMessages = LISNMessage.findAllByLisnAndIdLessThanEquals(lisn, lastMessageId, [sort: "dateCreated", order: "asc"])
                }

                newMessages.each {message ->
                    newMessagesList[message.id] = ["id": message?.id, "fullName": message?.user?.fullName, "user": message?.user?.id, "isImage": message.user.picture ? "true" : "false", "dateCreated": formatDateTimeStampForApi(timeStamp, message.dateCreated), "content": message.content]
                    if (userLisnMap.lastViewed) {
                        if (message?.dateCreated?.getTime() > userLisnMap?.lastViewed?.getTime()) {
                            newMessagesList[message.id] += ["isNew": "true"]
                        } else {
                            newMessagesList[message.id] += ["isNew": "false"]
                        }
                    }
                    else {
                        newMessagesList[message.id] += ["isNew": "true"]
                    }
                }

                oldMessages.each {LISNMessage message ->
                    oldMessagesList[message.id] = ["id": message?.id, "fullName": message?.user?.fullName, "user": message?.user?.id, "isImage": message.user.picture ? "true" : "false", "dateCreated": formatDateTimeStampForApi(timeStamp, message.dateCreated), "content": message.content]
                    if (userLisnMap.lastViewed) {
                        if (message?.dateCreated?.getTime() > userLisnMap?.lastViewed?.getTime()) {
                            oldMessagesList[message.id] += ["isNew": "true"]
                        } else {
                            oldMessagesList[message.id] += ["isNew": "false"]
                        }
                    }
                    else {
                        oldMessagesList[message.id] += ["isNew": "true"]
                    }
                }
                userLisnMap.lastViewed = new Date()
                userLisnMap.merge()

                log.trace "Inside ApiService:API Method:getLisnMessages. LISN found."
                result['status'] = "success"
                allMessagesWithCount["newMessages"] = newMessagesList
                allMessagesWithCount["oldMessages"] = oldMessagesList
                allMessagesWithCount["newMessagesCount"] = newMessagesList.size()
                allMessagesWithCount["oldMessagesCount"] = oldMessagesList.size()
                //                allMessagesWithCount["messagesCount"] = messagesCount
                result['message'] = allMessagesWithCount
                log.info "messages -->   " + allMessagesWithCount
            }
        }
        return result
    }

    def setLastViewedMessageId(token, id, lastMessageId) {
		
        Map result = [:]
        NayaxUser nayaxUser = getUserFromToken(token);
        log.trace "Inside service:API Method:getLisnMessages"
        LISN lisn = LISN.findById(id)
        def userLisnMap = UserLISNMap.findByLisnAndUser(lisn, nayaxUser)

        userLisnMap.lastViewedMessageId = lastMessageId
        if (userLisnMap.merge()) {
            result["status"] = "success"
            result["message"] = "Last message id is save successfully"
        } else {
            result["status"] = "error"
            result["message"] = "Last message id is not save"
        }

        return result
    }

    /**
     * @deprecated
     * @see #postLisnMessageV2()
     * @param token
     * @param id
     * @param content
     * @param timeStamp
     * @return
     */
    def postLisnMessage(token, id, content, timeStamp) {

        log.trace "Inside service:API Method:postLisnMessage"
        LISN lisn
        Map result = [:]

        Map messageList = [:]
        if (id) {
            lisn = LISN.read(id.toLong())
            if (lisn) {
                NayaxUser nayaxUser = getUserFromToken(token);
                LISNMessage lisnMessage = new LISNMessage(user: nayaxUser, dateCreated: new Date(), content: content, lisn: lisn, messageType:LISNMessage.MessageType.MESSAGE)
                lisnMessage.save()
                UserLISNMap userLISNMap = UserLISNMap.findByUserAndLisn(nayaxUser, lisn)
                userLISNMap.lastViewedMessageId = lisnMessage.id
                userLISNMap.save()
                messageList[lisnMessage.id] = ["id": lisnMessage.id, "user": lisnMessage.user.id,
						"dateCreated": formatDateTimeStampForApi(timeStamp, lisnMessage.dateCreated),
						"content": lisnMessage.content, "lastViewedMessageId": userLISNMap.lastViewedMessageId]
                log.trace "Inside ApiService:API Method:postLisnMessage. LISN found."
                result['status'] = "success"
                result['message'] = messageList
                log.info "messages -->   " + result
            }
        }

        return result
    }
    /**
     *
     * @param params - messageId (optional for async requests to identify), token, id (lisn id), content (message text),
     * image (for image type messages - multipartFile content_type should be
     * one of the 'image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg' options.
     * @return
     */
    def postLisnMessageV2(params){
        def result = [:]
        result.message = [:]
        result.message.messageId = params.messageId?params.messageId:'No_Message_Id'
        def token = params.token
        def lisnId = params.id
        def content = params.content
        def senderInstance = getUserFromToken(token);
        def lisn = LISN.get(lisnId);
        def picture = null
        result.message.sender = senderInstance.id
        result.message.id=lisnId
        result.message.content=content
        if (lisn) {
            LISNMessage lisnMessage = new LISNMessage(user: senderInstance, 
                dateCreated: new Date(), 
                content: content, 
                lisn: lisn, 
                messageType:LISNMessage.MessageType.MESSAGE)
			
            if(params.image){
                picture = saveImage(request)
            }
			
            if(picture){
                lisnMessage.picture= picture
                result.message.isImage = true
                result.message.imageId=picture.originalFilename
                result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
                result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
                result.message.likedCount = 0
                result.message.likedByUser=false
                result.message.likes = []
				
            }
            if (lisnMessage.save(flush: true)) {
                result.status = "success"
                def notificationMessage = "${senderInstance.fullName}: ${content}"
                if(picture)
                notificationMessage = "${senderInstance.fullName} shared an image with you."
					
                def messageInfoMap = [message_type:"LISN_MESSAGE", 
                    sender_id:senderInstance.id, 
                    fid:senderInstance.facebook?.fid, 
                    name:senderInstance.fullName]
				
                //This method uses dummy timeStamp as date is not sent in response. Needs refactoring.
                def lisnSummary = lisn.getLISNSummary("-7", this, senderInstance)
                messageInfoMap.members = lisnSummary.members
                messageInfoMap.moreMemberCount=lisnSummary.moreMemberCount
                messageInfoMap.lisnId = lisn.id
                messageInfoMap.lisnName = lisn.name
				
					
                lisn.joins.each{userLisnMap ->
                    def receiverInstance = userLisnMap.user
                    sendLISNMessageToDevice(senderInstance,
                        receiverInstance,
                        "LISN_MESSAGE",
                        result,
                        messageInfoMap,
                        notificationMessage,
                        "", "")
			
                }
				
				
            } else {
                result.status = "error"
                result.message = "error saving message on server"
            }
			
        }else {
            result.status = "error"
            result.message = "LISN Not Found"
        }

        return result
    }
    
    /**
     *
     * @param params - messageId (optional for async requests to identify), token, id (lisn id), content (message text),
     * image (for image type messages - multipartFile content_type should be
     * one of the 'image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg' options.
     * check whether to mute lisn message notification
     * @return
     */
    def postLisnMessageV3(params, request){
        
        def result = [:]
        result.message = [:]
        result.message.messageId = params.messageId?params.messageId:'No_Message_Id'
        def token = params.token
        def lisnId = params.id
        def content = params.content
        def senderInstance = getUserFromToken(token);
        def lisn = LISN.get(lisnId);
        def picture = null
        result.message.sender = senderInstance.id
        result.message.id=lisnId
        result.message.content=content
        if (lisn) {
            LISNMessage lisnMessage = new LISNMessage(user: senderInstance, 
                dateCreated: new Date(), 
                content: content, 
                lisn: lisn, 
                messageType:LISNMessage.MessageType.MESSAGE)
			
            if(params.image){
                picture = saveImage(request)
            }
			
            if(picture){
                lisnMessage.picture= picture
                result.message.isImage = true
                result.message.imageId=picture.originalFilename
                result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
                result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
                result.message.likedCount = 0
                result.message.likedByUser=false
                result.message.likes = []
				
            }
            if (lisnMessage.save(flush: true)) {
                result.status = "success"
                def notificationMessage = "${senderInstance.fullName}: ${content}"
                if(picture) {                    
                    notificationMessage = "${senderInstance.fullName} shared an image with you."
                }
					
                def messageInfoMap = [message_type:"LISN_MESSAGE", 
                    sender_id:senderInstance.id, 
                    fid:senderInstance.facebook?.fid, 
                    name:senderInstance.fullName]
				
                //This method uses dummy timeStamp as date is not sent in response. Needs refactoring.
                def lisnSummary = lisn.getLISNSummary("-7", this, senderInstance)
                messageInfoMap.members = lisnSummary.members
                messageInfoMap.moreMemberCount=lisnSummary.moreMemberCount
                messageInfoMap.lisnId = lisn.id
                messageInfoMap.lisnName = lisn.name
				
					
                lisn.joins.each{userLisnMap ->
                    def receiverInstance = userLisnMap.user
                    def muteLisnMessages = MuteLisnMessage.withCriteria {
                        and {
                            eq "user", receiverInstance
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

                    sendLISNMessageToDeviceV2(senderInstance,
                        receiverInstance,
                        "LISN_MESSAGE",
                        result,
                        messageInfoMap,
                        notificationMessage,
                        muteLisnMessage,
                        "", "")
                }				
				
            } else {
                result.status = "error"
                result.message = "error saving message on server"
            }
			
        }else {
            result.status = "error"
            result.message = "LISN Not Found"
        }

        return result
    }

    //This method is specifically created for IOS. Content is decoded message. content_push is non decoded message sent as push message.
    def postLisnMessageV4(request){
        def json = request.JSON
        
        def result = [:]
        result.message = [:]
        result.message.messageId = json.messageId?json.messageId:'No_Message_Id'
        def token = json.token
        def lisnId = json.id
        def content = json.content
        def contentPush = json.content_push
        def image = json.image
        def senderInstance = getUserFromToken(token);
        def lisn = LISN.get(lisnId);
        def picture = null
        
        result.message.sender = senderInstance.id
        result.message.id=lisnId
        result.message.content=content
        if (lisn) {
            LISNMessage lisnMessage = new LISNMessage(user: senderInstance, 
                dateCreated: new Date(), 
                content: content, 
                lisn: lisn, 
                messageType:LISNMessage.MessageType.MESSAGE)
			
            if(image){
                picture = saveImage(request)
            }
			
            if(picture){
                lisnMessage.picture= picture
                result.message.isImage = true
                result.message.imageId=picture.originalFilename
                result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
                result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
                result.message.likedCount = 0
                result.message.likedByUser=false
                result.message.likes = []
				
            }
            if (lisnMessage.save(flush: true)) {
                result.status = "success"
                def notificationMessage = "${senderInstance.fullName}: ${content}"
                if(picture)
                notificationMessage = "${senderInstance.fullName} shared an image with you."
					
                def messageInfoMap = [message_type:"LISN_MESSAGE", 
                    sender_id:senderInstance.id, 
                    fid:senderInstance.facebook?.fid, 
                    name:senderInstance.fullName]
				
                //This method uses dummy timeStamp as date is not sent in response. Needs refactoring.
                def lisnSummary = lisn.getLISNSummary("-7", this, senderInstance)
                messageInfoMap.members = lisnSummary.members
                messageInfoMap.moreMemberCount=lisnSummary.moreMemberCount
                messageInfoMap.lisnId = lisn.id
                messageInfoMap.lisnName = lisn.name
				
					
                lisn.joins.each{userLisnMap ->
                    def receiverInstance = userLisnMap.user
                    def muteLisnMessages = MuteLisnMessage.withCriteria {
                        and {
                            eq "user", receiverInstance
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

                    sendLISNMessageToDeviceV2(senderInstance,
                        receiverInstance,
                        "LISN_MESSAGE",
                        result,
                        messageInfoMap,
                        notificationMessage,
                        muteLisnMessage,
                        "", "")
			
                }
				
				
            } else {
                result.status = "error"
                result.message = "error saving message on server"
            }
			
        }else {
            result.status = "error"
            result.message = "LISN Not Found"
        }

        return result
    }
    
    private Picture saveImage(javax.servlet.http.HttpServletRequest request) {
        def multipartFile = request.getFile("image");
        Picture picture 
        log.info("File type/name : "+ multipartFile.getContentType()+":"+multipartFile.getOriginalFilename())
        if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
            def filename = UUID.randomUUID().toString()
            def originalFilename = multipartFile.getOriginalFilename()
            def fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length())

            picture = new Picture(mimeType: multipartFile.getContentType(),
                originalFilename: originalFilename,
                fileSize: multipartFile.getSize(),
                filename: filename+fileExtension,
                filePath: ConfigurationHolder.config.pictureLocation)
            log.info " mimeType  " + multipartFile.getContentType()
            log.info " originalFilename  " + originalFilename
            log.info " filePath  " + ConfigurationHolder.config.pictureLocation
            if (picture.validate()) {
                picture.save()
                File targetFile = new File(ConfigurationHolder.config.pictureLocation, filename +fileExtension )
                multipartFile.transferTo(targetFile)
                File targetThumbNailFile = new File(ConfigurationHolder.config.pictureLocation, 'tn_' +filename +fileExtension )
                def thumbnailImage = imageService.getThumbNail(targetFile, Setting.findBySettingType(SettingType.PIC_WIDTH).value as Integer)
                ImageIO.write(thumbnailImage, fileExtension.substring(1), targetThumbNailFile);

            }
        }
        return picture
    }

    def logout(token) {
        log.trace "Inside ApiService:API Method:logout with params : "
        Map result = [:]
        NayaxUser nayaxUser = getUserFromToken(token);
		
        def device = nayaxUser.iosDevice
        if (device) {
            nayaxUser.iosDevice.userStatus = 'USER_LOGGED_OUT'
            nayaxUser.merge()

            //device.delete()
        }
		
		

        MobileAuthToken mobileAuthToken = MobileAuthToken.findByToken(token)
        if (mobileAuthToken) {
            MobileAuthToken.withTransaction { status ->
                try {
                    mobileAuthToken.delete()
                    result['status'] = "success"
                    result['message'] = "logged out."
                }
                catch (Exception e) {
                    status.setRollbackOnly()
                    result['status'] = "error"
                    result['message'] = "API encountered an error, please try again"
                }
            }
        } else {
            result['status'] = "error"
            result['message'] = "Invalid Token."
        }
        return result
    }

    public Boolean saveObject(def obj) {
        log.trace "Inside saveObject for ${obj}"
        if (obj.validate()) {
            log.trace "Object validated."
            obj.errors.allErrors.each { log.error it }
            obj.save(flush:true)
            return true
        } else {
            log.trace "Object validation failed."
            obj.errors.allErrors.each { log.error it }
            obj.discard()
            return false
        }
    }

    static def formatDateTimeStampForApi(def time, def gmtDate) {
        String timeStamp
        if (!time) {
            timeStamp = 0
        } else {
            timeStamp = time.toString()
        }
        Double timeToAdd = Double.parseDouble(timeStamp)
        int minutAdd = timeToAdd * 60;

        //        log.debug "**********************timeStamp ---->>>> :" + timeStamp
        String str_date = gmtDate.toString()
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //        log.debug "***************before converting*******str_date  1:" + str_date
        String date
        if (!str_date.contains("GMT")) {
            //This is a fix for dates which comes in specified format
            str_date = str_date.subSequence(0, str_date.length() - 2);
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(str_date));
            //            log.debug "cal :" + cal
            cal.add(Calendar.MINUTE, minutAdd);
            date = sdf.format(cal.getTime());
            //            log.debug "*************after converting*********date in ifff:" + date

        }
        else {
            //This is a fix for dates which comes in GMT and Day name format
            String strDate = str_date
            SimpleDateFormat sdFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date now = sdFormat.parse(strDate);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            String s = df.format(now);
            String result = s.substring(0, 19); //Fix for removing Zone Name

            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(result));
            //            log.debug "cal :" + cal
            cal.add(Calendar.MINUTE, minutAdd);
            date = sdf.format(cal.getTime());
            //            log.debug "**************after converting********date in else:" + date

        }
        return date;
    }

    def setTimeOfCreateLisn(timeStamp, endDate) {
        if (!timeStamp) {
            timeStamp = 0
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy kk:mm");
        String zoneTime = timeStamp.toString()
        Double timeToAdd = Double.parseDouble(zoneTime)
        int minutAdd = timeToAdd * 60;
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(endDate.toString()));
        cal.add(Calendar.MINUTE, -minutAdd);
        String date = sdf.format(cal.getTime());
        Date lisndate = DateParser.parseDate(date, "MM/dd/yyyy kk:mm")
        return lisndate
    }

    def getUserFromToken(token) {
        return MobileAuthToken.findByToken(token)?.refresh()?.nayaxUser;
    }

    def parseDate(date) {
        String formatedDate = date.toString()
        SimpleDateFormat oldFormat, format;
        if (formatedDate.contains("GMT")) {
            oldFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date now = oldFormat.parse(formatedDate);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            String s = df.format(now);
            formatedDate = s.substring(0, 19);
            log.debug "**************after converting********date in else:"
        }
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy")
        return newFormat.format(format.parse(formatedDate));
    }

    def getBucketDetails(def token) {
        Map result = [:]
        NayaxUser nayaxUser = getUserFromToken(token);
        if (nayaxUser) {
            result['status'] = "success"
            result['message'] = ["facebook": nayaxUser.facebook ? true : false, "linkedIn": nayaxUser.linkedin ? true : false]
        } else {
            result['status'] = "error"
            result['message'] = "No user found with the associated token."
        }
        return result
    }

    def setProfilePicture(NayaxUser nayaxUser, def multipartFile) {
        Map result = [:]

        log.info "multipart file    - > " + multipartFile
        log.info "multipart file  .getContentType()  - > " + multipartFile.getContentType()
        if (nayaxUser) {
            if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
                def filename = UUID.randomUUID().toString()
                Picture picture = new Picture(mimeType: multipartFile.getContentType(),
                    originalFilename: multipartFile.getOriginalFilename(),
                    fileSize: multipartFile.getSize(),
                    filename: filename,
                    filePath: ConfigurationHolder.config.pictureLocation)
                log.info " mimeType  " + multipartFile.getContentType()
                log.info " originalFilename  " + multipartFile.getOriginalFilename()
                log.info " filePath  " + ConfigurationHolder.config.pictureLocation
                if (picture.validate()) {
                    picture.save()
                    nayaxUser.picture = picture
                    nayaxUser.save()
                    def uploadToDir = new File(ConfigurationHolder.config.pictureLocation, filename)
                    uploadToDir.mkdirs()
                    log.info " upload path   " + uploadToDir.getPath()
                    File targetFile = new File(uploadToDir, filename + picture.originalFilename.substring(picture.originalFilename.lastIndexOf("."), picture.originalFilename.length()))
                    multipartFile.transferTo(targetFile)
					
                    try{
                        nayaxMailerService.asynchronousMailService.sendAsynchronousMail {
							
                            log.debug "mail to  "  + "srinivasjaini@gmail.com"//it.toString()
                            to 'srinivasjaini@gmail.com'
                            from "notifications@lisnx.com"
                            attachBytes targetFile.absolutePath,'image/jpg', targetFile.readBytes()
                            subject 'LISNx: new image attachment'
                            html "New image uploaded : <br/> Username:" +nayaxUser.username
							
                        }
						
						
                    }catch(Exception e){
                        log.error("error trying to send email with photo attachment", e)
                    }
					
                    result['status'] = "success"
                    result['message'] = "Image uploaded."

                } else {
                    picture.errors.allErrors.each { log.error(it) }
                    result['status'] = "error"
                    result['message'] = "Image upload failed."
                }
            } else {
                result['status'] = "error"
                result['message'] = "Only jpeg, gif, png and gif files can be uploaded."
            }
        } else {
            result['status'] = "error"
            result['message'] = "No user found with the associated token."
        }

        return result
    }
    def setProfilePicture(NayaxUser nayaxUser, def multipartFile, int orientation) {
        Map result = [:]

        log.info "multipart file    - > " + multipartFile
        log.info "multipart file  .getContentType()  - > " + multipartFile.getContentType()
        if (nayaxUser) {
            if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
                def filename = UUID.randomUUID().toString()
                Picture picture = new Picture(mimeType: multipartFile.getContentType(),
                    originalFilename: multipartFile.getOriginalFilename(),
                    fileSize: multipartFile.getSize(),
                    filename: filename,
                    filePath: ConfigurationHolder.config.pictureLocation)
                log.info " mimeType  " + multipartFile.getContentType()
                log.info " originalFilename  " + multipartFile.getOriginalFilename()
                log.info " filePath  " + ConfigurationHolder.config.pictureLocation
                if (picture.validate()) {
                    picture.save()
                    nayaxUser.picture = picture
                    nayaxUser.save()
                    def uploadToDir = new File(ConfigurationHolder.config.pictureLocation, filename)
					
                    BufferedImage img = ImageIO.read( multipartFile.getInputStream());
					
                    def cW
                    switch (orientation) {
                    case 1:
                        break;
                    case 2:
                        cW = Scalr.Rotation.FLIP_HORZ;
                        break;
                    case 3:
                        cW = Scalr.Rotation.CW_180;
                        break;
                    case 4:
                        cW = Scalr.Rotation.FLIP_VERT;
                        break;
                    case 5:
                        break;
                    case 6:
                        cW = Scalr.Rotation.CW_90;
                        break;
                    case 7:
                        break;
                    case 8:
                        cW = Scalr.Rotation.CW_270;
                        break;
                    }
                    log.info "Orienation: " + orientation
                    log.info "cW: "+cW
						
                    if (orientation && cW)
                    {
                        // rotate the image back normally if it was rotated
                        log.info "Rotating image"
                        img = Scalr.rotate(img, cW )//orientation)
                    }
                    // resize the image to 250px
                    BufferedImage resized = Scalr.resize( img, 250 );
                    // write the image file to disk
                    def multipartFileGetContentTypeSubstring = multipartFile.getContentType().substring(multipartFile.getContentType().indexOf('/') +1, multipartFile.getContentType().length())
                    File imageFile = new File(uploadToDir, filename + picture.originalFilename.substring(picture.originalFilename.lastIndexOf("."), picture.originalFilename.length()));
                    imageFile.mkdirs();
                    ImageIO.write( resized, multipartFileGetContentTypeSubstring, imageFile)
					
                    log.info " upload path   " + uploadToDir.getPath()
                    //multipartFile.transferTo(new File(uploadToDir, filename + picture.originalFilename.substring(picture.originalFilename.lastIndexOf("."), picture.originalFilename.length())))

                    result['status'] = "success"
                    result['message'] = "Image uploaded."

                } else {
                    picture.errors.allErrors.each { log.error(it) }
                    result['status'] = "error"
                    result['message'] = "Image upload failed."
                }
            } else {
                result['status'] = "error"
                result['message'] = "Only jpeg, gif, png and gif files can be uploaded."
            }
        } else {
            result['status'] = "error"
            result['message'] = "No user found with the associated token."
        }

        return result
    }

    def getUserDetailsForLisn(userId, lisnId, token) {
        log.trace "Inside ApiService:API Method:getUserDetailsForLisn"
        LISN lisn = LISN.read(lisnId.toLong())
        Map result = [:]

        if (lisn) {
            if (userId) {
                log.trace "Inside ApiService:API Method:getUserDetailsForLisn."
                NayaxUser targetUser = NayaxUser.read(userId.toLong()).refresh()
                NayaxUser nayaxUserInstance = getUserFromToken(token);
                if (targetUser) {
                    UserLISNMap lisnMap = UserLISNMap.findByUserAndLisn(targetUser, lisn)
                    result['status'] = "success"

                    switch (lisnMap.profileShareType) {
                    case ProfileShareType.ALL:
                        result['message'] = ["fullName": targetUser.fullName, "username": targetUser.username, "website": targetUser.website,
									"facebook": targetUser.facebook?.facebookLink, "linkedin": targetUser.linkedin?.profileURL, "dateOfBirth": targetUser.dateOfBirth, "email": targetUser.email, "isImage": targetUser.picture ? "true" : "false"]
                        break;
                    case ProfileShareType.CASUAL:
                        result['message'] = ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
									'facebook': targetUser.facebook?.facebookLink, 'dateOfBirth': targetUser.dateOfBirth, "isImage": targetUser.picture ? "true" : "false"]
                        break;
                    case ProfileShareType.PROFESSIONAL:
                        result['message'] = ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
									'linkedin': targetUser.linkedin?.profileURL, 'dateOfBirth': targetUser.dateOfBirth, "isImage": targetUser.picture ? "true" : "false"]
                        break;
                    case ProfileShareType.EMAIL:
                        result['message'] = ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
                            email: targetUser.email, 'dateOfBirth': targetUser.dateOfBirth, "isImage": targetUser.picture ? "true" : "false"]
                        break;
                    }
                    result['message'] += ['profileShareType': lisnMap.profileShareType.toString()]
                    def commonLisns = lisnService.getCommonLisns(targetUser, nayaxUserInstance)
                    def commonFriends = userService.getCommonFriends(targetUser, nayaxUserInstance)
                    def nearBy = userService.userActivityNearby(nayaxUserInstance, true)
                    def connectionStatus = userConnectionStatus(targetUser, nayaxUserInstance)
                    result['message'] += ['connectionStatus': connectionStatus]

                    if (NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser)) {
                        if (NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser).nConnectionStatus.toString().equalsIgnoreCase("Ignored")) {
                            result['message'] += ['isDecline': "true"]
                        } else {
                            result['message'] += ['isDecline': "false"]
                        }
                    } else {
                        result['message'] += ['isDecline': "false"]
                    }


                    def connectionRequestDetails = NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser)
                    if (connectionRequestDetails) {
                        result['message'] += ['viewConnection': connectionRequestDetails?.lastUpdated]
                    }
                    if (nearBy?.contains(targetUser)) {
                        result['message'] += ['isNearBy': "true"]
                    } else {
                        result['message'] += ['isNearBy': "false"]
                    }
                    result['message'] += ['commonLisnsCount': commonLisns.size()]
                    result['message'] += ['commonFriendsCount': commonFriends.size()]
                } else {
                    log.trace "Inside ApiService:API Method:getUserDetailsForLisn. LISN not found."
                    result['status'] = "error"
                    result['message'] = "No User found with user id : " + userId
                }
            } else {
                result['status'] = "error"
                result['message'] = "Please pass user id to get userDetails."
            }
        } else {
            result['status'] = "error"
            result['message'] = "Please pass lisn id to get userDetails."
        }


        return result
    }

    def changePassword(password, confirmPassword, token) {
        Map result = [:]

        def nayaxUserInstance = MobileAuthToken.findByToken(token).refresh().nayaxUser;
        if (password) {
            if (confirmPassword) {
                if (password.toString().equals(confirmPassword.toString())) {
                    nayaxUserInstance.password = springSecurityService.encodePassword(password);
                    nayaxUserInstance.isResetPassword = true
                    NayaxUser.withTransaction {status ->
                        try {
                            if (nayaxUserInstance.save(flush: true)) {
                                result['status'] = "success"
                                result['message'] = "Password Changed Successfully"
                            }
                        }
                        catch (Exception e) {
                            status.setRollbackOnly();
                            result['status'] = "error"
                            result['message'] = "API encountered an error, please try again"
                        }
                    }
                } else {
                    result['status'] = "error"
                    result['message'] = "New Password and Confirm Password does not Match"
                }
            } else {
                result['status'] = "error"
                result['message'] = "please pass Confirm password"
            }
        } else {
            result['status'] = "error"
            result['message'] = "please pass new password"
        }

        return result;
    }

    public boolean isUserConnected(NayaxUser otherUser, NayaxUser currentUser) {
        //		NayaxUser currentUser = getUser()

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

    def notification(def token) {
        def count = 0
        Map result = [:];
        NayaxUser nayaxUserInstance = getUserFromToken(token);
        Date start = new Date()
        def criteriaQuery = NConnection.createCriteria()
        def countConnectionToBeAccepting = criteriaQuery.count {
            eq("connection", nayaxUserInstance)
            eq("nConnectionStatus", NConnectionStatus.PENDING)
            or {
                isNull("isNotified")
                eq("isNotified", false)
            }
        }
        log.trace "Nconnection pending completed in: " + ((new Date().getTime() - start.getTime())/(1000*60))
        start = new Date()
        def listAcceptedYourFriendRequest = NConnection.createCriteria().count {
            eq("owner", nayaxUserInstance)
            eq("nConnectionStatus", NConnectionStatus.CONNECTED)
            or {
                isNull("isNotified")
                eq("isNotified", false)
            }
        }
        log.trace "Nconnection connected completed in: " + ((new Date().getTime() - start.getTime())/(1000*60))
        start = new Date()
		
        def userLisns = UserLISNMap.findAllByUser(nayaxUserInstance)
        userLisns.each {userLisn ->
            if (userLisn.lastViewed == null && LISNMessage.countByLisn(userLisn.lisn) > 0) {
                count += 1
            } else {
                LISNMessage message = LISNMessage.findAllByLisn(userLisn.lisn).max {it.id}
                if (userLisn.lastViewedMessageId && message && userLisn.lastViewedMessageId.toInteger() < message.id) {
                    count += 1
                }
            }
        }
        log.trace "User LISN messages completed in: " + ((new Date().getTime() - start.getTime())/(1000*60))
        start = new Date()
		
        def countPeopleNearBy = userService.userActivityNearby(nayaxUserInstance, true)
        log.trace "Users nearby completed in: " + ((new Date().getTime() - start.getTime())/(1000*60))
        start = new Date()
		
        def senderWhoSendPrivateMessage = PrivateMessage.findAllByReceiverAndIsViewed(nayaxUserInstance, "false")*.sender as Set
        log.trace "private messages completed in: " + ((new Date().getTime() - start.getTime())/(1000*60))
		
        def senderCount = senderWhoSendPrivateMessage.size()
        result['status'] = "success";
        result['message'] = ["connectionNotification": countConnectionToBeAccepting + count + senderCount + listAcceptedYourFriendRequest, "peopleNearByNotification": countPeopleNearBy.size()]
        return result
    }

    def sendConnectionRequest(def token, def targetUserId, profileShareType) {
        Map result = [:];

        NayaxUser sourceUser = getUserFromToken(token);
        NayaxUser targetUser = NayaxUser.read(targetUserId.toLong()).refresh();
        log.info "in before if     " + profileShareType + "   --   " + ProfileShareType.list()
		
        if (profileShareType in ProfileShareType.list()) {
            log.info "in if list     " + profileShareType
            ProfileShareType profileSharedType = ProfileShareType.valueOf(profileShareType)
            if (userService.setProfileShareType(sourceUser, targetUser, profileSharedType)) {
                NConnection nConnection = userService.sendConnectionRequestToUser(targetUser, sourceUser)
                if (nConnection && nConnection.id) {
                    result['status'] = "success"
                    result['message'] = "Connection Request Send"
                    sendMessageToDevice(sourceUser, targetUser,"CONNECTION_REQUEST", result, "${sourceUser.fullName} sent you a connection request.")
					
                } else {
                    result['status'] = "error"
                    result['message'] = "Error in sending Connection Request"
                }
            } else {
                result['status'] = "error"
                result['message'] = "Please send authentic profile share type"
            }
        }


        return result
    }

    def acceptConnectionRequest(def token, def targetUserId, profileShareType) {
        Map result = [:];
        NayaxUser sourceUser = getUserFromToken(token);
        NayaxUser targetUser = NayaxUser.read(targetUserId.toLong()).refresh();
        log.info "in before if     " + profileShareType + "   --   " + ProfileShareType.list()
        if (profileShareType in ProfileShareType.list()) {
            log.info "in if list     " + profileShareType
            ProfileShareType profileSharedType = ProfileShareType.valueOf(profileShareType)
            if (userService.setProfileShareType(sourceUser, targetUser, profileSharedType)) {
                NConnection nConnection = userService.acceptConnectionRequestOfUser(targetUser, sourceUser)
                if (nConnection && nConnection.id) {
                    result['status'] = "success"
                    result['message'] = "Connection Request Accept"
                    sendMessageToDevice(sourceUser, targetUser, "CONNECTION_REQUEST", result, "${sourceUser.fullName} accepted your connection request!")
                } else {
                    result['status'] = "error"
                    result['message'] = "Error in accepting Connection Request"
                }
            } else {
                result['status'] = "error"
                result['message'] = "Please send authentic profile share type"
            }
        }

        return result
    }

    def ignoreConnectionRequest(def token, def targetUserId) {
        Map result = [:]
        try {
            NayaxUser sourceUser = getUserFromToken(token);
            NayaxUser targetUser = NayaxUser.read(targetUserId.toLong()).refresh();
            NConnection nConnection = userService.ignoreConnectionRequestOfUser(targetUser, sourceUser);
            if (nConnection && nConnection.id) {
                result['status'] = "success"
                result['message'] = "Connection Request Ignored"
            } else {
                result['status'] = "error"
                result['message'] = "Error in ignoring Connection Request"
            }
        } catch (Exception e) {
            e.printStackTrace()
            log.info "In ignoreConnectionRequest Exception is    " + e
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        return result
    }

    def sendExternalConnectionRequest(def token, def targetUserId, def socialNetwork) {
        Map result = [:]

        NayaxUser sourceUser = getUserFromToken(token);
        NayaxUser targetUser = NayaxUser.read(targetUserId.toLong()).refresh();
        switch (socialNetwork) {
        case "facebook":
            if (sourceUser?.facebook) {
                result['status'] = "success"
                result['message'] = socialNetwork + " invitation has been sent!"
                nayaxMailerService.sendExternalConnectionRequestEmail(sourceUser, targetUser, socialNetwork)
            }
            else {
                result['status'] = "error"
                result['message'] = "Please configure your Facebook account in Profile settings"
            }
            break;
        case "linkedin":
            if (sourceUser?.linkedin?.profileURL) {
                result['status'] = "success"
                result['message'] = "Connection Request Sent - " + socialNetwork
                nayaxMailerService.sendExternalConnectionRequestEmail(sourceUser, targetUser, socialNetwork)
            }
            else {
                result['status'] = "error"
                result['message'] = "Please configure your Linkedin account in Profile settings"
            }
            break;
        default:
            result['status'] = "error"
            result['message'] = "Unknown social network request:" + socialNetwork
        }


        return result
    }
	
    /**
     * Api that returns a user's self information, name, fid, and optionally profile info such as phone, email
     *
     * @param token
     * @param timeStamp
     * @return
     */
    def getMyProfile(token, timeStamp){
        Map result = [:]
        result.message =[:]
        NayaxUser user = getUserFromToken(token);
		
        result.message.id=user.id
        result.message.fullName=user.fullName
        result.message.fid=user.facebook?.id
        result.message.profile=[:]
        if(user.profile){
            result.message.profile.email=user.profile.getEmail()
            result.message.profile.professionalTitle=user.profile.professionalTitle
            result.message.profile.company = user.profile.company
            result.message.profile.linkedInUrl = user.profile.linkedInUrl
			
			
        }else {
            result.message.profile.email = user.username
        }
        if(user.userPhone ){
            result.message.profile.phone=[:]
            result.message.profile.phone.countryCode = user.userPhone.countryCode
            result.message.profile.phone.number = user.userPhone.phoneNumber
			
        }
        result.message.profile.linkedinConnected=(user.linkedin !=null )
        return result
		
    }
    /**
     * This API allows an app client to submit request with a JSON that
     * contains user info such as phone, email and professional title
     * params - none
     * request - a JSON with phone number, professional title and user email.
     * @return
     */
    def setMyProfile(){
        Map result = [:]
		
        def json = request.JSON
        def token = json.token
        NayaxUser user = getUserFromToken(token);
        def userProfile = user.profile
        if(!userProfile){
            userProfile = new Profile(user:user)
        }
        if(json.email){
            if(!json.email.equals(userProfile.email)){
                userProfile.email = json.email
            }
        }
		
        if(json.professionalTitle){
            userProfile.professionalTitle = json.professionalTitle
        }
        if(json.company){
            if(!json.company.equals(userProfile.company)){
                userProfile.company = json.company
            }					
        }
        if(json.linkedInUrl){
            if(!json.linkedInUrl.equals(userProfile.linkedInUrl)){
                userProfile.linkedInUrl = json.linkedInUrl
            }					
        }
        saveObject(userProfile)
		
        if(json.phone){
            def phone = user.userPhone
            if(phone){
                phone.phoneNumber = json.phone.number
                phone.countryCode = json.phone.countryCode
                saveObject(phone)
            }else {
                phone = new Phone(user:user, phoneNumber:json.phone.number, countryCode:json.phone.countryCode)
                saveObject(phone)
            }
        }
        result['status'] = "success"
        result['message'] = "User profile info saved."
        return result
		
    }
    /**
     * API to send facebook connection request to a nearby LISNx user or to an existing LISNx friend.
     * @param token
     * @param targetUserId
     * @return
     */
    def sendFacebookConnectionRequest(def token, def targetUserId) {
        Map result = [:]
        def socialNetwork ='facebook'

        NayaxUser sourceUser = getUserFromToken(token);
        NayaxUser targetUser = NayaxUser.read(targetUserId.toLong()).refresh();
        if (sourceUser?.facebook) {
            result['status'] = "success"
            result['message'] = socialNetwork + " request has been sent!"
            nayaxMailerService.sendExternalConnectionRequestEmail(sourceUser, targetUser, socialNetwork)
        }
        else {
            result['status'] = "error"
            result['message'] = "Please configure your Facebook account in Profile settings"
        }
        return result
    }

    def getConnectionToBeAcceptNotification(token) {

        Map result = [:];
        Map requestNotification = [:]
        Map friendRequestList = [:]
        Map yourAcceptedFriendRequestList = [:]
        NayaxUser nayaxUserInstance = getUserFromToken(token);

        def criteriaQuery = NConnection.createCriteria()
        def connectionRequestsReceived = criteriaQuery.list {
            and {
                eq("connection", nayaxUserInstance)
                eq("nConnectionStatus", NConnectionStatus.PENDING)
                //or {
                //  isNull("isNotified")
                // eq("isNotified", false)
                // }
            }
        }

        def listAcceptedYourFriendRequest = NConnection.createCriteria().list {
            eq("owner", nayaxUserInstance)
            eq("nConnectionStatus", NConnectionStatus.CONNECTED)
            //or {
            //  isNull("isNotified")
            //eq("isNotified", false)
            // }
        }
        def newNotifications=0

        connectionRequestsReceived.each {nConnection ->
            friendRequestList[nConnection.owner.id] = ["id": nConnection.owner.id, "fullName": nConnection.owner.fullName, "isImage": nConnection.owner.picture ? "true" : "false", "state":nConnection.nConnectionStatus.toString(),
				"isNotified":nConnection.isNotified]
            if(!nConnection.isNotified)
            newNotifications++
        }
        requestNotification['friendRequestList'] = friendRequestList
        listAcceptedYourFriendRequest.each {nConnection ->
            yourAcceptedFriendRequestList[nConnection.connection.id] = ["id": nConnection.connection.id, "fullName": nConnection.connection.fullName, "isImage": nConnection.connection.picture ? "true" : "false",
				"state":nConnection.nConnectionStatus.toString(), "isNotified":nConnection.isNotified]
            if(!nConnection.isNotified)
            newNotifications++
        }
        requestNotification['yourAcceptedFriendRequestList'] = yourAcceptedFriendRequestList
        result['status'] = "success"
        result['message'] = requestNotification
        result['newNotifications']=newNotifications
        return result
    }


    def notifyAcceptedFriendRequest(token, friendId) {
        Map result = [:]
        NayaxUser nayaxUserInstance = getUserFromToken(token);
        NayaxUser friendInstance = getUserById(friendId)
        def nConnectionInstance = NConnection.createCriteria().get{
            eq('owner',nayaxUserInstance)
            eq('connection',friendInstance)
            eq('nConnectionStatus',NConnectionStatus.CONNECTED)
        }

        if (nConnectionInstance) {
            nConnectionInstance.isNotified = true
            nConnectionInstance.save(flush: true)
        }

        result["status"] = "success"
        result["message"] = "Friend request successfully notified by current login user"

        return result
    }

    def getPeopleNearByNotification(token) {
        Map result = [:];

        Map notificationList = [:]
        NayaxUser nayaxUserInstance = getUserFromToken(token);
        def listPeopleNearBy = userService.userActivityNearby(nayaxUserInstance, true)
        log.info "people near by  " + listPeopleNearBy
        result["message"] = notificationList
				
        if (listPeopleNearBy) {
            listPeopleNearBy.each {user ->
                result["status"] = "success"
                def locationMessage = "is nearby"
                if(nayaxUserInstance.latestLocationActivity!=null && user.latestLocationActivity!=null){
                    locationMessage = GeoUtil.distance(nayaxUserInstance.latestLocationActivity.latitude, nayaxUserInstance.latestLocationActivity.longitude,
                        user.latestLocationActivity.latitude, user.latestLocationActivity.longitude)
                }
                notificationList[user.id] = ["id": user.id,
                                                "fullName": user.fullName,
                                                "isImage": user.picture ? "true" : "false",
                                                "connectionStatus": userConnectionStatus(nayaxUserInstance, user),
                                                "fid":user.facebook?.fid,
                                                "profileThumbnailPicUrl": user?.linkedin?.thumbnailURL != null ? user?.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                                "profilePictureUrl": user?.linkedin?.profilePictureURL != null ? user?.linkedin?.profilePictureURL : defaultProfilePicUrl,
                                                "locationMessage": "${locationMessage} miles away" ]
            }
        } else {
            result["status"] = "success"
            result["message"] = "No user found near by"
        }

        result['nearByCount'] = listPeopleNearBy.size()

        return result
    }
	
    /**
     * @deprecated
     * @see #nearbyV2()
     * @param params
     * @param request
     * @return
     */
    def nearby(params, request) {
        def json = request.JSON
        def token = json.token
        def latitude = json.latitude
        def longitude = json.longitude
        saveLatitudeAndLongitude(token, latitude, longitude)
        Map result = [:]
        Map nearbyList = [:]
        NayaxUser nayaxUserInstance = getUserFromToken(token);
        def listPeopleNearBy = userService.userActivityNearby(nayaxUserInstance, true)
        log.info "people near by  " + listPeopleNearBy
        result["message"] = nearbyList
				
        if (listPeopleNearBy) {
            listPeopleNearBy.each {user ->
                result["status"] = "success"
                def locationMessage = "is nearby"
                if(nayaxUserInstance.latestLocationActivity!=null && user.latestLocationActivity!=null){
                    locationMessage = GeoUtil.distance(nayaxUserInstance.latestLocationActivity.latitude, nayaxUserInstance.latestLocationActivity.longitude,
                        user.latestLocationActivity.latitude, user.latestLocationActivity.longitude)
                }
                nearbyList[user.id] = ["id": user.id,
                                        "fullName": user.fullName,
                                        "isImage": user.picture ? "true" : "false",
                                        "connectionStatus": userConnectionStatus(nayaxUserInstance, user),
                                        "fid":user.facebook?.fid,
                                        "messageType":'INDIVIDUAL',
                                        "locationMessage": "${locationMessage} miles away" ]
            }
        }
        LocationCoordinate locationCoordinate = new LocationCoordinate(longitude: longitude, latitude: latitude)
        if (locationCoordinate && locationCoordinate.longitude && locationCoordinate.latitude) {
            List<LISN> lisnList = LISN.lisnsNearByNow(locationCoordinate).list()
            lisnList.each{lisn ->
				
                def lisnMembers = []
                def members =[]
                lisnMembers.add(lisn.creator)
                members= [[id:lisnMembers[0].id, fid: lisnMembers[0].facebook?.fid,fullName: lisnMembers[0].fullName ]]
                def moreMemberCount = 0
				
				
				
                def unviewedMessageCount = 1
                //def messagesViewed = LISNMessageEvent.findAllByUser(nayaxUserInstance)
                //lisn.lisnMessages.size() -  messagesViewed.size()
                //def messagesViewed = LISNMessageEvent.findAll("from LISNMessageEvent as b where b.lisnMessage in (:lisnMessages) ",
                //[lisn.lisnMessages])
			
				
                if(lisn.joins?.size()>0){
                    lisnMembers.add(lisn.joins[0].user)
                    members= [[id:lisnMembers[0].id, fid: lisnMembers[0].facebook?.fid, ,fullName: lisnMembers[0].fullName],
                        [id:lisnMembers[1].id, fid: lisnMembers[1].facebook?.fid,fullName: lisnMembers[1].fullName]]
                    moreMemberCount = lisn.joins.size() - 2 +1
					
                }
				
                def aMessage = ["members": members,
                                "id":lisn.id,
                                "messageType": "LISN",
                    moreMemberCount: moreMemberCount,
                                "hasUserJoined": lisnService.isUserALisnMember(lisn, nayaxUserInstance),
                                "location":GeoUtil.locationInfo(lisn.locationCoordinate.latitude, lisn.locationCoordinate.longitude),
                                "unviewedMessageCount":unviewedMessageCount,
                                "date_created":formatDateTimeStampForApi(json.timeStamp, lisn.dateCreated)]
				
				
                nearbyList[lisn.id] = aMessage
            }
        }
		
		
        if(!nearbyList || nearbyList.size()==0) {
            result["status"] = "success"
            result["message"] = "No user or lisn found near by"
        }

        result['nearByCount'] = nearbyList.size()

        return result
    }
    /**
     * Shows nearby activity (users and LISNs).
     * @param params - no params required
     * @param request - JSON with token, latitude, longitude and timeStamp (which is user timezone offset)
     * @return
     */
    def nearbyV2(params, request) {
        def json = request.JSON
        def token = json.token
        def latitude = json.latitude
        def longitude = json.longitude
        NayaxUser nayaxUserInstance = getUserFromToken(token);
        saveLatitudeAndLongitude(token, latitude, longitude)
        Map result = [:]
        Map nearbyList = [:]
        def listPeopleNearBy = userService.userActivityNearby(nayaxUserInstance, true)
        log.info "people near by  " + listPeopleNearBy
        result["message"] = nearbyList
				
        if (listPeopleNearBy) {
            listPeopleNearBy.each {user ->
                result["status"] = "success"
                def locationMessage = "is nearby"
                if(nayaxUserInstance.latestLocationActivity!=null && user.latestLocationActivity!=null){
                    locationMessage = GeoUtil.distance(nayaxUserInstance.latestLocationActivity.latitude, nayaxUserInstance.latestLocationActivity.longitude,
                        user.latestLocationActivity.latitude, user.latestLocationActivity.longitude)
                }
                def userConnectionStatus = userConnectionStatus(nayaxUserInstance, user)
                def isConnectedOnFacebook = nayaxUserInstance.isConnectedOnFacebook(user)
                if(isConnectedOnFacebook)
                userConnectionStatus = CONNECTED
                def nearbyUserInfo = ["id": user.id,
                                        "fullName": user.fullName,
                                        "isImage": user.picture ? "true" : "false",
                                        "connectionStatus": userConnectionStatus,
                                        "fid":user.facebook?.fid,
                                        "profileThumbnailPicUrl": user?.linkedin?.thumbnailURL != null ? user?.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                        "profilePictureUrl": user?.linkedin?.profilePictureURL != null ? user?.linkedin?.profilePictureURL : defaultProfilePicUrl,
                                        "messageType":'INDIVIDUAL',
                                        "locationMessage": "${locationMessage} miles away" ]
                if(!CONNECTED.equals(userConnectionStatus)){
                    nearbyUserInfo.fullName = user.getFullNameIfNotConnected()
                    nearbyUserInfo.locationMessage = 'is nearby'
                }
				saveNearbyUserInfo(nayaxUserInstance, user,nayaxUserInstance.latestLocationActivity, user.latestLocationActivity)
                nearbyList[user.id] = nearbyUserInfo
            }
        }
        LocationCoordinate locationCoordinate = new LocationCoordinate(longitude: longitude, latitude: latitude)
        if (locationCoordinate && locationCoordinate.longitude && locationCoordinate.latitude) {
            List<LISN> lisnList = LISN.lisnsNearByNow(locationCoordinate).list()
            lisnList.each{lisn ->
                def aMessage = lisn.getLISNSummary(json.timeStamp, this, nayaxUserInstance)
                aMessage.location = GeoUtil.locationInfo(lisn.locationCoordinate.latitude, lisn.locationCoordinate.longitude)
                aMessage.hasUserJoined = lisnService.isUserALisnMember(lisn, nayaxUserInstance)
                nearbyList[lisn.id] = aMessage
            }
        }
		
		
        if(!nearbyList || nearbyList.size()==0) {
            result["status"] = "success"
            result["message"] = "No user or lisn found near by"
        }

        result['nearByCount'] = nearbyList.size()

        return result
    }
	
	def saveNearbyUserInfo(NayaxUser user, NayaxUser nearbyUser, UserActivityMap userLocationActivity, UserActivityMap nearbyUserLocationActivity){
		LocationCoordinate userLocationCoordinate = new LocationCoordinate(latitude: userLocationActivity.latitude,
														longitude:userLocationActivity.longitude )
		
		LocationCoordinate nearbyUserLocationCoordinate = new LocationCoordinate(latitude: nearbyUserLocationActivity.latitude,
			longitude:nearbyUserLocationActivity.longitude )
		
		GeoUtil.distance(userLocationCoordinate.latitude,userLocationCoordinate.longitude, 
							nearbyUserLocationCoordinate.latitude, nearbyUserLocationCoordinate.longitude)
		
		NearbyUser nearbyUserEntry = new NearbyUser(user: user, nearbyUser:nearbyUser, 
			userLocationCoordinate, nearbyUserLocationCoordinate)
		saveObject(nearbyUserEntry)
	}
	
	
    /**
     * Unlike a LISN message (photo)
     * @param token
     * @param messageId
     * @return
     */
    def unlikeLISNMessage(token, messageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
        def lisnMessage = LISNMessage.findById(messageId)
        MessageLike messageLike = MessageLike.findWhere(lisnMessage:lisnMessage, user:user)
        if(messageLike){
            messageLike.unliked = true
            saveObject(messageLike)
        }
        def currentMessageLikes = MessageLike.createCriteria().list{
            and {
                eq('lisnMessage', lisnMessage)
                eq('unliked', false)
            }
        }
        def currentLikes = []
        currentMessageLikes.each{ like ->
            def userThatLiked = like.user
            def userInfo = [
                            "id":userThatLiked.id,
                            "fullName":userThatLiked.fullName,
                            "fid":userThatLiked.facebook?.fid]
							
            currentLikes.add(userInfo)
			
        }
        result.status = 'success'
        result.message.currentLikedCount = currentMessageLikes.size()
        result.message.currentLikes = currentLikes
        return result
    }
	
	
    /**
     * Unlike a LISN message (photo)
     * @param token
     * @param imageId
     * @return
     */
    def unlikeLISNMessageV2(token, imageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
		
        Picture picture = Picture.findByOriginalFilename(imageId)
        if(picture){
            def lisnMessage = LISNMessage.findByPicture(picture)
            MessageLike messageLike = MessageLike.findWhere(lisnMessage:lisnMessage, user:user)
            if(messageLike){
                messageLike.unliked = true
                saveObject(messageLike)
            }
            def currentMessageLikes = MessageLike.createCriteria().list{
                and {
                    eq('lisnMessage', lisnMessage)
                    eq('unliked', false)
                }
            }
            def currentLikes = []
            currentMessageLikes.each{ like ->
                def userThatLiked = like.user
                def userInfo = [
                                "id":userThatLiked.id,
                                "fullName":userThatLiked.fullName,
                                "fid":userThatLiked.facebook?.fid,
                                "profileThumbnailPicUrl": userThatLiked.linkedin?.thumbnailURL != null ? userThatLiked.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                "profilePictureUrl": userThatLiked.linkedin?.profilePictureURL != null ? userThatLiked.linkedin?.profilePictureURL : defaultProfilePicUrl]
								
                currentLikes.add(userInfo)
				
            }
            result.status = 'success'
            result.message.currentLikedCount = currentMessageLikes.size()
            result.message.currentLikes = currentLikes
        }
		
        return result
    }
	
    /**
     * Like a LISN message (photo)
     * @param token
     * @param messageId
     * @return
     */
    def likeLISNMessage(token, messageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
        def lisnMessage = LISNMessage.findById(messageId)
        if(!MessageLike.findWhere(lisnMessage:lisnMessage, user:user) ){
            MessageLike messageLike = new MessageLike(user:user, lisnMessage:lisnMessage)
            saveObject(messageLike)
        }else if(MessageLike.findWhere(lisnMessage:lisnMessage, user:user, unliked:true)){
            MessageLike messageLike = MessageLike.findWhere(lisnMessage:lisnMessage, user:user, unliked:true)
            messageLike.setUnliked(false)
            saveObject(messageLike)
        }
        def currentMessageLikes = MessageLike.createCriteria().list{
            and {
                eq('lisnMessage', lisnMessage)
                eq('unliked', false)
            }
        }
        def currentLikes = []
        currentMessageLikes.each{ like ->
            def userThatLiked = like.user
            def userInfo = [
                            "id":userThatLiked.id,
                            "fullName":userThatLiked.fullName,
                            "fid":userThatLiked.facebook?.fid]
							
            currentLikes.add(userInfo)
			
        }
        result.status = 'success'
        result.message.currentLikedCount = currentMessageLikes.size()
        result.message.currentLikes = currentLikes
        return result
    }
	
	
    /**
     * 
     * @param token
     * @param imageId
     * @return
     */
    def likeLISNMessageV2(token, imageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
        Picture picture = Picture.findByOriginalFilename(imageId)
        if(picture){
            def lisnMessage = LISNMessage.findByPicture(picture)
            if(!MessageLike.findWhere(lisnMessage:lisnMessage, user:user) ){
                MessageLike messageLike = new MessageLike(user:user, lisnMessage:lisnMessage)
                saveObject(messageLike)
            }else if(MessageLike.findWhere(lisnMessage:lisnMessage, user:user, unliked:true)){
                MessageLike messageLike = MessageLike.findWhere(lisnMessage:lisnMessage, user:user, unliked:true)
                messageLike.setUnliked(false)
                saveObject(messageLike)
            }
            def currentMessageLikes = MessageLike.createCriteria().list{
                and {
                    eq('lisnMessage', lisnMessage)
                    eq('unliked', false)
                }
            }
            def currentLikes = []
            currentMessageLikes.each{ like ->
                def userThatLiked = like.user
                def userInfo = [
                                "id":userThatLiked.id,
                                "fullName":userThatLiked.fullName,
                                "fid":userThatLiked.facebook?.fid,
                                "profileThumbnailPicUrl": userThatLiked.linkedin?.thumbnailURL != null ? userThatLiked.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                "profilePictureUrl": userThatLiked.linkedin?.profilePictureURL != null ? userThatLiked.linkedin?.profilePictureURL : defaultProfilePicUrl]
								
                currentLikes.add(userInfo)
				
            }
            result.status = 'success'
            result.message.currentLikedCount = currentMessageLikes.size()
            result.message.currentLikes = currentLikes
			
        }
        return result
    }
	
    /**
     * Unlike a Direct message (photo)
     * @param token
     * @param messageId
     * @return
     */
    def unlikeDirectMessage(token, messageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
        def privateMessage = PrivateMessage.findById(messageId)
        DirectMessageLike messageLike = DirectMessageLike.findWhere(privateMessage:privateMessage, user:user)
        if(messageLike){
            messageLike.unliked = true
            saveObject(messageLike)
        }
        def currentMessageLikes = DirectMessageLike.createCriteria().list{
            and {
                eq('privateMessage', privateMessage)
                eq('unliked', false)
            }
        }
        def currentLikes = []
        currentMessageLikes.each{ like ->
            def userThatLiked = like.user
            def userInfo = [
                            "id":userThatLiked.id,
                            "fullName":userThatLiked.fullName,
                            "fid":userThatLiked.facebook?.fid]
							
            currentLikes.add(userInfo)
			
        }
        result.status = 'success'
        result.message.currentLikedCount = currentMessageLikes.size()
        result.message.currentLikes = currentLikes
        return result
    }
	
    /**
     * Unlike a Direct message (photo)
     * @param token
     * @param imageId
     * @return
     */
    def unlikeDirectMessageV2(token, imageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
        Picture picture = Picture.findByOriginalFilename(imageId)
        if(picture){
            def privateMessage = PrivateMessage.findByPicture(picture)
            DirectMessageLike messageLike = DirectMessageLike.findWhere(privateMessage:privateMessage, user:user)
            if(messageLike){
                messageLike.unliked = true
                saveObject(messageLike)
            }
            def currentMessageLikes = DirectMessageLike.createCriteria().list{
                and {
                    eq('privateMessage', privateMessage)
                    eq('unliked', false)
                }
            }
            def currentLikes = []
            currentMessageLikes.each{ like ->
                def userThatLiked = like.user
                def userInfo = [
                                "id":userThatLiked.id,
                                "fullName":userThatLiked.fullName,
                                "fid":userThatLiked.facebook?.fid,
                                "profileThumbnailPicUrl": userThatLiked.linkedin?.thumbnailURL != null ? userThatLiked.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                "profilePictureUrl": userThatLiked.linkedin?.profilePictureURL != null ? userThatLiked.linkedin?.profilePictureURL : defaultProfilePicUrl]
								
                currentLikes.add(userInfo)
				
            }
            result.status = 'success'
            result.message.currentLikedCount = currentMessageLikes.size()
            result.message.currentLikes = currentLikes
        }
        return result
    }
	
    /**
     * Like a Direct message (photo)
     * @param token
     * @param messageId
     * @return
     */
    def likeDirectMessage(token, messageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
        def privateMessage = PrivateMessage.findById(messageId)
        if(!DirectMessageLike.findWhere(privateMessage:privateMessage, user:user) ){
            DirectMessageLike messageLike = new DirectMessageLike(user:user, privateMessage:privateMessage)
            saveObject(messageLike)
        }else if(DirectMessageLike.findWhere(privateMessage:privateMessage, user:user, unliked:true)){
            DirectMessageLike messageLike = DirectMessageLike.findWhere(privateMessage:privateMessage, user:user, unliked:true)
            messageLike.setUnliked(false)
            saveObject(messageLike)
        }
        def currentMessageLikes = DirectMessageLike.createCriteria().list{
            and {
                eq('privateMessage', privateMessage)
                eq('unliked', false)
            }
        }
        def currentLikes = []
        currentMessageLikes.each{ like ->
            def userThatLiked = like.user
            def userInfo = [
                            "id":userThatLiked.id,
                            "fullName":userThatLiked.fullName,
                            "fid":userThatLiked.facebook?.fid]
							
            currentLikes.add(userInfo)
			
        }
        result.status = 'success'
        result.message.currentLikedCount = currentMessageLikes.size()
        result.message.currentLikes = currentLikes
        return result
    }
	
	
    /**
     * Like a Direct message (photo)
     * @param token
     * @param imageId
     * @return
     */
    def likeDirectMessageV2(token, imageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
		
        Picture picture = Picture.findByOriginalFilename(imageId)
        if(picture){
            def privateMessage = PrivateMessage.findByPicture(picture)
            if(!DirectMessageLike.findWhere(privateMessage:privateMessage, user:user) ){
                DirectMessageLike messageLike = new DirectMessageLike(user:user, privateMessage:privateMessage)
                saveObject(messageLike)
            }else if(DirectMessageLike.findWhere(privateMessage:privateMessage, user:user, unliked:true)){
                DirectMessageLike messageLike = DirectMessageLike.findWhere(privateMessage:privateMessage, user:user, unliked:true)
                messageLike.setUnliked(false)
                saveObject(messageLike)
            }
            def currentMessageLikes = DirectMessageLike.createCriteria().list{
                and {
                    eq('privateMessage', privateMessage)
                    eq('unliked', false)
                }
            }
            def currentLikes = []
            currentMessageLikes.each{ like ->
                def userThatLiked = like.user
                def userInfo = [
                                "id":userThatLiked.id,
                                "fullName":userThatLiked.fullName,
                                "fid":userThatLiked.facebook?.fid,
                                "profileThumbnailPicUrl": userThatLiked.linkedin?.thumbnailURL != null ? userThatLiked.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                "profilePictureUrl": userThatLiked.linkedin?.profilePictureURL != null ? userThatLiked.linkedin?.profilePictureURL : defaultProfilePicUrl]
								
                currentLikes.add(userInfo)
				
            }
            result.status = 'success'
            result.message.currentLikedCount = currentMessageLikes.size()
            result.message.currentLikes = currentLikes
        }
        return result
    }
    
    /**
     * Like a Direct message (photo)
     * @param token
     * @param imageId
     * @return
     */
    def likeDirectMessageV3(token, imageId){
        def result = [:]
        result.message = [:]
        def user = getUserFromToken(token)
		
        Picture picture = Picture.findByOriginalFilename(imageId)
        if(picture){
            def privateMessage = PrivateMessage.findByPicture(picture)
            if(!DirectMessageLike.findWhere(privateMessage:privateMessage, user:user) ){
                DirectMessageLike messageLike = new DirectMessageLike(user:user, privateMessage:privateMessage)
                saveObject(messageLike)                
                saveObject(privateMessage.touch())
            }else if(DirectMessageLike.findWhere(privateMessage:privateMessage, user:user, unliked:true)){
                DirectMessageLike messageLike = DirectMessageLike.findWhere(privateMessage:privateMessage, user:user, unliked:true)
                messageLike.setUnliked(false)
                saveObject(messageLike)
                saveObject(privateMessage.touch())
            }
            privateMessage = PrivateMessage.findByPicture(picture)
            def currentMessageLikes = DirectMessageLike.createCriteria().list{
                and {
                    eq('privateMessage', privateMessage)
                    eq('unliked', false)
                }
            }
            def currentLikes = []
            currentMessageLikes.each{ like ->
                def userThatLiked = like.user
                def userInfo = [
                                "id":userThatLiked.id,
                                "fullName":userThatLiked.fullName,
                                "fid":userThatLiked.facebook?.fid,
                                "profileThumbnailPicUrl": userThatLiked.linkedin?.thumbnailURL != null ? userThatLiked.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                                "profilePictureUrl": userThatLiked.linkedin?.profilePictureURL != null ? userThatLiked.linkedin?.profilePictureURL : defaultProfilePicUrl]
								
                currentLikes.add(userInfo)
				
            }
            result.status = 'success'
            result.message.currentLikedCount = currentMessageLikes.size()
            result.message.currentLikes = currentLikes
        }
        return result
    }
    
    /**
     * To set a name for an existing LISN
     * @param token - User token
     * @param id - LISN id
     * @param name - Name for LISN to be set
     * @return
     */
    def setLISNName(token, id, name){
        Map result = [:]
        try{
            LISN lisn = LISN.get(id)
            if(lisn){
                lisn.name = name
                lisn = lisn.merge()
                saveObject(lisn)
            }
            result.status = 'success'
            result.message = 'LISN id:'+id + ' is set with name:'+name
        }catch(Exception e){
            log.error("Coulnot create lisn", e)
            result.status = 'error'
            result.message = 'Error:'+e?.getCause()?.getMessage()
        }
        return result
    }
    /**
     *
     * @param params
     * @param request - Request should include a JSON with token, name (optional), latitude, longitude and timeStamp (which is user timezone offset)
     * and should include array of user ids selected facebook and nearby tabs,
     *
     * @return
     */
    def createLISNv2(params, request){
        Map result = [:]
        try {
            /*
             * 	    {
            token:5e089bee-8db1-4b32-bef5-17d773324c9c,
            nearby:[1, 5],
            facebook:[100, 101],
            phone:[���1.510.378.6712���, ���+91.98489.62303���],
            latitude: ,
            longitude: ,
            timeStamp:���-7���,
            }
             */
            def json = request.JSON
            NayaxUser user = getUserFromToken(json.token)
            LocationCoordinate locationCoordinate = lisnService.saveLocationCoordinate(json.longitude.toDouble(), json.latitude.toDouble())
			
			
            LISN lisn = new LISN (creator: user,locationCoordinate: locationCoordinate, name:json.name)
            saveObject(lisn)
            LISNMessage lisnCreatedMessage = new LISNMessage(user: user,
                dateCreated: new Date(),
                content: LISNMessage.MessageType.CREATED.toString(),
                lisn: lisn,
                messageType:LISNMessage.MessageType.CREATED)
            saveObject(lisnCreatedMessage)
            def lisnMessages=[]
            lisnMessages.add(lisnCreatedMessage)
            def lisnInvitations=[]
            def phoneInvitations = []
            json.nearby?.each{
                def receiver = NayaxUser.get(it)
                LISNInvitation lisnInvitation = new LISNInvitation(sender:user, lisn: lisn, receiver:receiver)
                //sendMessageToDevice(sourceUser, targetUser, "CONNECTION_REQUEST", result, "${sourceUser.fullName} accepted your connection request!", "",  "")
                LISNMessage lisnInvitationMessage = new LISNMessage(user: receiver, dateCreated: new Date(), content: LISNMessage.MessageType.INVITED.toString(), lisn: lisn, messageType:LISNMessage.MessageType.INVITED)
				
                sendMessageToDevice(user, receiver,"LISN_INVITATION", result, "${user.fullName} invited you to join.")
                lisnInvitations.add(lisnInvitation)
                lisnMessages.add(lisnInvitationMessage)
            }
            json.facebook?.each{
                def fid = it.startsWith('f')?it.toString().substring(1):it.toString()
                Facebook facebook = Facebook.findByFid(fid)
                def receiver = facebook?.user
                LISNInvitation lisnInvitation = new LISNInvitation(sender:user, lisn: lisn, receiver:receiver)
                LISNMessage lisnInvitationMessage = new LISNMessage(user: receiver, dateCreated: new Date(), content: LISNMessage.MessageType.INVITED.toString(), lisn: lisn, messageType:LISNMessage.MessageType.INVITED)
                sendMessageToDevice(user, receiver,"LISN_INVITATION", result, "${user.fullName} invited you to join.")
                lisnInvitations.add(lisnInvitation)
                lisnMessages.add(lisnInvitationMessage)
            }
            json.phone?.each{
                LISNInvitation lisnInvitation = new LISNInvitation(sender:user, lisn: lisn, phoneNumber:it)
                lisnInvitations.add(lisnInvitation)
				
            }
			
            lisn.lisnInvitations = lisnInvitations
            lisn.messages = lisnMessages
            saveObject(lisn)
            UserLISNMap userLISNMap = new UserLISNMap(user: user, lisn: lisn, profileShareType: ProfileShareType.ALL)
            saveObject(userLISNMap)
			
            result.status = 'success'
            lisn.refresh()
            def aMessage = lisn.getLISNSummary(json.timeStamp, this, user)
            result.message = aMessage
			
        }catch(Exception e){
            log.error("Coulnot create lisn", e)
            result.status = 'error'
            result.message = 'Couldnot create lisn'
        }
        return result
    }
    
    /**
     *
     * @param params
     * @param request - Request should include a JSON with token, name (optional), latitude, longitude and timeStamp (which is user timezone offset)
     * and should include array of user ids selected facebook and nearby tabs,
     *
     * @return
     */
    def createLISNV3(params, request){
        Map result = [:]
        try {
            /*
             * 	    {
            token:5e089bee-8db1-4b32-bef5-17d773324c9c,
            users:[1, 5],
            latitude: ,
            longitude: ,
            timeStamp:���-7���,
            }
             */
            def json = request.JSON
            NayaxUser user = getUserFromToken(json.token)
            LocationCoordinate locationCoordinate = lisnService.saveLocationCoordinate(json.longitude.toDouble(), json.latitude.toDouble())
			
			
            LISN lisn = new LISN (creator: user,locationCoordinate: locationCoordinate, name:json.name)
            saveObject(lisn)
            LISNMessage lisnCreatedMessage = new LISNMessage(user: user,
                dateCreated: new Date(),
                content: LISNMessage.MessageType.CREATED.toString(),
                lisn: lisn,
                messageType:LISNMessage.MessageType.CREATED)
            saveObject(lisnCreatedMessage)
            def lisnMessages=[]
            lisnMessages.add(lisnCreatedMessage)
            def lisnInvitations=[]
            def phoneInvitations = []
            json.users?.each{
                def receiver = NayaxUser.get(it)
                LISNInvitation lisnInvitation = new LISNInvitation(sender:user, lisn: lisn, receiver:receiver)
                //sendMessageToDevice(sourceUser, targetUser, "CONNECTION_REQUEST", result, "${sourceUser.fullName} accepted your connection request!", "",  "")
                LISNMessage lisnInvitationMessage = new LISNMessage(user: receiver, dateCreated: new Date(), content: LISNMessage.MessageType.INVITED.toString(), lisn: lisn, messageType:LISNMessage.MessageType.INVITED)
				
                sendMessageToDevice(user, receiver,"LISN_INVITATION", result, "${user.fullName} invited you to join.")
                lisnInvitations.add(lisnInvitation)
                lisnMessages.add(lisnInvitationMessage)
            }
            	
            lisn.lisnInvitations = lisnInvitations
            lisn.messages = lisnMessages
            saveObject(lisn)
            UserLISNMap userLISNMap = new UserLISNMap(user: user, lisn: lisn, profileShareType: ProfileShareType.ALL)
            saveObject(userLISNMap)
			
            result.status = 'success'
            lisn.refresh()
            def aMessage = lisn.getLISNSummary(json.timeStamp, this, user)
            result.message = aMessage
			
        }catch(Exception e){
            log.error("Coulnot create lisn", e)
            result.status = 'error'
            result.message = 'Couldnot create lisn'
        }
        return result
    }
	
    /**
     *
     * @param params
     * @param request - request contains JSON with token, lisnId and arrays of facebook, nearby and phone to be added to existing LISN
     *
     * @return
     */
    def inviteFriendsToLISN(params, request){
        Map result = [:]
        try {
            /*
             * 	    {
            token:5e089bee-8db1-4b32-bef5-17d773324c9c,
            nearby:[1, 5],
            facebook:[100, 101],
            phone:[���1.510.378.6712���, ���+91.98489.62303���],
            latitude: ,
            longitude: ,
            timeStamp:���-7���,
            }
             */
            def json = request.JSON
            NayaxUser user = getUserFromToken(json.token)
            //LocationCoordinate locationCoordinate = lisnService.saveLocationCoordinate(json.longitude.toDouble(), json.latitude.toDouble())
			
            LISN lisn = LISN.get(json.lisnId)
            json.nearby?.each{
                def receiver = NayaxUser.get(it)
                LISNInvitation lisnInvitation = new LISNInvitation(sender:user, lisn: lisn, receiver:receiver)
                LISNMessage lisnInvitationMessage = new LISNMessage(user: receiver,
                    dateCreated: new Date(),
                    content: LISNMessage.MessageType.INVITED.toString(),
                    lisn: lisn,
                    messageType:LISNMessage.MessageType.INVITED)
				
				
                sendMessageToDevice(user, receiver,"LISN_INVITATION", result, "${user.fullName} invited you to join." )
                lisn.messages.add(lisnInvitationMessage)
                lisn.lisnInvitations.add(lisnInvitation)
            }
            json.facebook?.each{
                def fid = it.startsWith('f')?it.toString().substring(1):it.toString()
                Facebook facebook = Facebook.findByFid(fid)
                def receiver = facebook?.user
                LISNInvitation lisnInvitation = new LISNInvitation(sender:user, lisn: lisn, receiver:receiver)
                LISNMessage lisnInvitationMessage = new LISNMessage(user: receiver,
                    dateCreated: new Date(),
                    content: LISNMessage.MessageType.INVITED.toString(),
                    lisn: lisn,
                    messageType:LISNMessage.MessageType.INVITED)

                sendMessageToDevice(user, receiver,"LISN_INVITATION", result, "${user.fullName} invited you to join.")
                lisn.lisnInvitations.add(lisnInvitation)
                lisn.messages.add(lisnInvitationMessage)
            }
            json.phone?.each{
                LISNInvitation lisnInvitation = new LISNInvitation(sender:user, lisn: lisn, phoneNumber:it)
                lisn.lisnInvitations.add(lisnInvitation)
            }
			
            saveObject(lisn)
            result.status = 'success'
            result.message = 'saved lisn'
			
			
			
        }catch(Exception e){
            log.error("Coulnot create lisn", e)
            result.status = 'error'
            result.message = 'Couldnot create lisn'
        }
        return result
    }
	
    def addPhoneToUser (token, phoneNumberString){
		
        Map result = [:];
        try {
            NayaxUser user = getUserFromToken(token);
            Phone userPhone
            if(user?.userPhone){
                userPhone = user.userPhone
                userPhone.phoneNumber = phoneNumberString
            }
            else
            userPhone = new Phone(phoneNumber:phoneNumberString, user:user)
            saveObject(userPhone)
        }catch(Exception e){
            log.error("Coulnot save phone number", e)
            result.status = 'error'
            result.message = 'Couldnot save phone number'
        }
        result.status = 'success'
        result.message = 'saved phone number'
        return result
		
    }
    
    /**
     * A user can share their profile contact info (email, phone, professional title) with another LISNx user.
     * @param token - LISNx user token
     * @param targetId - id of lisn or user
     * @param targetIsLisn - true when targetId provided is that of LISN, and false when targetId is that of a user
     * @param request JSON will be used when shared from my profile screen sending JSON in request as in createLISNV2 api
     * @return
     */
    def shareContactInfo (token, targetId, targetIsLisn){
        Map result = [:]
        NayaxUser user;
        def notificationMessage 
		
        if(request.JSON){
            def json = request.JSON
            user = getUserFromToken(json.token);
            notificationMessage = "${user.fullName} shared contact information."
            json.nearby?.each{
                def targetUser = NayaxUser.get(it)
                def profileShare = ProfileShare.findWhere(user:user, receiver:targetUser)
                if(!profileShare){
                    profileShare = new ProfileShare(user:user, receiver:targetUser)
                    saveObject(profileShare)
                }
                def privateMessageInstance = new PrivateMessage(sender: user, receiver: targetUser,  content: " shared contact information",
                    messageType:PrivateMessage.MessageType.CONTACT_INFO)
                saveObject(privateMessageInstance)
                sendMessageToDevice(user, targetUser, "DIRECT_MESSAGE", result, notificationMessage)
				
            }
            json.facebook?.each{
                def fid = it.startsWith('f')?it.toString().substring(1):it.toString()
                Facebook facebook = Facebook.findByFid(fid)
                def targetUser = facebook?.user
                def profileShare = ProfileShare.findWhere(user:user, receiver:targetUser)
                if(!profileShare){
                    profileShare = new ProfileShare(user:user, receiver:targetUser)
                    saveObject(profileShare)
                }
                def privateMessageInstance = new PrivateMessage(sender: user, receiver: targetUser,  content: " shared contact information",
                    messageType:PrivateMessage.MessageType.CONTACT_INFO)
                saveObject(privateMessageInstance)
                sendMessageToDevice(user, targetUser, "DIRECT_MESSAGE", result, notificationMessage)
            }
        }else {
            user = getUserFromToken(token);
            notificationMessage = "${user.fullName} shared contact information."
            if(!targetIsLisn){
                NayaxUser targetUser = NayaxUser.read(targetId.toLong()).refresh()
                def profileShare = ProfileShare.findWhere(user:user, receiver:targetUser)
                if(!profileShare){
                    profileShare = new ProfileShare(user:user, receiver:targetUser)
                    saveObject(profileShare)
                }
                def privateMessageInstance = new PrivateMessage(sender: user, receiver: targetUser,  content: " shared contact information",
                    messageType:PrivateMessage.MessageType.CONTACT_INFO)
                saveObject(privateMessageInstance)
                sendMessageToDevice(user, targetUser, "DIRECT_MESSAGE", result, notificationMessage)
            }else {
                LISN lisn = LISN.findById(targetId)
                def profileShare = ProfileShare.findWhere(user:user, lisn:lisn)
                if(!profileShare){
                    profileShare = new ProfileShare(user:user, lisn:lisn)
                    saveObject(profileShare)
                }
                def messageInfoMap = [message_type:"LISN_MESSAGE", sender_id:user.id, fid:user.facebook?.fid, name:user.fullName]
                LISNMessage lisnMessage = new LISNMessage(user: user, dateCreated: new Date(),
                    content: 'shared contact information.',
                    lisn: lisn, messageType:LISNMessage.MessageType.CONTACT_INFO)
                saveObject(lisnMessage)
				
                //This method uses dummy timeStamp as date is not sent in response. Needs refactoring.
                def lisnSummary = lisn.getLISNSummary("-7", this, user)
                messageInfoMap.members = lisnSummary.members
                messageInfoMap.moreMemberCount=lisnSummary.moreMemberCount
                messageInfoMap.lisnId = lisn.id
                messageInfoMap.lisnName = lisn.name
                lisn.joins.each{userLisnMap ->
                    def receiverInstance = userLisnMap.user
                    sendLISNMessageToDevice(user,
                        receiverInstance,
                        "LISN_MESSAGE",
                        result,
                        messageInfoMap,
                        notificationMessage,
                        "", "")
						
                }
				
            }
        }
        result.status = 'success'
        result.message = 'shared contact info'
        return result
		
		
    }
    
    /**
     * A user can share their profile contact info (email, phone, professional title) with another LISNx user.
     * @param token - LISNx user token
     * @param targetId - id of lisn or user
     * @param targetIsLisn - true when targetId provided is that of LISN, and false when targetId is that of a user
     * @param request JSON will be used when shared from my profile screen sending JSON in request as in createLISNV2 api
     * @return
     */
    def shareContactInfoV2 (token, targetId, targetIsLisn){
        Map result = [:]
        NayaxUser user;
        def notificationMessage 
		
        if(request.JSON){
            def json = request.JSON
            user = getUserFromToken(json.token);
            notificationMessage = "${user.fullName} shared contact information."
            json.users?.each{
                def targetUser = NayaxUser.get(it)
                def profileShare = ProfileShare.findWhere(user:user, receiver:targetUser)
                if(!profileShare){
                    profileShare = new ProfileShare(user:user, receiver:targetUser)
                    saveObject(profileShare)
                }
                def privateMessageInstance = new PrivateMessage(sender: user, receiver: targetUser,  content: " shared contact information",
                    messageType:PrivateMessage.MessageType.CONTACT_INFO)
                saveObject(privateMessageInstance)
                sendMessageToDevice(user, targetUser, "DIRECT_MESSAGE", result, notificationMessage)				
            }            
        }else {
            user = getUserFromToken(token);
            notificationMessage = "${user.fullName} shared contact information."
            if(!targetIsLisn){
                NayaxUser targetUser = NayaxUser.read(targetId.toLong()).refresh()
                def profileShare = ProfileShare.findWhere(user:user, receiver:targetUser)
                if(!profileShare){
                    profileShare = new ProfileShare(user:user, receiver:targetUser)
                    saveObject(profileShare)
                }
                def privateMessageInstance = new PrivateMessage(sender: user, receiver: targetUser,  content: " shared contact information",
                    messageType:PrivateMessage.MessageType.CONTACT_INFO)
                saveObject(privateMessageInstance)
                sendMessageToDevice(user, targetUser, "DIRECT_MESSAGE", result, notificationMessage)
            }else {
                LISN lisn = LISN.findById(targetId)
                def profileShare = ProfileShare.findWhere(user:user, lisn:lisn)
                if(!profileShare){
                    profileShare = new ProfileShare(user:user, lisn:lisn)
                    saveObject(profileShare)
                }
                def messageInfoMap = [message_type:"LISN_MESSAGE", sender_id:user.id, fid:user.facebook?.fid, name:user.fullName]
                LISNMessage lisnMessage = new LISNMessage(user: user, dateCreated: new Date(),
                    content: 'shared contact information.',
                    lisn: lisn, messageType:LISNMessage.MessageType.CONTACT_INFO)
                saveObject(lisnMessage)
				
                //This method uses dummy timeStamp as date is not sent in response. Needs refactoring.
                def lisnSummary = lisn.getLISNSummary("-7", this, user)
                messageInfoMap.members = lisnSummary.members
                messageInfoMap.moreMemberCount=lisnSummary.moreMemberCount
                messageInfoMap.lisnId = lisn.id
                messageInfoMap.lisnName = lisn.name
                lisn.joins.each{userLisnMap ->
                    def receiverInstance = userLisnMap.user
                    sendLISNMessageToDevice(user,
                        receiverInstance,
                        "LISN_MESSAGE",
                        result,
                        messageInfoMap,
                        notificationMessage,
                        "", "")
						
                }
				
            }
        }
        result.status = 'success'
        result.message = 'shared contact info'
        return result
		
		
    }
    
    /**
     * To get user profile such as name, fid and optionally profile info
     * @param token
     * @param otherUserId
     * @return
     */
    def getProfileOfUserV2(token, otherUserId){
        Map result = [:]
        result.message = [:]
        NayaxUser targetUser =  NayaxUser.read(otherUserId.toLong()).refresh()
        NayaxUser user = getUserFromToken(token);
        def nearBy = userService.userActivityNearby(user, true)
        def locationMessage = ''
        def isNearBy = false
		
        if(nearBy.contains(targetUser)){
            isNearBy = true
            if(user.latestLocationActivity!=null && targetUser.latestLocationActivity!=null){
                def distance = GeoUtil.distance(user.latestLocationActivity.latitude, user.latestLocationActivity.longitude,
                    targetUser.latestLocationActivity.latitude, targetUser.latestLocationActivity.longitude)
                locationMessage = "${distance} miles away"
            }
        }
        def connectionStatus = userConnectionStatus(targetUser, user)
        def isConnectedOnFacebook = user.isConnectedOnFacebook(targetUser)
        def thumbnailPicUrl = defaultThumbnailPicUrl
        def profilePicUrl = defaultProfilePicUrl

        if (targetUser.linkedin?.thumbnailURL) {
            thumbnailPicUrl = targetUser.linkedin?.thumbnailURL
        }
        if (targetUser.linkedin?.profilePictureURL) {
            profilePicUrl = targetUser.linkedin?.profilePictureURL
        }
        result['message'] = ['isNearBy': isNearBy,
			'connectionStatus': connectionStatus,
			'fullName': targetUser.fullName,
			'locationMessage': locationMessage,
			'isImage': targetUser.facebook!=null? "true" : targetUser.picture?"true":"false" ,
			'connectedOnFacebook':isConnectedOnFacebook,
			'connectedOnLinkedin': false,
			'connectedOnLisnx': user.isConnectedToUser(targetUser),
            isUserLinkedInConnected: user.linkedin!=null,
			'id' : targetUser.id,
			'fid': targetUser.facebook?.fid,
                        'profileThumbnailPicUrl': thumbnailPicUrl,
                        'profilePictureUrl': profilePicUrl,
			'isLisnxUser': true,
			'commonFriendsCount': 0]
        if(connectionStatus!='Connected' && !isConnectedOnFacebook){
            result.message.fullName = targetUser.getFullNameIfNotConnected()
            result.message.locationMessage = 'is nearby'
        }
		
        def profileShareInLISN = false
        ProfileShare profileShare = ProfileShare.findWhere(user:targetUser, receiver:user)
        if(!profileShare){
            def lisnsProfileShares = ProfileShare.createCriteria().list{
                isNotNull("lisn")
                eq("user", targetUser) 
            }
            lisnsProfileShares.each{
                def thisLisn = it.lisn
                if(thisLisn?.isMember(user))
                profileShareInLISN = true
            }
        }
		
        if(profileShare!=null|| profileShareInLISN==true){
            result.message.profileShared = true
            result.message.profile = [:]
            result.message.profile.phone = targetUser.userPhone?.getPhoneNumberFormatted()
            result.message.profile.email = targetUser.profile?.getEmail()
            result.message.profile.professionalTitle = targetUser.profile?.professionalTitle
            result.message.profile.company = targetUser.profile?.company
            result.message.profile.linkedInUrl = targetUser.profile?.linkedInUrl
			
        }
		
        result.status = 'success'
        return result
    }
	
    /**
     * @deprecated
     * @see #getProfileOfUserV2()
     * @param token
     * @param otherUserId
     * @return
     */
    def getProfileOfUser(token, otherUserId){
        Map result = [:]
        boolean userFound = false
        NayaxUser targetUser
        boolean isLisnxUser = false;
        boolean wasInvited = false;
        def fid
        NayaxUser nayaxUserInstance = getUserFromToken(token);
        def invitation = LISNxInvitation.findByUserAndTargetUserId(nayaxUserInstance, otherUserId)
        wasInvited = (invitation!=null)
		
        if (otherUserId.startsWith('f')){
            fid = otherUserId.substring(1)
            def facebookConnection = FacebookConnection.findByConnectionFacebookId(otherUserId.substring(1))
            result["status"] = "success"
            result['message'] = ['isNearBy': 'false',
                                    'connectionStatus': CONNECTED,
                                    'fullName': facebookConnection.firstName +" " +facebookConnection.lastName ,
                                    'username': 'unknown',
                                    'website': 'unknown',
                                    'facebook': 'unknown',
                                    'linkedin': 'unknown',
                                    'dateOfBirth': 'unknown',
                                    'email': 'unknown',
                                    'isImage': "true" ,
                                    'commonLisnsCount': 0,
                                    'connectedOnFacebook':FacebookConnection.findByUserFacebookIdAndConnectionFacebookId(nayaxUserInstance.facebook.fid, targetUser.facebook.fid)!=null,
                                    'connectedOnLinkedin':'false',
                                    'commonFriendsCount': 0]
            userFound=true
			
        }else {
            targetUser = getUserById(otherUserId)
        }
	
        if(targetUser){
            userFound=true
            fid = targetUser.facebook?.fid
            isLisnxUser = true;
            if(SERVER_LISNX_COM.equals(targetUser?.username)){
				
                result['message'] = ['isNearBy': "true"]
                result['message'] += ['connectionStatus': CONNECTED]
                result["status"] = "success"
                result['message'] += ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
					'facebook': targetUser.facebook?.facebookLink, 'linkedin': targetUser.linkedin?.profileURL, 'dateOfBirth': targetUser.dateOfBirth,
                    email: targetUser.email, "isImage": targetUser.facebook!=null? "true" : targetUser.picture?"true":"false"]
                result['message'] += ['commonLisnsCount': 0]
                result['message'] += ['commonFriendsCount': 0]
            }
			
            else{
                def findProfile = ConnectionSharingType.findWhere(sourceUser: targetUser, targetUser: nayaxUserInstance);
                log.info "find profile  " + findProfile?.profileShareType + "  id === " + findProfile?.id
                result["status"] = "success"
				
				
                def isImage = (targetUser.facebook!=null) ? "true" : (targetUser.picture?"true":"false")
				
                switch (findProfile?.profileShareType) {
                case ProfileShareType.ALL:
                    result['message'] = ['fullName': targetUser.fullName,
                                        'username': targetUser.username,
                                        'website': targetUser.website,
                                        'facebook': targetUser.facebook?.facebookLink,
                                        'linkedin': targetUser.linkedin?.profileURL,
                                        'dateOfBirth': targetUser.dateOfBirth,
                        email: targetUser.email,
                                        "isImage": isImage]
                    break;
                case ProfileShareType.CASUAL:
                    result['message'] = ['fullName': targetUser.fullName,
                                            'username': targetUser.username,
                                            'website': targetUser.website,
                                            'facebook': targetUser.facebook?.facebookLink,
                                            'dateOfBirth': targetUser.dateOfBirth,
                                            "isImage": isImage]
                    break;
                case ProfileShareType.PROFESSIONAL:
                    result['message'] = ['fullName': targetUser.fullName,
                                            'username': targetUser.username,
                                            'website': targetUser.website,
                                            'linkedin': targetUser.linkedin?.profileURL,
                                            'dateOfBirth': targetUser.dateOfBirth,
                                            "isImage": isImage]
                    break;
                case ProfileShareType.EMAIL:
                    result['message'] = ['fullName': targetUser.fullName,
                                            'username': targetUser.username,
                                            'website': targetUser.website,
                        email: targetUser.email,
                                            'dateOfBirth': targetUser.dateOfBirth,
                                            "isImage": isImage]
                    break;
                default:
                    result['message'] = ['fullName': targetUser.fullName, "isImage": isImage]
                }
                def commonLisns = lisnService.getCommonLisns(targetUser, nayaxUserInstance)
                def commonFriends = userService.getCommonFriends(targetUser, nayaxUserInstance)
                def nearBy = userService.userActivityNearby(nayaxUserInstance, true)
                def connectionStatus = userConnectionStatus(targetUser, nayaxUserInstance)
                result['message'] += ['connectionStatus': connectionStatus]
                def connectionRequestDetails = NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser)
                if (connectionRequestDetails) {
                    result['message'] += ['viewConnection': connectionRequestDetails?.lastUpdated]
                }
                if (NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser)) {
                    if (NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser).nConnectionStatus.toString().equalsIgnoreCase("Ignored")) {
                        result['message'] += ['isDecline': 'true']
                    } else {
                        result['message'] += ['isDecline': 'false']
                    }
                } else {
                    result['message'] += ['isDecline': 'false']
                }
	
                if (nearBy?.contains(targetUser)) {
                    result['message'] += ['isNearBy': "true"]
                    def locationMessage = "is nearby"
                    if(nayaxUserInstance.latestLocationActivity!=null && targetUser.latestLocationActivity!=null){
                        def distance = GeoUtil.distance(nayaxUserInstance.latestLocationActivity.latitude, nayaxUserInstance.latestLocationActivity.longitude,
                            targetUser.latestLocationActivity.latitude, targetUser.latestLocationActivity.longitude)
                        locationMessage = "${distance} miles away"
                    }
                    log.info ("location message"+locationMessage)
                    result['message'] += ['locationMessage': locationMessage]
                } else {
                    result['message'] += ['isNearBy': "false"]
                }
                /* result['message'] += ['isNearBy': "true"]*/
	
                result['message'] += ['commonLisnsCount': commonLisns.size()]
                result['message'] += ['commonFriendsCount': commonFriends.size()]
                result['message'] += ['connectedOnLisnx': nayaxUserInstance.isConnectedToUser(targetUser)]
                if(nayaxUserInstance.facebook!=null && targetUser.facebook!=null)
                result['message'] += ['connectedOnFacebook': FacebookConnection.findByUserFacebookIdAndConnectionFacebookId(nayaxUserInstance.facebook.fid, targetUser.facebook.fid)!=null]
				
            }
        }
        if(!userFound){
            result["status"] = "error"
            result["message"] = "No user found with this Id"+otherUserId
        }
        result.message.id = otherUserId
        result.message.isLisnxUser =isLisnxUser
        result.message.fid=fid
        result.message.wasInvited = wasInvited

        return result
    }

    private NayaxUser getUserById(otherUserId) {
        def targetUser = NayaxUser.read(otherUserId.toLong()).refresh()
        return targetUser
    }

    def getProfileOfPeopleNearBy(token, otherUserId) {
        Map result = [:]
        boolean userFound = false
        NayaxUser targetUser
        boolean isLisnxUser = false;
        boolean wasInvited = false;
		
        def invitation = LISNxInvitation.findByUserAndTargetUserId(getUserFromToken(token), otherUserId)
        wasInvited = (invitation!=null)
		
        if (otherUserId.startsWith('f')){
            def facebookConnection = FacebookConnection.findByConnectionFacebookId(otherUserId.substring(1))
            result["status"] = "success"
            result['message'] = ['isNearBy': 'false',
                                    'connectionStatus': CONNECTED,
                                    'fullName': facebookConnection.firstName +" " +facebookConnection.lastName ,
                                    'username': 'unknown',
                                    'website': 'unknown',
                                    'facebook': 'unknown',
                                    'linkedin': 'unknown',
                                    'dateOfBirth': 'unknown',
                                    'email': 'unknown',
                                    'isImage': "true" ,
                                    'commonLisnsCount': 0,
                                    'connectedOnFacebook':'true',
                                    'connectedOnLinkedin':'false',
                                    'commonFriendsCount': 0]
            userFound=true
			
        }else {
            targetUser = NayaxUser.read(otherUserId.toLong()).refresh()
        }
	
        if(targetUser){
            userFound=true
            isLisnxUser = true;
            if(SERVER_LISNX_COM.equals(targetUser?.username)){
				
                result['message'] = ['isNearBy': "true"]
                result['message'] += ['connectionStatus': CONNECTED]
                result["status"] = "success"
                result['message'] += ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
					'facebook': targetUser.facebook?.facebookLink, 'linkedin': targetUser.linkedin?.profileURL, 'dateOfBirth': targetUser.dateOfBirth,
                    email: targetUser.email, "isImage": targetUser.facebook!=null? "true" : targetUser.picture?"true":"false"]
                result['message'] += ['commonLisnsCount': 0]
                result['message'] += ['commonFriendsCount': 0]
            }
			
            else{
                NayaxUser nayaxUserInstance = getUserFromToken(token);
                def findProfile = ConnectionSharingType.findWhere(sourceUser: targetUser, targetUser: nayaxUserInstance);
                log.info "find profile  " + findProfile?.profileShareType + "  id === " + findProfile?.id
                result["status"] = "success"
				
				
                def isImage = (targetUser.facebook!=null) ? "true" : (targetUser.picture?"true":"false")
				
                switch (findProfile?.profileShareType) {
                case ProfileShareType.ALL:
                    result['message'] = ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
								'facebook': targetUser.facebook?.facebookLink, 'linkedin': targetUser.linkedin?.profileURL, 'dateOfBirth': targetUser.dateOfBirth, email: targetUser.email, "isImage": isImage]
                    break;
                case ProfileShareType.CASUAL:
                    result['message'] = ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
								'facebook': targetUser.facebook?.facebookLink, 'dateOfBirth': targetUser.dateOfBirth, "isImage": isImage]
                    break;
                case ProfileShareType.PROFESSIONAL:
                    result['message'] = ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
								'linkedin': targetUser.linkedin?.profileURL, 'dateOfBirth': targetUser.dateOfBirth, "isImage": isImage]
                    break;
                case ProfileShareType.EMAIL:
                    result['message'] = ['fullName': targetUser.fullName, 'username': targetUser.username, 'website': targetUser.website,
                        email: targetUser.email, 'dateOfBirth': targetUser.dateOfBirth, "isImage": isImage]
                    break;
                default:
                    result['message'] = ['fullName': targetUser.fullName, "isImage": isImage]
                }
                def commonLisns = lisnService.getCommonLisns(targetUser, nayaxUserInstance)
                def commonFriends = userService.getCommonFriends(targetUser, nayaxUserInstance)
                def nearBy = userService.userActivityNearby(nayaxUserInstance, true)
                def connectionStatus = userConnectionStatus(targetUser, nayaxUserInstance)
                result['message'] += ['connectionStatus': connectionStatus]
                def connectionRequestDetails = NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser)
                if (connectionRequestDetails) {
                    result['message'] += ['viewConnection': connectionRequestDetails?.lastUpdated]
                }
                if (NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser)) {
                    if (NConnection.findByConnectionAndOwner(nayaxUserInstance, targetUser).nConnectionStatus.toString().equalsIgnoreCase("Ignored")) {
                        result['message'] += ['isDecline': 'true']
                    } else {
                        result['message'] += ['isDecline': 'false']
                    }
                } else {
                    result['message'] += ['isDecline': 'false']
                }
	
                if (nearBy?.contains(targetUser)) {
                    result['message'] += ['isNearBy': "true"]
                } else {
                    result['message'] += ['isNearBy': "false"]
                }
                /* result['message'] += ['isNearBy': "true"]*/
	
                result['message'] += ['commonLisnsCount': commonLisns.size()]
                result['message'] += ['commonFriendsCount': commonFriends.size()]
                result['message'] += ['connectedOnLisnx': nayaxUserInstance.isConnectedToUser(targetUser)]
                if(nayaxUserInstance.facebook!=null && targetUser.facebook!=null)
                result['message'] += ['connectedOnFacebook': FacebookConnection.findByUserFacebookIdAndConnectionFacebookId(nayaxUserInstance.facebook.fid, targetUser.facebook.fid)!=null]
				
            }
        }
        if(!userFound){
            result["status"] = "error"
            result["message"] = "No user found with this Id"+otherUserId
        }
        result['message'] += ['id':otherUserId]
        result['message'] += ['isLisnxUser':isLisnxUser]
		
        result['message'] += ['wasInvited':wasInvited]


        return result
    }

    def userConnectionStatus(NayaxUser targetUser, NayaxUser currentUser) {
        def status = ""
        if(SERVER_LISNX_COM.equals(targetUser.username)){
            status=CONNECTED
        }else {
            def findStatus = NConnection.createCriteria().list {
                eq("owner", currentUser)
                eq("connection", targetUser)
                /* ne("nConnectionStatus", NConnectionStatus.IGNORED)*/
            }
            if (findStatus) {
                log.info "findStatus -- > " + findStatus.id + findStatus.nConnectionStatus.toString()
	
                findStatus.each {
                    if (it.nConnectionStatus.toString().equalsIgnoreCase("Pending")) {
                        status = "Pending"
                    } else if (it.nConnectionStatus.toString().equalsIgnoreCase("Connected")) {
                        status = CONNECTED
                    } else {
                        status = "Ignored"
                    }
                }
            } else {
	
                findStatus = NConnection.createCriteria().list {
                    eq("owner", targetUser)
                    eq("connection", currentUser)
                    /* ne("nConnectionStatus", NConnectionStatus.IGNORED)*/
                }
                log.info "findStatus in else -- > " + findStatus.id + findStatus.nConnectionStatus.toString()
                if (findStatus) {
                    findStatus.each {
                        if (it.nConnectionStatus.toString().equalsIgnoreCase("Pending")) {
                            status = "To_Be_Accept"
                        } else if (it.nConnectionStatus.toString().equalsIgnoreCase("Connected")) {
                            status = "Connected"
                        }
                        /*else if (it.nConnectionStatus.toString().equalsIgnoreCase("Ignored")) {
                        status = "Ignored"
                        } */
                        else {
                            status = "Not_Connected"
                        }
                    }
                } else {
                    status = "Not_Connected"
                }
            }
        }
        log.info "status is === > " + status
        return status;
    }

    def saveLatitudeAndLongitude(token, def latitude, def longitude) {
        Map result = [:]
        if(!latitude?.trim() || !longitude?.trim()){
            result['status'] = "error"
            result["message"] = "Invalid coordinates"
			
        }else {
            def locationActivityEvent = new nayax.UserActivityMap()
            NayaxUser nayaxUserInstance = getUserFromToken(token);
            //locationActivityEvent.nayaxUser = nayaxUserInstance
            locationActivityEvent.activity = Activity.findByName('Login')
            locationActivityEvent.activityTime = new Date()
            locationActivityEvent.latitude = latitude.toDouble() + 90
            locationActivityEvent.longitude = longitude.toDouble() + 180
            //saveObject(locationActivityEvent)
            
            //nayaxUserInstance = getUserFromToken(token);
            nayaxUserInstance.latestLocationActivity = locationActivityEvent
            nayaxUserInstance.addToActivityMaps(locationActivityEvent)
            saveObject(nayaxUserInstance)
			
            result['status'] = "success"
            result["message"] = "Coordinates has been saved"
        }

        return result
    }

    def saveThumbnailImage(def path, def fileName, def type) {
        log.info "saving thumbnail image   - - - >  "
        def imageTool = new ImageTool()
        imageTool.load(path + "/" + fileName);
        imageTool.thumbnail(2500);
        imageTool.writeResult(path + "/" + fileName + "_thImage", type);
        return true
    }

    def getCommonLisns(token, otherUserId, timeStamp) {
        log.trace "Inside services:APIservice method:getCommonLisns:"
        Map result = [:]

        Map message = [:]
        NayaxUser nayaxUser = getUserFromToken(token);
        NayaxUser otherUser = NayaxUser.read(otherUserId.toString().toLong())
        def RSVP
        if (nayaxUser && otherUser) {
            result['status'] = "success"

            List<LISN> myLisns = []
            List<UserLISNMap> userLisns = UserLISNMap.findAllWhere(user: nayaxUser)
            if (userLisns) {
                myLisns = userLisns.size() > 0 ? userLisns*.lisn : []
            }
            userLisns = []
            List<LISN> otherUserLisns = []
            userLisns = UserLISNMap.findAllWhere(user: otherUser)
            if (userLisns) {
                otherUserLisns = userLisns.size() > 0 ? userLisns*.lisn : []
            }

            int countLisns = 0, countJoinedLisns = 0;

            List<LISN> commonLisns = myLisns.intersect(otherUserLisns)
            commonLisns.each {lisn ->
                def messagesCount = LISNMessage.findAllByLisn(lisn).size()
                message["${lisn.id}"] = ["name": lisn.name, "member": lisn.joins.size(), "description": lisn.description, "startDate": formatDateTimeStampForApi(timeStamp, lisn.dateCreated), "endDate": formatDateTimeStampForApi(timeStamp, lisn.endDate), "totalMessage": messagesCount]
            }
            result['message'] = message;
        } else {
            result['status'] = "error"
            result['message'] = "No user found with the associated token or Id."
        }

        return result
    }

    def getCommonFriends(token, otherUserId) {
        Map result = [:]

        Map friends = [:]
        NayaxUser nayaxUser = getUserFromToken(token);
        NayaxUser otherUser = NayaxUser.read(otherUserId.toString().toLong())
        if (nayaxUser && otherUser) {
            List commonFriends = userService.getCommonFriends(otherUser, nayaxUser)
            commonFriends.each {friend ->
                friends[friend.id] = ["id": friend.id, "fullName": friend.fullName, "isImage": friend.picture ? "true" : "false"]
            }
            result['status'] = 'success'
            result['message'] = friends
        } else {
            result['status'] = "error"
            result['message'] = "No user found with the associated token or Id"
        }

        return result
    }

    def getUnreadMessageCount(token) {
        Map result = [:]
        def message = [:]
        def dashBoardMessages = [:]
        def newPrivateMessages

        def unreadPrivateMessages = [:]

        def nayaxUserInstance = getUserFromToken(token);
        def userLisns = UserLISNMap.findAllByUser(nayaxUserInstance)
        userLisns.each {userLisn ->
            println "working on lisn : ${userLisn.lisn}"
            def newMessagesCount
            if (userLisn.lastViewedMessageId) {
                newMessagesCount = LISNMessage.countByLisnAndIdGreaterThan(userLisn.lisn, userLisn.lastViewedMessageId.toInteger())
            } else {
                newMessagesCount = LISNMessage.countByLisn(userLisn.lisn)
            }
            println "newMessagesCount --------" + newMessagesCount
            if (newMessagesCount > 0) {
                dashBoardMessages[userLisn.lisn.id] = ["lisnName": userLisn.lisn.name, "count": newMessagesCount]
            }
        }
        newPrivateMessages = PrivateMessage.findAllByIsViewedAndReceiver("false", nayaxUserInstance)
        def senders = newPrivateMessages*.sender as Set
        def newPrivateMessagesCount = newPrivateMessages.size()
        println "newPrivateMessagesCount------------" + newPrivateMessagesCount
        if (senders) {
            senders.each {sender ->
                def privateMessageCount = PrivateMessage.createCriteria().list {
                    and {
                        eq("sender", sender)
                        eq("receiver", nayaxUserInstance)
                        eq("isViewed", "false")
                    }
                }.size()
                unreadPrivateMessages[sender.id] = ["sender": sender.id, "senderName": sender.fullName, "privateMessageCount": privateMessageCount, "isImage": sender?.picture ? "true" : "flase"]
            }
        }
        message['dashBoardMessages'] = dashBoardMessages
        message['newPrivateMessagesCount'] = newPrivateMessagesCount
        message['newPrivateMessages'] = unreadPrivateMessages
        result["status"] = "success"
        result["message"] = message
        return result
    }
	
    def getMessageSummary(token, timeStamp) {
        Map result = [:]
        def message = [:]
        def dashBoardMessages = [:]
        def directMessages = [:]
        def newPrivateMessages
        Map friendRequestList = [:]
        Map yourAcceptedFriendRequestList = [:]
        NayaxUser nayaxUserInstance = getUserFromToken(token);

        def criteriaQuery = NConnection.createCriteria()
        def connectionRequestsReceived = criteriaQuery.list {
            and {
                eq("connection", nayaxUserInstance)
                eq("nConnectionStatus", NConnectionStatus.PENDING)
                //or {
                //  isNull("isNotified")
                // eq("isNotified", false)
                // }
            }
        }

        def listAcceptedYourFriendRequest = NConnection.createCriteria().list {
            eq("owner", nayaxUserInstance)
            eq("nConnectionStatus", NConnectionStatus.CONNECTED)
            //or {
            //  isNull("isNotified")
            //eq("isNotified", false)
            // }
        }
        def newNotifications=0

        connectionRequestsReceived.each {nConnection ->
            friendRequestList[nConnection.owner.id] = ["id": nConnection.owner.id, "fullName": nConnection.owner.fullName, "isImage": nConnection.owner.picture ? "true" : "false", "state":nConnection.nConnectionStatus.toString(),
				"isNotified":nConnection.isNotified]
            if(!nConnection.isNotified)
            newNotifications++
        }
        listAcceptedYourFriendRequest.each {nConnection ->
            yourAcceptedFriendRequestList[nConnection.connection.id] = ["id": nConnection.connection.id, "fullName": nConnection.connection.fullName, "isImage": nConnection.connection.picture ? "true" : "false",
				"state":nConnection.nConnectionStatus.toString(), "isNotified":nConnection.isNotified]
            if(!nConnection.isNotified)
            newNotifications++
        }

        def userLisns = UserLISNMap.findAllByUser(nayaxUserInstance)
        userLisns.each {userLisn ->
            def newMessagesCount
            if (userLisn.lastViewedMessageId) {
                newMessagesCount = LISNMessage.countByLisnAndIdGreaterThan(userLisn.lisn, userLisn.lastViewedMessageId.toInteger())
            } else {
                newMessagesCount = LISNMessage.countByLisn(userLisn.lisn)
            }
            if (newMessagesCount > 0) {
                dashBoardMessages[userLisn.lisn.id] = ["lisnName": userLisn.lisn.name, "count": newMessagesCount]
            }
        }
        newPrivateMessages = PrivateMessage.findAllByReceiver(nayaxUserInstance)
        def senders = newPrivateMessages*.sender as Set
        def newPrivateMessagesCount = 0
        if (senders) {
            senders.each {sender ->
                def privateMessageList = PrivateMessage.createCriteria().list {
                    and {
                        eq("sender", sender)
                        eq("receiver", nayaxUserInstance)
						
                    }
                    order("id", "desc")
                    maxResults(1)
                }
                def privateMessageCount = PrivateMessage.createCriteria().get {
                    and {
                        eq("sender", sender)
                        eq("receiver", nayaxUserInstance)
                        eq("isViewed", "false")
                    }
                    projections{
                        count "id"
                    }
                }
                def privateMessage = privateMessageList.get(0)
                directMessages[sender.id] = ["sender": sender.id, "senderName": sender.fullName, "privateMessageCount": privateMessageCount,
					"latestMessageContent":privateMessage.content,//privateMessage.content.toString(),
					"isImage": sender?.picture ? "true" : "false", "isViewed":privateMessage.isViewed, "date_created":formatDateTimeStampForApi(timeStamp, privateMessage.dateCreated)]
                if(!"false".equals(privateMessage.isViewed)){
                    newPrivateMessagesCount++
                }
            }
        }
        directMessages = directMessages.sort{it.value.date_created}
        message['dashBoardMessages'] = dashBoardMessages
        message['newPrivateMessagesCount'] = newPrivateMessagesCount
        message['newPrivateMessages'] = directMessages
        message['friendRequestsReceived'] = friendRequestList
        message['friendRequestsAccepted'] = yourAcceptedFriendRequestList
		
        result["status"] = "success"
        result["message"] = message
        return result
    }
    def logTime(message, start){
        Date now = new Date()
        //log.info(message + (now.getTime() - start.getTime()))
        return now
    }
    
    /**
     * Returns data to be displayed in messages tab in app.
     * @param token
     * @param timeStamp
     * @return
     */
    def getMessageSummaryV2(token, timeStamp) {
        Map result = [:]
        def message = [:]
        def messages = []
        log.info('In method')
        def aMessage
        def sendersAdded = [] as Set
        def newPrivateMessages
        NayaxUser nayaxUserInstance = getUserFromToken(token);
        Date start = new Date()
        newPrivateMessages = PrivateMessage.findAllByReceiver(nayaxUserInstance)
        def senders = newPrivateMessages*.sender as Set
        def newPrivateMessagesCount = 0
        start = logTime('Senders calculated:', start)
        if (senders) {
            senders.each {sender ->
                def privateMessageList = PrivateMessage.createCriteria().list {
                    and {
                        eq("sender", sender)
                        eq("receiver", nayaxUserInstance)
                    }
                    order("id", "desc")
                    maxResults(1)
                }
                def privateMessageCount = PrivateMessage.createCriteria().get {
                    and {
                        eq("sender", sender)
                        eq("receiver", nayaxUserInstance)
                        eq("isViewed", "false")
                    }
                    projections{
                        count "id"
                    }
                }
                def privateMessage = privateMessageList.get(0)
                aMessage = ["sender": sender.id,
                            "id":sender.id,
                            "fullName":sender.fullName,
                            "senderName": sender.fullName,
                            "fid":sender.facebook?.fid,
                            "privateMessageCount": privateMessageCount,
                            "latestMessageContent":privateMessage.content,//privateMessage.content.toString(),
                            "messageType": "DIRECT_MESSAGE",
                            "isViewed":privateMessage.isViewed,
                            "date_created":formatDateTimeStampForApi(timeStamp, privateMessage.dateCreated)]
                messages.add(aMessage)
                if(!"false".equals(privateMessage.isViewed)){
                    newPrivateMessagesCount++
                }
                sendersAdded.add(sender.id)
			
            }
        }
        start = logTime('Time for direct messages:' , start)
        def criteriaQuery = NConnection.createCriteria()
        def connectionRequestsReceived = criteriaQuery.list {
            and {
                eq("connection", nayaxUserInstance)
                eq("nConnectionStatus", NConnectionStatus.PENDING)
            }
        }
		

        def listAcceptedYourFriendRequest = NConnection.createCriteria().list {
            eq("owner", nayaxUserInstance)
            eq("nConnectionStatus", NConnectionStatus.CONNECTED)
        }
        def newNotifications=0

        connectionRequestsReceived.each {nConnection ->
            if(!sendersAdded.contains(nConnection.owner.id)){
                aMessage = ["sender": nConnection.owner.id,
                            "id":nConnection.owner.id,
                            "fullName":nConnection.owner.fullName,
                            "senderName": nConnection.owner.fullName,
                            "fid":nConnection.owner.facebook?.fid,
                            "state":nConnection.nConnectionStatus.toString(),
                            "messageType": "CONNECTION_REQUEST_RECEIVED",
                            "isNotified":nConnection.isNotified,
                            "date_created":formatDateTimeStampForApi(timeStamp, nConnection.dateCreated)]
                messages.add(aMessage)
                if(!nConnection.isNotified)
                newNotifications++
            }
        }
        listAcceptedYourFriendRequest.each {nConnection ->
            if(!sendersAdded.contains(nConnection.connection.id)){
                aMessage = ["sender": nConnection.connection.id,
                            "id":nConnection.connection.id,
                            "fullName":nConnection.connection.fullName,
                            "senderName": nConnection.connection.fullName,
                            "fid":nConnection.connection.facebook?.fid,
                            "state":nConnection.nConnectionStatus.toString(),
                            "messageType": "CONNECTION_REQUEST_ACCEPTED",
                            "isNotified":nConnection.isNotified,
                            "date_created":formatDateTimeStampForApi(timeStamp, nConnection.dateCreated)]
                messages.add(aMessage)
                if(!nConnection.isNotified)
                newNotifications++
				
            }
        }
        start = logTime('Time for connections received and accepted messages:', start)
		
        //TODO: Remove hard-coded.
        def lisnInvitations = LISNInvitation.findAllByReceiverAndInvitationAcceptedIsNullAndInvitationIgnoredIsNullAndIdGreaterThan(nayaxUserInstance,121)
        lisnInvitations.each{lisnInvitation ->
            aMessage = ["sender": lisnInvitation.sender.id,
                        "id":lisnInvitation.id,
                        "senderName": lisnInvitation.sender.fullName,
                        "fid":lisnInvitation.sender.facebook?.fid,
                        "messageType": "LISN_INVITATION",
                        "name":lisnInvitation.lisn.name,
                        "date_created":formatDateTimeStampForApi(timeStamp, lisnInvitation.dateCreated)]
            messages.add(aMessage)
		
        }
        //def joins = UserLISNMap.findAllByUser(nayaxUserInstance) - TODO:Remove hard-coded
        def joins = UserLISNMap.findAllByUserAndIdGreaterThan(nayaxUserInstance, 845)
				
        joins.each{thisJoin ->
            def lisn = thisJoin.lisn
			
            aMessage = lisn.getLISNSummary(timeStamp, this, nayaxUserInstance)
            //aMessage.location = GeoUtil.locationInfo(lisn.locationCoordinate)
            messages.add(aMessage)
			
        }
				
		
        def eventsUserParticipatedIn = nayaxUserInstance.getEventsUserParticipatedIn()
        eventsUserParticipatedIn.each{event ->
			
            def eventGoersMessage = getEventGoers(token, event.eventId, event.eventSource.toString())?.message
            def messagesCount = 0
            def externalEvent = ExternalEvent.findWhere(eventId:event.eventId)
            if (externalEvent) {
                messagesCount = EventMessage.findAllByExternalEvent(
                    externalEvent)?.size()
            }
			
            def eventDetails = [name:event.eventName,
                description:event.description,
                start_date:formatDateTimeStampForApi(timeStamp, event.startDate),
                city:event.city,
                state:event.state,
                messageType:'EXTERNAL_EVENT',
                logo_url:event.logoUrl,
                event_url:event.eventUrl,
                unviewedMessageCount:0,
                date_created:formatDateTimeStampForApi(timeStamp, event.lastUpdated),
                eventSource:event.eventSource.toString(),
                event_id: event.id,
                externalEventId:event.eventId,
                eventGoers:eventGoersMessage?.goers,
                isUserGoing:eventGoersMessage?.isUserGoing,
                messagesCount:messagesCount
            ]
			
            messages.add(eventDetails)
        }
        start = logTime('Time for LISN messages:' , start)
		
		
		
        messages = messages.sort{it.date_created}.reverse()
        message['newMessagesCount'] = newPrivateMessagesCount
        message['messages']=messages
		
        result["status"] = "success"
        result["message"] = message
        return result
    }
    
    /**
     * Returns data to be displayed in messages tab in app.
     * @param token
     * @param timeStamp
     * @return
     */
    def getMessageSummaryV3(token, timeStamp) {
        Map result = [:]
        def message = [:]
        def messages = []
        log.info('In method')
        def aMessage
        def sendersAdded = [] as Set
        def newPrivateMessages
        NayaxUser nayaxUserInstance = getUserFromToken(token);
        Date start = new Date()
        newPrivateMessages = PrivateMessage.findAllByReceiver(nayaxUserInstance)
        def senders = newPrivateMessages*.sender as Set
        def newPrivateMessagesCount = 0
        start = logTime('Senders calculated:', start)
        if (senders) {
            senders.each {sender ->
                def privateMessageList = PrivateMessage.createCriteria().list {
                    or {
                        and {
                            eq("sender", sender)
                            eq("receiver", nayaxUserInstance)
                        }
                        and {
                            eq("sender", nayaxUserInstance)
                            eq("receiver", sender)
                        }
                    }
                    order("id", "desc")
                    maxResults(1)
                }
                def privateMessageCount = PrivateMessage.createCriteria().get {
                    and {
                        eq("sender", sender)
                        eq("receiver", nayaxUserInstance)
                        eq("isViewed", "false")
                    }
                    projections{
                        count "id"
                    }
                }
                def privateMessage = privateMessageList.get(0)
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (sender.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = sender.linkedin?.thumbnailURL
                }
                if (sender.linkedin?.profilePictureURL) {
                    profilePicUrl = sender.linkedin?.profilePictureURL
                }
                
                aMessage = ["sender": sender.id,
                            "id":sender.id,
                            "fullName": sender.fullName,
                            "senderName": sender.fullName,
                            "fid": sender.facebook?.fid,
                            "profileThumbnailPicUrl": thumbnailPicUrl,
                            "profilePictureUrl": profilePicUrl,
                            "privateMessageCount": privateMessageCount,
                            "latestMessageContent": privateMessage.content,//privateMessage.content.toString(),
                            "messageType": "DIRECT_MESSAGE",
                            "isViewed": privateMessage.isViewed,
                            "date_created": formatDateTimeStampForApi(timeStamp, privateMessage.dateCreated)]
                messages.add(aMessage)
                if(!"false".equals(privateMessage.isViewed)){
                    newPrivateMessagesCount++
                }
                sendersAdded.add(sender.id)
			
            }
        } 
        // Start of the code block to get remaining one way messages where user is only sender.
        
        if (senders) {        
            newPrivateMessages = PrivateMessage.findAllBySenderAndReceiverNotInList(nayaxUserInstance, senders)            
        } else {
            newPrivateMessages = PrivateMessage.findAllBySender(nayaxUserInstance)  
        }
        def receivers = newPrivateMessages*.receiver as Set
        if (receivers) {
            receivers.each{receiver ->
                def privateMessageList = PrivateMessage.createCriteria().list {
                    
                    and {
                        eq("sender", nayaxUserInstance)
                        eq("receiver", receiver)
                    }
                    
                    order("id", "desc")
                    maxResults(1)
                }

                def privateMessage = privateMessageList.get(0)
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (receiver.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = receiver.linkedin?.thumbnailURL
                }
                if (receiver.linkedin?.profilePictureURL) {
                    profilePicUrl = receiver.linkedin?.profilePictureURL
                }

                aMessage = ["sender": receiver.id,
                            "id":receiver.id,
                            "fullName": receiver.fullName,
                            "senderName": receiver.fullName,
                            "fid": receiver.facebook?.fid,
                            "profileThumbnailPicUrl": thumbnailPicUrl,
                            "profilePictureUrl": profilePicUrl,
                            "privateMessageCount": 0,
                            "latestMessageContent": privateMessage.content,//privateMessage.content.toString(),
                            "messageType": "DIRECT_MESSAGE",
                            "isViewed": true,
                            "date_created": formatDateTimeStampForApi(timeStamp, privateMessage.dateCreated)]
                messages.add(aMessage)
                
                sendersAdded.add(receiver.id)

            }
        }
        //End of the code block
        start = logTime('Time for direct messages:' , start)
        def criteriaQuery = NConnection.createCriteria()
        def connectionRequestsReceived = criteriaQuery.list {
            and {
                eq("connection", nayaxUserInstance)
                eq("nConnectionStatus", NConnectionStatus.PENDING)
            }
        }
		

        def listAcceptedYourFriendRequest = NConnection.createCriteria().list {
            eq("owner", nayaxUserInstance)
            eq("nConnectionStatus", NConnectionStatus.CONNECTED)
        }
        def newNotifications=0

        connectionRequestsReceived.each {nConnection ->
            if(!sendersAdded.contains(nConnection.owner.id)){
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (nConnection.owner.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = nConnection.owner.linkedin?.thumbnailURL
                }
                if (nConnection.owner.linkedin?.profilePictureURL) {
                    profilePicUrl = nConnection.owner.linkedin?.profilePictureURL
                }
                
                aMessage = ["sender": nConnection.owner.id,
                            "id": nConnection.owner.id,
                            "fullName": nConnection.owner.fullName,
                            "senderName": nConnection.owner.fullName,
                            "fid": nConnection.owner.facebook?.fid,
                            "profileThumbnailPicUrl": thumbnailPicUrl,
                            "profilePictureUrl": profilePicUrl,
                            "state": nConnection.nConnectionStatus.toString(),
                            "messageType": "CONNECTION_REQUEST_RECEIVED",
                            "isNotified": nConnection.isNotified,
                            "date_created": formatDateTimeStampForApi(timeStamp, nConnection.dateCreated)]
                messages.add(aMessage)
                if(!nConnection.isNotified)
                newNotifications++
            }
        }
        listAcceptedYourFriendRequest.each {nConnection ->
            if(!sendersAdded.contains(nConnection.connection.id)){
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (nConnection.connection.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = nConnection.connection.linkedin?.thumbnailURL
                }
                if (nConnection.connection.linkedin?.profilePictureURL) {
                    profilePicUrl = nConnection.connection.linkedin?.profilePictureURL
                }
                
                aMessage = ["sender": nConnection.connection.id,
                            "id": nConnection.connection.id,
                            "fullName": nConnection.connection.fullName,
                            "senderName": nConnection.connection.fullName,
                            "fid": nConnection.connection.facebook?.fid,
                            "profileThumbnailPicUrl": thumbnailPicUrl,
                            "profilePictureUrl": profilePicUrl,
                            "state": nConnection.nConnectionStatus.toString(),
                            "messageType": "CONNECTION_REQUEST_ACCEPTED",
                            "isNotified": nConnection.isNotified,
                            "date_created": formatDateTimeStampForApi(timeStamp, nConnection.dateCreated)]
                messages.add(aMessage)
                if(!nConnection.isNotified)
                newNotifications++
				
            }
        }
        start = logTime('Time for connections received and accepted messages:', start)
		
        //TODO: Remove hard-coded.
        def lisnInvitations = LISNInvitation.findAllByReceiverAndInvitationAcceptedIsNullAndInvitationIgnoredIsNullAndIdGreaterThan(nayaxUserInstance,121)
        lisnInvitations.each{lisnInvitation ->
            def thumbnailPicUrl = defaultThumbnailPicUrl
            def profilePicUrl = defaultProfilePicUrl

            if (lisnInvitation.sender.linkedin?.thumbnailURL) {
                thumbnailPicUrl = lisnInvitation.sender.linkedin?.thumbnailURL
            }
            if (lisnInvitation.sender.linkedin?.profilePictureURL) {
                profilePicUrl = lisnInvitation.sender.linkedin?.profilePictureURL
            }
            
            aMessage = ["sender": lisnInvitation.sender.id,
                        "id":lisnInvitation.id,
                        "senderName": lisnInvitation.sender.fullName,
                        "fid":lisnInvitation.sender.facebook?.fid,
                        "profileThumbnailPicUrl": thumbnailPicUrl,
                        "profilePictureUrl": profilePicUrl,
                        "messageType": "LISN_INVITATION",
                        "name":lisnInvitation.lisn.name,
                        "date_created":formatDateTimeStampForApi(timeStamp, lisnInvitation.dateCreated)]
            messages.add(aMessage)
		
        }
        //def joins = UserLISNMap.findAllByUser(nayaxUserInstance) - TODO:Remove hard-coded
        def joins = UserLISNMap.findAllByUserAndIdGreaterThan(nayaxUserInstance, 845)
				
        joins.each{thisJoin ->
            def lisn = thisJoin.lisn
			
            aMessage = lisn.getLISNSummary(timeStamp, this, nayaxUserInstance)
            //aMessage.location = GeoUtil.locationInfo(lisn.locationCoordinate)
            messages.add(aMessage)
			
        }
				
		
        def eventsUserParticipatedIn = nayaxUserInstance.getEventsUserParticipatedIn()
        eventsUserParticipatedIn.each{event ->
			
            def eventGoersMessage = getEventGoers(token, event.eventId, event.eventSource.toString())?.message
            def messagesCount = 0
            def externalEvent = ExternalEvent.findWhere(eventId:event.eventId)
            if (externalEvent) {
                messagesCount = EventMessage.findAllByExternalEvent(
                    externalEvent)?.size()
            }
			
            def muteEventMessage = getMuteEventMessageInstance(nayaxUserInstance, event);
            def isMuteEnabled = false
            if (muteEventMessage) {
                isMuteEnabled = true;
            }
            def eventDetails = [name:event.eventName,
                description:event.description,
                start_date:formatDateTimeStampForApi(timeStamp, event.startDate),
                city:event.city,
                state:event.state,
                messageType:'EXTERNAL_EVENT',
                logo_url:event.logoUrl,
                event_url:event.eventUrl,
                unviewedMessageCount:0,
                date_created:formatDateTimeStampForApi(timeStamp, event.lastUpdated),
                eventSource:event.eventSource.toString(),
                event_id: event.id,
                externalEventId:event.eventId,
                eventGoers:eventGoersMessage?.goers,
                isUserGoing:eventGoersMessage?.isUserGoing,
                messagesCount:messagesCount,
                isMuteEnabled: isMuteEnabled
            ]
			
            messages.add(eventDetails)
        }
        
        def eventInvites = EventInvite.findWhere(receiver:nayaxUserInstance)
        eventInvites?.each{ eventInvite -> 
            def muteEventMessage = getMuteEventMessageInstance(nayaxUserInstance, eventInvite.externalEvent);
            def isMuteEnabled = false
            if (muteEventMessage) {
                isMuteEnabled = true;
            }
            def eventInviteDetails = [name:eventInvite.externalEvent.eventName,
                description:eventInvite.externalEvent.description,
                start_date:formatDateTimeStampForApi(timeStamp, eventInvite.externalEvent.startDate),
                messageType:'EVENT_INVITE',
                logo_url:eventInvite.externalEvent.logoUrl,
                event_url:eventInvite.externalEvent.eventUrl,
                date_created:formatDateTimeStampForApi(timeStamp, eventInvite.lastUpdated),
                eventSource:eventInvite.externalEvent.eventSource.toString(),
                event_id: eventInvite.externalEvent.id,
                externalEventId:eventInvite.externalEvent.eventId,
                "latestMessageContent":"${eventInvite.sender.fullName} has invited you to this event.",
                isMuteEnabled: isMuteEnabled
                
            ]
            
            messages.add(eventInviteDetails)
        }
                
        start = logTime('Time for LISN messages:' , start)
		
		
		
        messages = messages.sort{it.date_created}.reverse()
        message['newMessagesCount'] = newPrivateMessagesCount
        message['messages']=messages
		
        result["status"] = "success"
        result["message"] = message
        return result
    }
    
    /**
     *
     * @param params - id (lisnInvitationId, number), accept (true|false, boolean), timeStamp (users timezone offset), token
     * @return
     */
    def lisnInvitationResponse(params){
        Map result =[:]
        result.message=[:]
        def token = params.token
        def user = getUserFromToken(params.token);
        try{
            LISNInvitation lisnInvitation = LISNInvitation.findById(params.id)
            def lisn = lisnInvitation.lisn
            if(Boolean.valueOf(params.accept)){
                lisnInvitation.setInvitationAccepted(new Date())
                if (!UserLISNMap.countByLisnAndUser(lisn, user)) {
                    UserLISNMap userLISNMap = new UserLISNMap(user: user, lisn: lisn, profileShareType: ProfileShareType.ALL)
                    saveObject(userLISNMap)
                }
                LISNMessage lisnInviteMessage = LISNMessage.findWhere(user:user, lisn:lisn, messageType:LISNMessage.MessageType.INVITED);
				
                if(lisnInviteMessage){
                    lisnInviteMessage.setMessageType(LISNMessage.MessageType.JOINED)
                    saveObject(lisnInviteMessage)
                }
				
                //lisnDetail should be changed to use lisn.getLISNSummary
                def lisnDetail = lisn.getLISNDetail(params.timeStamp, this, user)
                result.message.lisnDetail = lisnDetail
                def messageInfoMap = [message_type:"LISN_MESSAGE", sender_id:user.id, fid:user.facebook?.fid, name:user.fullName]
				
                //This method uses dummy timeStamp as date is not sent in response. Needs refactoring.
                def lisnSummary = lisn.getLISNSummary("-7", this, user)
                messageInfoMap.members = lisnSummary.members
                messageInfoMap.moreMemberCount=lisnSummary.moreMemberCount
                messageInfoMap.lisnId = lisn.id
                messageInfoMap.lisnName = lisn.name
                def notificationMessage = "${user.fullName} joined "+lisn.name?lisn.name:''
                sendLISNMessageToDevice(user,
                    lisn.creator,
                    "LISN_MESSAGE",
                    result,
                    messageInfoMap,
                    notificationMessage,
                    "", "")
				
				
				
            }
            else {
                lisnInvitation.setInvitationIgnored(new Date())
                LISNMessage lisnInviteMessage = LISNMessage.findWhere(user:user, lisn:lisn, messageType:LISNMessage.MessageType.INVITED);
				
                if(lisnInviteMessage){
                    lisnInviteMessage.setMessageType(LISNMessage.MessageType.IGNORED)
                    saveObject(lisnInviteMessage)
                }
            }
			
            saveObject(lisnInvitation)
			
            result["status"] = "success"
            result.message.info = lisnInvitation.id +' response saved'
			
			
        }catch(Exception e){
            def errorMessage = 'Couldnot process LISN invitation response'
            log.error(errorMessage, e)
            result.status = 'error'
            result.message = errorMessage
        }
		
        return result
		
    }
	
    def getExistingToken(params) {
        Map result =[:]
        def token = MobileAuthToken.createCriteria().get(){
            nayaxUser{
                like('fullName', "${params.name}%")
            }
            order('dateCreated', 'desc')
            maxResults(1)
        }
        result['message']=token
        def user = token.nayaxUser
        result['user']=['Email/Login':user.username,
                        'facebook':user.facebook,
                        'linkedin':user.linkedin,
                        'fullName':user.fullName,
                        'device':user.iosDevice,
                        'lastUpdated':user.lastUpdated
        ]
        return result
    }	
	
    def getMessages(token) {
        Map result = [:]
        def message = [:]
        def dashBoardMessages = [:]
        def directMessagesFromDb

        def directMessages = [:]

        def nayaxUserInstance = getUserFromToken(token);
        def userLisns = UserLISNMap.findAllByUser(nayaxUserInstance, [sort:'lisn.endDate', order:'desc'])
        userLisns.each {userLisn ->
            println "working on lisn : ${userLisn.lisn}"
            def newMessagesCount
            if (userLisn.lastViewedMessageId) {
                newMessagesCount = LISNMessage.countByLisnAndIdGreaterThan(userLisn.lisn, userLisn.lastViewedMessageId.toInteger())
            } else {
                newMessagesCount = LISNMessage.countByLisn(userLisn.lisn)
            }
            println "newMessagesCount --------" + newMessagesCount
            dashBoardMessages[userLisn.lisn.id] = ["lisnName": userLisn.lisn.name, "count": newMessagesCount, "hasNewMessages":newMessagesCount>0]
			
        }
        directMessagesFromDb = PrivateMessage.findAllByReceiver(nayaxUserInstance)
        def senders = directMessagesFromDb*.sender as Set
        def newPrivateMessagesCount = directMessagesFromDb.size()
        println "newPrivateMessagesCount------------" + newPrivateMessagesCount
        if (senders) {
            senders.each {sender ->
                def privateMessageCount = PrivateMessage.createCriteria().list {
                    and {
                        eq("sender", sender)
                        eq("receiver", nayaxUserInstance)
                        eq("isViewed", "false")
                    }
                }.size()
                def totalMessageCount = PrivateMessage.createCriteria().list {
                    and {
                        eq("sender", sender)
                        eq("receiver", nayaxUserInstance)
                    }
                }.size()
                directMessages[sender.id] = ["sender": sender.id, "senderName": sender.fullName, "privateMessageCount": privateMessageCount, "isImage": sender?.picture ? "true" : "flase",
					"totalMessageCount": totalMessageCount]
            }
        }
        message['dashBoardMessages'] = dashBoardMessages
        message['newPrivateMessagesCount'] = newPrivateMessagesCount
        message['newPrivateMessages'] = directMessages
        result["status"] = "success"
        result["message"] = message
        return result
    }
    /**
     * @deprecated
     * @see #getNotificationCountV2()
     */
    def getNotificationCount(token) {
		
        int count = 0
		
        NayaxUser nayaxUserInstance = getUserFromToken(token);
		
        def criteriaQuery = NConnection.createCriteria()
        def countConnectionToBeAccepting = criteriaQuery.count {
            eq("connection", nayaxUserInstance)
            eq("nConnectionStatus", NConnectionStatus.PENDING)
            or {
                isNull("isNotified")
                eq("isNotified", false)
            }
        }
		
        def listAcceptedYourFriendRequest = NConnection.createCriteria().count {
            eq("owner", nayaxUserInstance)
            eq("nConnectionStatus", NConnectionStatus.CONNECTED)
            or {
                isNull("isNotified")
                eq("isNotified", false)
            }
        }
		
        def userLisns = UserLISNMap.findAllByUser(nayaxUserInstance)
        userLisns.each {userLisn ->
			
            if (userLisn.lastViewed == null && LISNMessage.countByLisn(userLisn.lisn) > 0) {
                count += 1
            } else {
                LISNMessage message = LISNMessage.findAllByLisn(userLisn.lisn).max {it.id}
                if (userLisn.lastViewedMessageId && message && userLisn.lastViewedMessageId.toInteger() < message.id) {
                    count += 1
                }
            }
        }
		
        def senderWhoSendPrivateMessage = PrivateMessage.findAllByReceiverAndIsViewed(nayaxUserInstance, "false")*.sender as Set
        def senderCount = senderWhoSendPrivateMessage.size()
        count= countConnectionToBeAccepting + count + senderCount + listAcceptedYourFriendRequest;
		
        return count
	
    }
    /**
     * This method is for badge count display on the apps.
     * @param user {@link #NayaxUser}
     * @return count of new messages/invitations that are not viewed by the user
     */
    def getNotificationCountV2(user){
        def newMessageCount = 0
        int privateMessageCount = PrivateMessage.createCriteria().get {
            and {
                eq("receiver", user)
                eq("isViewed", "false")
            }
            projections{
                count "id"
            }
        }
        log.info('DirectMessageCount:' +privateMessageCount)
        newMessageCount=newMessageCount+privateMessageCount
					
			
        def criteriaQuery = NConnection.createCriteria()
        int connectionRequestsReceived = criteriaQuery.get {
            and {
                eq("connection", user)
                eq("nConnectionStatus", NConnectionStatus.PENDING)
            }
            projections{
                count "id"
            }
        }
			
        log.info('connectionRequestsReceived:' +connectionRequestsReceived)
        newMessageCount=newMessageCount+connectionRequestsReceived
			
	
        /*TODO- to check if this is required.
         * int listAcceptedYourFriendRequest = NConnection.createCriteria().get {
        and {
        eq("owner", nayaxUserInstance)
        eq("nConnectionStatus", NConnectionStatus.CONNECTED)
        eq("isNotified",false)
        }
        projections{
        count "id"
        }
        }
        log.info('listAcceptedYourFriendRequest:' +listAcceptedYourFriendRequest)
        newMessageCount=newMessageCount+listAcceptedYourFriendRequest
         */
        int lisnInvitations = LISNInvitation.createCriteria().get{
            and {
                eq("receiver", user)
                isNull("invitationAccepted")
                isNull("invitationIgnored")
                //TODO: Remove hard-coded.
                gt("id", 121L)
            }
            projections{
                count "id"
            }
        }
        log.info('lisnInvitations:' +lisnInvitations)
			
        newMessageCount=newMessageCount+lisnInvitations
			
        //def joins = UserLISNMap.findAllByUser(nayaxUserInstance) - TODO:Remove hard-coded
        def joins = UserLISNMap.findAllByUserAndIdGreaterThan(user, 845)
        def newLisnMessagesCount = 0
        joins.each{thisJoin ->
            UserLISNMap lisnMember = UserLISNMap.findWhere(user:user, lisn:thisJoin.lisn)
            if(lisnMember?.lastViewedMessageId){
                int unviewedLisnMessageCount = LISNMessage.createCriteria().list {
                    eq("lisn", thisJoin.lisn)
                    ne("user", user)
                    gt("id", lisnMember.lastViewedMessageId as long)
                    //not {'in' ("messageType", [LISNMessage.MessageType.CREATED, LISNMessage.MessageType.INVITED, LISNMessage.MessageType.JOINED])}
                    or {
                        isNull("messageType")
                        eq("messageType", LISNMessage.MessageType.MESSAGE)
                    }
                }?.size()
                newLisnMessagesCount+=unviewedLisnMessageCount
            }
        }
        log.info('newLisnMessagesCount:' +newLisnMessagesCount)
			
        newMessageCount=newMessageCount+newLisnMessagesCount
        log.info('newMessageCount:' +newMessageCount)
			
			
        return newMessageCount
    }
	
	
    def getPictureUrl(fid){
	//	findByFidAndFacebookAccessTokenId(facebookConnection.userFacebookId, !null)
        def setting = Setting.findBySettingType(SettingType.PIC_WIDTH)
        def url = 'https://graph.facebook.com/' + fid +'/picture?width='+setting.value+'&height='+setting.value
        return url
    }

    def getPrivateMessage(token, receiver, timeStamp) {
        return getLatestDirectMessages(token, receiver, timeStamp, 0)
    }
	
    def getLatestDirectMessagesBefore(token, receiver, timeStamp, lastMessageId){
        def result = [:]
        def message = [:]
        def senderInstance = getUserFromToken(token);
        def receiverInstance
        def endOfMessages = false
		
        if (receiver.startsWith('f')){
            def facebookAccount = Facebook.findByFid(receiver.substring(1))
            if(facebookAccount){
                receiverInstance = facebookAccount.user
            }
        }else{
            receiverInstance =  getUserById(receiver);
        }
			
        if (receiverInstance) {

            def privateMessagesBySender = PrivateMessage.findAllByReceiverAndSenderAndIdLessThan(receiverInstance, senderInstance, lastMessageId)
	
            def privateMessagesByReceiver = PrivateMessage.findAllByReceiverAndSenderAndIdLessThan(senderInstance, receiverInstance, lastMessageId)
	
            def privateMessages = []
	
            privateMessages.addAll(privateMessagesBySender)
            privateMessages.addAll(privateMessagesByReceiver)
            privateMessages = privateMessages.sort {a, b ->
                a.dateCreated <=> b.dateCreated
            }
            def privateMessagesFirst50 = []
				
            def privateMessagesSize = privateMessages.size()
            if(privateMessagesSize>50)
            privateMessagesFirst50 = privateMessages[(privateMessagesSize-50)..privateMessagesSize-1]
            else{
                privateMessagesFirst50 = privateMessages
                endOfMessages = true
            }
					
            privateMessagesFirst50.each {privateMessage ->
                if (privateMessage.receiver.id == senderInstance.id) {
                    privateMessage.isViewed = "true"
                }
                message[privateMessage.id] = ["receiver": privateMessage.receiver.id,
                                                "sender": privateMessage.sender.id,
                                                "content": privateMessage.content,
                                                "dateCreated": formatDateTimeStampForApi(timeStamp, privateMessage.dateCreated)]
                if(privateMessage.picture){
                    message[privateMessage.id].isImage = true;
                    message[privateMessage.id].imageUrl = ConfigurationHolder.config.webImagePath +privateMessage.picture.filename
                    message[privateMessage.id].thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +privateMessage.picture.filename
                    message[privateMessage.id].imageId = privateMessage.picture.originalFilename
                    message[privateMessage.id].likedByUser =  DirectMessageLike.findWhere(privateMessage:privateMessage, user:senderInstance, unliked:false)!=null
						
                    message[privateMessage.id].likes = []
                    privateMessage.getLikes()?.each{messageLike ->
                        def messageLikedUser = messageLike.user
                        def likeDetails = ["id": messageLikedUser.id,
                                            "fid":messageLikedUser.fid,
                                            "fullName":messageLikedUser.fullName ]
                        message[privateMessage.id].likes.add(likeDetails)
                    }
                }
                if(PrivateMessage.MessageType.CONTACT_INFO.equals( privateMessage.messageType)){
                    message[privateMessage.id].messageType = PrivateMessage.MessageType.CONTACT_INFO.toString()
                }
            }
            message.endOfMessages = endOfMessages;

            result.status = "success"
        } else {
            result.status = "error"
        }
        result["receiverFid"] = receiverInstance?.facebook?.fid
		
        result["senderFid"] = senderInstance?.facebook?.fid
        result["message"] = message
		
        return result
    }
		
	
    def getLatestDirectMessages(token, receiver, timeStamp, lastMessageId){
        def result = [:]
        def message = [:]
        def senderInstance = getUserFromToken(token);
        def receiverInstance
		
        if (receiver.startsWith('f')){
            def facebookAccount = Facebook.findByFid(receiver.substring(1))
            if(facebookAccount){
                receiverInstance = facebookAccount.user
            }
        }else{
            receiverInstance =  getUserById(receiver);
        }
			
        if (receiverInstance) {

            def privateMessagesBySender = PrivateMessage.findAllByReceiverAndSenderAndIdGreaterThan(receiverInstance, senderInstance, lastMessageId)
	
            def privateMessagesByReceiver = PrivateMessage.findAllByReceiverAndSenderAndIdGreaterThan(senderInstance, receiverInstance, lastMessageId)
	
            def privateMessages = []
	
            privateMessages.addAll(privateMessagesBySender)
            privateMessages.addAll(privateMessagesByReceiver)
            privateMessages = privateMessages.sort {a, b ->
                a.dateCreated <=> b.dateCreated
            }
            def privateMessagesFirst50 = []
				
            def privateMessagesSize = privateMessages.size()
            if(privateMessagesSize>50)
            privateMessagesFirst50 = privateMessages[(privateMessagesSize-50)..privateMessagesSize-1]
            else
            privateMessagesFirst50 = privateMessages
					
            privateMessagesFirst50.each {privateMessage ->
                if (privateMessage.receiver.id == senderInstance.id) {
                    privateMessage.isViewed = "true"
                }
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (privateMessage.sender.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = privateMessage.sender.linkedin?.thumbnailURL
                }
                if (privateMessage.sender.linkedin?.profilePictureURL) {
                    profilePicUrl = privateMessage.sender.linkedin?.profilePictureURL
                }
                
                message[privateMessage.id] = ["receiver": privateMessage.receiver.id,
                                                "sender": privateMessage.sender.id,
                                                "profileThumbnailPicUrl": thumbnailPicUrl,
                                                "profilePictureUrl": profilePicUrl,
                                                "content": privateMessage.content,
                                                "dateCreated": formatDateTimeStampForApi(timeStamp, privateMessage.dateCreated)]
                if(privateMessage.picture){
                    message[privateMessage.id].isImage = true;
                    message[privateMessage.id].imageUrl = ConfigurationHolder.config.webImagePath +privateMessage.picture.filename
                    message[privateMessage.id].thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +privateMessage.picture.filename
                    message[privateMessage.id].imageId = privateMessage.picture.originalFilename
                    message[privateMessage.id].likedByUser =  DirectMessageLike.findWhere(privateMessage:privateMessage, user:senderInstance, unliked:false)!=null
						
                    message[privateMessage.id].likes = []
                    privateMessage.getLikes()?.each{messageLike ->
                        def messageLikedUser = messageLike.user
                        thumbnailPicUrl = defaultThumbnailPicUrl
                        profilePicUrl = defaultProfilePicUrl

                        if (messageLikedUser.linkedin?.thumbnailURL) {
                            thumbnailPicUrl = messageLikedUser.linkedin?.thumbnailURL
                        }
                        if (messageLikedUser.linkedin?.profilePictureURL) {
                            profilePicUrl = messageLikedUser.linkedin?.profilePictureURL
                        }
                        
                        def likeDetails = ["id": messageLikedUser.id,
                                            "fid":messageLikedUser.fid,
                                            "profileThumbnailPicUrl": thumbnailPicUrl,
                                            "profilePictureUrl": profilePicUrl,
                                            "fullName":messageLikedUser.fullName ]
                        message[privateMessage.id].likes.add(likeDetails)
                    }
                }
                if(PrivateMessage.MessageType.CONTACT_INFO.equals( privateMessage.messageType)){
                    message[privateMessage.id].messageType = PrivateMessage.MessageType.CONTACT_INFO.toString()
                }
            }

            result.status = "success"
        } else {
            result.status = "error"
        }
        result["receiverFid"] = receiverInstance?.facebook?.fid
        result["senderFid"] = senderInstance?.facebook?.fid
        result["message"] = message
		
        return result
    }
    
    def getLatestDirectMessagesV2(token, receiver, timeStamp, lastUpdated){
        def result = [:]
        def message = [:]
        def senderInstance = getUserFromToken(token);
        def receiverInstance
		
        if (receiver.startsWith('f')){
            def facebookAccount = Facebook.findByFid(receiver.substring(1))
            if(facebookAccount){
                receiverInstance = facebookAccount.user
            }
        }else{
            receiverInstance =  getUserById(receiver);
        }
			
        if (receiverInstance) {

            def privateMessagesBySender = PrivateMessage.findAllByReceiverAndSenderAndLastUpdatedGreaterThanEquals(receiverInstance, senderInstance, lastUpdated)
	
            def privateMessagesByReceiver = PrivateMessage.findAllByReceiverAndSenderAndLastUpdatedGreaterThanEquals(senderInstance, receiverInstance, lastUpdated)
            
            //def messageLikes = DirectMessageLikes.findAllByLastUpdatedGreaterThanEquals(lastUpdated)
	
            def privateMessages = []
	
            privateMessages.addAll(privateMessagesBySender)
            privateMessages.addAll(privateMessagesByReceiver)
            
            //            messageLikes.each{ messageLike ->
            //                privateMessages.add(messageLike.privateMessage)
            //            }
            
            privateMessages = privateMessages.sort {a, b ->
                a.dateCreated <=> b.dateCreated
            }
            def privateMessagesFirst50 = []
				
            def privateMessagesSize = privateMessages.size()
            if(privateMessagesSize>50)
            privateMessagesFirst50 = privateMessages[(privateMessagesSize-50)..privateMessagesSize-1]
            else
            privateMessagesFirst50 = privateMessages
					
            privateMessagesFirst50.each {privateMessage ->
                if (privateMessage.receiver.id == senderInstance.id) {
                    privateMessage.isViewed = "true"
                }
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (privateMessage.sender.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = privateMessage.sender.linkedin?.thumbnailURL
                }
                if (privateMessage.sender.linkedin?.profilePictureURL) {
                    profilePicUrl = privateMessage.sender.linkedin?.profilePictureURL
                }
                
                message[privateMessage.id] = ["receiver": privateMessage.receiver.id,
                                                "sender": privateMessage.sender.id,
                                                "profileThumbnailPicUrl": thumbnailPicUrl,
                                                "profilePictureUrl": profilePicUrl,
                                                "content": privateMessage.content,
                                                "dateCreated": formatDateTimeStampForApi(timeStamp, privateMessage.dateCreated),
                                                "lastUpdated": formatDateTimeStampForApi(timeStamp, privateMessage.lastUpdated)]
                if(privateMessage.picture){
                    message[privateMessage.id].isImage = true;
                    message[privateMessage.id].imageUrl = ConfigurationHolder.config.webImagePath +privateMessage.picture.filename
                    message[privateMessage.id].thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +privateMessage.picture.filename
                    message[privateMessage.id].imageId = privateMessage.picture.originalFilename
                    message[privateMessage.id].likedByUser =  DirectMessageLike.findWhere(privateMessage:privateMessage, user:senderInstance, unliked:false)!=null
						
                    message[privateMessage.id].likes = []
                    privateMessage.getLikes()?.each{messageLike ->
                        def messageLikedUser = messageLike.user
                        thumbnailPicUrl = defaultThumbnailPicUrl
                        profilePicUrl = defaultProfilePicUrl

                        if (messageLikedUser.linkedin?.thumbnailURL) {
                            thumbnailPicUrl = messageLikedUser.linkedin?.thumbnailURL
                        }
                        if (messageLikedUser.linkedin?.profilePictureURL) {
                            profilePicUrl = messageLikedUser.linkedin?.profilePictureURL
                        }
                        
                        def likeDetails = ["id": messageLikedUser.id,
                                            "fid":messageLikedUser.fid,
                                            "profileThumbnailPicUrl": thumbnailPicUrl,
                                            "profilePictureUrl": profilePicUrl,
                                            "fullName":messageLikedUser.fullName ]
                        message[privateMessage.id].likes.add(likeDetails)
                    }
                }
                if(PrivateMessage.MessageType.CONTACT_INFO.equals( privateMessage.messageType)){
                    message[privateMessage.id].messageType = PrivateMessage.MessageType.CONTACT_INFO.toString()
                }
            }

            result.status = "success"
        } else {
            result.status = "error"
        }
        result["receiverFid"] = receiverInstance?.facebook?.fid
        result["senderFid"] = senderInstance?.facebook?.fid
        result["message"] = message
		
        return result
    }
	
    /**
     *
     * @param token - user token
     * @param id - lisn id
     * @param timeStamp - user timezone offset
     * @param lastMessageId - last message id
     * @return returns messages and lisn members
     */
    def getLisnMessagesV2(token, id, timeStamp, lastMessageId){
        def result = [:]
        def message = [:]
        def user = getUserFromToken(token);
        def lisn = LISN.findById(id)
        if (lisn) {
            //message.members = lisn.getLisnMembers()
			
				
            def lisnMessages = LISNMessage.findAllByLisnAndIdGreaterThan(lisn, lastMessageId)
            def currentLastMessageId = 0
            lisnMessages.each {lisnMessage ->
                def aMessage = ["sender": lisnMessage.user.id,
                                "senderFid":lisnMessage.user?.facebook?.fid,
                                "content": lisnMessage.content,
                                "dateCreated": formatDateTimeStampForApi(timeStamp, lisnMessage.dateCreated),
                                "fullName":lisnMessage.user.fullName ]
                
                if(lisnMessage.picture){
                    aMessage.isImage = true;
                    aMessage.imageUrl = ConfigurationHolder.config.webImagePath +lisnMessage.picture.filename
                    aMessage.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +lisnMessage.picture.filename
                    aMessage.imageId = lisnMessage.picture.originalFilename
						
                    aMessage.likedByUser =  MessageLike.findWhere(lisnMessage:lisnMessage, user:user, unliked:false)!=null
						
                    aMessage.likes = []
                    lisnMessage.getLikes()?.each{messageLike ->
                        def messageLikedUser = messageLike.user
                        def likeDetails = ["id": messageLikedUser.id,
                                            "fid":messageLikedUser.fid,
                                            "fullName":messageLikedUser.fullName ]
                        aMessage.likes.add(likeDetails)
                    }
                }
                if(lisnMessage.messageType.equals(LISNMessage.MessageType.CONTACT_INFO)){
                    aMessage.messageType= LISNMessage.MessageType.CONTACT_INFO.toString()
                }
                else if(lisnMessage.messageType && !lisnMessage.messageType.equals(LISNMessage.MessageType.MESSAGE)){
                    aMessage.messageType = 'INFO'
                    aMessage.content = lisnMessage.messageType.toString().toLowerCase()
                    if(lisnMessage.messageType.equals(LISNMessage.MessageType.JOINED)){
                        //In case of joined, the lastUpdated date is the date of joining and date created is the date of invitation.
                        aMessage.dateCreated = formatDateTimeStampForApi(timeStamp, lisnMessage.lastUpdated)
                    }
                }
                message[lisnMessage.id] = aMessage
            }
            if(lisnMessages.size()>0){
                def lastLisnMessage = lisnMessages.get(lisnMessages.size()-1)
                if(lastLisnMessage.messageType==null || lastLisnMessage.messageType.equals(LISNMessage.MessageType.MESSAGE)
                    ||lastLisnMessage.messageType.equals(LISNMessage.MessageType.CONTACT_INFO)){
                    currentLastMessageId = lastLisnMessage.id
                }
            }
            if(currentLastMessageId!=0){
                UserLISNMap lisnMember = UserLISNMap.findWhere(user:user, lisn:lisn)
                if(lisnMember){
                    lisnMember.lastViewedMessageId = currentLastMessageId
                    saveObject(lisnMember)
                }
            }			
	
            result.status = "success"
        } else {
            result.status = "error"
        }
		
		
        result["message"] = message
		
        return result
	
	
    }
    
    /**
     *
     * @param token - user token
     * @param id - lisn id
     * @param timeStamp - user timezone offset
     * @param lastMessageId - last message id
     * @return returns messages and lisn members
     */
    def getLisnMessagesV3(token, id, timeStamp, lastMessageId){
        def result = [:]
        def message = [:]
        def user = getUserFromToken(token);
        def lisn = LISN.findById(id)
        if (lisn) {
            //message.members = lisn.getLisnMembers()
			
				
            def lisnMessages = LISNMessage.findAllByLisnAndIdGreaterThan(lisn, lastMessageId)
            def currentLastMessageId = 0
            
            lisnMessages.each {lisnMessage ->
                def thumbnailPicUrl = defaultThumbnailPicUrl
                def profilePicUrl = defaultProfilePicUrl
                
                if (lisnMessage.user?.linkedin?.thumbnailURL) {
                    thumbnailPicUrl = lisnMessage.user?.linkedin?.thumbnailURL
                }
                if (lisnMessage.user?.linkedin?.profilePictureURL) {
                    profilePicUrl = lisnMessage.user?.linkedin?.profilePictureURL
                }
                def aMessage = ["sender": lisnMessage.user.id,
                                "senderFid":lisnMessage.user?.facebook?.fid,
                                "profileThumbnailPicUrl": thumbnailPicUrl,
                                "profilePictureUrl": profilePicUrl,
                                "content": lisnMessage.content,
                                "dateCreated": formatDateTimeStampForApi(timeStamp, lisnMessage.dateCreated),
                                "fullName":lisnMessage.user.fullName ]
                
                if(lisnMessage.picture){
                    aMessage.isImage = true;
                    aMessage.imageUrl = ConfigurationHolder.config.webImagePath +lisnMessage.picture.filename
                    aMessage.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +lisnMessage.picture.filename
                    aMessage.imageId = lisnMessage.picture.originalFilename
						
                    aMessage.likedByUser =  MessageLike.findWhere(lisnMessage:lisnMessage, user:user, unliked:false)!=null
						
                    aMessage.likes = []
                    lisnMessage.getLikes()?.each{messageLike ->
                        def messageLikeThumbnailPicUrl = defaultThumbnailPicUrl
                        def messageLikeProfilePicUrl = defaultProfilePicUrl
                        def messageLikedUser = messageLike.user
                        if (messageLikedUser?.linkedin?.thumbnailURL) {
                            messageLikeThumbnailPicUrl = messageLikedUser?.linkedin?.thumbnailURL
                        }
                        if (messageLikedUser?.linkedin?.profilePictureURL) {
                            messageLikeProfilePicUrl = messageLikedUser?.linkedin?.profilePictureURL
                        }
                        def likeDetails = ["id": messageLikedUser.id,
                                            "fid":messageLikedUser.fid,
                                            "profileThumbnailPicUrl": messageLikeThumbnailPicUrl,
                                            "profilePictureUrl": messageLikeProfilePicUrl,
                                            "fullName":messageLikedUser.fullName ]
                        aMessage.likes.add(likeDetails)
                    }
                }
                if(lisnMessage.messageType.equals(LISNMessage.MessageType.CONTACT_INFO)){
                    aMessage.messageType= LISNMessage.MessageType.CONTACT_INFO.toString()
                }
                else if(lisnMessage.messageType && !lisnMessage.messageType.equals(LISNMessage.MessageType.MESSAGE)){
                    aMessage.messageType = 'INFO'
                    aMessage.content = lisnMessage.messageType.toString().toLowerCase()
                    if(lisnMessage.messageType.equals(LISNMessage.MessageType.JOINED)){
                        //In case of joined, the lastUpdated date is the date of joining and date created is the date of invitation.
                        aMessage.dateCreated = formatDateTimeStampForApi(timeStamp, lisnMessage.lastUpdated)
                    }
                }
                message[lisnMessage.id] = aMessage
            }
            if(lisnMessages.size()>0){
                def lastLisnMessage = lisnMessages.get(lisnMessages.size()-1)
                if(lastLisnMessage.messageType==null || lastLisnMessage.messageType.equals(LISNMessage.MessageType.MESSAGE)
                    ||lastLisnMessage.messageType.equals(LISNMessage.MessageType.CONTACT_INFO)){
                    currentLastMessageId = lastLisnMessage.id
                }
            }
            if(currentLastMessageId!=0){
                UserLISNMap lisnMember = UserLISNMap.findWhere(user:user, lisn:lisn)
                if(lisnMember){
                    lisnMember.lastViewedMessageId = currentLastMessageId
                    saveObject(lisnMember)
                }
            }			
	
            result.status = "success"
        } else {
            result.status = "error"
        }
		
        result["message"] = message
		
        return result
	
	
    }
	
    def sendLISNMessageToDevice(NayaxUser sender, NayaxUser user, String messageType, def result, def messageInfoMap, String messageKey, String... arguments) {
        if(sender?.id == user?.id)
        return result
		
        Device device = user.iosDevice
		
        if(device){
            if("android".equals(device.deviceType)){
                try{
                    JSONArray channelArray = new JSONArray()
                    channelArray.add(device.token);
                    String response = "No Response";
                    JSONObject dataJson = new JSONObject();
                    JSONObject jsonObject = dataJson;
                    jsonObject.put("channels", channelArray);
                    dataJson.put("message_type", messageType);
                    dataJson.put("sender_id", sender.id);
                    dataJson.put("lisn_id", messageInfoMap.lisnId);
                    dataJson.put("fid", sender.facebook?.fid);
                    dataJson.put("name", sender.fullName);
                    dataJson.put("details", messageInfoMap)
                    dataJson.put("alert", messageKey);
                    jsonObject.put("data", dataJson.toString() );
                    ParseUtil.sendNotification(jsonObject.toString())
                }catch (Exception e) {
                    log.error("Could not connect to PARSE to send the notification", e)
                    log.error("error sending push notifications", e);
                    result.status = "error sending PARSE push notification "
                }
            }else {
                //TODO: assuming other devices are ios, at this point. Need to fix this.
			
                try{
                    //def messageInfoMap = [message_type:messageType, sender_id:sender.id, fid:sender.facebook?.fid, name:sender.fullName]
                    def payload = APNS.newPayload()
                    .badge( getNotificationCountV2(sender))
                    .localizedKey(messageKey)
                    .localizedArguments(arguments)
                    .sound("default")
                    .customFields(messageInfoMap)
	
                    if (payload.isTooLong()) log.info("Message is too long: " + payload.length())
                    try {
                        ApnsNotification notification = new SimpleApnsNotification(device.token, payload.build())
                        log.info("Pushing APNS notification:")
                        log.info(notification.toString())
                        apnsService.push(notification)
                    } catch (Exception e) {
                        log.error("Could not connect to APNs to send the notification", e)
                    }
                }catch(Exception e){
                    log.error("error sending push notifications", e);
                    result.status = "error sending APNS push notification"
                }
            }

        }
        return result
		
    }
	
    def sendLISNMessageToDeviceV2(NayaxUser sender, NayaxUser user, String messageType, def result, def messageInfoMap, String messageKey, def muteMessage, String... arguments) {
        if(sender?.id == user?.id)
        return result
		
        Device device = user.iosDevice
		
        if(device){
            
            def endDate
            def currentDate = new Date();
            def isMuteSoundNotification = false
            def showNotification = true
                    
            if (muteMessage) {      
                isMuteSoundNotification = true
                showNotification = muteMessage.showNotification
            }                    
            
            if("android".equals(device.deviceType)){
                try{
                    JSONArray channelArray = new JSONArray()
                    channelArray.add(device.token);
                    String response = "No Response";
                    JSONObject dataJson = new JSONObject();
                    JSONObject jsonObject = dataJson;
                    jsonObject.put("channels", channelArray);
                    dataJson.put("message_type", messageType);
                    dataJson.put("sender_id", sender.id);
                    dataJson.put("lisn_id", messageInfoMap.lisnId);
                    dataJson.put("fid", sender.facebook?.fid);
                    dataJson.put("name", sender.fullName);
                    dataJson.put("details", messageInfoMap)
                    dataJson.put("alert", messageKey);
                    dataJson.put("mute_sound_notification", isMuteSoundNotification)
                    dataJson.put("showNotification", showNotification)
                    jsonObject.put("data", dataJson.toString() );
                    ParseUtil.sendNotification(jsonObject.toString())
                }catch (Exception e) {
                    log.error("Could not connect to PARSE to send the notification", e)
                    log.error("error sending push notifications", e);
                    result.status = "error sending PARSE push notification "
                }
            }else {
                //TODO: assuming other devices are ios, at this point. Need to fix this.

                try{
                    //def messageInfoMap = [message_type:messageType, sender_id:sender.id, fid:sender.facebook?.fid, name:sender.fullName]
                    messageInfoMap.mute_sound_notification = isMuteSoundNotification
                    messageInfoMap.showNotification = showNotification 
                    def payload = APNS.newPayload()
                    .badge( getNotificationCountV2(sender))
                    .localizedKey(StringEscapeUtils.unescapeJava(messageKey))
                    .localizedArguments(arguments)
                    .customFields(messageInfoMap)
                    
                    if (!isMuteSoundNotification) {
                        payload.sound("default")
                    }

                    if (payload.isTooLong()) log.info("Message is too long: " + payload.length())
                    try {
                        ApnsNotification notification = new SimpleApnsNotification(device.token, payload.build())
                        log.info("Pushing APNS notification:")
                        log.info(notification.toString())
                        apnsService.push(notification)
                    } catch (Exception e) {
                        log.error("Could not connect to APNs to send the notification", e)
                    }
                }catch(Exception e){
                    log.error("error sending push notifications", e);
                    result.status = "error sending APNS push notification"
                }
            }

        }
        return result
		
    }
    
    def sendMessageToDevice(NayaxUser sender, NayaxUser user, String messageType, def result, String messageKey, String... arguments) {
        Device device = user.iosDevice
		
        if(device){
            if("android".equals(device.deviceType)){
                try{
                    JSONArray channelArray = new JSONArray()
                    channelArray.add(device.token);
                    String response = "No Response";
                    JSONObject dataJson = new JSONObject();
                    JSONObject jsonObject = dataJson;
                    jsonObject.put("channels", channelArray);
                    dataJson.put("message_type", messageType);
                    dataJson.put("sender_id", sender.id);
                    dataJson.put("fid", sender.facebook?.fid);
                    dataJson.put("name", sender.fullName);
                    dataJson.put("alert", messageKey);
                    jsonObject.put("data", dataJson.toString() );
                    ParseUtil.sendNotification(jsonObject.toString())
                }catch (Exception e) {
                    log.error("Could not connect to PARSE to send the notification", e)
                    log.error("error sending push notifications", e);
                    result.status = "error sending PARSE push notification "
                }
            }else {
                //TODO: assuming other devices are ios, at this point. Need to fix this.
			
                try{
                    def messageInfoMap = [message_type:messageType, sender_id:sender.id, fid:sender.facebook?.fid, name:sender.fullName, 
                        profileThumbnailPicUrl: sender.linkedin?.thumbnailURL != null ? sender.linkedin?.thumbnailURL : defaultThumbnailPicUrl,
                        profilePictureUrl: sender.linkedin?.profilePictureURL != null ? sender.linkedin?.profilePictureURL : defaultProfilePicUrl]
                    def payload = APNS.newPayload()
                    .badge( getNotificationCountV2(sender))
                    .localizedKey(StringEscapeUtils.unescapeJava(messageKey))
                    .localizedArguments(arguments)
                    .sound("default")
                    .customFields(messageInfoMap)
	
                    if (payload.isTooLong()) log.info("Message is too long: " + payload.length())
                    try {
                        ApnsNotification notification = new SimpleApnsNotification(device.token, payload.build())
                        log.info("Pushing APNS notification:")
                        log.info(notification.toString())
                        apnsService.push(notification)
                    } catch (Exception e) {
                        log.error("Could not connect to APNs to send the notification", e)
                    }
                }catch(Exception e){
                    log.error("error sending push notifications", e);
                    result.status = "error sending APNS push notification"
                }
            }

        }
        return result
		
    }
	
    /*def sendMessageToAndroid(NayaxUser user, def result, String userToken, String messageKey, String... arguments){
    androidGcmService.sendMessage();
    }
	
    def index() {
    androidGcmService.sendMessage(
    [
    aMessageKey : 'The message value for the key aMessageKey',
    anotherMessageKey : 'The message value for the key anotherMessageKey'
    ]);
    }
     */
	
	
	
	
    def updateBadgeCount(token, newCount) {
        def result = [:]
        def message = [:]
        def user = getUserFromToken(token);
        if(user.iosDevice){
            def userDevice = user.iosDevice
            userDevice.badgeCount = newCount
			
            boolean flag = userDevice.save(flush:true);
            if (!flag) {
                result['status'] = "error"
                result['message'] = "Error updatingBadgeCount. Please try again."
            } else {
                result['status'] = "success"
                message['details']="Updated badge count."
                message['newCount'] = userDevice.badgeCount
                result['message'] = message
            }
        }
    }
    def decrementBadgeCountByOne(token){
        def result = [:]
        def message = [:]
        def user = getUserFromToken(token);
        if(user.iosDevice){
            def userDevice = user.iosDevice
            userDevice.badgeCount = userDevice.badgeCount -1
			
            boolean flag = userDevice.save(flush:true);
            if (!flag) {
                result['status'] = "error"
                result['message'] = "Error updatingBadgeCount. Please try again."
            } else {
                result['status'] = "success"
                message['details']="Updated badge count."
                message['newCount'] = userDevice.badgeCount
                result['message'] = message
            }
        }
    }
	
    def getBadgeCount(token) {
        def result = [:]
        def message = [:]
        def user = getUserFromToken(token);
        if(user.iosDevice){
            result['status'] = "success"
            message['badgeCount']=user.iosDevice.badgeCount
            result['message'] = message
				
        }
        else {
            result['status'] = "error"
            message['details']="No registered device for this user."
            result['message'] = message
        }
    }
    /**
     * @deprecated
     * @see #sendPrivateMessageV2()
     * @param token
     * @param receiver
     * @param content
     * @return
     */
    def sendPrivateMessage(token, receiver, content) {
        def result = [:]
        def senderInstance = getUserFromToken(token);
        def receiverInstance = NayaxUser.get(receiver);
        if (receiverInstance) {
            def privateMessageInstance = new PrivateMessage(receiver: receiverInstance, sender: senderInstance, content: content)
            if (privateMessageInstance.save(flush: true)) {
                result.status = "success"
                result.message = "success"
                result = sendMessageToDevice(senderInstance, receiverInstance, "DIRECT_MESSAGE", result, "${senderInstance.fullName}: ${content}")
            } else {
                result.status = "error"
            }
        } else {
            result.status = "receiver Not Found"
        }

        return result
    }
    /**
     * @param messageId (optional unique identifier for async responses)
     * @param token
     * @param receiver
     * @param content
     * @param image Also accepts image and params should contain image along with a file in request of
     *         content_type ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']
     * @return
     */
    def sendPrivateMessageV2(messageId, token, receiver, content, image){
        def result = [:]
        result.message = [:]
        result.message.messageId = messageId?messageId:'No_Message_Id'
		 
        def senderInstance = getUserFromToken(token);
        def receiverInstance = NayaxUser.get(receiver);
        result.message.receiver = receiverInstance.id
        result.message.sender = senderInstance.id
        result.message.content=content
        def picture = null
        if (receiverInstance) {
            log.info("From receiver:" + receiverInstance.fullName)
            log.info "Image Value: ${image}"
            if(image){
                def multipartFile = request.getFile("image");
                log.info("File type/name : "+ multipartFile.getContentType()+":"+multipartFile.getOriginalFilename())
                if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
                    def filename = UUID.randomUUID().toString()
                    def originalFilename = multipartFile.getOriginalFilename()
                    def fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length())
					 
                    picture = new Picture(mimeType: multipartFile.getContentType(),
                        originalFilename: originalFilename,
                        fileSize: multipartFile.getSize(),
                        filename: filename+fileExtension,
                        filePath: ConfigurationHolder.config.pictureLocation)
                    log.info " mimeType  " + multipartFile.getContentType()
                    log.info " originalFilename  " + originalFilename
                    log.info " filePath  " + ConfigurationHolder.config.pictureLocation
                    if (picture.validate()) {
                        picture.save()
                        File targetFile = new File(ConfigurationHolder.config.pictureLocation, filename +fileExtension )
                        multipartFile.transferTo(targetFile)
                        File targetThumbNailFile = new File(ConfigurationHolder.config.pictureLocation, 'tn_' +filename +fileExtension )
                        def thumbnailImage = imageService.getThumbNail(targetFile, Setting.findBySettingType(SettingType.PIC_WIDTH).value as Integer)
                        ImageIO.write(thumbnailImage, fileExtension.substring(1), targetThumbNailFile);
						 
                    }
                }
            }
            def privateMessageInstance = new PrivateMessage(receiver: receiverInstance, sender: senderInstance, content: content)
            if(picture){
                privateMessageInstance.picture= picture
                result.message.isImage = true
                result.message.imageId=picture.originalFilename
                result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
                result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
                result.message.likedCount = 0
                result.message.likedByUser=false
                result.message.likes = []
				 
            }
            if (privateMessageInstance.save(flush: true)) {
                result.status = "success"
                def notificationMessage = "${senderInstance.fullName}: ${content}"
                if(picture)
                notificationMessage = "${senderInstance.fullName} shared an image with you."
					 
                result = sendMessageToDevice(senderInstance, receiverInstance, "DIRECT_MESSAGE", result, notificationMessage )
            } else {
                result.status = "error"
                result.message = "error saving message on server"
            }
			 
        }else {
            result.status = "error"
            result.message = "receiver Not Found"
        }

        return result
    }
    
    /**
     * @param messageId (optional unique identifier for async responses)
     * @param token
     * @param receiver
     * @param content
     * @param image Also accepts image and params should contain image along with a file in request of
     *         content_type ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']
     * @param suggestedImageUrl
     * @return
     */
    def sendPrivateMessageV3(messageId, token, receiver, content, image, suggestedImageUrl){
        def result = [:]
        result.message = [:]
        result.message.messageId = messageId?messageId:'No_Message_Id'
		 
        def senderInstance = getUserFromToken(token);
        def receiverInstance = NayaxUser.get(receiver);
        result.message.receiver = receiverInstance.id
        result.message.sender = senderInstance.id
        result.message.content = content
        def picture = null
        if (receiverInstance) {
            log.info("From receiver:" + receiverInstance.fullName)
            log.info "Image Value: ${image}"
            if(image){
                def multipartFile = request.getFile("image");
                log.info("File type/name : "+ multipartFile.getContentType()+":"+multipartFile.getOriginalFilename())
                if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
                    def filename = UUID.randomUUID().toString()
                    def originalFilename = multipartFile.getOriginalFilename()
                    def fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length())
					 
                    picture = new Picture(mimeType: multipartFile.getContentType(),
                        originalFilename: originalFilename,
                        fileSize: multipartFile.getSize(),
                        filename: filename+fileExtension,
                        filePath: ConfigurationHolder.config.pictureLocation)
                    log.info " mimeType  " + multipartFile.getContentType()
                    log.info " originalFilename  " + originalFilename
                    log.info " filePath  " + ConfigurationHolder.config.pictureLocation
                    if (picture.validate()) {
                        picture.save()
                        File targetFile = new File(ConfigurationHolder.config.pictureLocation, filename + fileExtension )
                        multipartFile.transferTo(targetFile)
                        File targetThumbNailFile = new File(ConfigurationHolder.config.pictureLocation, 'tn_' +filename + fileExtension )
                        def thumbnailImage = imageService.getThumbNail(targetFile, Setting.findBySettingType(SettingType.PIC_WIDTH).value as Integer)
                        ImageIO.write(thumbnailImage, fileExtension.substring(1), targetThumbNailFile);
						 
                    }
                }
            }
            if (suggestedImageUrl) {
                
                picture = getPicture(suggestedImageUrl)
            }
            
            def privateMessageInstance = new PrivateMessage(receiver: receiverInstance, sender: senderInstance, content: content)
            if(picture){
                privateMessageInstance.picture= picture
                result.message.isImage = true
                result.message.imageId = picture.originalFilename
                result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
                result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
                result.message.likedCount = 0
                result.message.likedByUser=false
                result.message.likes = []
				 
            }
            if (privateMessageInstance.save(flush: true)) {
                result.status = "success"
                def notificationMessage = "${senderInstance.fullName}: ${content}"
                if(picture) {
                    if (content == null) {
                        notificationMessage = "${senderInstance.fullName} shared an image with you."
                        
                    }                    
                }
					 
                result = sendMessageToDevice(senderInstance, receiverInstance, "DIRECT_MESSAGE", result, notificationMessage )
            } else {
                result.status = "error"
                result.message = "error saving message on server"
            }
			 
        }else {
            result.status = "error"
            result.message = "receiver Not Found"
        }

        return result
    }
    
    private getPicture(def suggestedImageUrl) {
        def fileName = UUID.randomUUID().toString()
        def fileExtension = suggestedImageUrl.substring(suggestedImageUrl.lastIndexOf("."), suggestedImageUrl.length())
        
        def localImageFile = imageService.downloadToLocal(suggestedImageUrl, fileName, fileExtension)
        def picture = new Picture(mimeType: "image/jpeg",
            originalFilename: suggestedImageUrl,
            fileSize: localImageFile.length(),
            filename: fileName + fileExtension,
            filePath: ConfigurationHolder.config.pictureLocation)
        
        
        log.info " originalFilename  " + suggestedImageUrl
        log.info " filePath  " + ConfigurationHolder.config.pictureLocation
        
        if (picture.validate()) {
            picture.save()
            File thumbNailFile = new File(ConfigurationHolder.config.pictureLocation, "tn_" + fileName + fileExtension )
            def thumbNailImage = imageService.getThumbNail(localImageFile, Setting.findBySettingType(SettingType.PIC_WIDTH).value as Integer)
            ImageIO.write(thumbNailImage, fileExtension.substring(1), thumbNailFile);
            
        }
        
        return picture
    }
    /**
     * @param messageId (optional unique identifier for async responses)
     * @param token
     * @param receiver
     * @param content
     * @param image Also accepts image and params should contain image along with a file in request of
     *         content_type ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']
     * @param multipartFile
     * @return
     */
    def sendPrivateMessageWithMultipartInParam(messageId, token, receiver, content, image, multipartFile){
        def result = [:]
        result.message = [:]
        result.message.messageId = messageId?messageId:'No_Message_Id'
		 
        def senderInstance = getUserFromToken(token);
        def receiverInstance = NayaxUser.get(receiver);
        result.message.receiver = receiverInstance.id
        result.message.sender = senderInstance.id
        result.message.content=content
        def picture = null
        if (receiverInstance) {
            log.info("From receiver:" + receiverInstance.fullName)
            log.info "Image Value: ${image}"
            if(image){
                log.info("File type/name : "+ multipartFile.getContentType()+":"+multipartFile.getOriginalFilename())
                if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
                    def filename = UUID.randomUUID().toString()
                    def originalFilename = multipartFile.getOriginalFilename()
                    def fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length())
					 
                    picture = new Picture(mimeType: multipartFile.getContentType(),
                        originalFilename: originalFilename,
                        fileSize: multipartFile.getSize(),
                        filename: filename+fileExtension,
                        filePath: ConfigurationHolder.config.pictureLocation)
                    log.info " mimeType  " + multipartFile.getContentType()
                    log.info " originalFilename  " + originalFilename
                    log.info " filePath  " + ConfigurationHolder.config.pictureLocation
                    if (picture.validate()) {
                        picture.save()
                        File targetFile = new File(ConfigurationHolder.config.pictureLocation, filename +fileExtension )
                        multipartFile.transferTo(targetFile)
                        File targetThumbNailFile = new File(ConfigurationHolder.config.pictureLocation, 'tn_' +filename +fileExtension )
                        def thumbnailImage = imageService.getThumbNail(targetFile, Setting.findBySettingType(SettingType.PIC_WIDTH).value as Integer)
                        ImageIO.write(thumbnailImage, fileExtension.substring(1), targetThumbNailFile);
						 
                    }
                }
            }
            def privateMessageInstance = new PrivateMessage(receiver: receiverInstance, sender: senderInstance, content: content)
            if(picture){
                privateMessageInstance.picture= picture
                result.message.isImage = true
                result.message.imageId=picture.originalFilename
                result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
                result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
                result.message.likedCount = 0
                result.message.likedByUser=false
                result.message.likes = []
				 
            }
            if (privateMessageInstance.save(flush: true)) {
                result.status = "success"
                def notificationMessage = "${senderInstance.fullName}: ${content}"
               
                result = sendMessageToDevice(senderInstance, receiverInstance, "DIRECT_MESSAGE", result, notificationMessage )
            } else {
                result.status = "error"
                result.message = "error saving message on server"
            }
			 
        }else {
            result.status = "error"
            result.message = "receiver Not Found"
        }

        return result
    }
    /**
     * @param messageId (optional unique identifier for async responses)
     * @param token
     * @param receiver
     * @param content
     * @param image Also accepts image and params should contain image along with a file in request of
     *         content_type ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']
     * @param multipartFile
     * @return
     */
    def sendPrivateMessageWithDefaultImage(messageId, token, receiver, content ){
        def result = [:]
        result.message = [:]
        result.message.messageId = messageId?messageId:'No_Message_Id'
		 
        def senderInstance = getUserFromToken(token);
        def receiverInstance = NayaxUser.get(receiver);
        result.message.receiver = receiverInstance.id
        result.message.sender = senderInstance.id
        result.message.content=content
        ///def picture = Picture.get(1508) //july 4th
        def picture = Picture.get(1772) //aug 15th
        if (receiverInstance) {
            log.info("From receiver:" + receiverInstance.fullName)
			
            def privateMessageInstance = new PrivateMessage(receiver: receiverInstance, sender: senderInstance, content: content)
            if(picture){
                privateMessageInstance.picture= picture
                result.message.isImage = true
                result.message.imageId=picture.originalFilename
                result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
                result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
                result.message.likedCount = 0
                result.message.likedByUser=false
                result.message.likes = []
				 
            }
            if (privateMessageInstance.save(flush: true)) {
                result.status = "success"
                def notificationMessage = "${senderInstance.fullName}: ${content}"
                	 
                result = sendMessageToDevice(senderInstance, receiverInstance, "DIRECT_MESSAGE", result, notificationMessage )
            } else {
                result.status = "error"
                result.message = "error saving message on server"
            }
			 
        }else {
            result.status = "error"
            result.message = "receiver Not Found"
        }

        return result
    }
	
    
    /**
     *This method is specifically created for IOS. Content is decoded message. content_push is non decoded message sent as push message. 
     * @param request
     * @return
     */
    def sendPrivateMessageV4(request){
        
        def json = request.JSON
        
        def messageId = json.messageId
        def token = json.token
        def receiver = json.receiver
        def content = json.content
        def contentPush = json.content_push
        def image = json.image
        def result = [:]
        result.message = [:]
        result.message.messageId = messageId?messageId:'No_Message_Id'
		 
        def senderInstance = getUserFromToken(token);
        def receiverInstance = NayaxUser.get(receiver);
        result.message.receiver = receiverInstance.id
        result.message.sender = senderInstance.id
        result.message.content=content
        def picture = null
        if (receiverInstance) {
            log.info("From receiver:" + receiverInstance.fullName)
            if(image){
                def multipartFile = request.getFile("image");
                log.info("File type/name : "+ multipartFile.getContentType()+":"+multipartFile.getOriginalFilename())
                if (multipartFile.getContentType() in ['image/jpeg', 'image/gif', 'image/png', 'image/bmp', 'image/jpg']) {
                    def filename = UUID.randomUUID().toString()
                    def originalFilename = multipartFile.getOriginalFilename()
                    def fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length())
					 
                    picture = new Picture(mimeType: multipartFile.getContentType(),
                        originalFilename: originalFilename,
                        fileSize: multipartFile.getSize(),
                        filename: filename+fileExtension,
                        filePath: ConfigurationHolder.config.pictureLocation)
                    log.info " mimeType  " + multipartFile.getContentType()
                    log.info " originalFilename  " + originalFilename
                    log.info " filePath  " + ConfigurationHolder.config.pictureLocation
                    if (picture.validate()) {
                        picture.save()
                        File targetFile = new File(ConfigurationHolder.config.pictureLocation, filename +fileExtension )
                        multipartFile.transferTo(targetFile)
                        File targetThumbNailFile = new File(ConfigurationHolder.config.pictureLocation, 'tn_' +filename +fileExtension )
                        def thumbnailImage = imageService.getThumbNail(targetFile, Setting.findBySettingType(SettingType.PIC_WIDTH).value as Integer)
                        ImageIO.write(thumbnailImage, fileExtension.substring(1), targetThumbNailFile);
						 
                    }
                }
            }
            def privateMessageInstance = new PrivateMessage(receiver: receiverInstance, sender: senderInstance, content: content)
            if(picture){
                privateMessageInstance.picture= picture
                result.message.isImage = true
                result.message.imageId=picture.originalFilename
                result.message.imageUrl = ConfigurationHolder.config.webImagePath +picture.filename
                result.message.thumbnailImageUrl = ConfigurationHolder.config.webImagePath +'tn_' +picture.filename
                result.message.likedCount = 0
                result.message.likedByUser=false
                result.message.likes = []
				 
            }
            if (privateMessageInstance.save(flush: true)) {
                result.status = "success"
                def notificationMessage = "${senderInstance.fullName}: ${content}"
                if(picture)
                notificationMessage = "${senderInstance.fullName} shared an image with you."
					 
                result = sendMessageToDevice(senderInstance, receiverInstance, "DIRECT_MESSAGE", result, notificationMessage )
            } else {
                result.status = "error"
                result.message = "error saving message on server"
            }
			 
        }else {
            result.status = "error"
            result.message = "receiver Not Found"
        }

        return result
    }
	 
	 
    def inviteFriends(params){
        def result = [:]
        def user = getUserFromToken(params.token)
        log.info('after USER')
        def channel = params.channel
        log.info('channel---:' +channel)
		 
        def invitees = params.inviteeList.split(',')
        log.info(invitees)
        def invitationMessage = params.invitationMessage
        log.info(invitationMessage)
        LISNxInvitation lisnxInvitation
        invitees.each {invitee ->
            lisnxInvitation = new LISNxInvitation(
                channel: INVITATION_CHANNEL.valueOf(params.channel),
                user:user,
                invitationMessage: invitationMessage,
                targetUserId: invitee
            )
            log.info(lisnxInvitation)
            boolean flag = saveObject(lisnxInvitation)
            if (flag) {
                result.status = "success"
                result.message = "Your invitation has been sent!"
            } else {
                result.status = "error"
            }
		 
        }
        return result
    }

    def sendTempPassword(username) {

        def result = [:]
        def mailSendingStatus
        def user = NayaxUser.findByUsername(username)
        if (user) {
            def tempPassword = Math.round(Math.random() * 100000000)
            try {
                mailService.sendMail {
                    to "${username}"
                    subject "Lisnx : Forget Password Notification"
                    body(view: "/api/mailer", model: [tempPassword: tempPassword])
                }
                mailSendingStatus = "success"
            } catch (Exception e) {
                e.printStackTrace()
                mailSendingStatus = "fail"
            }
            if (mailSendingStatus.equals("success")) {
                user.password = springSecurityService.encodePassword("${tempPassword}")
                user.isResetPassword = false
                user.save(flush: true)
                result.status = "success"
                result.message = "Please check email"
            } else {
                result.status = "error"
                result.message = "Error in sending mail"
            }
        } else {
            result.status = "error"
            result.message = "Email Not registered"
        }
        return result
    }

    def disableSocialNetworkSetting(token, disable) {

        def result = [:]
        def userInstance = getUserFromToken(token);
        switch (disable) {
        case "FB":
            def faceBookInstance = userInstance.facebook
            if (faceBookInstance) {
                userInstance.facebook = null
                userInstance.merge()
                faceBookInstance.delete()
            }

            break;
        case "LINKEDIN":
            def linkedinInstance = userInstance.linkedin
            if (linkedinInstance) {
                userInstance.linkedin = null
                userInstance.merge()

                linkedinInstance.delete()
            }
            break;
        }

        result["status"] = "success"
        result["message"] = "successully disconnect "

        return result

    }
	
    def sendPhoneInvite(params) {
        def result = [:]
        try {
            // These code snippets use an open-source library.
            HttpResponse<JsonNode> response = Unirest.post("https://clicksend-clicksend-sms.p.mashape.com/send.json")
            .header("Authorization", ConfigurationHolder.config.mashape.authorization)
            .header("X-Mashape-Key", ConfigurationHolder.config.mashape.key)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .field("message", params.message)
            .field("method", "mashape")
            .field("senderid", params.sender?params.sender:"Lisnx")
            .field("to", params.receiver)
            .asJson()
			
            log.info "SMS status: ${response.getStatus()}"
            result["status"] = response.getStatus()
            result["message"] = "SMS sent"
            return result
        } catch (UnirestException e) {
            log.info e.getMessage()
        }
    }
	
    def loginWithLinkedIn(def params) {
		
        def lnResponse = linkedinService.getLinkedInUserDetails(params.ACCESS_TOKEN)
        def lnResultJson = new JsonSlurper().parseText(lnResponse);
		
        def user
        Linkedin linkedIn = Linkedin.findByLoginProviderUID(lnResultJson.id)
        
        String profilePictureUrl = null
        if (lnResultJson?.pictureUrls?._total > 0) {
            profilePictureUrl = lnResultJson.pictureUrls.values[0]
        }
		
        if(linkedIn){
			
            linkedIn.thumbnailURL = lnResultJson.pictureUrl
            linkedIn.profilePictureURL = profilePictureUrl
            linkedIn.lastLogin = new Date()
            
            //Update LinkedIn access token
            def linkedinAccessToken = linkedIn.linkedinAccessToken
            if(!linkedinAccessToken){
                linkedinAccessToken = new LinkedinAccessToken()
                linkedIn.linkedinAccessToken = linkedinAccessToken;
					
            } 		
            if(linkedinAccessToken.accessToken != params.ACCESS_TOKEN){				
				
                linkedinAccessToken.accessToken = params.ACCESS_TOKEN
                linkedinAccessToken.expirationDate= new Date(Long.valueOf(params.TOKEN_EXPIRATION_DATE))
                linkedinAccessToken.permissions= params.PERMISSIONS
                saveObject(linkedinAccessToken)
               			
            }	
            saveObject(linkedIn)	
        }
        
        def newUserCreated = false
		
        if(linkedIn){
            user = linkedIn.user
            if(!user){
                user = new NayaxUser(fullName: lnResultJson.formattedName,
                    password: springSecurityService.encodePassword(RandomStringUtils.random(6)),
                    enabled: true,
                    username: lnResultJson.emailAddress,
                    emailSent: false)
                
                saveObject(user)
                sendWelcomeEmail(user)
                user.emailSent = true
                saveObject(user)
                newUserCreated = true
            }
        }
        else {
            user = NayaxUser.findByUsername(lnResultJson.emailAddress)
            		
            if(!user){
                user = new NayaxUser(fullName: lnResultJson.formattedName,
                    password: springSecurityService.encodePassword(RandomStringUtils.random(6)),
                    enabled: true,
                    username: lnResultJson.emailAddress,
                    emailSent: false)
                
                saveObject(user)
                sendWelcomeEmail(user)
                user.emailSent = true
                saveObject(user)
                newUserCreated = true
            }
            		
            Linkedin newLinkedIn = new Linkedin(loginProviderUID: lnResultJson.id, dateCreated: new Date(), thumbnailURL: lnResultJson.pictureUrl, 
                profilePictureURL: profilePictureUrl, lastLogin: new Date(), email: lnResultJson.emailAddress, user: user, profileURL: lnResultJson.publicProfileUrl);
            LinkedinAccessToken linkedinAccessToken = new LinkedinAccessToken(accessToken: params.ACCESS_TOKEN,
                expirationDate: new Date(Long.valueOf(params.TOKEN_EXPIRATION_DATE)), permissions: params.PERMISSIONS);
            newLinkedIn.linkedinAccessToken = linkedinAccessToken
            newLinkedIn.save(flush:true)
            newLinkedIn = Linkedin.findByLoginProviderUID(lnResultJson.id)
            linkedinAccessToken = newLinkedIn.linkedinAccessToken	
            		
        }
        
        if(newUserCreated){
            //notifyInviteesAndFriends(user, params.token)
            /*sendPrivateMessageV2(null, MobileAuthToken.findWhere(nayaxUser: NayaxUser.get(83)).token,
            1, "${user.fullName} has just joined ", null)*/
            def localParams = [:]
            NayaxUser adminUser = NayaxUser.findByUsername("server@lisnx.com")
            MobileAuthToken mobileAuthToken = MobileAuthToken.findByNayaxUser(adminUser)
            localParams.token = mobileAuthToken.token
            localParams.id = "521" //Lisnx Team lisn id
            localParams.content = "${user.fullName} has just joined "
            postLisnMessageV3(localParams, request)
        }	
		
        if(params.appVersion){
            if(params.appVersion!=user.currentAppVersion){
                user.currentAppVersion = params.appVersion
                saveObject(user)
                user = user.refresh()
            }			
        }
		
        //def user = NayaxUser.get(1)
        def usingThirdPartyLogin = true
		
        def result =  login(user.username, user.password, generateLoginToken(), params.iosDeviceToken, params.parseInstallationId, usingThirdPartyLogin )
        def thumbnailPicUrl = defaultThumbnailPicUrl
        def profilePicUrl = defaultProfilePicUrl

        if (lnResultJson.pictureUrl) {
            thumbnailPicUrl = lnResultJson.pictureUrl
        }
        if (profilePictureUrl) {
            profilePicUrl = profilePictureUrl
        }
        result.message.updateRequired = isUpdateRequired(user, params.appVersion)
        result.message.loginAccountId = lnResultJson.id
        result.message.profileThumbnailPicUrl = thumbnailPicUrl
        result.message.profilePictureUrl = profilePicUrl
        result.message.loginAccountType = "LINKEDIN"
			
        return result
    }
	
    def loginWithPhoneDigits(def params) {
		
        def user
        PhoneUser phoneUser = PhoneUser.findByUserId(params.userId)
		
        String profilePictureUrl = null
		
        if(phoneUser){			
            phoneUser.phoneNumber = params.phoneNumber
            phoneUser.lastLogin = new Date()			
			
            saveObject(phoneUser)
        }
		
        def newUserCreated = false
        
        //TODO temporarily setting username to id@lisnx.com. Need to change later.
        
        String userName = "${params.userId}@lisnx.com"
		
        if(phoneUser){
            user = phoneUser.user
            if(!user){
                user = new NayaxUser(fullName: phoneUser.phoneNumber,
                    password: springSecurityService.encodePassword(RandomStringUtils.random(6)),
                    enabled: true,
                    username: userName,
                    phoneUser: phoneUser,
                    emailSent: false)
				
                saveObject(user)
                sendWelcomeEmail(user)
                user.emailSent = true
                saveObject(user)
                newUserCreated = true
            }
        }
        else {
            user = NayaxUser.findByUsername(userName)
					
            if(!user){
                user = new NayaxUser(fullName: params.phoneNumber,
                    password: springSecurityService.encodePassword(RandomStringUtils.random(6)),
                    enabled: true,
                    username: userName,
                    emailSent: false)
				
                saveObject(user)
                sendWelcomeEmail(user)
                user.emailSent = true
                saveObject(user)
                newUserCreated = true
            }
					
            PhoneUser newPhoneUser = new PhoneUser(userId: params.userId, dateCreated: new Date(), lastLogin: new Date(), email: userName, 
                user: user);
			
            newPhoneUser.save(flush:true)
					
        }
		
        if(newUserCreated){
            //notifyInviteesAndFriends(user, params.token)
            /*sendPrivateMessageV2(null, MobileAuthToken.findWhere(nayaxUser: NayaxUser.get(83)).token,
            1, "${user.fullName} has just joined ", null)*/
            def localParams = [:]
            NayaxUser adminUser = NayaxUser.findByUsername("server@lisnx.com")
            MobileAuthToken mobileAuthToken = MobileAuthToken.findByNayaxUser(adminUser)
            localParams.token = mobileAuthToken.token
            localParams.id = "521" //Lisnx Team lisn id
            localParams.content = "${user.fullName} has just joined "
            postLisnMessageV3(localParams, request)
        }
		
        if(params.appVersion){
            if(params.appVersion!=user.currentAppVersion){
                user.currentAppVersion = params.appVersion
                saveObject(user)
                user = user.refresh()
            }
        }
		
        //def user = NayaxUser.get(1)
        def usingThirdPartyLogin = true
		
        def result =  login(user.username, user.password, generateLoginToken(), params.iosDeviceToken, params.parseInstallationId, usingThirdPartyLogin )
        def thumbnailPicUrl = defaultThumbnailPicUrl
        def profilePicUrl = defaultProfilePicUrl

        result.message.updateRequired = isUpdateRequired(user, params.appVersion)
        result.message.loginAccountId = params.userId
        result.message.profileThumbnailPicUrl = thumbnailPicUrl
        result.message.profilePictureUrl = profilePicUrl
        result.message.loginAccountType = "PHONE"
			
        return result
    }

    /**
     * Invite selected friends (receivers) to and event identifed by externalEventId.
     * 
     * @param token
     * @param receivers - list of invited friends
     * @param externalEventId
     * 
     * @return
     */
    def inviteFriendsToEvent(def params, def request) {		
        /*{
        token:5e089bee-8db1-4b32-bef5-17d773324c9c,
        sender:1,
        receivers:[101,102,103],
        externalEventId:123		
        }*/
        def json = request.JSON
		
        def user = getUserFromToken(json.token)
        def saveSuccess = true;
        def result = [:]
        result.status='success'
        result.message=[:]
        	
        if (user) {		
            ExternalEvent externalEvent = ExternalEvent.findByEventId("${json.externalEventId}")
            NayaxUser sendingUser = user
            
            if (!externalEvent) {
                if(json.eventSource && EventSource.Eventbrite.toString().equals(json.eventSource)){
                    def eventDetails = eventbriteService.getEventDetails(json.externalEventId)
                    if(eventDetails){
                        externalEvent = eventbriteService.populateEventDetails(eventDetails)
                    }
                }else {
                    externalEvent.eventSource = EventSource.Unknown
                }
                if(externalEvent.locationCoordinate)
                saveSuccess = saveObject(externalEvent.locationCoordinate)
                saveSuccess = saveObject(externalEvent)
                
                if (!saveSuccess) {
                    result.status = "error"
                    result.message = "Error while saving External Event"
                    return result
                }
            }
            
            log.info "External event: ${externalEvent.eventName}"
            log.info "Sender: ${sendingUser.fullName}"
        
            json.receivers?.each{receiver ->
                log.info "Receiver: ${receiver}"
                NayaxUser receivingUser = NayaxUser.findById(receiver)
                def eventInvites = EventInvite.withCriteria {
                    eq "sender", sendingUser
                    eq "receiver", receivingUser
                    eq "externalEvent", externalEvent                  
                }			
			
                if (eventInvites) {
                    eventInvites.each { eventInvite ->
                        saveSuccess = saveObject(eventInvite)	
                    }
                } else {
                                           
                    EventInvite eventInvite = new EventInvite(sender: sendingUser, receiver: receivingUser, externalEvent: externalEvent, dateCreated: new Date()) 
                    saveSuccess = saveObject(eventInvite)                    
                    sendMessageToDevice(sendingUser, receivingUser, "EVENT_INVITE", result, "${sendingUser.fullName} has invited you to ${externalEvent.eventName} event.")
                    
                }
                
                if (!saveSuccess) {
                    result.status = "error"
                    result.message = "Error while saving Event Invite"
                    return result
                }
            }
        }
        
        return result
    } 
	
    /**
     * 
     * @param text should provide the text for which image suggestions are requested
     * @param token - user token
     * @return
     * 
     */
    def getSuggestedImages(def params){
        def result = [:]
        result.status='success'
        result.message= []
        def imageResults =[]
		
        /*imageResults.add([url:'https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTWVx92iBIauGsoAM-PIR70urNEozmKfl7CejaJLX7R8HXeCdFt'])
        imageResults.add([url:'http://cdn.someecards.com/someecards/usercards/fd06ac2398bf3b7c2b33745391d66ff7.png'])
        imageResults.add([url:'http://cdn.someecards.com/someecards/usercards/1298498516584_4129121.png'])
        imageResults.add([url:'http://cdn.someecards.com/someecards/filestorage/happy-birthday-to-one-of-the-few-people-AFd.png'])*/
        imageResults.addAll(searchImages(params))
        result.message=imageResults
        return result
		
    }
    
    /**
     * This method will enable mute on Lisn. Disable only sound if showNotification value is true.
     * 
     * @param token
     * @param lisnId - lisn on which mute should be enabled
     * @param duration - how long mute should be active
     * @param durationType - HOUR, DAY, WEEK, MONTH
     * @param showNotification - boolean value wheter to show notification
     * 
     * @return result status
     */
    
    def enableLisnMute(token, lisnId, duration, durationType, showNotification) {
        
        def result = [:]
        NayaxUser user = getUserFromToken(token)
        LISN lisn = LISN.findById(lisnId)
        Date startDate = new Date()
        
        if (lisn) {
            Date endDate = new DateUtil().getEndDate(startDate, duration, MuteMessage.DurationType.valueOf(durationType))
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
                muteLisnMessage.startDate = startDate
                muteLisnMessage.duration = Integer.valueOf(duration)
                muteLisnMessage.endDate = endDate
                muteLisnMessage.showNotification = Boolean.valueOf(showNotification)
                muteLisnMessage.durationType = MuteMessage.DurationType.valueOf(durationType)

            } else {

                muteLisnMessage = new MuteLisnMessage(user: user, lisn: lisn, startDate: startDate, endDate: endDate, 
                    duration: duration, showNotification: showNotification, durationType: MuteMessage.DurationType.valueOf(durationType), isCancelled: false)
            }
            
            saveObject(muteLisnMessage)
            result.status = "success"
            result.message = "Successfully mutted lisn message"
        } else {
            result.status = "error"
            result.message = "Error in muting lisn message"
        }
        
        
        return result
    }
    
    /**
     * This method will enable mute on Event. Disable only sound if showNotification value is true.
     * 
     * @param token
     * @param externalEventId - event id on which mute should be enabled
     * @param duration - how long mute should be active
     * @param durationType - HOUR, DAY, WEEK, MONTH
     * @param showNotification - boolean value wheter to show notification
     * 
     * @return result status
     */
    def enableEventMute(token, externalEventId, duration, durationType, showNotification) {
        
        def result = [:]
        NayaxUser user = getUserFromToken(token)
        ExternalEvent externalEvent = ExternalEvent.findByEventId(externalEventId)
        Date startDate = new Date()
        
        if (externalEvent) {
            Date endDate = new DateUtil().getEndDate(startDate, duration, MuteMessage.DurationType.valueOf(durationType))
            def muteEventMessages = MuteEventMessage.withCriteria {
                and {
                    eq "user", user
                    eq "externalEvent", externalEvent

                    eq "isCancelled", false
                    ge "endDate", new Date()
                }
            }

            MuteEventMessage muteEventMessage = null

            if (muteEventMessages?.size() > 0) {
                log.info "Total MuteEventMessage records found: " + muteEventMessages.size()
                muteEventMessage = muteEventMessages.get(0)
            }
        
            if (muteEventMessage) {
                muteEventMessage.startDate = startDate
                muteEventMessage.duration = Integer.valueOf(duration)
                muteEventMessage.endDate = endDate
                muteEventMessage.showNotification = Boolean.valueOf(showNotification)
                muteEventMessage.durationType = MuteMessage.DurationType.valueOf(durationType)

            } else {

                muteEventMessage = new MuteEventMessage(user: user, externalEvent: externalEvent, startDate: startDate, endDate: endDate, 
                    duration: duration, showNotification: showNotification, durationType: MuteMessage.DurationType.valueOf(durationType), isCancelled: false)
            }
            
            saveObject(muteEventMessage)
            result.status = "success"
            result.message = "Successfully mutted event message"
        } else {
            result.status = "error"
            result.message = "Error in muting event message"
        }
        
        
        return result
    }
    
    /**
     * This method will cancel/disable mute which enabled on Lisn.
     * 
     * @param token
     * @param lisnId - lisn id on which mute should be disabled
     * 
     * @return result status
     */
    def disableLisnMute(token, lisnId) {
        
        NayaxUser user = getUserFromToken(token)
        LISN lisn = LISN.findById(lisnId)
        def result = [:]
        
        def query = MuteLisnMessage.where {
            and {
                eq "user", user
                eq "lisn", lisn
                eq "isCancelled", false
                ge "endDate", new Date()
            }
        }

        int total = query.updateAll(isCancelled: true) 
             
        result.status = "success"
        result.message= "Successfully updated MuteLisnMessage records"
        
        return result
    }
    
    /**
     * This method will cancel/disable mute which enabled on ExternalEvent.
     * 
     * @param token
     * @param externalEventId - event id on which mute should be disabled
     * 
     * @return result status
     */
    def disableEventMute(token, externalEventId) {
        
        NayaxUser user = getUserFromToken(token)
        ExternalEvent externalEvent = ExternalEvent.findByEventId(externalEventId)
        def result = [:]
        
        def query = MuteEventMessage.where {
            and {
                eq "user", user
                eq "externalEvent", externalEvent
                eq "isCancelled", false
                ge "endDate", new Date()
            }
        }

        int total = query.updateAll(isCancelled: true) 
             
        result.status = "success"
        result.message = "Successfully updated MuteEventMessage records"   
        
        return result
    }
    
    def getMuteEventMessageInstance(def user, def externalEvent) {
        def muteEventMessages = MuteEventMessage.withCriteria {
            and {
                eq "user", user
                eq "externalEvent", externalEvent
                eq "isCancelled", false
                ge "endDate", new Date()
            }
        }

        MuteEventMessage muteEventMessage = null

        if (muteEventMessages?.size() > 0) {
            log.info "Total MuteEventMessage records found: " + muteEventMessages.size()
            muteEventMessage = muteEventMessages.get(0)
        }
        
        return muteEventMessage
    }
    
    /**
     * This method will determine whether mute is enabled for the given LISN/Event id.
     * 
     * @param token
     * @param id - Lisn/event id
     * @param type - messageType. Values should be any of "LISN", "EVENT" depending on "id" passed.
     * 
     * @return result status with boolean value in result message.
     */
    def isMuteEnabled(def token, def id, def type){
        def isMuteEnabled = false
        NayaxUser user = getUserFromToken(token)
        def result = [:]
        
        if (type == "LISN") {
            
            LISN lisn = LISN.findById(id)

            if (lisn) {
                def muteLisnMessages = MuteLisnMessage.withCriteria {
                    and {
                        eq "user", user
                        eq "lisn", lisn
                        eq "isCancelled", false
                        ge "endDate", new Date()

                    }
                }

                if (muteLisnMessages?.size() > 0) {
                    log.info "Total MuteLisnMessage records found: " + muteLisnMessages.size()
                    isMuteEnabled = true
                }

            }
        }
        if (type == "EVENT") {
            ExternalEvent externalEvent = ExternalEvent.findByEventId(id)
             
            if (externalEvent) {
                def muteEventMessages = MuteEventMessage.withCriteria {
                    and {
                        eq "user", user
                        eq "externalEvent", externalEvent
                        eq "isCancelled", false
                        ge "endDate", new Date()
                    }
                }

                if (muteEventMessages?.size() > 0) {
                    log.info "Total MuteEventMessage records found: " + muteEventMessages.size()
                    isMuteEnabled = true
                }
            }
        }
        
        result["status"] = "success"
        result["message"] = isMuteEnabled
        
        return result
    }
    
    def getGoogleContacts(def token, def googleAccessToken) {

        def result = googleService.getContacts(googleAccessToken)

        if (result.contacts.size() > 0) {
            result.status = "success"
        } else {
            result.status = "error"
            result.message = "No contacts found"
        }
		
        return result
    }
    
    def saveInviteesList(def params, def request) {
        def result = [:]
        result["status"] = "success"
        
        def json = request.JSON
        
        def token = json.token
        def inviteeType = json.inviteeType
        def accessToken = json.accessToken
        def inviteesList = json.inviteesList
        
        switch(inviteeType) {
        case "GOOGLE": 
            def nayaxUser = getUserFromToken(token)
            def googleUserInfo = googleService.getUserInfo(accessToken)
            
            boolean newHost = false
            
            def host = GoogleInviteeHost.findByEmail(googleUserInfo?.email)
            
            if(!host) {
                host = new GoogleInviteeHost()
                host.email = googleUserInfo?.email
                newHost = true
            }
            
            inviteesList.each{
                def invitee = null
                if (!newHost) {
                    invitee = GoogleInvitee.findByEmailAndHost(it, host)
                }
                if (!invitee) {
                    invitee = new GoogleInvitee()
                    invitee.email = it
                    invitee.invitedDate = new Date()
                    host.addToInvitees(invitee)
                }  
            }
            
            nayaxUser.addToGoogleInviteeHosts(host)
            saveObject(nayaxUser)					
            
            result["message"] = "Google invitees list saved"
            
            break
            
        default:
            result["message"] = "Invitee type not found"
        }  		
        
        return result      
    }
}

