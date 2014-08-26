package com.taobao.zeus.util;

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
		String v=new ZeusDateTool().add(Calendar.DAY_OF_MONTH,-1000).format("yyyy-MM-dd");
		System.out.println(v);
        System.out.println(DateUtil.getDefaultRawOffset());
	}
}
