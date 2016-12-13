package nayax;

/**
 * Created by IntelliJ IDEA.
 * User: jft
 * Date: 29/12/11
 * Time: 7:06 PM
 * To change this template use File | Settings | File Templates.
 */
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionCounterListener implements HttpSessionListener {

  /*private static int totalActiveSessions=0;

  public static int getTotalActiveSession(){
	return totalActiveSessions;
  }*/


  public void sessionCreated(HttpSessionEvent arg0) {
	//totalActiveSessions++;
    System.out.println("sessionCreated - add one session into counter");
  }


  public void sessionDestroyed(HttpSessionEvent arg0) {
	//totalActiveSessions--;
	System.out.println("sessionDestroyed - deduct one session from counter");
  }
}
