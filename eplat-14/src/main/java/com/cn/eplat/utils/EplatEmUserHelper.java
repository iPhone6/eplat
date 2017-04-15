package com.cn.eplat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EplatEmUserHelper {
	
	private static final String EPLAT_EM_USER_PREFIX = "EplatEmUser";
	
	public static String getCurrentTimeStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date nowTime = new Date();
		String nowTimeStr = sdf.format(nowTime);
		
		return nowTimeStr;
	}
	
	public static Integer getRandom4Digits() {
		double rand = Math.random();
		Integer result = new Integer((int) (rand*9000+1000));
		
		return result;
	}
	
	public static Integer getRandom6Digits() {
		double rand = Math.random();
		Integer result = new Integer((int) (rand*900000+100000));
		
		return result;
	}
	
	/**
	 * 新创建环信用户的用户名命名规则：EplatEmUseryyyyMMddHHmmssSSSrrrr（前面EplatEmUser为固定字符，后面的以当前服务器系统时间为准，yyyy表示4位年份，MM表示2位月份，dd表示2位日，HH表示24小时的小时数，mm表示分钟数，ss表示秒数，SSS表示毫秒数，rrrr表示4位随机数）
	 * @return String
	 */
	public static String genEpEmUsername() {
		return EPLAT_EM_USER_PREFIX + getCurrentTimeStr() + getRandom4Digits();
	}
	
	/**
	 * 新创建环信用户的初始密码设定规则：epem_rrrrrr（前面epem_为固定字符，后面rrrrrr表示一个6位随机数）
	 * @return String
	 */
	public static String genEpEmUserpwd() {
		return "epem_" + getRandom6Digits();
	}
	
	public static void main(String[] args) {
		System.out.println(getCurrentTimeStr());
		System.out.println("4 random digits = " + getRandom4Digits());
		System.out.println("6 random digits = " + getRandom6Digits());
		System.out.println("Generate a EplatEmUser name = " + genEpEmUsername());
		System.out.println("Generate a EplatEmUser password = " + genEpEmUserpwd());
	}
	
}
