package com.cn.eplat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.cn.eplat.timedtask.PushPunchCardDatas;

public class DateUtil {
	
	private static Logger logger = Logger.getLogger(DateUtil.class);
	
	public static final String DU_YMD = "yyyy-MM-dd";
	public static final String DU_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	
	private static SimpleDateFormat sdf_ymd = new SimpleDateFormat(DU_YMD);	// “年-月-日”格式的日期格式化对象（对应日期格式的类型 type = 1）
	private static SimpleDateFormat sdf_hms = new SimpleDateFormat(DU_YMD_HMS);	// “年-月-日 时:分:秒”格式的日期格式化对象（对应日期格式的类型 type = 2）
	
	public static final long ONE_DAY_TIME_MILLS = 24*60*60*1000;	// 表示一天时间对应的时间毫秒数
	
	public static final String[] WEEK_DIGITS = {"0", "1", "2", "3", "4", "5", "6",};
	public static final String[] CHINESE_WEEK_NUMBERS = {"日", "一", "二", "三", "四", "五", "六",};
	public static final String[] ENGLISH_WEEK_DAYS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",};
	public static final String[] SHORT_ENGLISH_WEEK_DAYS = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat",};
	
	public static SimpleDateFormat getSdf_ymd() {
		return sdf_ymd;
	}
	public static void setSdf_ymd(SimpleDateFormat sdf_ymd) {
		DateUtil.sdf_ymd = sdf_ymd;
	}
	public static SimpleDateFormat getSdf_hms() {
		return sdf_hms;
	}
	public static void setSdf_hms(SimpleDateFormat sdf_hms) {
		DateUtil.sdf_hms = sdf_hms;
	}
	
	
	/**
	 * 用给定的时间毫秒数计算给定的日期时间相加之后的日期对象
	 * 
	 * @param date
	 * @param timeMills
	 * @return
	 */
	public static Date calcDatePlusGivenTimeMills(Date date, long timeMills) {
		if(date == null) {
			return null;
		}
		
		if(timeMills == 0) {
			return date;
		}
		
		long date_time_mills = date.getTime();
		
		return new Date(date_time_mills + timeMills);
		
	}
	
	
	
	/**
	 * 获取从现在开始往前数xdays天的日期
	 * 
	 * @param xdays
	 * @return
	 */
	public static String getDateXDaysAgo(int xdays) {
		
		Date dNow = new Date();   //当前时间
		Date dBefore = new Date();
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1*xdays);  //设置为前xdays天
		dBefore = calendar.getTime();   //得到前xdays天的时间

//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String defaultStartDate = sdf.format(dBefore);    //格式化前xdays天
//		String defaultEndDate = sdf.format(dNow); //格式化当前时间

		System.out.println(xdays + " 天前的日期是：" + defaultStartDate);
//		System.out.println("生成的时间是：" + defaultEndDate);
		
