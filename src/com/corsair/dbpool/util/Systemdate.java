package com.corsair.dbpool.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 日期处理类
 * 
 * @author Administrator
 *
 */
public final class Systemdate {

	// ///标准
	/**
	 * @return 返回yyyy-MM-dd HH:mm:ss 格式日期
	 */
	public static String getStrDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //
		return df.format(new Date()); //
	}

	/**
	 * @param dt
	 * @return 返回yyyy-MM-dd HH:mm:ss 格式日期
	 */
	public static String getStrDate(Date dt) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //
		return df.format(dt); //
	}

	/**
	 * 支持将以下格式字符串转为日期
	 * 2016-02-03 14:30:12:667;
	 * 2016-02-03 14:30:12;
	 * 2016-02-03 14:30;
	 * 2016-02-03 14;
	 * 2016-02-03;
	 * 2016-02;
	 * 2016;
	 * 2016-2-3 14:30:12;
	 * 2016/2/3 14:30:12;
	 * 16/2/3 14:30:12;
	 * 2001-11-06 11:10:58.098
	 * 
	 * @param strdt
	 * @return
	 */
	public static Date getDateByStr(String strdt) {
		try {
			String ymd = null;
			String hms = null;
			int dhidx = strdt.indexOf(" ");
			if (dhidx == -1) {
				ymd = strdt;
				hms = "00:00:00";
			} else {
				ymd = strdt.substring(0, dhidx);
				hms = strdt.substring(dhidx + 1, strdt.length());
			}
			String dsp = getSepDate(ymd);// 获取年月日分隔符
			String[] dts = (dsp == null) ? new String[] { ymd } : ymd.split(getSepDate(ymd));
			int y = (dts.length > 0) ? Integer.valueOf(dts[0]) : 1970;
			y = (y < 100) ? 2000 + y : y;
			int m = (dts.length > 1) ? Integer.valueOf(dts[1]) : 1;
			int d = (dts.length > 2) ? Integer.valueOf(dts[2]) : 1;
			hms = hms.replace(".", ":");
			dts = hms.split(":");
			int hh = (dts.length > 0) ? Integer.valueOf(dts[0]) : 0;
			int mm = (dts.length > 1) ? Integer.valueOf(dts[1]) : 0;
			int ss = (dts.length > 2) ? Integer.valueOf(dts[2]) : 0;
			int mmm = (dts.length > 3) ? Integer.valueOf(dts[3]) : 0;

			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m - 1);
			c.set(Calendar.DAY_OF_MONTH, d);
			c.set(Calendar.HOUR_OF_DAY, hh);
			c.set(Calendar.MINUTE, mm);
			c.set(Calendar.SECOND, ss);
			c.set(Calendar.MILLISECOND, mmm);
			return c.getTime();
		} catch (NumberFormatException e) {
			Logsw.debug("解析日期字符串错误【" + strdt + "】");
			throw e;
		}
	}

	/** 
	 * @param strdt
	 * @param format
	 * @return 返回指定格式日期
	 * @throws Exception
	 */
	public static Date getDateByStr(String strdt, String format) throws Exception {
		return new SimpleDateFormat(format).parse(strdt);
	}

	private static String getSepDate(String ymd) {
		for (int i = 0; i < ymd.length(); i++) {
			if (!Character.isDigit(ymd.charAt(i))) {
				return ymd.substring(i, i + 1);
			}
		}
		return null;
	}

	// public static Date getDateByStr(String dts) {
	// SimpleDateFormat sdf = null;
	// if (dts.indexOf(":") > 0)
	// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// else
	// sdf = new SimpleDateFormat("yyyy-MM-dd");
	// try {
	// return sdf.parse(dts);
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// try {
	// Logsw.error(e);
	// } catch (Exception e1) {
	// return null;
	// }
	// return null;
	// }
	// }

	public static String getStrDateByFmt(Date dt, String fmt) {
		SimpleDateFormat df = new SimpleDateFormat(fmt); //
		return df.format(dt); //
	}

	/**
	 * @return yyyy-MM-dd HH:mm:ss:SSS毫秒
	 */
	public static String getStrDateMs() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); //
		return df.format(new Date()); //
	}

	/**
	 * @param dt
	 * @return yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static String getStrDateMs(Date dt) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); //
		return df.format(dt); //
	}

	/**
	 * @return yyyy-MM-dd
	 */
	public static String getStrDateyyyy_mm_dd() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //
		return df.format(new Date()); //
	}
	/*
	 * 获取当月1号
	 */
	public static String getStrDateyyyy_mm_01() throws Exception {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-01"); //
		return df.format(new Date()); //
	}
	/**
	 * @param dt
	 * @return yyyy-MM-dd
	 */
	public static String getStrDateyyyy_mm_dd(Date dt) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //
		return df.format(dt); //
	}

	/**
	 * @param value
	 * @return yyyy-MM-dd
	 */
	public static Date getDateByyyyy_mm_dd(String value) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				return null;
			}
			return null;
		}
	}

	// ////非标准

	/**
	 * @return yyMM
	 */
	public static String getStrDateNowYYMM() {
		SimpleDateFormat df = new SimpleDateFormat("yyMM");
		return df.format(new Date()); //
	}

	/**
	 * @return yyMMdd
	 */
	public static String getStrDateYYMMDD() {
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		return df.format(new Date()); //
	}

	/**
	 * 去掉时分秒
	 * 
	 * @param date
	 * @return yyyy-MM-dd
	 * @throws ParseException
	 */
	public static Date getDateYYYYMMDD(Date date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = sdf.format(date);
		return sdf.parse(s);
	}

	/**
	 * 去掉时分秒
	 * 
	 * @param date
	 * @throws ParseException
	 */
	public static Date getDateYYYYMMDD(String datestr) throws ParseException {
		return getDateYYYYMMDD(getDateByStr(datestr));
	}

	//
	/**
	 * 日期加秒 原日期不变
	 * 
	 * @param date
	 * @param second
	 * @return
	 */
	public static Date dateSecondAdd(Date date, int second) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, second);
		return calendar.getTime();
	}

	//
	/**
	 * 日期加分钟 原日期不变
	 * 
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date dateMinuteAdd(Date date, int minute) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	/**
	 * 日期加小时 原日期不变
	 * 
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date dateHourAdd(Date date, int hour) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		return calendar.getTime();
	}

	/**
	 * 日期加天 原日期不变
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date dateDayAdd(Date date, int days) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	/**
	 * 日期加月 原日期不变
	 * 
	 * @param date
	 * @param months
	 * @return
	 */
	public static Date dateMonthAdd(Date date, int months) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	/**
	 * 获取季度
	 * 
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public static int getQuarterByMoth(int month) throws Exception {
		if ((month < 1) || (month > 12))
			throw new Exception("计算季度错误，月份必须在1到12之间");
		if ((month >= 1) && (month <= 3)) {
			return 1;
		} else if ((month >= 4) && (month <= 6)) {
			return 2;
		} else if ((month >= 7) && (month <= 9)) {
			return 3;
		} else
			return 4;
	}

	/**
	 * 获取月最大一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public static int getMothMaxDay(int year, int month) throws Exception {
		if ((month < 1) || (month > 12))
			throw new Exception("计算日期错误，月份必须在1到12之间");
		Calendar cd = Calendar.getInstance();

		cd.set(year, month - 1, 1);
		return cd.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @param dt
	 * @return 返回指定日期中文星期
	 */
	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/**
	 * 获取两个给定时间之间相差月份
	 * 
	 * @param dtbegin
	 * @param dtEnd
	 * @return
	 */
	public static int getBetweenMonth(Date dtbegin, Date dtEnd) {
		Calendar bef = new GregorianCalendar();
		Calendar aft = new GregorianCalendar();
		bef.setTime(dtbegin);
		aft.setTime(dtEnd);
		int ms = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
		int yms = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
		return Math.abs(yms + ms);
	}

	/**
	 * 时间段 f1-->t1 ，f2-->t2是否交叉
	 * 
	 * @param f1
	 * @param t1
	 * @param f2
	 * @param t2
	 * @return
	 */
	public static boolean isOverlapDate(Date f1, Date t1, Date f2, Date t2) {
		return ((f2.getTime() >= f1.getTime()) && (f2.getTime() <= t1.getTime())
				|| (t2.getTime() >= f1.getTime()) && (t2.getTime() <= t1.getTime())
				|| ((f2.getTime() <= f1.getTime()) && (t2.getTime() >= t1.getTime())));
	}

	/**
	 * 时间段 f1-->t1 ，f2-->t2 交叉长度 （单位 分钟）
	 * 
	 * @param f1
	 * @param t1
	 * @param f2
	 * @param t2
	 * @return
	 */
	public static float getMinuteOverlapDate(Date f1, Date t1, Date f2, Date t2) {
		long lf1 = f1.getTime(), lt1 = t1.getTime(), lf2 = f2.getTime(), lt2 = t2.getTime();
		long lmm = 0;
		System.out.println("lf1:" + lf1);
		System.out.println("lt1:" + lt1);

		System.out.println("lf2:" + lf2);
		System.out.println("lt2:" + lt2);
		if ((lf2 <= lf1) && (lt2 >= lf1) && (lt2 <= lt1)) {
			// ********f1**********t1*****
			// ****f2******t2*************
			// System.out.println("tp1");
			lmm = lt2 - lf1;
		} else if ((lf2 <= lf1) && (lt2 >= lt1)) {
			// ********f1**********t1*****
			// ****f2******************t2*
			// System.out.println("tp2");
			lmm = lt1 - lf1;
		} else if ((lf2 >= lf1) && (lt2 <= lt1)) {
			// ********f1**********t1*****
			// ***********f2****t2********
			// System.out.println("tp3");
			lmm = lt2 - lf2;
		} else if ((lf2 >= lf1) && (lf2 <= lt1) && (lt2 > lt1)) {
			// ********f1**********t1*****
			// ***********f2**********t2**
			// System.out.println("tp4");
			lmm = lt1 - lf2;
		} else {
			System.out.println("GRD~~~~~~~~~~~");
		}
		float rst = lmm / (1000 * 60);
		return rst;
	}

	/**
	 * @param date
	 * @return 获取指定日期所在周，周一的 日期
	 */
	public static Date getFirstOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int d = 0;
		if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
			d = -6;
		} else {
			d = 2 - cal.get(Calendar.DAY_OF_WEEK);
		}
		cal.add(Calendar.DAY_OF_WEEK, d);
		// 所在周开始日期
		return cal.getTime();
	}

	/**
	 * @param date
	 * @return 返回指定日期所在周，周一，周天 的日期
	 */
	public static TwoDate getFirstAndLastOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int d = 0;
		if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
			d = -6;
		} else {
			d = 2 - cal.get(Calendar.DAY_OF_WEEK);
		}
		cal.add(Calendar.DAY_OF_WEEK, d);
		// 所在周开始日期
		Date date1 = cal.getTime();
		cal.add(Calendar.DAY_OF_WEEK, 6);
		// 所在周结束日期
		Date date2 = cal.getTime();
		return new TwoDate(date1, date2);
	}

	/**
	 * @param date
	 * @return 获取指定日期所在月， 第一天和最后一天所在日期
	 * @throws Exception
	 */
	public static TwoDate getFirstAndLastOfMonth(Date date) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String ym = getStrDateByFmt(date, "yyyy-MM");
		Date date1 = getDateByStr(ym + "-01");
		int md = getMothMaxDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
		Date date2 = getDateByStr(ym + "-" + md);
		return new TwoDate(date1, date2);
	}



	/**
	 * @param yearmonth
	 * yyyy-MM
	 * @param weekday
	 * 1:周天 2：周一 ......
	 * 与Calendar定义的常量一致 Calendar.MONDAY
	 * @return 获取某月第一周，周几所在日期
	 */
	public static Date getMonthFirstWeekDay(String yearmonth, int weekday) {
		Date date = getDateByStr(yearmonth + "-01");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int i = 0;
		while (cal.get(Calendar.DAY_OF_WEEK) != weekday) {
			cal.add(Calendar.DATE, i++);
		}
		return cal.getTime();
	}

	public static void main(String[] args) throws Exception {
		Date f1 = getMonthFirstWeekDay("2018-04", Calendar.MONDAY);
		System.out.println("第一个周一：" + getStrDateyyyy_mm_dd(f1));
		Calendar time = Calendar.getInstance();
		time.setTime(f1);
		int days = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数

		System.out.println(days);
	}

}
