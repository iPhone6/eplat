package com.cn.eplat.utils;

import java.util.Random;

/**
 * 根据算法生成一个数字与小写字母混合的6位初始密码
 * @author zhangshun
 *
 */
public class CreatePasswordHelper {

	//生成原始密码
	public static String createPassword(int length){
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

			if ("char".equalsIgnoreCase(charOrNum)) // 字符串
			{
//				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				int choice = 97;
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) // 数字
			{
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}
}