		return defaultStartDate;
	}
	
	/**
	 * 根据给定的两个日期值（一个开始日期，一个结束日期），返回这两个日期（包含这两个日期在内）之间的日期（yyyy-MM-dd 格式）的字符串数组
	 * 
	 * @param date1    第一个日期
	 * @param date2    第二个日期值
	 * @return 两个日期之间（含原来两个日期在内）的日期字符串数组（yyyy-MM-dd 格式）
	 */
	public static String[] getDateArrBy2Dates(Date date1, Date date2) {
		if(date1 == null && date2 == null) {
			return null;	// 如果传入的两个日期都为空，则直接返回null
		}
		
		Date start_date = null;	// 开始日期
		Date end_date = null;	// 结束日期
		Date now_date = new Date();	// 当前日期
		
		// 如果传入的两个日期中有一个为空，则将其中不为空的日期视为开始日期，结束日期默认设为当前日期
		if(date1 == null) {	// 如果date1为空，那么date2一定不为空，此时将date2设为开始日期
			start_date = date2;
			end_date = now_date;
		}
		if(date2 == null) {	// 如果date2为空，那么date1一定不为空，此时将date1设为开始日期
			start_date = date1;
			end_date = now_date;
		}
		
		// 如果传入的两个日期都不为空，则设其中一个为开始日期，另一个为结束日期
		if(date1 != null && date2 != null) {
			start_date = date1;
			end_date = date2;
		}
		
		// 开始日期和结束日期设置好后，如果开始日期在结束日期之后，则调换开始和结束日期的位置
		Date tmp_date = null;
		if(start_date.after(end_date)) {
			tmp_date = start_date;
			start_date = end_date;
			end_date = tmp_date;
		}
		
		// 得到这两个日期之间相隔的天数
		int days_interval = daysBetween2Dates(start_date, end_date);
		if(days_interval < 0) {	// 如果计算两个日期之间间隔的天数时出现异常，则直接返回null
			return null;
		}
		
		// 将开始日期和结束日期按照“yyyy-MM-dd”的日期格式转换成日期字符串
		String start_date_ymd = formatDate(1, start_date);
//		String end_date_ymd = formatDate(1, end_date);
		
		if(days_interval == 0) {	// 如果计算得到的两个日期之间间隔的天数等于0，则说明这两个日期是同一天，那么直接返回只包含开始日期这一天的日期字符串数组
			return new String[] {start_date_ymd};
		}
		
//		if(start_date_ymd != null && end_date_ymd != null) {
//			if(start_date_ymd.equals(end_date_ymd)) {	// 如果两个转换格式后的日期字符串完全相同，则直接返回只包含开始日期这一天的日期字符串数组
//				
//			}
//		}
		
		// 如果计算得到的两个日期之间间隔的天数大于0，则说明这两个日期不是同一天
		// 那么就从开始日期那一天开始，生成(days_interval+1)个日期字符串，然后组装成一个日期字符串数组返回
		String[] date_strs = new String[days_interval+1];	// 新创建一个包含(days_interval+1)个日期字符串的字符串数组，用来存放将要返回的日期字符串
		date_strs[0] = start_date_ymd;	// 第一个日期其实就是开始日期，所以这里就不通过计算，直接将它放进字符串数组
//		date_strs[days_interval] = end_date
		for(int i=1; i<=days_interval; i++) {	// 从第2个日期字符串开始计算i天后的日期字符串，循环逐个向日期字符串数组中添加(days_interval+1)个日期字符串
			date_strs[i] = getNDaysAfterADate(i, start_date);
		}
		
		return date_strs;
	}
	
	/**
	 * 得到一个日期之后n天的日期字符串（“yyyy-MM-dd”格式）
	 * 
	 * @param nDays    n天（n为整数，可以为负数和0。当n=0时，表示要计算这个日期当天的日期字符串；当n<0时，表示要计算这个日期之前|n|天的日期字符串）
	 * @param date    作为参数传入的日期对象
	 * @return    这个日期之后n天的日期字符串（“yyyy-MM-dd”格式）
	 */
	public static String getNDaysAfterADate(int nDays, Date date) {
		if(date == null) {	// 如果传入的日期参数为空，则直接返回null
			return null;
		}
		
		String date_ymd_str = formatDate(1, date);	// 将传入的日期转换成“yyyy-MM-dd”格式的日期字符串
		
		if(nDays == 0) {	// 如果n=0，则直接返回当天的日期字符串
			return date_ymd_str;
		}
		
//		Date date_ymd = parse2date(1, date_ymd_str);	// 将格式化后的日期字符串再转换成一个日期对象（这时这个日期对象就只剩下有效的“年-月-日”信息了，原日期的“时:分:秒”信息已经丢失，变成了“00:00:00”）
		long date_time_mills = date.getTime();	// 得到原日期的时间毫秒数
		
		try {
			long calc_date_time_mills = date_time_mills + nDays*ONE_DAY_TIME_MILLS;	// 根据n天时间的毫秒数与原日期时间毫秒数计算得到|n|天前或|n|天后的日期对应的时间毫秒数
			
//			if(calc_date_time_mills < 0) {	// 如果计算得到的时间毫秒数小于0，则说明计算得到的这个日期已经早于了1970-01-01 00:00:00 (GMT时间，如果是北京时间，则是1970-01-01 08:00:00)，
//				
//			}
			
			Date calc_date = new Date(calc_date_time_mills);	// 根据计算得到的时间毫秒数，将其转换成想要的日期对象，表示|n|天前或|n|天后的日期
			
			String calc_date_ymd = formatDate(1, calc_date);	// 将计算得到的日期对象按照“yyyy-MM-dd”格式转换成对应的日期字符串，然后直接将其返回
			return calc_date_ymd;
			
		} catch (Exception e) {	// 如果计算时间毫秒数的过程中出现了如长整型数据溢出的异常时，直接返回null
			e.printStackTrace();
			return null;
		}
		
//		if(nDays > 0) {	// 如果n>0，则表示要得到这个日期之后n天的日期字符串
//			
//		}
//		
//		if(nDays < 0) {	// 如果n<0，则表示要得到这个日期之前|n|天的日期字符串
//			
//		}
		
//		return null;
	}
	
	/**
	 * 计算一个日期之后x天的时间
	 * 
	 * @param xDays 	x天（x为整数，可以为负数和0。当x=0时，就是传入的日期；当x<0时，表示要计算传入的日期之前|x|天的日期）
	 * @param date 		作为参数传入的日期对象
	 * @return 	x天后的日期（如果传入的日期为null或计算过程出现异常时，返回null）
	 */
	public static Date calcXDaysAfterADate(int xDays, Date date) {
		if(date == null) {	// 如果传入的日期参数为空，则直接返回null
			return null;
		}
		
		if(xDays == 0) {	// 如果传入的天数为0，则直接返回传入的日期
			return date;
		}
		
		// 如果传入的天数不为0，且传入的日期对象不为null
		long date_time_mills = date.getTime();	// 得到原日期的时间毫秒数
		try {
			long calc_date_time_mills = date_time_mills + xDays*ONE_DAY_TIME_MILLS;	// 根据x天时间的毫秒数与原日期时间毫秒数计算得到|x|天前或|x|天后的日期对应的时间毫秒数
			Date calc_date = new Date(calc_date_time_mills);	// 根据计算得到的时间毫秒数，将其转换成想要的日期对象，表示|x|天前或|x|天后的日期
			return calc_date;
		} catch (Exception e) {	// 如果计算时间毫秒数的过程中出现了如长整型数据溢出的异常时，直接返回null
			e.printStackTrace();
			return null;
		}
	}
	
	
	// （版本1）
	// REF: http://blog.csdn.net/shiyuezhong/article/details/9196803
	public static Integer getIntervalDays(Date fDate, Date oDate) {

		if (null == fDate || null == oDate) {
			return null;
		}
		long intervalMilli = oDate.getTime() - fDate.getTime();

		return (int) (intervalMilli / (24 * 60 * 60 * 1000));
	}
	
	
	// （版本2）
	// REF: http://blog.csdn.net/shiyuezhong/article/details/9196803
	public static int daysOfTwo(Date fDate, Date oDate) {
		
       Calendar aCalendar = Calendar.getInstance();
       
       aCalendar.setTime(fDate);

       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

       aCalendar.setTime(oDate);

       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

       return day2 - day1;

    }
	
	
	/**
	 * 计算传入的两个日期之间相差的天数（版本3，参考了版本1和版本2的算法）
	 * 
	 * @param date1    第一个日期参数
	 * @param date2    第二个日期参数
	 * @return 两个日期之间相差的天数（若两个日期是同一天，则返回0）；如果出现异常，则返回值为负值（-1、-2或-3）
	 */
	public static int daysBetween2Dates(Date date1, Date date2) {
		if(date1 == null || date2 == null) {
			return -1;	// 如果两个日期中有一个为空，则直接返回-1
		}
		
		if(date1.equals(date2)) {	// 如果两个日期的引用相同，则直接返回0
			return 0;
		}
		
//		if(date1.before(date2)) {	// 如果第一个日期在第二个日期之前
//			date1.getTime();
//		}
		
		// 转换得到两个日期的“年-月-日”格式的字符串
		String date1_ymd_str = formatDate(1, date1);
		String date2_ymd_str = formatDate(1, date2);
		
		if(date1_ymd_str == null || date2_ymd_str == null) {
			return -2;	// 如果两个日期的“年-月-日”格式化后的字符串为空，则直接返回-2
		}
		
		if(date1_ymd_str.equals(date2_ymd_str)) {	// 如果格式化后的两个日期字符串内容完全相同（即是同一天），则直接返回0
			return 0;
		}
		
		/*
		int len_ymd = DU_YMD.length();	// “yyyy-MM-dd”字符串的长度，值为10
		
		// 截取“yyyy-MM-dd”格式化的日期字符串中的前9个字符组成的字符串（即“yyyy-MM-d”这部分的字符串）
		String date1_sub_str = date1_ymd.substring(0, len_ymd-2);
		String date2_sub_str = date2_ymd.substring(0, len_ymd-2);
		
		if(date1_sub_str.equals(date2_sub_str)) {	// 如果前面8个字符组成的字符串完全相同，则可以进一步比较“日”的两位数值，看是否只相差1天
			
			// 取出两个格式化后的日期字符串的最末位的那个字符（即“日”的个位数）
			char tail_char1 = date1_ymd.charAt(len_ymd-1);
			char tail_char2 = date2_ymd.charAt(len_ymd-1);
			if(tail_char1 == tail_char2) {	// 最末位的那个字符也相同，则认为这两个日期是同一天，返回0
				return 0;
			}
			// 如果最末位的那个字符（应该是一个0~9的数字字符）不同，则两个字符的ASCII码的差值就是这两个日期之间相差的天数
			if(tail_char1 < tail_char2) {
				return tail_char2 - tail_char1;
			}
			if(tail_char1 > tail_char2) {
				return tail_char1 - tail_char2;
			}
			
			
			// 取出两个格式化后的日期字符串的末两位（即“年-月-日”中的“日”的数字组成的字符串）
			String tail_str_1 = date1_ymd.substring(len_ymd-2);
			String tail_str_2 = date2_ymd.substring(len_ymd-2);
			
			
//			char ch1, ch2;
			String str1 = null, str2 = null;
			if(tail_str_1.startsWith("0")) {
//				ch1 = tail_str_1.charAt(1);
				str1 = tail_str_1.substring(1);
			}
			if(tail_str_2.startsWith("0")) {
//				ch2 = tail_str_2.charAt(1);
				str2 = tail_str_2.substring(1);
			}
			
			if(str1 == null) {
				str1 = tail_str_1;
			}
			if(str2 == null) {
				str2 = tail_str_2;
			}
			
			int intVal1 = Integer.parseInt(str1);
			int intVal2 = Integer.parseInt(str2);
			
			
			// 将末两位的数字字符串转换成对应的整数值
			int tail_int_1 = 0, tail_int_2 = 0;
			try {
				tail_int_1 = Integer.parseInt(tail_str_1);
				tail_int_2 = Integer.parseInt(tail_str_2);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				logger.error("转换日期中的“日”为整数数值出现异常：" + tail_str_1 + "，" + tail_str_2);
			}
			
			if(tail_int_1 == tail_int_2) {
				return 0;
			}
			
			
			// 如果末两位的“日”整数值只相差1，则原来的两个日期就是只相差一天
			if(tail_int_1 - tail_int_2 == 1 || tail_int_2 - tail_int_1 == 1) {
				return 1;
			}
			
			
//			return (tail_int_1 > tail_int_2) ? (tail_int_1 - tail_int_2) : (tail_int_2 - tail_int_1);
			// 如果末两位的“日”整数值不相等，则返回这两个整数值之差的绝对值
			return absIntVal(tail_int_1, tail_int_2);
			
		}
		
		// 如果两个格式化后的日期字符串的前8个字符不完全相同，
		*/
		
		// 如果两个格式化后的日期字符串不完全相同（即表示不是同一天），
		// 先把这两个格式化后的日期字符串再转回日期类型，这样这两个日期的时分秒就都变成了00:00:00了，这样就可以通过计算两个日期的毫秒数之差来计算两个日期之间相隔的天数了
		Date date1_ymd = parse2date(1, date1_ymd_str);
		Date date2_ymd = parse2date(1, date2_ymd_str);
		Integer days_calc = getIntervalDays(date1_ymd, date2_ymd);
		if(days_calc != null) {
			int days_val = days_calc.intValue();
			return days_val>0?days_val:-days_val;
		} else {	// 如果用于计算的两个日期中有一个为null时，则返回-3错误代码
			return -3;
		}
		
//		return 0;
	}
	
	/**
	 * 返回两个整数之差的绝对值
	 * @param a
	 * @param b
	 * @return
	 */
	public static int absIntVal(int a, int b) {
		return a>b?(a-b):(b-a);
	}
	
	
	/**
	 * 按开始日期、结束日期和给定的日期列表，计算在开始日期和结束日期范围内排除掉给定日期列表中的日期之后的日期
	 * 
	 * @param given_dates	给定的日期列表
	 * @param start	开始日期
	 * @param end	结束日期
	 * @return	在开始日期和结束日期范围内排除掉给定日期列表中的日期之后的那部分日期
	 */
	public static List<Date> calcDatesExcludeGivenDatesByStartEndDate(List<Date> given_dates, Date start, Date end) {
		if(start == null || end == null) {
			return null;
		}
		
		List<Date> need_dates = getDatesBetweenTwoDates(start, end);
		
		if(given_dates == null || given_dates.size() == 0) {
			return need_dates;
		}
		
		for(Date date : given_dates) {
			Date trans_date = transToDateIgnoreHHmmss(date);
			if(need_dates.contains(trans_date)) {
				need_dates.remove(trans_date);
			}
		}
		
		return need_dates;
		
		// TODO: 优化遍历日期列表的算法。。。
//		if(given_dates.size() <= need_dates.size()) {
//			
//		} else {
//			
//			return null;
//		}
	}
	
	/**
	 * 返回传入日期忽略时分秒（即将时分秒都设为0）后的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date transToDateIgnoreHHmmss(Date date) {
		if(date == null) {
			return null;
		}
		
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		
		cld.set(Calendar.HOUR_OF_DAY, 0);
		cld.set(Calendar.MINUTE, 0);
		cld.set(Calendar.SECOND, 0);
		
		return cld.getTime();
	}
	
	
	/**
	 * 根据给定的两个日期，返回这两个日期之间的所有日期
	 * 
	 * @param date1	日期1
	 * @param date2	日期2
	 * @return	这两个日期之间的所有日期
	 */
	public static List<Date> getDatesBetweenTwoDates(Date date1, Date date2) {
		if(date1 == null || date2 == null) {
			return null;
		}
		
		Calendar cld1 = Calendar.getInstance();
		Calendar cld2 = Calendar.getInstance();
		
		cld1.setTime(date1);
		cld2.setTime(date2);
		
		cld1.set(Calendar.HOUR_OF_DAY, 0);
		cld1.set(Calendar.MINUTE, 0);
		cld1.set(Calendar.SECOND, 0);
		cld1.set(Calendar.MILLISECOND, 0);
		
		cld2.set(Calendar.HOUR_OF_DAY, 0);
		cld2.set(Calendar.MINUTE, 0);
		cld2.set(Calendar.SECOND, 0);
		cld2.set(Calendar.MILLISECOND, 0);
		
		Calendar cld_tmp = null;
		
		if(cld1.after(cld2)) {
			cld_tmp = cld1;
			cld1 = cld2;
			cld2 = cld_tmp;
		}
		
		List<Date> dates_ret = new ArrayList<Date>();
		
		while(true) {
			dates_ret.add(cld1.getTime());
			cld1.add(Calendar.DATE, 1);
			if(cld1.after(cld2)) {
				break;
			}
		}
		
		return dates_ret;
	}
	
	
	/**
	 * 将传入的日期字符串转换成对应格式的日期
	 * 
	 * @param type 日期格式的类型（1 代表“yyyy-MM-dd”格式，2 代表“yyyy-MM-dd HH:mm:ss”格式）
	 * @param date_str 日期字符串参数
	 * @return 如果转换正常，则返回一个日期对象；如果有任何异常，则返回null
	 */
	public static Date parse2date(int type, String date_str) {
		if(type != 1 && type != 2) {	// 如果日期格式类型既不是1也不是2，则表示日期格式类型错误，直接返回null
			return null;
		}
		
		if(StringUtils.isBlank(date_str)) {	// 如果将要转换的日期字符串为空白字符串，则直接返回null
			return null;
		}
		
		String date_str_trim = date_str.trim();
		
		if(type == 1) {	// 如果选择的是第一种日期格式（yyyy-MM-dd），则返回该日期格式的日期
			try {
				Date date_parse = sdf_ymd.parse(date_str_trim);
				return date_parse;
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error("按“yyyy-MM-dd”格式转换日期字符串时出现异常：" + date_str);
				return null;	// 转换出现异常时，输出错误日志信息后，直接返回null
			}
		}
		
		if(type == 2) {	// 如果选择的是第二种日期格式（yyyy-MM-dd HH:mm:ss），则返回该日期格式的日期
			try {
				Date date_parse = sdf_hms.parse(date_str_trim);
				return date_parse;
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error("按“yyyy-MM-dd HH:mm:ss”格式转换日期字符串时出现异常：" + date_str);
				return null;	// 转换出现异常时，输出错误日志信息后，直接返回null
			}
		}
		
		return null;
	}
	
	/**
	 * 将一个日期对象转换成对应格式的日期字符串
	 * 
	 * @param type 日期格式的类型（1 代表“yyyy-MM-dd”格式，2 代表“yyyy-MM-dd HH:mm:ss”格式）
	 * @param date 要转换的日期对象
	 * @return 如果转换正常，则返回一个日期字符串；如果有任何异常，则返回null
	 */
	public static String formatDate(int type, Date date) {
		if(type != 1 && type != 2) {	// 如果日期格式类型既不是1也不是2，则表示日期格式类型错误，直接返回null
			return null;
		}
		
		if(date == null) {
			return null;
		}
		
		if(type == 1) {	// 如果选择的是第一种日期格式（yyyy-MM-dd），则返回该日期格式的日期字符串
			String ymd_str = sdf_ymd.format(date);
			return ymd_str;
		}
		
		if(type == 2) {	// 如果选择的是第二种日期格式（yyyy-MM-dd HH:mm:ss），则返回该日期格式的日期字符串
			String hms_str = sdf_hms.format(date);
			return hms_str;
		}
		
		return null;	// 如果日期格式类型既不是1也不是2，则返回null
	}
	
	
	public static void main(String[] args) {
		
		
		
		long time_mills = -62135798400000l;
		Date date = new Date(time_mills);
		System.out.println("date = " + formatDate(2, date));	// 输出: date = 0001-01-01 00:00:00
		
		
		
		
		
		/*
		Date now_time = new Date();
		Date result_date = calcDatePlusGivenTimeMills(now_time, -1*PushPunchCardDatas.getTolerance_time_mills());
		System.out.println("计算结果为：" + formatDate(2, result_date));
		*/
		
		
		/*
		Date date = new Date(-10l);
//		if(date == null) {
//			System.out.println("");
//		}
		System.out.println(formatDate(2, date));
		*/
		
		
		/*
		long max_time_mills = Integer.MAX_VALUE*60l*1000l;
		String max_tm_str = timeMills2ReadableStr(max_time_mills);
		
		System.out.println("最大整数分钟转成易读的时间为：max_tm_str = " + max_tm_str);
		*/
		
		/*
		Date now_time = new Date();
		
		Date two_days_ago = DateUtil.calcXDaysAfterADate(-2, now_time);
		Date yesterday = DateUtil.calcXDaysAfterADate(-1, now_time);
		
		System.out.println("two_days_ago = " + DateUtil.formatDate(2, two_days_ago));
		System.out.println("yesterday = " + DateUtil.formatDate(2, yesterday));
		System.out.println("today = " + DateUtil.formatDate(2, now_time));
		*/
		
		
		/*
		Date date = DateUtil.parse2date(1, "2017-01-23");
		
		for(int i=1; i<6; i++) {
			for(int j=-4; j<5; j++) {
				Date date2 = DateUtil.calcXDaysAfterADate(j, date);
				String result = getDayOfWeekByDate(date2, i);
				System.out.println("date2 = " + DateUtil.formatDate(1, date2) +  ", result = " + result);
			}
			System.out.println("-----------------------");
		}
		*/
		
		
		
		/*
		Date date1 = DateUtil.parse2date(2, "2017-01-02 13:20:33");
		Date date2 = DateUtil.parse2date(2, "2017-01-03 05:56:51");
		
		List<Date> dates_exc1 = getDatesBetweenTwoDates(date1, date2);
		
		date1 = DateUtil.parse2date(2, "2017-01-11 13:20:33");
		date2 = DateUtil.parse2date(2, "2017-04-02 13:20:33");
		
		List<Date> dates_exc2 = getDatesBetweenTwoDates(date1, date2);
		
		List<Date> dates_exclude = new ArrayList<Date>();
		dates_exclude.addAll(dates_exc1);
		dates_exclude.addAll(dates_exc2);
		
		date1 = DateUtil.parse2date(2, "2017-01-03 09:20:33");
		date2 = DateUtil.parse2date(2, "2017-01-12 13:20:33");
		
//		List<Date> dates_need = getDatesBetweenTwoDates(date1, date2);
		
		List<Date> results = calcDatesExcludeGivenDatesByStartEndDate(dates_exclude, date1, date2);
		
		if(results == null) {
			System.out.println("计算给定日期以外的所需日期失败");
		} else {
			System.out.println("计算给定日期以外的所需日期成功，results.size() = " + results.size());
			for(Date date : results) {
				System.out.println(formatDate(2, date));
				
			}
		}
		*/
		
		/*
		Date date1 = DateUtil.parse2date(2, "2018-01-02 13:20:33");
		Date date2 = DateUtil.parse2date(2, "2017-01-01 05:56:51");
		
//		date1 = null;
		
		List<Date> dates_result = getDatesBetweenTwoDates(date1, date2);
		
		if(dates_result == null) {
			System.out.println("获取两个日期之间的所有日期失败");
		} else {
			System.out.println("获取两个日期之间的所有日期成功，dates_result.size() = " + dates_result.size());
			for(Date date : dates_result) {
				System.out.println(DateUtil.formatDate(2, date));
				
			}
		}
		*/
		
		
		
		
//		int x = 1;
//		System.out.println("今天前 " + x + " 天的日期是：" + DateUtil.getDateXDaysAgo(x));
		
//		int pint = Integer.parseInt("0000109");
//		System.out.println("parse int pint = " + pint);
		
		/*
		int a = 10, b = 10;
		System.out.println("两个整数a、b的差的绝对值是：" + absIntVal(b, a));
		*/
		
		/*
		Date dt1 = parse2date(2, "2018-01-31 01:00:00");
		Date dt2 = parse2date(2, "2018-01-31 00:00:01");
		
		int interval_days = getIntervalDays(dt1, dt2);
		System.out.println("（算法一）两个日期之间相差的天数是：" + interval_days + "（天）");
		System.out.println("（算法二）两个日期之间相差的天数是：" + daysOfTwo(dt1, dt2) + "（天）");
		System.out.println("（算法三）两个日期之间相差的天数是：" + daysBetween2Dates(dt1, dt2) + "（天）");
		*/
		
		
//		int a = 5;
//		int b = -a;
		
		
		/*
		Date pd = parse2date(2, "1970-01-01 08:00:00");
		long pd_time_mills = pd.getTime();
		System.out.println("pd time mills = " + pd_time_mills);
		
		long tm = -60*60*24*1000;
		Date dt = new Date(tm);
		System.out.println("dt = " + dt);
		System.out.println("dt` = " + formatDate(2, dt));
		*/
		
		
		/*
		int n = -0;
		Date in_date = parse2date(2, "2015-10-08 19:35:22");
		String out_date_str = getNDaysAfterADate(n, in_date);
		System.out.println("out_date_string = " + out_date_str);
		*/
		
		
		/*
		Date date1 = parse2date(2, "2015-07-20 04:25:06");
		Date date2 = parse2date(2, "2014-07-20 17:31:48");
		String[] dt_arr = getDateArrBy2Dates(date1, date2);
		if(dt_arr != null) {
			int len = dt_arr.length;
			if(len > 0) {
				System.out.println("计算得到的日期字符串数组中，共有 " + len + " 个日期字符串");
				for(String str:dt_arr) {
					System.out.println("  -->  " + str);
				}
			} else {
				System.out.println("计算得到的两个日期之间的日期字符串数组长度为0");
			}
		} else {
			System.out.println("计算两个日期之间的日期字符串数组时出现异常");
		}
		*/
		
		
		/*
		int x = 31;
		Date date = new Date();
		date = parse2date(2, "2018-10-30 19:22:57");
		Date another_date = calcXDaysAfterADate(x, date);
		System.out.println("计算得到的日期：" + formatDate(2, another_date));
		*/
		
		
		
	}

	//将日期从小到大排序
	public static  Comparator<Date> dateComparator = new Comparator<Date>() {
		Calendar calendar = Calendar.getInstance();

		@Override
		public int compare(Date o1, Date o2) {
			calendar.setTime(o1);
			long millis1 = calendar.getTimeInMillis();

			calendar.setTime(o2);
			long millis2 = calendar.getTimeInMillis();
			return (int) (millis1 - millis2);
		}
	};
	
	/**
	 * 得到2个日期之间的所有的工作日
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	 public static List<Date> getAllWorkDates(Date startDate, Date endDate,List<Date> restDates) {
		 List<Date> result = new ArrayList<Date>();
		 Calendar startCalendar = Calendar.getInstance();
		 startCalendar.setTime(startDate);
		 
		 Calendar endCalendar = Calendar.getInstance();
		 endCalendar.setTime(endDate);
		 
	        while (startCalendar.before(endCalendar)) {
	        	if(isWorkDay(startCalendar.getTime(), restDates)){
	        		result.add(startCalendar.getTime());
	        	}
	            startCalendar.add(Calendar.DAY_OF_YEAR, 1);
	        }
	        return result;
	    }
	 
	 /**
	  * 判断当前日期是不是工作日
	  * @param needCheck 当前日期
	  * @param restDates 休息日期集合
	  * @return true-是  false-不是
	  */
	 private static boolean isWorkDay(Date needCheck,List<Date> restDates){
		 if(restDates == null || restDates.isEmpty()){
			 return true;
		 }
		 for (Date date : restDates) {
			String needCheckStr = DateUtil.formatDate(1, needCheck);
			String currentStr = DateUtil.formatDate(1, date);
			if(needCheckStr != null && needCheckStr.equals(currentStr)){
				return false;
			}
		}
		 return true;
	 }
	
	 /**
	 * 将时间毫秒数转化成易于人可读的字符串形式
	 * 
	 * @param timeMills
	 * @return
	 */
	public static String timeMills2ReadableStr(long timeMills) {
		if(timeMills < 0) {
			return null;
		}
		
		if(timeMills < 1000) {
			return timeMills + " (毫秒).";
		}
		
		long timeSeconds = timeMills/1000;
		long timeMills_remain = timeMills%1000;
		
		if(timeSeconds < 60) {
			return timeSeconds + " (秒), " + timeMills_remain + " (毫秒).";
		}
		
		long timeMinutes = timeSeconds/60;
		long timeSeconds_remain = timeSeconds%60;
		
		if(timeMinutes < 60) {
			return timeMinutes + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		}
		
		long timeHours = timeMinutes/60;
		long timeMinutes_remain = timeMinutes%60;
		
		if(timeHours < 24) {
			return timeHours + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		}
		
		long timeDays = timeHours/24;
		long timeHours_remain = timeHours%24;
		
		if(timeDays < 7) {
			return timeDays + " (天), " + timeHours_remain + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		} else if(timeDays < 30) {
			long timeWeeks = timeDays/7;
			long timeDays_remain = timeDays%7;
			return timeWeeks + " (周), " + timeDays_remain + " (天), " + timeHours_remain + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		} else if(timeDays < 365) {
			long timeMonths = timeDays/30;
			long timeDays_30 = timeDays%30;
			long timeWeeks = timeDays_30/7;
			long timeDays_remain = timeDays_30%7;
			return timeMonths + " (月), " + timeWeeks + " (周), " + timeDays_remain + " (天), " + timeHours_remain + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		} else {
			long timeYears = timeDays/365;
			long timeDays_365 = timeDays%365;
			long timeMonths = timeDays_365/30;
			long timeDays_365_30 = timeDays_365%30;
			long timeWeeks = timeDays_365_30/7;
			long timeDays_remain = timeDays_365_30%7;
			
			return timeYears + " (年), " + timeMonths + " (月), " + timeWeeks + " (周), " + timeDays_remain + " (天), " + timeHours_remain + " (小时), " + timeMinutes_remain + " (分), " + timeSeconds_remain + " (秒), " + timeMills_remain + " (毫秒).";
		}
	}
	
	
	/**
	 * 根据传入日期返回这个日期是星期几
	 * 
	 * @param date	给定的日期
	 * @param type	给定的类型（<br/>
	 * 				1 表示数字类型，即用1~6和0来表示周一到周六和周日；<br/>
	 * 				2 表示中文数字类型，即用一到六和日来表示周一到周六和周日；<br/>
	 * 				3 表示英文星期类型，即用Monday到Sunday来表示周一到周日；<br/>
	 * 				4 表示英文短星期类型，即用Mon到Sun来表示周一到周日；<br/>
	 * 			）
	 * @return
	 */
	public static String getDayOfWeekByDate(Date date, int type) {
		if(date == null || type < 1 || type > 4) {
			return null;
		}
		
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		
		int ret = cld.get(Calendar.DAY_OF_WEEK);
		
//		System.out.println("ret = " + ret);
		
		switch(type) {
			case 1: return WEEK_DIGITS[ret -1];
			case 2: return CHINESE_WEEK_NUMBERS[ret -1];
			case 3: return ENGLISH_WEEK_DAYS[ret -1];
			case 4: return SHORT_ENGLISH_WEEK_DAYS[ret -1];
		}
		
		return null;
	}
	
	
	
}
