package nayax

import grails.converters.JSON
import nayax.CO.EmailCommand
import nayax.Enum.ProfileShareType

import org.grails.plugins.imagetools.ImageTool

class ApiController extends grails.plugins.springsecurity.ui.RegisterController {
    def lisnService
    def apiService
    def imageService
    def nayaxMailerService
    def springSecurityService
    def asynchronousMailService
    def linkedinService
    def campaignService
    def facebookService
    def beforeInterceptor = [action: this.&checkTokenInParams, except: [
                                                                        'getExistingToken',
                                                                        'hasProfilePic',
                                                                        'getPromoShare',
                                                                        'getFBStats',
                                                                        'getLIStats',
                                                                        'getSocialStatus',
                                                                        'promo',
                                                                        'getFinalUrl',
                                                                        'testGeoUtil',
                                                                        'loginWithFacebook',
                                                                        'getOtherUserProfilePicture',
                                                                        'sendWelcomeEmail',
                                                                        'renderTokenError',
                                                                        'forgotPassword',
                                                                        'getLoginToken',
                                                                        'getLinkedInToken',
                                                                        'renderTokenMissingError',
                                                                        'getProfileShareTypes',
                                                                        'testParseUtil',
                                                                        'saveProfileImageFromUserName',
                                                                        'setProfilePicture2',
                                                                        'getFBPermissions',
                                                                        'getCampaignStats',
                                                                        'sendPhoneInvite',
                                                                        'createLISNv2',
                                                                        'inviteFriendsToLISN',
                                                                        'nearby',
                                                                        'getAppInviteDetails',
                                                                        'loginWithLinkedIn',
                                                                        'inviteFriendToEvent',
                                                                        'postLisnMessageV3',
                                                                        'loginWithPhone',
																		'pub'
        ]]


    def testLinkedInURL(){
        render apiService.getInformationFromLinkedIn(params.token) as JSON;
    }

    def testGeoUtil(){
        render apiService.testGeoUtil()
    }
    def sendWelcomeEmail(){
        apiService.sendWelcomeEmail(NayaxUser.get(1))
        //render(view:"welcomemail", model:[]);
        return "success"
        /*File targetFile = new File("/Users/sjaini/Downloads/lisnxImages/images/forward_disabled.jpg")
        asynchronousMailService.sendAsynchronousMail {
        log.debug "mail to  "  + "srinivasjaini@gmail.com"//it.toString()
        to 'srinivasjaini@gmail.com'
        from "notifications@lisnx.com"
        attachBytes targetFile.absolutePath,'image/jpg', targetFile.readBytes()
        subject 'LISNx: test image attachment'
        html "New user registered : <br/> Username"
        }*/
    }

    def checkTokenInParams() {
        log.trace "Inside controller:API beforeInterceptor with params : ${params}"
        def token = params?.token?params.token:request.JSON?.token
        Boolean status = true
        if (params.action != "aboutUs") {
            if (!(token)) {
                redirect(action: renderTokenMissingError)
                status = false
            }
            else if (!(MobileAuthToken.isTokenValid(token))) {
                redirect(action: renderTokenError)
                status = false
            }
        }
        return status
    }
    def hasProfilePic(){
        render facebookService.hasProfilePic(params.fid);
    }

