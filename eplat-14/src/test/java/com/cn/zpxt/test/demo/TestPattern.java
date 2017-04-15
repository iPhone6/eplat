package com.cn.zpxt.test.demo;

public class TestPattern {
	
	// 判断一个字符串是不是非负整数
    private static boolean isNumber(String str) {
    	return str.matches("^[1-9]\\d*|0$");	// 匹配非负整数的正则表达式
    }

	public static void main(String[] args) {
		String str = "1.099";
		System.out.println(isNumber(str));
	}

}
