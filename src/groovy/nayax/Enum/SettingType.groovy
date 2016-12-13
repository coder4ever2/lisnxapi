package nayax.Enum

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 4/25/12
 * Time: 12:46 AM
 * To change this template use File | Settings | File Templates.
 */
public enum SettingType {
	
	LATLONGRANGE("Lat Long Range"), 
	TIME_WINDOW("Time window for nearby activity"), 
	PIC_WIDTH("Pic width"), 
	CURRENT_ANDROID_BUILD("Current Android Build"), 
	CURRENT_IOS_BUILD("Current iOS Build")
	
	private final String str;
	
	SettingType(String str) {
		this.str = str;
	}
	
	public String getKey() { return name()
	}
	
	public String getValue() { return toString()
	}
	
	public String toString() {
		return str;
	}
	
	public static list() {
		List settingTypes = []
		SettingType.values().each {SettingType settingType ->
			settingTypes.add(settingType.getKey())
		}
		return settingTypes
	}
}