package com.cn.eplat.utils.elead2huawei;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.poi.sl.draw.DrawNotImplemented;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.cn.eplat.model.PushLog;
import com.cn.eplat.model.PushToHw;
import com.cn.eplat.utils.DateUtil;
import com.mysql.fabric.xmlrpc.base.Array;

/**
 * 将数据库里的考勤数据导入到华为数据库
 * 
 * @author zhangshun
 *
 */
public class ExportData2HWHelper {
	
	private static ExportData2HWHelper helper = new ExportData2HWHelper();
	
	public static ExportData2HWHelper getInstance(){
		return helper;
	}
	
	private ExportData2HWHelper() {
		
	}
	@SuppressWarnings("deprecation")
	private class SSLClient extends DefaultHttpClient {
		public SSLClient() throws Exception {
			super();
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = this.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
		}
	}

	@SuppressWarnings("resource")
	private String send(String token,final String body) {
		String result = "";
		try {
			HttpClient client = new SSLClient();
			HttpPost post = new HttpPost(Constant.IMPORT_ATTENDS_URL);
			ContentProducer cp = new ContentProducer() {
				public void writeTo(OutputStream outstream) throws IOException {
					Writer writer = new OutputStreamWriter(outstream, "UTF-8");
					writer.write(body);
					writer.flush();
				}
			};
			post.setEntity(new EntityTemplate(cp));
			post.setHeader("Authorization","Bearer "+token);
			post.setHeader("SOAPAction", "");

			HttpResponse response = client.execute(post);
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 组装导入考勤数据的请求体
	 * 
	 * @return
	 */
	private static String combinBody(ArrayList<Map<String, String>> lists) {
		StringBuilder soap = new StringBuilder();
		// 定义soap 请求xml格式字符：
		soap.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://schemas.xmlsoap.org/soap/enveloper\">");
		soap.append("<soapenv:Header/>");
		soap.append("<soapenv:Body>");
		soap.append("<com:importSupplierCardtime>");
		soap.append("<cardTimeInputList>");
		
		for (Map<String, String> parameters : lists) {
			soap.append("<TimeSheetCardTimeParameters>");

			// 供应商编码(必填)
			soap.append("<companyCode>").append(Constant.COMPANY_CODE).append("</companyCode>");

			// 身份证号(必填)
			if (parameters.containsKey("staffIdNo")) {
				soap.append("<staffIdNo>").append(parameters.get("staffIdNo")).append("</staffIdNo>");
			}

			// 姓名(必填)
			if (parameters.containsKey("staffName")) {
				soap.append("<staffName>").append(parameters.get("staffName")).append("</staffName>");
			} 

			// 打卡时间(必填)(DATE yyyy-MM-dd)
			if (parameters.containsKey("swipeDate")) {
				soap.append("<swipeDate>").append(parameters.get("swipeDate")).append("</swipeDate>");
			} 

			// 打卡时间(必填) DATE yyyy-MM-dd HH:MI:SS
			soap.append("<swipeTime>").append(parameters.get("swipeTime")).append("</swipeTime>");

			// 周(必填) 0[周日],1[周一],2[周二],3[周三],4[周四],5[周五],6[周六]
			if (parameters.containsKey("week")) {
				soap.append("<week>").append(parameters.get("week")).append("</week>");
			} 

			// 供应商工号(非必填)
			if (parameters.containsKey("staffNo")) {
				soap.append("<staffNo>").append(parameters.get("staffNo")).append("</staffNo>");
			}

			// 供应商卡证号(非必填)
			if (parameters.containsKey("staffCardNo")) {
				soap.append("<staffCardNo>").append(parameters.get("staffCardNo")).append("</staffCardNo>");
			}

			// 华为工号(非必填)
			if (parameters.containsKey("staffHWNo")) {
				soap.append("<staffHWNo>").append(parameters.get("staffHWNo")).append("</staffHWNo>");
			}

			// 华为卡证号(非必填)
			if (parameters.containsKey("statffHWCardNo")) {
				soap.append("<statffHWCardNo>").append(parameters.get("statffHWCardNo")).append("</statffHWCardNo>");
			}

			soap.append("</TimeSheetCardTimeParameters>");
		}
	
		soap.append("</cardTimeInputList>");
		soap.append("</com:importSupplierCardtime>");
		soap.append("</soapenv:Body>");
		soap.append("</soapenv:Envelope>");
		return soap.toString();
	}

	/**
	 * 将数据插入到华为方数据库
	 * @param maps 参数map
	 * @param token 访问令牌
	 * @return
	 */
	@Deprecated
	private Map<String, String>  insert(ArrayList<Map<String, String>> lists,String token){
		String body = combinBody(lists);
		String result = send(token, body);
		System.out.println(result);
		Map<String, String> resultMaps = new HashMap<String, String>(3);
		if(result.contains("resultFlag")){
			String resultFlag = GetTokenHelper.getInnerTextByTag(result, "resultFlag");
			String resultMessage = GetTokenHelper.getInnerTextByTag(result, "resultMessage");
			resultMessage = StringEscapeUtils.unescapeXml(resultMessage);//unicode内码转换成中文
			System.out.println(resultFlag);
			System.out.println(resultMessage);
			resultMaps.put("resultFlag", resultFlag);
			resultMaps.put("resultMessage", resultMessage);
		}else if(result.contains("faultcode")){
			String faultcode = GetTokenHelper.getInnerTextByTag(result, "faultcode");
			String faultstring = GetTokenHelper.getInnerTextByTag(result, "faultstring");
			String detail = GetTokenHelper.getInnerTextByTag(result, "detail");
			resultMaps.put("faultcode", faultcode);
			resultMaps.put("faultstring", faultstring);
			resultMaps.put("detail", detail);
		}else if(result.contains("ams:fault")){
			String amsCode = GetTokenHelper.getInnerTextByTag(result, "ams:code");
			String amsMessage = GetTokenHelper.getInnerTextByTag(result, "ams:message");
			String amsDescription = GetTokenHelper.getInnerTextByTag(result, "ams:description");
			resultMaps.put("faultcode", amsCode);
			resultMaps.put("faultstring", amsMessage);
			resultMaps.put("detail", amsDescription);
		}else{
			resultMaps.put("faultMessage", result);
		}
		String jsonString = JSON.toJSONString(resultMaps);//转换成json
		System.out.println(jsonString);
		return resultMaps;
	}
	
	/**
	 * 将数据插入到华为方数据库
	 * @param maps 参数map
	 * @param token 访问令牌
	 * @return true-成功   false-失败
	 */
	public boolean insert2HW(ArrayList<Map<String, String>> lists,String token){
		String body = combinBody(lists);
		String result = send(token, body);
		System.out.println(result);
		boolean isSuccess = false;
		if(result.contains("resultFlag")){
			String resultFlag = GetTokenHelper.getInnerTextByTag(result, "resultFlag");
			String resultMessage = GetTokenHelper.getInnerTextByTag(result, "resultMessage");
			resultMessage = StringEscapeUtils.unescapeXml(resultMessage);//unicode内码转换成中文
			System.out.println(resultFlag);
			System.out.println(resultMessage);
			if("S00".equalsIgnoreCase(resultFlag)){
				isSuccess = true;
			}
		}
		return isSuccess;
	}
}
