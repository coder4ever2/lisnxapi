package nayax

import nayax.MuteMessage.DurationType

/**
 *
 * @author Vijay
 * 
 */
class DateUtil {
    
    def getEndDate(Date startDate, String duration, MuteMessage.DurationType type) {
        Calendar calendar = GregorianCalendar.getInstance()
        calendar.setTime(startDate)
        
        switch(type) {
        
            case DurationType.HOUR:
                calendar.add(Calendar.HOUR, Integer.valueOf(duration))
                break
            case DurationType.DAY:
                calendar.add(Calendar.DATE, Integer.valueOf(duration))
                break
            case DurationType.WEEK:
                calendar.add(Calendar.WEEK_OF_MONTH, Integer.valueOf(duration))
                break
            case DurationType.MONTH:
                calendar.add(Calendar.MONTH, Integer.valueOf(duration))
                break
            default:
                break
                
        }
        
        return calendar.getTime();
    }
	
}

