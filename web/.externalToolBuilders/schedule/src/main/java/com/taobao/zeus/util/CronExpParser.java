package com.taobao.zeus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.quartz.CronExpression;

public class CronExpParser {
	/**
	 * 解析corn表达式，生成指定日期的时间序列
	 * 
	 * @param cronExpression
	 *            cron表达式
	 * @param cronDate
	 *            cron解析日期
	 * @param result
	 *            crom解析时间序列
	 * @return 解析成功失败
	 * */
	public static boolean Parser(String cronExpression, String cronDate,
			List<String> result) {
		if (cronExpression == null || cronExpression.length() < 1
				|| cronDate == null || cronDate.length() < 1) {
			return false;
		} else {
			CronExpression exp = null;
			try {
				// 初始化cron表达式解析器
				exp = new CronExpression(cronExpression);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
			// 定义生成时间范围
			// 定义开始时间，前一天的23点59分59秒
			Calendar c = Calendar.getInstance();
			String sStart = cronDate + " 00:00:00";
			SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date dStart = null;
			try {
				dStart = sdf.parse(sStart);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			c.setTime(dStart);
			c.add(Calendar.SECOND, -1);
			dStart = c.getTime();

			// 定义结束时间，当天的23点59分59秒
			c.add(Calendar.DATE, 1);
			Date dEnd = c.getTime();

			// 生成时间序列
			java.util.Date dd = dStart;
			dd = exp.getNextValidTimeAfter(dd);
			while ((dd.getTime() >= dStart.getTime())
					&& (dd.getTime() <= dEnd.getTime())) {
				result.add(sdf.format(dd));
				dd = exp.getNextValidTimeAfter(dd);
			}
			exp = null;
		}
		return true;
	}

	// CRON表达式
	 private static final String CRON_EXPRESSION = "2014-09-14 03:00:00";
	//private static final String CRON_EXPRESSION = "0 0 3 ? * sun";
	// 生成指定日期的CRON时间序列
	private static final String CRON_DATE = "2014-09-14";

	public static void main(String[] args) {
		List<String> lTime = new ArrayList<String>();
		if (!CronExpParser.Parser(CRON_EXPRESSION, CRON_DATE, lTime)) {
			System.out.println("无法生成Cron表达式：日期," + CRON_DATE + ";不符合规则cron表达式："
					+ CRON_EXPRESSION);
		}
		for (int i = 0; i < lTime.size(); i++) {
			System.out.println(lTime.get(i));
		}
		System.out.println(lTime.size());
	}
}
