package com.taobao.zeus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Zeus日期工具类
 * 用于界面配置项的动态替换
 * @author zhoufang
 *
 */
public class ZeusDateTool {
	public ZeusDateTool(Date date){
		this.calendar.setTime(date);
	}
	private Calendar calendar=Calendar.getInstance();
	
	public ZeusDateTool addDay(int amount){
		calendar.add(Calendar.DAY_OF_YEAR, amount);
		return this;
	}
	
	public ZeusDateTool add(int field,int amount){
		calendar.add(field, amount);
		return this;
	}
	
	public String format(String pattern){
		SimpleDateFormat format=new SimpleDateFormat(pattern);
		return format.format(calendar.getTime());
	}
    public String currentTimestamp(){
        return String.valueOf(System.currentTimeMillis()/1000);
    }
	
	public static void main(String[] args) {
		String v=new ZeusDateTool(new Date()).format("yyyy-MM-dd");
		System.out.println(v);
        System.out.println(DateUtil.getDefaultRawOffset());
	}
	
	/**
	 * 日期格式字符串互相转换
	 * @param dateStr 需要转换的字符串
	 * @param formatStr 需要格式的目标字符串  举例 yyyy-MM-dd
	 * @return outFormatStr 需要格式的输出字符串  举例 yyyy-MM-dd
	 * @throws ParseException 转换异常
	 */
	public static String StringToDateStr(String dateStr,String formatStr, String outFormatStr){
		DateFormat sdf = new SimpleDateFormat(formatStr);
		SimpleDateFormat outDateFormat=new SimpleDateFormat(outFormatStr);
		Date date = null;
		String outDateStr = "";
		try {
			date = sdf.parse(dateStr);
			outDateStr = outDateFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return outDateStr;
	}
	
	/**
	 * 字符串转换到时间格式
	 * @param dateStr 需要转换的字符串
	 * @param formatStr 需要格式的目标字符串  举例 yyyy-MM-dd
	 * @return Date 返回转换后的时间
	 * @throws ParseException 转换异常
	 */
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
}
