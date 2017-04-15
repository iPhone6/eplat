package com.cn.eplat.utils.elead2huawei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.util.TextUtils;

/**
 * 获取token
 * @author zhangshun
 */
public class GetTokenHelper {
	
	private static String sendSoapRequest(String requestBody) {
		URL postUrl = null;
		String dataLine;
		String result = "";
		BufferedReader reader = null;
		OutputStream output = null;
		InputStreamReader isr = null;
		HttpsURLConnection connection = null;
		try {
			postUrl = new URL(Constant.TOKEN_URL);
			connection = (HttpsURLConnection) postUrl.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			 connection.setConnectTimeout(20000);
			connection.addRequestProperty("SOAPAction", "");
			output = connection.getOutputStream();
			output.write(requestBody.getBytes("UTF-8"));
			isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
			reader = new BufferedReader(isr);
			while ((dataLine = reader.readLine()) != null) {
				result = dataLine + result;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != output) {
				try {
					output.close();
				} catch (IOException e) {
					output = null;
				}
			}
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					isr = null;
				}
			}
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					isr = null;
				}
			}
			if (null != connection) {
				connection.disconnect();
			}
		}
		return result;
	}

	/**
	 * 请求token的请求体
	 * 
	 * @param key
	 * @param secret
	 * @return
	 */
	private static String getSOAPTokenRequestBody() {
		StringBuilder soap = new StringBuilder();
		soap.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oaut=\"http://oauth2soap.openapi.huawei.com/\">");
		soap.append("<soapenv:Header></soapenv:Header>");
		soap.append("<soapenv:Body>");
		soap.append("<oaut:getToken>");
		soap.append("<consumer_key>").append(Constant.APP_KEY).append("</consumer_key>");
		soap.append("<consumer_secret>").append(Constant.APP_SECRET).append("</consumer_secret>");
		soap.append("</oaut:getToken>");
		soap.append("</soapenv:Body>");
		soap.append("</soapenv:Envelope>");
		return soap.toString();
	}

	public static String getToken() {
		String token = sendSoapRequest(getSOAPTokenRequestBody());
		return getInnerTextByTag(token, "access_token");
	}
	
	/**
	 * 从源字符串里截取对应的内容
	 * @param source 源字符串
	 * @param tag 标签
	 * @return 标签里的内容
	 */
	public static String getInnerTextByTag(String source,String tag){
		if(TextUtils.isEmpty(source)){
			return "";
		}
		
		if(TextUtils.isEmpty(tag)||!source.contains(tag)){
			return source;
		}
		
		String result = "";
		try {
			String startStr = "<"+tag+">";
			String endStr = "</"+tag+">";
			int startIndex = source.indexOf(startStr);
			int endIndex = source.indexOf(endStr);
			result = source.substring(startIndex+startStr.length(), endIndex);
		} catch (Exception e) {
			e.printStackTrace();
			result = source;
		}
		return result;
	}

}
