import groovy.json.JsonSlurper;
import groovy.lang.GroovyInterceptable;
import nayax.Facebook;
import nayax.FacebookPalsCache

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.request.RequestContextHolder

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONElement

import com.daureos.facebook.*;


class FacebookService implements InitializingBean, GroovyInterceptable {


    def static transactional = false
    def facebookGraphService
    def grailsApplication
    def setting
    def facebookPalsCache
	def static String facebookURL = "https://graph.facebook.com/v2.3/"

    /**
     * This method publishes the message passed as parameter in the
     * wall of the session user. If the session user hasn't associated
     * a facebook session this method returns null.
     *
     * @param fid Friend's facebook ID
     * @param message The message to publish
     */
    def publishOnFriendsWall(fid, message) {
        def result
        def facebookData = facebookGraphService.getFacebookData()

        log.debug("Facebook data: ${facebookData}")

        if (facebookData) {
            try {
                result = api("/" + fid + "/feed", facebookData, [message: message], 'POST')
            } catch (Exception e) {
                log.error(e)
            }
        }

        return result
    }
	
	def hasProfilePic(fid){
		def result
		boolean hasProfilePic = false
		Facebook facebook = Facebook.findByFid(fid)
   	//https://graph.facebook.com/1273617912?fields=picture{is_silhouette}	
		String fbPicUrl = facebookURL +fid+"?fields=picture{is_silhouette}"+"&access_token="+facebook.facebookAccessToken.accessToken
		log.info("FB Pic Url:"+fbPicUrl)
		URL url = new URL(fbPicUrl)
		String jsonResponse = getResponseFromUrl(url)
		log.debug("Json Response is ${jsonResponse}")
		if (jsonResponse) {
			def results = new JsonSlurper().parseText(jsonResponse);
			 def isSilHouette = results.picture.data.is_silhouette;
			 hasProfilePic = !isSilHouette;
		}
		
		return hasProfilePic
	}

    def getUserLikes(def accessToken) {
        List<String> listOfUrls = []
        String basicFbUrl = "https://graph.facebook.com/"
        String likeUrl = basicFbUrl + "me/likes?access_token="+accessToken
        URL url = new URL(likeUrl)
        String jsonResponse = getResponseFromUrl(url)
        log.debug("Json Response is ${jsonResponse}")
        if (jsonResponse) {
            JSONElement userJson = JSON.parse(jsonResponse)
            List<String> listOfIds = []
            userJson.data.each {userData ->
                listOfIds += userData.id
                log.debug "it---->${userData}"
            }
            log.info("All The Ids are ${listOfIds}")
            listOfUrls = getUrlLinksForEachIdInList(listOfIds)
            if (listOfUrls) {
                log.info("Got The Urls ${listOfUrls}")
            }
            else {
                log.error("NOT ABLE TO GET LIST OF URLS")
            }
        }
        return listOfUrls
    }
	def getUserFriends(def accessToken, def fid){
		//https://graph.facebook.com/1273617912/friends?access_token=
		def fields="fields=id,first_name,last_name,bio,email,picture,devices,installed,security_settings"
		def facebookUrlForFriends = facebookURL + fid+"/friends?"+fields+"&access_token="+accessToken
		log.info("User's facebook url for friends "+ facebookUrlForFriends)
		return getResponseFromUrl(new URL(facebookUrlForFriends))
	}
	def getFacebookUserDetails(def accessToken, def fid){
		//https://graph.facebook.com/1273617912/friends?access_token=
		def fields="fields=id,first_name,last_name,bio,email,picture,devices,installed,security_settings,username,link,verified,timezone,about,gender,locale,updated_time"
		def facebookUrlForFriends = facebookURL +"me?"+fields+"&access_token="+accessToken
		log.info("User's facebook details url "+ facebookUrlForFriends)
		return getResponseFromUrl(new URL(facebookUrlForFriends))
	}
	
	def getFacebookUserDetailsForInitialLogin(def accessToken, def fid){
		//https://graph.facebook.com/1273617912/friends?access_token=
		def fields="fields=id,first_name,name,last_name,email,picture,installed,link,verified,timezone,locale,updated_time"
		def facebookUrlForFriends = facebookURL +"me?"+fields+"&access_token="+accessToken
		log.info("User's facebook details url initial login "+ facebookUrlForFriends)
		return getResponseFromUrl(new URL(facebookUrlForFriends))
	}

    String getResponseFromUrl(URL url) {
        String response = null;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            int respCode = conn.responseCode
            if (respCode == 400) {
                log.error("COULD NOT MAKE CONNECTION")
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                def jsonResp = JSON.parse(br.text)
            } else {
                response = conn.getInputStream().getText()
            }
        } finally {
            conn.disconnect()
        }
        log.info("RETURNIG RESPONSE")
        return response;
    }

    List<String> getUrlLinksForEachIdInList(List<String> listOfIds) {
        String basicFbUrl = "https://graph.facebook.com/"
        List<String> listOfLikedUrls = []
        listOfIds.each {String facebookId ->
            String linkUrl = basicFbUrl + "?ids=${facebookId}"
            log.info("The current link url is ${linkUrl}")
            URL url = new URL(linkUrl)
            String jsonResponse = getResponseFromUrl(url)
            log.info("Json Response is ${jsonResponse}")
            if (jsonResponse) {
                JSONElement userJson = JSON.parse(jsonResponse)
                String link = userJson[facebookId].link
                listOfLikedUrls += link
            }
        }
        return listOfLikedUrls
    }


    void afterPropertiesSet() {
        this.setting = grailsApplication.config.setting
    }

    def invokeMethod(String name, args) {
        if (facebookPalsCache == null)
            facebookPalsCache = new FacebookPalsCache(1000)
        log.debug ("time before ${name} called: ${new Date()}")

        //Get the method that was originally called.
        def calledMethod = metaClass.getMetaMethod(name, args)

        //The "?" operator first checks to see that the "calledMethod" is not
        //null (i.e. it exists).
        if (name.equals("getFriends")) {
            log.debug "getFriends..."
            def session = RequestContextHolder.currentRequestAttributes().getSession()
            def friends = facebookPalsCache.get(session.facebook.uid)
            if (!friends) {
                def getFriends = facebookGraphService.invokeMethod(name, args)
                log.debug "Saving FBFRIENDS in CACHE"
                facebookPalsCache.put(session.facebook.uid, getFriends)
                return getFriends
            }
            else return friends
        }

        else {
            if (calledMethod) {
                return calledMethod.invoke(this, args)
            }
            else {
                return facebookGraphService.invokeMethod(name, args)
            }
        }

        log.debug("time after ${name} called: ${new Date()}\n")
    }
}
