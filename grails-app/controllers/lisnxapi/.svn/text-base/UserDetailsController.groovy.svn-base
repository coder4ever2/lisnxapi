package lisnxapi

import nayax.NayaxUser;

class UserDetailsController {

    def index() {
		def users = []
		
			if(params.in!=null || (params.id=="ALL" && params.max && params.min)){
				def allUsers = []
				if(params.in){
					def inList = []
					params.in.split(",").each{
						inList.add(it.toString().toLong())
					}
					allUsers = NayaxUser.createCriteria().list{
						"in" ("id", inList)
					}
				}else {
					def max = 0
					def min =1000
					if(params.max)
						 max = params.long('max')
					if(params.min)
						 min = params.long('min')
					allUsers = NayaxUser.createCriteria().list{
						between ("id",min, max)
					}
				}
				
				allUsers.each{user ->
				try{
				def userDetails = [name:user.fullName, 
									id:user.id,
									username: user.username,
									fbPicUrl:'https://graph.facebook.com/'+user.facebook?.fid+'/picture?width=800&height=800&type=square'
									]
				users.add(userDetails)
				}catch(Exception e){
				}
			}
		}
		def userCount = NayaxUser.count()
			
		[users:users, userCount:userCount]
		
	}
	def list (){
		def users = []
		def allUsers = NayaxUser.all
		allUsers.each{user ->
			
			def userDetails = [name:user.fullName,
								id:user.id,
								username: user.username,
								fbPicUrl:'https://graph.facebook.com/'+user.facebook?.fid+'/picture?width=800&height=800&type=square'
								]
			users.add(userDetails)
		}
		[users:users, userCount:userCount]
		
	}
}
