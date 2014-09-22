package com.taobao.zeus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static void main(String[] args) throws ParseException {
		System.out.println(string2Date("2014-01-22 23:59:59").toString());
		System.out.println(getDateTimeStrByTZ("2014-09-22 00:09:59"));
		System.out.println(timestamp2Date(1411315799000L,"GMT+0800"));
		System.out.println(getRawOffset("2014-09-22 00:09:59"));
		System.out.println(StringToDate("2014-09-22 00:09:59", "yyyy-MM-dd HH:mm:ss").getTime());
		System.out.println(getDelayStartTime(4, "GMT+0800"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(10, -1);
		System.out.println(calendar.getTime());
		calendar.add(Calendar.HOUR,-1);
		System.out.println(calendar.getTime());
	}
	
	public static Date StringToDate(String dateStr,String formatStr){
		DateFormat sdf=new SimpleDateFormat(formatStr);
		Date date=null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date timestamp2Date(long timestamp, String timezone)
			throws ParseException {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				DATE_FORMAT);
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timezone));

		SimpleDateFormat dateFormatLocal = new SimpleDateFormat(
				DATE_FORMAT);

		return dateFormatLocal.parse(dateFormatGmt.format(new Date(timestamp)));
	}

	public static String getDelayStartTime(int hour, String timezone) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(10, hour);
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"yyyy-MM-dd HH:00:00");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timezone));
		return dateFormatGmt.format(calendar.getTime());
	}

	public static String getDelayTime(int hour, String date)
			throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(string2Date(date).getTime());
		calendar.add(10, hour);
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				DATE_FORMAT);
		return dateFormatGmt.format(calendar.getTime());
	}

	public static String getDelayEndTime(int hour, String timezone) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(10, hour);
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"yyyy-MM-dd HH:59:59");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timezone));
		return dateFormatGmt.format(calendar.getTime());
	}

	public static String getDayStartTime(int off, String timezone) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(5, off);
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"yyyy-MM-dd 00:00:00");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timezone));
		return dateFormatGmt.format(calendar.getTime());
	}

	public static String getDayEndTime(int off, String timezone) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(5, off);
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"yyyy-MM-dd 23:59:59");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timezone));
		return dateFormatGmt.format(calendar.getTime());
	}

	public static String getTimeStrByTimestamp(long timestamp, String tz) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				DATE_FORMAT);
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone(tz));
		return dateFormatGmt.format(calendar.getTime());
	}

	public static String getDateTimeStrByTZ(String tzStr) {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				DATE_FORMAT);
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone(tzStr));
		return dateFormatGmt.format(new Date());
	}

	public static long getDefaultRawOffset() {
		return TimeZone.getDefault().getRawOffset();
	}

	public static long getRawOffset(String timeZoneStr) {
		return TimeZone.getTimeZone(timeZoneStr).getRawOffset();
	}

	public static String timestamp2DataTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(Long.valueOf(timestamp));
	}

	public static long string2Timestamp(String dateString, String timezone)
			throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if ((timezone != null) && (!timezone.equals(""))) {
			df.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		Date date1 = df.parse(dateString);
		long temp = date1.getTime();
		return temp;
	}

	public static Date string2Date(String dateString) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
	}

	public static String date2String(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	public static String getDefaultTZStr() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("Z");
		StringBuilder sb = new StringBuilder("GMT");
		sb.append(dateFormat.format(new Date()));
		return sb.toString();
	}

	public static String getToday() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

}
