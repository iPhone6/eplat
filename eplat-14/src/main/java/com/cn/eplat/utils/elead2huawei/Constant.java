package com.cn.eplat.utils.elead2huawei;

/**
 * 配置跟华为方对接的一些请求URL等常量
 * @author zhangshun
 *
 */
public class Constant {
	//把数据导入到华为方的url
	public static final String IMPORT_ATTENDS_URL= "https://openapi.huawei.com:443/service/saveCardRecords2eResource/1.0";
	//请求token的url
	public static final String TOKEN_URL = "https://openapi.huawei.com/oauth2soap/services/token?wsdl";
	//对应后台创建应用的key
	public static final String APP_KEY = "dQPzdl7XsAgzLuvAqOaqbsSanCoa";
	//对应后台创建应用的secret
	public static final String APP_SECRET = "vfaSOfPACxqkWGChjVIYxcY7bNka";
	//用于将考勤数据推送到华为方的供应商编码
	public static final String COMPANY_CODE = "C29970";
	//每次推送条数
	public static final int COUNTS_PER_REQUEST = 20; 
}
