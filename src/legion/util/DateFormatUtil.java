package legion.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {
	private final static SimpleDateFormat SDF_MONTH = new SimpleDateFormat("yyyy-MM");
	private final static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
	
	// -----------------------------------------------------------
	public static String transToMonth(Date _date) {
		if (_date == null)
			return "";
		else
			return SDF_MONTH.format(_date);
	}
	
	public static String transToDate(Date _date){
		if (_date == null)
			return "";
		else
			return SDF_DATE.format(_date);
	}
	
	public static String transToTime(Date _date) {
		if (_date == null)
			return "";
		else
			return SDF_DATE_TIME.format(_date);
	}
	
	// -----------------------------------------------------------
	/**
	 * input date String must be format 'yyyy-MM-dd' or 'yyyy-MM-dd-hh:mm:ss'.<br/>
	 * This mothod does not check whether the input is reasonable.
	 */
	public static Date parse(String _sdf) {
		if(DataFO.isEmptyString(_sdf))
			return null;
		
		Date date = null;
		try {
			date = SDF_DATE_TIME.parse(_sdf);
		} catch (ParseException e) {
			try {
				date = SDF_DATE.parse(_sdf);
			} catch (ParseException e1) {
				return null;
			}
		}
		return date;
	}
	
	public static Date getThisMonthFirstDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	public static Date getThisMonthLastDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.roll(Calendar.DATE, -1);
		return cal.getTime();
	}
	
	public static Date getEarliestTimeInDate(Date _date) {
		if(_date==null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(_date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date getLatestTimeInDate(Date _date) {
		if(_date==null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(_date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	// -----------------------------------------------------------
	public static LocalDate parseLocalDate(Date _date) {
		if (_date == null)
			return null;
		return LocalDate.parse(transToDate(_date));
	}

}
