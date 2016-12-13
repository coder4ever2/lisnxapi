package com.lisn.api

import nayax.Facebook;
import nayax.FacebookConnection;
import nayax.LinkedinConnection;
import nayax.NayaxUser;
import grails.transaction.Transactional

@Transactional
class CampaignService {

    def getCampaignStats(def fid, def lid) {
		//NayaxUser nayaxUser = NayaxUser.get(1)
		//fid = nayaxUser.facebook.fid
		//lid = nayaxUser.linkedin.loginProviderUID
		Map result = [:]
		def facebookFriends = FacebookConnection.findAllByUserFacebookId(fid)
		def linkedInConnections = LinkedinConnection.findAllByUserLinkedinId(lid)
		
		int linkedinConnectionCount = linkedInConnections.size();
		//int facebookFriendsCount = facebookFriends.size();
		Facebook facebook = Facebook.findByFid(fid)
		int facebookFriendsCount = 0
		if (facebook) {
			facebookFriendsCount = facebook.friendsTotalCount
		}
		int overlap = 0;
		for(FacebookConnection facebookConnection:facebookFriends ){
			for(LinkedinConnection linkedinConnection: linkedInConnections){
				if(facebookConnection.firstName?.equals(linkedinConnection.firstName))
				 	if(facebookConnection.lastName?.equals(linkedinConnection.lastName))
					 	overlap++
			}
		}
		int totalFriends = linkedinConnectionCount + facebookFriendsCount +1
		int total = totalFriends * 2
		int disjoint = (linkedinConnectionCount+ facebookFriendsCount -overlap)*0.8
		
		
		def liPercent = linkedinConnectionCount.div( total);
		def fbPercent = facebookFriendsCount.div(  total);
		def overlapPercent = overlap.div( total);
		def disjointPercent = disjoint.div( total);
		
		def championBadge = 'Socialite'
		
		if(overlapPercent > 0.4)
			championBadge = 'Boundary Spanner'
		else if (liPercent > 2* fbPercent)
			championBadge = 'Professional Guru'
		else if (fbPercent > 2* liPercent)
			championBadge = 'Socialite'
		else 
			championBadge = 'Organized'
		
		result['linkedin_count'] = linkedinConnectionCount
		result['facebook_count'] = facebookFriendsCount
		result ['overlap'] = overlap
		result ['disjoint'] = disjoint
		result['championBadge'] =championBadge 
		//log.info["Results " + result]
		return result		

    }
}
