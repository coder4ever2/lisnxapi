package nayax

class MobileAuthToken {

    NayaxUser nayaxUser
    String token = UUID.randomUUID().toString()
    Date dateCreated
    Date lastUpdated
    static constraints = {
        nayaxUser(nullable: true)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }

    public static Boolean isTokenValid(String token) {
        return MobileAuthToken.countByToken(token) > 0
    }

    public static String generateTokenForUser(NayaxUser user) {
        MobileAuthToken mobileAuthToken = MobileAuthToken.findByNayaxUser(user)
        def token = null;
        mobileAuthToken = new MobileAuthToken(nayaxUser: user)
        MobileAuthToken.withTransaction { status ->
            try{
                mobileAuthToken.save(flush:true).refresh()
                token = mobileAuthToken.token
            }
            catch (Exception e){
                // token = null
            }
        }
        return token;
    }

    //TODO: Write a job which cleans up the login token.
    // Delete the tokens which are acquired using getLoginToken() these are the ones without user account
    // Also add the expiry login in code.
}