    def getSuggestedImages(){
        def result = [:]
        try {
            result = apiService.getSuggestedImages(params)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def afterInterceptor = { model ->
        log.trace "Tracing in afterInterceptor: ${actionUri}, ${params}"
    }

    def addUserPhone = {
        def result = [:]
        try {
            result = apiService.addPhoneToUser(params.token, params.userPhoneNumber)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON;
    }

    def addFacebookAccount = {
        def result = [:]
        try {
            result = apiService.addFacebookAccount(params.token, params.fid, params)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON;
    }

    def loginWithFacebook = {
        def result = [:]
        try {
            result = apiService.loginWithFacebook(params)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON;
    }

    def getFBStats = {
        def result = [:]
        try {
            result = apiService.getFBStats(params.fid)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON;
    }
    def getCampaignStats = {
        def result = [:]
        result = campaignService.getCampaignStats(params.fid, params.lid)
        def returnVal = result as JSON
        //render view: 'vlc-p1', model: [status: result]
        render returnVal;
    }

    def getLIStats = {
        def result = [:]
        try {
            result = apiService.getLIStats(params.lid)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON;
    }

    def getFinalUrl = {
        def imageUrl = imageService.getFinalImage(params);
        render imageUrl;
    }

    def getPromoShare = {

        //def userName = apiService.getFirstNameByFid(params.fid)
        def campaignStats = campaignService.getCampaignStats(params.fid, params.lid)
        def soFarCountFromDb = apiService.getPromoSoFarCount()
        render view:'promoShare', model: [image: params.imageUrl , fid:params.fid, lid:params.lid,
            description: 'I am '+campaignStats.championBadge + ', what are you?',
            soFarCount:soFarCountFromDb, personalMessage: 'You are ',
            champion:campaignStats.championBadge,
            linkedInDescription: soFarCountFromDb + ' people have found out so far.',
            personalMessage2:', who is making a global impact! Share now.']
        return
    }

    def promo ={
        render view: '/index2', model: [image: ""
            , description:""
        ]
    }
	
	def pub ={
		render view: '/pub', model: [image: ""
			, description:""
		]
	}

    def getSocialStatus = {
        def campaignStats = campaignService.getCampaignStats(params.fid, params.lid)
        render view: '/index2', model: [soFarCount:apiService.getPromoSoFarCount(),
            description: 'I am '+campaignStats.championBadge + ', what are you?',
            image: params.imageUrl]
        return
    }

    def addLinkedInAccount = {
        def result = [:]
        try {
            result = apiService.addLinkedInAccount(params.token, params.lid, params.linkedinProfileURL, params)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON;
    }

    def addLinkedInOauth2 = {

        def result = [:]
        try {
            result = apiService.addLinkedInOauth2(params.token,
                params.lid,
                params.linkedinProfileURL,
                params.expires_in,
                params.LINKEDIN_ACCESS_TOKEN,
                params.permissions)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def getAppInviteDetails(){
        def result = [:]
        try {
            result = apiService.getAppInviteDetails()
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }


    def getNearbyEvents = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.getNearbyEvents(params.latitude, params.longitude, params.within, params.token, params.timeStamp)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def getMessageSummary = {
        def result = [:]
        try {
            result = apiService.getMessageSummary(params.token, params.timeStamp)
        } catch (Exception e) {
            log.error(session, e)
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON;
    }

    def likeLISNMessageV2 = {
        def result = [:]
        try {
            result = apiService.likeLISNMessageV2(params.token, params.imageId);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def unlikeLISNMessageV2 = {
        def result = [:]
        try {
            result = apiService.unlikeLISNMessageV2(params.token, params.imageId);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def likeLISNMessage = {
        def result = [:]
        try {
            result = apiService.likeLISNMessage(params.token, params.messageId);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def unlikeLISNMessage = {
        def result = [:]
        try {
            result = apiService.unlikeLISNMessage(params.token, params.messageId);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }
    def likeDirectMessage = {
        def result = [:]
        try {
            result = apiService.likeDirectMessage(params.token, params.messageId);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def unlikeDirectMessage = {
        def result = [:]
        try {
            result = apiService.unlikeDirectMessage(params.token, params.messageId);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def likeDirectMessageV2 = {
        def result = [:]
        try {
            result = apiService.likeDirectMessageV2(params.token, params.imageId);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def unlikeDirectMessageV2 = {
        def result = [:]
        try {
            result = apiService.unlikeDirectMessageV2(params.token, params.imageId);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def likeEventMessage = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.likeEventMessage(params.token, params.messageId)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def unlikeEventMessage = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.unlikeEventMessage(params.token, params.messageId)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def likeEventMessageV2 = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.likeEventMessageV2(params.token, params.imageId)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def unlikeEventMessageV2 = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.unlikeEventMessageV2(params.token, params.imageId)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def getMessageSummaryV2 = {
        def result = [:]
        try {
            result = apiService.getMessageSummaryV2(params.token, params.timeStamp)
        } catch (Exception e) {
            log.error(session, e)
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON;
    }

    def getMessageSummaryV3 = {
        def result = [:]
        try {
            result = apiService.getMessageSummaryV3(params.token, params.timeStamp)
        } catch (Exception e) {
            log.error(session, e)
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON;
    }

    def isUpdateAvailable = {
        def result=[:]
        try {
            boolean isUpdateAvailable = apiService.isUpdateAvailable(params.token, params.currentAppVersion)
            result.status='success'
            result.message=[:]
            result.message.isUpdateAvailable = isUpdateAvailable;
        }catch (Exception e) {
            log.error(session, e)
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON;
    }
    def invokeMethod(String name, args) {
        return apiService.invokeMethod(name, args)
    }

    def getExistingToken = {
        def result = [:]
        try {
            result = apiService.getExistingToken(params)
        } catch (Exception e) {
            log.error(session, e)
            result["status"] = "error"
            result["message"] = e.message
        }

        render result as JSON;
    }

    def getLisnsAroundMe = {
        log.debug "Inside controller:API action:getLisnsAroundMe with params : ${params}"
        def result = [:]
        def longitude = params.longitude;
        def latitude = params.latitude;
        def token = params.token;
        try {
            result = apiService.getLisnsAroundMe(longitude, latitude, token, params.timeStamp)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getUserRegisterDetails = { register.call() }

    def register = {
        log.trace "param in register is ${params}";
        def result = [:]
        def username = params.username;
        def fullName = params.fullName;
        def dateOfBirth = params.dateOfBirth;
        def password = params.password;
        def password2 = params.password2;
        def token = params.token;
        try {
            result = apiService.register(fullName, username, password, password2, dateOfBirth, token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getPastLisns = {
        log.trace "Inside controller:API action:getPastLisns with params : ${params}"
        def result = [:]
        try {
            result = apiService.getPastLisns(params.token, params.timeStamp);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def searchLisn = {
        log.trace "Inside controller:API action:searchLisn with params : ${params}"
        def result = [:]
        try {
            result = apiService.searchLisn(params.token, params.now, params.past, params.longitude, params.latitude, params.nameLike, params.timeStamp);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getLisnDetails = {
        log.trace "Inside controller:API action:getLisnDetails with params : ${params}"
        def result = [:]
        try {
            result = apiService.getLisnDetails(params.id, params.token, params.timeStamp);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getLisnDetailsV2 = {
        def result = [:]
        try {
            result = apiService.getLisnDetailsV2(params.id, params.token, params.timeStamp);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def setLISNName = {
        def result = [:]
        try {
            result = apiService.setLISNName(params.token, params.id, params.name);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }



    def joinLisn = {
        def result = [:]
        try {
            result = apiService.joinLisn(params.id, params.token, params.timeStamp)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def setMyProfile = {
        def result = [:]
        try {
            result = apiService.setMyProfile();
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }
    def getMyProfile = {
        def result = [:]
        try {
            result = apiService.getMyProfile(params.token, params.timeStamp);
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }


    def getNotificationCountV2 = {
        def result = [:]
        try {
            result = [count: apiService.getNotificationCountV2(params.token)];
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def getUserDetails = {
        log.trace "Inside controller:API action:getUserDetails with params : ${params}"
        def result = [:]
        try {
            result = apiService.getUserDetails(params.id)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def updateUser = {
        def result = [:]
        try {
            result = apiService.updateUser(params.fullName, params.website, params.dateOfBirth, params.password, params.newPassword, params.confirmPassword, params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getLoginUserDetails = {
        log.trace "Inside controller:API action:getUserDetails with params : ${params}"
        def result = [:]
        try {
            result = apiService.getLoginUserDetails(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def renderTokenMissingError = {
        log.trace "Inside controller:API action:renderTokenError. AUTH token Missing."
        Map result = [:]
        result['status'] = "error"
        result['message'] = "Please pass token with your request."
        render result as JSON
    }

    def renderTokenError = {
        log.trace "Inside controller:API action:renderTokenError. AUTH token invalid."
        Map result = [:]
        result['status'] = "error"
        result['message'] = "Invalid token. Please pass a valid token with your reuqest."
        render result as JSON
    }

    def createLisn = {
        log.trace "Inside controller:API action:createLisn : with params : ${params}"
        def result = [:]
        LISN lisn = new LISN();
        bindData(lisn, params, ['startDate', 'endDate'])
        def latitude = params.latitude;
        def longitude = params.longitude;
        def token = params.token;
        String endDate = params.endDate;
        try {
            result = apiService.createLisn(lisn, latitude, longitude, token, endDate, params.timeStamp, params.android, params.profileShareType);
            render result as JSON
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
    }

    def getLoginToken = {
        Map result = [:]
        try {
            result = apiService.getLoginToken()
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def login = {
        //TODO: Can we handle it using interceptor ?
        def result = [:]
        def username = params.username;
        def password = params.password;
        def token = params.token;
        def iosDeviceToken = params.iosDeviceToken;
        def parseInstallationId = params.parseInstallationId
        try {
            result = apiService.login(username, password, token, iosDeviceToken, parseInstallationId)
        } catch (Exception e) {
            log.error(session, e)
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def testParseUtil = {
        def result = ParseUtil.sendNotification();
        render result
    }

    def sendMessageToLisners = {
        log.trace "Inside controller:API action:sendMessageToLisners with params : ${params}"
        def result = [:]
        try {
            result = apiService.sendMessageToLisners(params.id, params.message, params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getFriends = {
        log.trace "Inside controller:API action:getFriends with params : ${params}"
        def result = [:]
        result = apiService.getFriends(params.token);
        render result as JSON
    }
    def handleException(Exception e) {
        log.error("Exception:",e)
        def result = [:]
        result["status"] = "error"
        if (e.cause?.message != null && !"null".equals(e.cause?.message)) {
            result["message"] = "Error:" + e.getCause()?.getMessage()
        } else {
            try {
                result["message"] = "Error:" +  e.details?.message
            } catch (Exception ex) {
                result["message"] = "Error:"
            }
        }
       
        render result as JSON
    }
    def getFBPermissions = {
        def result = [:]
        result = apiService.getFBPermissions();
        render result as JSON
    }

    def getFacebookFriends = {
        def result = [:]
        try {
            result = apiService.getFacebookFriends(params);
        } catch (Exception e) {
            log.error "Error: ${e.message}", e
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getFriendsV2 = {
        log.trace "Inside controller:API action:getFriends with params : ${params}"
        def result = [:]
        try {
            result = apiService.getFriendsV2(params);
        } catch (Exception e) {
            log.error "Error: ${e.message}", e
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getLisnMessages = {
        log.trace "Inside controller:API action:getLisnMessages with params : ${params}"
        def result = [:]
        try {
            result = apiService.getLisnMessages(params.token, params.id, params.timeStamp, params.lastMessageId);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getLisnMessagesV2 = {
        def result = [:]
        try {
            result = apiService.getLisnMessagesV2(params.token, params.id, params.timeStamp, params.lastMessageId);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getLisnMessagesV3 = {
        def result = [:]
        try {
            result = apiService.getLisnMessagesV3(params.token, params.id, params.timeStamp, params.lastMessageId);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }


    def setLastViewedMessageId = {
        def result = [:]
        try {
            result = apiService.setLastViewedMessageId(params.token, params.id, params.lastMessageId)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def postLisnMessageV2 = {
        log.trace "Inside controller:API action:postLisnMessageV2 with params : ${params}"
        def result = [:]
        try {
            result = apiService.postLisnMessageV2(params);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def postLisnMessageV3 = {
        log.trace "Inside controller:API action:postLisnMessageV3 with params and request: ${params} *** ${request}"
        def result = [:]
        try {
            result = apiService.postLisnMessageV3(params, request);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def postLisnMessage = {
        log.trace "Inside controller:API action:postLisnMessages with params : ${params}"
        def result = [:]
        try {
            result = apiService.postLisnMessage(params.token, params.id, params.content, params.timeStamp);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    //For IOS
    def postLisnMessageV4 = {
        log.trace "Inside controller:API action:postLisnMessageV4 with params : ${request}"
        def result = [:]
        try {
            result = apiService.postLisnMessageV4(request);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def logout = {
        log.trace "Inside controller:API action:logout with params : ${params}"
        def result = [:]
        try {
            result = apiService.logout(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def aboutUs = {
        log.trace "Inside controller:API action:aboutUs with params : ${params}"
        def htmlCode
        if (params.html) {
            render(template: "/template/aboutUs")
        }
        else {

            htmlCode = '''About Us: \n  Connect instantly with people you meet. \n
LISNx introduces a cool new simple way of connecting and keeping in touch with people you meet at every intersection of your life. Remembering emails, noting down phone numbers, looking up on social networks or exchanging business cards are old school. \n
Stop printing or carrying business cards. \n
At your next gathering, show off your tech savvy-ness and use LISNx to connect instantly with people nearby :) \n
\n \n What is LISNX?
\n LISNx (Local Instant Social Network neXt generation app) lets you connect and interact instantly with people nearby in real-time with a simple tap on your smartphone.
\n You can also extend your connection to other social networks such as Facebook or Linkedin.

                        '''
            //\n Check out what���s happening nearby, join a LISN��� or create one and invite others to join and have fun.\n \n

            render text: htmlCode, contentType: "text/html", encoding: "UTF-8"
        }
    }

    def aboutUsV2 = {
        log.trace "Inside controller:API action:aboutUs with params : ${params}"
        def htmlCode
        if (params.html) {
            render(template: "/template/aboutUs")
        }
        else {

            htmlCode = '''About Us: <br>  Connect instantly with people you meet. <br>
LISNx introduces a cool new simple way of connecting and keeping in touch with people you meet at every intersection of your life. Remembering emails, noting down phone numbers, looking up on social networks or exchanging business cards are old school. <br>
Stop printing or carrying business cards. <br>
At your next gathering, show off your tech savvy-ness and use LISNx to connect instantly with people nearby :) <br>
<br> <br> What is LISNx?
<br> LISNx (Local Instant Social Network neXt generation app) lets you connect and interact instantly with people nearby in real-time with a simple tap on your smartphone.
<br> You can also extend your connection to other social networks such as Facebook or Linkedin.

                        '''
            //<br> Check out what���s happening nearby, join a LISN��� or create one and invite others to join and have fun.<br> <br>

            render text: htmlCode, contentType: "text/html", encoding: "UTF-8"
        }
    }

    def getBucketDetails = {
        def result = [:]
        try {
            result = apiService.getBucketDetails(params.token);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def saveProfileImageFromUserName = {
        def result = [:]
        try {
            if (params.profileImage) {
                def uploadedFile = request.getFile('profileImage')
                log.info "uploaded  file  - > " + uploadedFile
                NayaxUser nayaxUser = NayaxUser.findByUsername(params.userName)
                result = apiService.setProfilePicture(nayaxUser, uploadedFile)
            } else {
                result['status'] = "error"
                result['message'] = "No content uploaded. Please upload other image."
            }
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def setProfilePicture = {
        def result = [:]
        try {
            if (params.profileImage) {
                def uploadedFile = request.getFile('profileImage')
                log.info "uploaded  file  - > " + uploadedFile
                NayaxUser nayaxUser = apiService.getUserFromToken(params.token);
                result = apiService.setProfilePicture(nayaxUser, uploadedFile)
            } else {
                result['status'] = "error"
                result['message'] = "No content uploaded. Please upload other image."
            }
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def setProfilePicture2 = {
        def result = [:]
        try {
            if (params.profileImage) {
                def uploadedFile = request.getFile('profileImage')
                //def uploadedFile = new MockMultipartFile("testimage", "1329389308_administrator.png", "image/png", new File("/Users/sjaini/Downloads/1329389308_administrator.png").getBytes())
                log.info "uploaded  file  - > " + uploadedFile

                NayaxUser nayaxUser = apiService.getUserFromToken(params.token);
                result = apiService.setProfilePicture(nayaxUser, uploadedFile, params.int('orientation'))
            } else {
                result['status'] = "error"
                result['message'] = "No content uploaded. Please upload other image."
            }
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getProfilePicture = {
        def result = [:]
        NayaxUser nayaxUser = MobileAuthToken.findByToken(params.token).refresh().nayaxUser;
        try {
            Picture picture = nayaxUser.picture
            if(nayaxUser.facebook?.fid){
                streamFacebookPicture(nayaxUser.facebook.fid)
                return;
            }
            if (picture) {
                def mimeType = picture.mimeType.split("/")
                log.info "mimeType == >  " + mimeType[1].toUpperCase()
                log.info "path ===>  " + picture.filePath + picture.filename + "/" + picture.filename + picture.originalFilename.substring(picture.originalFilename.lastIndexOf("."), picture.originalFilename.length())
                def imageTool = new ImageTool();
                imageTool.load(picture.filePath + picture.filename + "/" + picture.filename + picture.originalFilename.substring(picture.originalFilename.lastIndexOf("."), picture.originalFilename.length()));
                imageTool.thumbnail(500);
                def thumbImage = imageTool.getBytes("PNG")
                response.setHeader("Content-disposition", "attachment; filename=${picture.originalFilename}")
                response.contentType = picture.mimeType//'image/jpeg' will do too
                response.outputStream << thumbImage
                response.outputStream.flush();
                log.info "size of image --  > ${thumbImage.length}"
                return;
            }
            else {
                result['status'] = "error"
                result['message'] = "No profile picture found."
            }
        } catch (Exception e) {
            e.printStackTrace()
            result['status'] = "error"
            result['message'] = "No profile picture found."
        }
        render result as JSON
    }

    def streamFacebookPicture(fid){
        def pictureUrl = apiService.getPictureUrl(fid)
        log.info ('Fetching facebook user image')
        response.setHeader("Content-disposition", "attachment; filename=facebookfilename}")
        response.contentType = 'image/jpeg'
        response.outputStream << new URL(pictureUrl).openStream()
        response.outputStream.flush();

    }

    def getOtherUserProfilePicture = {
        def result = [:]
        try {

            if(params.id.startsWith('f')){
                streamFacebookPicture(params.id.substring(1))
                return;
            }
            NayaxUser nayaxUser = NayaxUser.get(params.id.toLong()).refresh()
            Picture picture = nayaxUser.picture
            if(nayaxUser.facebook?.fid){
                streamFacebookPicture(nayaxUser.facebook.fid)
                return;
            }
            else if (picture) {
                def mimeType = picture.mimeType.split("/")
                log.info "mimeType == >  " + mimeType[1].toUpperCase()
                log.info "path ===>  " + picture.filePath + picture.filename + "/" + picture.filename + picture.originalFilename.substring(picture.originalFilename.lastIndexOf("."), picture.originalFilename.length())
                def imageTool = new ImageTool();
                imageTool.load(picture.filePath + picture.filename + "/" + picture.filename + picture.originalFilename.substring(picture.originalFilename.lastIndexOf("."), picture.originalFilename.length()));
                imageTool.thumbnail(500);
                def thumbImage = imageTool.getBytes("PNG")
                response.setHeader("Content-disposition", "attachment; filename=${picture.originalFilename}")
                response.contentType = picture.mimeType//'image/jpeg' will do too
                response.outputStream << thumbImage
                response.outputStream.flush();
                log.info "size of image --  > ${thumbImage.length}"
                return;
            } else {
                result['status'] = "error"
                result['message'] = "No profile picture found."
            }
        } catch (Exception e) {
            e.printStackTrace()
            result['status'] = "error"
            result['message'] = "No profile picture found."
        }

        render result as JSON
    }

    def getProfileShareTypes = {
        def result = [:]
        result['status'] = 'success'
        result['message'] = ProfileShareType.list()
        render result as JSON
    }

    def getUserDetailsForLisn = {
        log.trace "Inside controller:API action:getUserDetailsForLisn with params : ${params}"
        def result = [:]
        try {
            result = apiService.getUserDetailsForLisn(params.id, params.lisnId, params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def changePassword = {
        def result = [:]
        try {
            result = apiService.changePassword(params.password, params.password2, params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def notification = {
        def result = [:];
        try {
            result = apiService.notification(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getConnectionToBeAcceptNotification = {
        def result = [:];
        try {
            result = apiService.getConnectionToBeAcceptNotification(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def notifyAcceptedFriendRequest = {
        def result = [:]
        try {
            result = apiService.notifyAcceptedFriendRequest(params.token, params.friendId)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getPeopleNearByNotification = {
        def result = [:];
        try {
            result = apiService.getPeopleNearByNotification(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def nearby = {
        def result = [:]
        try {
            result = apiService.nearby(params, request)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }
    def nearbyV2 = {
        def result = [:]
        try {
            result = apiService.nearbyV2(params, request)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def sendConnectionRequest = {
        def result = [:];
        try {
            result = apiService.sendConnectionRequest(params.token, params.targetUserId, params.profileShareType);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def createLISNv2 = {
        def result = [:]
        try {
            result = apiService.createLISNv2(params, request);
            render result as JSON
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
    }

    def createLISNV3 = {
        def result = [:]
        try {
            result = apiService.createLISNV3(params, request);
            render result as JSON
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
    }

    def inviteFriendsToLISN = {
        def result = [:]
        try {
            result = apiService.inviteFriendsToLISN(params, request);
            render result as JSON
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
    }

    def sendFacebookConnectionRequest = {
        def result = [:];
        try {
            result = apiService.sendFacebookConnectionRequest(params.token, params.otherUserId);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }
    def sendExternalConnectionRequest = {
        def result = [:];
        try {
            result = apiService.sendExternalConnectionRequest(params.token, params.targetUserId, params.socialNetwork);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def acceptConnectionRequest = {
        def result = [:];
        try {
            result = apiService.acceptConnectionRequest(params.token, params.targetUserId, params.profileShareType);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }
    def lisnInvitationResponse = {
        def result = [:];
        try {
            result = apiService.lisnInvitationResponse(params);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def ignoreConnectionRequest = {
        def result = [:]
        try {
            result = apiService.ignoreConnectionRequest(params.token, params.targetUserId)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getProfileOfPeopleNearBy = {
        def result = [:];
        try {
            result = apiService.getProfileOfPeopleNearBy(params.token, params.otherUserId);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getProfileOfUser = {
        def result = [:];
        try {
            result = apiService.getProfileOfUser(params.token, params.otherUserId);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getProfileOfUserV2 = {
        def result = [:];
        try {
            result = apiService.getProfileOfUserV2(params.token, params.otherUserId);
        } catch (Exception e) {
            handleException(e);
        }

        render result as JSON
    }

    def shareContactInfo = {
        def result = [:];
        try {
            result = apiService.shareContactInfo(params.token, params.targetId, params.boolean('targetIsLisn'));
        } catch (Exception e) {
            handleException(e);
        }

        render result as JSON
    }

    def shareContactInfoV2 = {
        def result = [:];
        try {
            result = apiService.shareContactInfoV2(params.token, params.targetId, params.boolean('targetIsLisn'));
        } catch (Exception e) {
            handleException(e);
        }

        render result as JSON
    }

    def saveLatitudeAndLongitude = {
        def result = [:];
        try {
            result = apiService.saveLatitudeAndLongitude(params.token, params.latitude, params.longitude)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getCommonLisns = {
        def result = [:]
        try {
            result = apiService.getCommonLisns(params.token, params.otherUserId, params.timeStamp);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        log.info "getCommonLisns result : " + result
        render result as JSON
    }

    def getCommonFriends = {
        def result = [:]
        try {
            result = apiService.getCommonFriends(params.token, params.otherUserId);
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        log.info "getCommonFriends result : " + result
        render result as JSON
    }

    def getUnreadMessageCount = {
        def result = [:]
        try {
            result = apiService.getUnreadMessageCount(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }
    def getMessages = {
        def result = [:]
        try {
            result = apiService.getMessages(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        render result as JSON
    }

    def getPrivateMessage = {
        def result = [:]
        try {
            result = apiService.getPrivateMessage(params.token, params.receiver, params.timeStamp)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }
    def getLatestDirectMessages = {
        def result = [:]
        try {
            log.info(params.getPrevious)
            if( params.boolean('getPrevious')){
                result = apiService.getLatestDirectMessagesBefore(params.token, params.receiver, params.timeStamp, params.lastMessageId)
            }else{
                result = apiService.getLatestDirectMessages(params.token, params.receiver, params.timeStamp, params.lastMessageId)
            }
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON
    }

    def getLatestDirectMessagesV2 = {
        def result = [:]
        try {
            log.info(params.getPrevious)
            if( params.boolean('getPrevious')){
                result = apiService.getLatestDirectMessagesBefore(params.token, params.receiver, params.timeStamp, params.lastMessageId)
            }else{
                result = apiService.getLatestDirectMessagesV2(params.token, params.receiver, params.timeStamp, params.lastUpdated)
            }
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON
    }

    def sendPrivateMessage = {
        def result = [:]
        try {
            // Making v2 default
            // result = apiService.sendPrivateMessage(params.token, params.receiver, params.content)
            result = apiService.sendPrivateMessageV2( params.messageId, params.token, params.receiver, params.content, params.image)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        log.info ("Result JSON: ${result}" )

        render result as JSON
    }

    def sendPrivateMessageV2 = {
        def result = [:]
        try {
            result = apiService.sendPrivateMessageV2( params.messageId, params.token, params.receiver, params.content, params.image)

        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        log.info ("Result JSON: ${result}" )

        render result as JSON
    }

    def sendPrivateMessageV3 = {
        def result = [:]
        try {
            result = apiService.sendPrivateMessageV3( params.messageId, params.token, params.receiver, params.content, params.image, params.suggestedImageUrl)

        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        log.info ("Result JSON: ${result}" )

        render result as JSON
    }

    //For IOS
    def sendPrivateMessageV4 = {
        def result = [:]
        try {
            result = apiService.sendPrivateMessageV4(request)

        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        log.info ("Result JSON: ${result}" )

        render result as JSON
    }

    def inviteFriends = {
        def result = [:]
        try {
            result = apiService.inviteFriends(params)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }
        log.info ("Result JSON: ${result}" )

        render result as JSON
    }

    def updateBadgeCount = {
        def result = [:]
        try {
            result = apiService.updateBadgeCount(params.token, params.int('newCount'))
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def decrementBadgeCountByOne = {
        def result = [:]
        try {
            result = apiService.decrementBadgeCountByOne(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }

    def getBadgeCount = {
        def result = [:]
        try {
            result = apiService.getBadgeCount(params.token)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "Some Exception occured, Please try again."
        }

        render result as JSON
    }


    def forgotPassword = {EmailCommand ec ->

        def result = [:]

        if (ec.validate()) {
            try {
                result = apiService.sendTempPassword(params.email)
            } catch (Exception e) {
                e.printStackTrace()
                result.status = "error"
                result.message = "Error in sending Mail"
            }

        } else {
            result.status = "error"
            result.message = "Invalid Email Address"
        }
        render result as JSON
    }

    def disableSocialNetworkSetting = {

        def result = [:]
        try {
            result = apiService.disableSocialNetworkSetting(params.token, params.disable)
        } catch (Exception e) {
            e.printStackTrace()
            result["status"] = "error"
            result["message"] = "error in disconnect"
            log.debug "=========================Exception============Disable SocialNetwork Setting" + e
        }

        render result as JSON
    }

    def sendPhoneInvite = {
        return apiService.sendPhoneInvite(params)
    }

    def getExternalEventDetails = {
        def result = [:]
        try {
            result = apiService.getExternalEventDetails(params.token, params.externalEventId, params.eventSource, params.timeStamp)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }


    def getEventGoers = {
        def result = [:]
        try {
            result = apiService.getEventGoers(params.token, params.externalEventId, params.eventSource)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def goingToEvent = {

        def result = [:]
        try {
            result = apiService.goingToEvent(params.token, params.externalEventId, params.eventSource)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def notGoingToEvent = {

        def result = [:]
        try {
            result = apiService.notGoingToEvent(params.token, params.externalEventId, params.eventSource)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def getEventMessages = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.getEventMessages(params.token, params.externalEventId, params.timeStamp, params.lastMessageId, params.eventSource)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }



    def getEventTrends = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.getEventTrends(params.token, params.externalEventId, params.eventSource, params.timeStamp)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def getEventsUserParticipatedIn = {
        def result = [:]
        try {
            result = apiService.getEventsUserParticipatedIn(params.token, params.timeStamp)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def postEventMessage = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.postEventMessage(params.token, params.image, params.messageId, params.content, params.externalEventId)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def postEventMessageV2 = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.postEventMessageV2(params.token, params.image, params.messageId, params.content, params.externalEventId)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    //For IOS
    def postEventMessageV4 = {

        def result = [:]
        try {
            //log.info ="IN API controller getNearbyEvents"
            result = apiService.postEventMessageV4(request)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON;
    }

    def loginWithLinkedIn = {
        def result = [:]
        try {
            result = apiService.loginWithLinkedIn(params)
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON;
    }
	
    def loginWithPhone = {
        def result = [:]
        try {
            result = apiService.loginWithPhoneDigits(params)
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON;
    }

    def inviteFriendsToEvent = {
        def result = [:]
        try {
            result = apiService.inviteFriendsToEvent(params, request)
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON;
    }

    def muteNotification = {

        def result = [:]
        try {
            if (params.messageType == "LISN") {
                result = apiService.enableLisnMute(params.token, params.id, params.duration, params.durationType, params.showNotification)
            }
            if (params.messageType == "EVENT") {
                result = apiService.enableEventMute(params.token, params.id, params.duration, params.durationType, params.showNotification)
            }
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def unMuteNotification = {

        def result = [:]
        try {
            if (params.messageType == "LISN") {
                result = apiService.disableLisnMute(params.token, params.id)
            }
            if (params.messageType == "EVENT") {
                result = apiService.disableEventMute(params.token, params.id)
            }
        } catch (Exception e) {
            handleException(e)
        }
        render result as JSON
    }

    def getMuteStatus = {
        def result = [:]
        try {
            result = apiService.isMuteEnabled(params.token, params.id, params.messageType)

        } catch (Exception e) {
            handleException(e)
        }

        log.info ("Result JSON: ${result}" )

        render result as JSON
    }

    def getGoogleContacts = {
        def result = [:]

        try {
            result = apiService.getGoogleContacts(params.token, params.googleAccessToken)
        } catch (Exception e) {
            handleException(e)
        }

        render result as JSON
    }
	
    def saveInviteesList = {
        def result = [:]
        
        try {
            result = apiService.saveInviteesList(params, request)
        } catch (Exception e) {
            handleException(e)
        }
        
        render result as JSON
    }

}
