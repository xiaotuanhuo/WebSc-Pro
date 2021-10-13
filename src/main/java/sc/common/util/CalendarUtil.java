package sc.common.util;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {

	public static void main(String[] args) {
		
		String date = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", CalendarUtil.dayAdds(new Date(), -1));
		
		System.out.println(date);
	}
	
	/**
	 * 获取年份
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		
		return year;
	}
	
	/**
	 * 获取当月的号数
	 * @param date
	 * @return
	 */
	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		return day;
	}
	
	/**
	 * 获取月份
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		
		return month + 1;
	}
	
	/**
	 * 日期加减天数
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date dayAdds(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);//加days天
		
		return calendar.getTime();
	}
	
	/**
	 * 月份加减
	 * @param date
	 * @param ms
	 * @return
	 */
	public static Date monthAdds(Date date, int ms) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, ms);//加ms月
		
		return calendar.getTime();
	}
	
	/**
	 * 年份加减
	 * @param date
	 * @param ys
	 * @return
	 */
	public static Date yearAdds(Date date, int ys) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, ys);//加ys年
		
		return calendar.getTime();
	}

}
