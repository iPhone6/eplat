package com.cn.eplat.consts;

public class Constants {
	public static final String ANDROID_APKS_DIR = "/ROOT/app";//andorid apk上传到服务器上的目录路径(当前项目下)
	public static final String ANDROID_APKS_URL_ALUYUN = "https://hr.e-lead.cn:8443/app/";//andorid apk的下载路径(aliyun正是环境)
	public static final String ANDROID_APKS_URL_TEST = "http://192.168.1.225:8080/app/";//andorid apk的下载路径(测试环境)
	public static final String ANDROID_APKS_URL_LOCAL = "http://192.168.1.180:8080/app/";//andorid apk的下载路径(本地环境)
//	public static final String ANDROID_APKS_URL = ANDROID_APKS_URL_LOCAL;
//	public static final String ANDROID_APKS_URL = ANDROID_APKS_URL_TEST;
	public static final String ANDROID_APKS_URL = ANDROID_APKS_URL_ALUYUN;
}
