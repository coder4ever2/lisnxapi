/**
 * 
 */
package nayax;

/**
 * @author sjaini
 *
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

class ParseUtil {

	/*
	 * curl -X POST   -H "X-Parse-Application-Id: 8rOOQ5Q2a54cTdhwcohTgenTeDsVojZToRHktguz"   
	 * -H "X-Parse-REST-API-Key: p9u9RbMM088BPqa7u1L5gRdGcRmSnVYtaoipsKsK"   -H "Content-Type: application/json"   -d '{ 
	 "installationId": "47a87734-191f-4ca4-a6f8-74bd75e271e0",
	 "channels": [
	 "Giants",
	 "Mets"
	 ],
	 "data": {
	 "alert": "Hi LISNer, lets push to Android"
	 }
	 }'   https://api.parse.com/1/push
	 */



	public static StringBuffer generatePostRequestParametersFromMap(Map parameters) throws UnsupportedEncodingException {
		StringBuffer requestParams = new StringBuffer();
		for(Object key: parameters.keySet()) {
			requestParams.append(URLEncoder.encode(key.toString(), "UTF-8"));
			
			requestParams.append("=").append(URLEncoder.encode(parameters.get(key.toString()).toString(), "UTF-8"));
			requestParams.append("&");
		}
		return requestParams;
	}


	public static String sendNotification(String requestData) throws Exception {
		HttpsURLConnection conn = getConnection();
		String response = "";
		try {

			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(requestData);
			wr.flush();
			wr.close();
			response= conn.getResponseMessage();
			System.out.println(" Response Received :" + response);
		}finally {
			if(conn!=null){
				conn.disconnect();
			}
		}
		return response;
	}

	
	
	public static HttpsURLConnection getConnection() throws IOException{
		URL url = new URL("https://api.parse.com/1/push");
		HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
		
		 // Allow Inputs
        conn.setDoInput(true);

        // Allow Outputs
        conn.setDoOutput(true);

        // Don't use a cached copy.
        conn.setUseCaches(false);

        // Use a post method.
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
		
		conn.setRequestProperty("X-Parse-Application-Id", "8rOOQ5Q2a54cTdhwcohTgenTeDsVojZToRHktguz");
		conn.setRequestProperty("X-Parse-REST-API-Key", "p9u9RbMM088BPqa7u1L5gRdGcRmSnVYtaoipsKsK");
		conn.setRequestProperty("Content-Type", "application/json");
		return conn;
	}
	public static void main (String[] args){
		System.out.println("Running");
		
		sendNotification();
	}
	public static String sendNotification(){
		JSONArray channelArray = new JSONArray();
		channelArray.add("b5f950cd-52ad-40c2-8524-262b19bf9a03");
		String response = "No Response";
		JSONObject dataJson = new JSONObject();
		JSONObject jsonObject = dataJson;
		jsonObject.put("channels", channelArray);
		dataJson.put("alert", "Hi from program at "+ new Date());
		jsonObject.put("data", dataJson.toJSONString() );
		try {
			response = sendNotification(jsonObject.toJSONString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
}

