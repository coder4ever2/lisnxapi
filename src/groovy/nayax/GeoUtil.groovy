package nayax

import grails.converters.JSON;
import groovy.json.JsonSlurper
import java.net.URL;
import java.text.DecimalFormat;
import java.util.logging.Logger;

class GeoUtil {
	private static double d2r = (Math.PI/180);
	private static DecimalFormat df = new DecimalFormat("##.#")
	

	static main(args) {
		double latitude = 37.709976;
		double longitude = -122.051964;
		//home : 37.709976, -122.051964
		double end_latitude = 37.709976;
		double end_longitude = -122.051964;
		System.out.println(distance(latitude, longitude, end_latitude, end_longitude))
		def result = locationInfo(latitude, longitude)
		println result
	}
	
	static def distance(double latitude, double longitude, double end_latitude, double end_longitude){
		def  distance = 0.1;
		try{
			double dlong = (latitude - end_latitude) * d2r;
			double dlat = (longitude - end_longitude) * d2r;
			double a =
				Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(latitude * d2r) * Math.cos(latitude+0.2 * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			distance = 6367 * c;
			
			if(distance <0.1)
				distance = 0.1
			else {
				distance = df.format(distance);
			}
			
			if (Float.isNaN(distance.floatValue())){
				distance = "<1"
			}
			
		} catch(Exception e){
			
		}
		return distance;
	}
	
	static def locationInfo(LocationCoordinate locationCoordinate){
		return locationInfo(locationCoordinate.latitude, locationCoordinate.longitude)
	}
	
	static def locationInfo(double latitude, double longitude){
		def locationInfo = [:];
		//https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=API_KEY
		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude
		def response = getResponseFromUrl(new URL(url))
		def results = new JsonSlurper().parseText(response);
		//results.address_components.
		def city
		def state
		def foundCity  = false
		def foundState = false
		results.results.each { result ->
			if (!foundCity || !foundState){
				result.address_components.each{addressComponent ->
				//println addressComponent.types
					if (!foundCity || !foundState){
						if(addressComponent.types.contains("locality")){
							city = addressComponent.short_name
							locationInfo.city = city
							foundCity = true
						}
						if(addressComponent.types.contains("administrative_area_level_1")){
							state = addressComponent.short_name
							locationInfo.state = state
							foundState = true
						}
						
					}
				}
			}
		}
		return locationInfo.city+", "+locationInfo.state
		
		
	}
	
	static String getResponseFromUrl(URL url) {
		String response = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			int respCode = conn.responseCode
			if (respCode == 400) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				def jsonResp = JSON.parse(br.text)
			} else {
				response = conn.getInputStream().getText()
			}
		} finally {
			conn.disconnect()
		}
		return response;
	}

}
