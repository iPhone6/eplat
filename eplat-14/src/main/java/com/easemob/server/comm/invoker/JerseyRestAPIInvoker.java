package com.easemob.server.comm.invoker;

import com.cn.eplat.timedtask.PushPunchCardDatas;
import com.easemob.server.api.RestAPIInvoker;
import com.easemob.server.comm.MessageTemplate;
import com.easemob.server.comm.constant.HTTPMethod;
import com.easemob.server.comm.utils.RestAPIUtils;
import com.easemob.server.comm.wrapper.BodyWrapper;
import com.easemob.server.comm.wrapper.HeaderWrapper;
import com.easemob.server.comm.wrapper.QueryWrapper;
import com.easemob.server.comm.wrapper.ResponseWrapper;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.io.File;
import java.io.IOException;

public class JerseyRestAPIInvoker implements RestAPIInvoker {

    private static final Logger log = LoggerFactory.getLogger(JerseyRestAPIInvoker.class);
    
    public ResponseWrapper sendRequest(String method, String url, HeaderWrapper header, BodyWrapper body, QueryWrapper query) {

        ResponseWrapper responseWrapper = new ResponseWrapper();
        ObjectNode responseNode = JsonNodeFactory.instance.objectNode();

        responseWrapper.setResponseBody(responseNode);

        if (!HTTPMethod.METHOD_GET.equalsIgnoreCase(method) && !HTTPMethod.METHOD_POST.equalsIgnoreCase(method) && !HTTPMethod.METHOD_PUT.equalsIgnoreCase(method) && !HTTPMethod.METHOD_DELETE.equalsIgnoreCase(method)) {
            String msg = MessageTemplate.print(MessageTemplate.UNKNOW_TYPE_MSG, new String[]{method, "HTTP methods"});
            responseWrapper.addError(msg);
        }
        if (StringUtils.isBlank(url)) {
            String msg = MessageTemplate.print(MessageTemplate.BLANK_OBJ_MSG, new String[]{"Parameter url"});
            responseWrapper.addError(msg);
        }
        if (!RestAPIUtils.match("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?", url)) {
            String msg = MessageTemplate.print(MessageTemplate.INVAILID_FORMAT_MSG, new String[]{"Parameter url"});
            responseWrapper.addError(msg);
        }
        if (null == header) {
            String msg = MessageTemplate.print(MessageTemplate.BLANK_OBJ_MSG, new String[]{"Parameter header"});
            responseWrapper.addError(msg);
        }
//        if (null == body || !body.validate()) {
//            responseWrapper.addError(MessageTemplate.INVALID_BODY_MSG);
//        }

        if (responseWrapper.hasError()) {
            return responseWrapper;
        }

        log.debug("=============Request=============");
        log.debug("Method: " + method);
        log.debug("URL: " + url);
        log.debug("Header: " + header);
        log.debug("Body: " + ((null == body) ? "" : body.getBody()));
        log.debug("Query: " + query);
        log.debug("===========Request End===========");

        JerseyClient client = RestAPIUtils.getJerseyClient(StringUtils.startsWithIgnoreCase(url, "HTTPS"));
        JerseyWebTarget target = client.target(url);
        
        // // default timeout value for all requests
        client.property(ClientProperties.CONNECT_TIMEOUT, 30000);	// 设置连接超时时间为30秒（30000毫秒）
        client.property(ClientProperties.READ_TIMEOUT, 30000);

        buildQuery(target, query);

        Invocation.Builder inBuilder = target.request();
        // // overriden timeout value for this request
        inBuilder.property(ClientProperties.CONNECT_TIMEOUT, 20000);
        inBuilder.property(ClientProperties.READ_TIMEOUT, 20000);
        
        buildHeader(inBuilder, header);

        Response response = null;
        Object b = null == body ? null : body.getBody();
        try {
			if (HTTPMethod.METHOD_GET.equals(method)) {
			    response = inBuilder.get(Response.class);
			} else if (HTTPMethod.METHOD_POST.equals(method)) {
			    response = inBuilder.post(Entity.entity(b, MediaType.APPLICATION_JSON), Response.class);
			} else if (HTTPMethod.METHOD_PUT.equals(method)) {
			    response = inBuilder.put(Entity.entity(b, MediaType.APPLICATION_JSON), Response.class);
			} else if (HTTPMethod.METHOD_DELETE.equals(method)) {
			    response = inBuilder.delete(Response.class);
			}
		} catch (Exception e1) {
			log.error("请求出现异常，可能是请求超时。Error_Info = " + e1.getMessage());
//			return null;
		}
        if(response != null) {
        	responseWrapper.setResponseStatus(response.getStatus());
        	String responseContent = response.readEntity(String.class);
        	ObjectMapper mapper = new ObjectMapper();
        	JsonFactory factory = mapper.getFactory();
        	JsonParser jp;
        	try {
        		jp = factory.createParser(responseContent);
        		responseNode = mapper.readTree(jp);
        		responseWrapper.setResponseBody(responseNode);
        	} catch (IOException e) {
        		log.error(MessageTemplate.STR2JSON_ERROR_MSG, e);
        		responseWrapper.addError(MessageTemplate.STR2JSON_ERROR_MSG);
        	}
        } else {
//        	responseWrapper.setResponseStatus(408);	// 请求超时状态码（408）
        	responseWrapper.setResponseStatus(Status.REQUEST_TIMEOUT.getStatusCode());
        	responseWrapper.addError("Request Error: possibly request timeout exception occured!");
        }

        log.debug("=============Response=============");
        log.debug(responseWrapper.toString());
        log.debug("===========Response End===========");
        return responseWrapper;
    }

    public ResponseWrapper uploadFile(String url, HeaderWrapper header, File file) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        ObjectNode responseNode = JsonNodeFactory.instance.objectNode();

        responseWrapper.setResponseBody(responseNode);

        if (StringUtils.isBlank(url)) {
            String msg = MessageTemplate.print(MessageTemplate.BLANK_OBJ_MSG, new String[]{"Parameter url"});
            responseWrapper.addError(msg);
        }
        if (!RestAPIUtils.match("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?", url)) {
            String msg = MessageTemplate.print(MessageTemplate.INVAILID_FORMAT_MSG, new String[]{"Parameter url"});
            responseWrapper.addError(msg);
        }
        if (null == header) {
            String msg = MessageTemplate.print(MessageTemplate.BLANK_OBJ_MSG, new String[]{"Parameter header"});
            responseWrapper.addError(msg);
        }
        if (null == file || !file.exists() || !file.isFile() || !file.canRead()) {
            responseWrapper.addError(MessageTemplate.INVALID_BODY_MSG);
        }

        if (responseWrapper.hasError()) {
            return responseWrapper;
        }

        log.debug("=============Request=============");
        log.debug("URL: " + url);
        log.debug("Header: " + header);
        log.debug("File: " + ((null == file) ? "" : file.getName()));
        log.debug("===========Request End===========");

        JerseyClient client = RestAPIUtils.getJerseyClient(StringUtils.startsWithIgnoreCase(url, "HTTPS"));
        JerseyWebTarget target = client.target(url);

        Invocation.Builder inBuilder = target.request();

        buildHeader(inBuilder, header);

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FileDataBodyPart("file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        Response response = inBuilder.post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA), Response.class);

        responseWrapper.setResponseStatus(response.getStatus());

        String responseContent = response.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser jp;
        try {
            jp = factory.createParser(responseContent);
            responseNode = mapper.readTree(jp);
            responseWrapper.setResponseBody(responseNode);
        } catch (IOException e) {
            log.error(MessageTemplate.STR2JSON_ERROR_MSG, e);
            responseWrapper.addError(MessageTemplate.STR2JSON_ERROR_MSG);
        }

        log.debug("=============Response=============");
        log.debug(responseWrapper.toString());
        log.debug("===========Response End===========");
        return responseWrapper;
    }

    public ResponseWrapper downloadFile(String url, HeaderWrapper header, QueryWrapper query) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        ObjectNode responseNode = JsonNodeFactory.instance.objectNode();

        responseWrapper.setResponseBody(responseNode);

        if (StringUtils.isBlank(url)) {
            String msg = MessageTemplate.print(MessageTemplate.BLANK_OBJ_MSG, new String[]{"Parameter url"});
            responseWrapper.addError(msg);
        }
        if (!RestAPIUtils.match("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?", url)) {
            String msg = MessageTemplate.print(MessageTemplate.INVAILID_FORMAT_MSG, new String[]{"Parameter url"});
            responseWrapper.addError(msg);
        }
        if (null == header) {
            String msg = MessageTemplate.print(MessageTemplate.BLANK_OBJ_MSG, new String[]{"Parameter header"});
            responseWrapper.addError(msg);
        }

        if (responseWrapper.hasError()) {
            return responseWrapper;
        }

        log.debug("=============Request=============");
        log.debug("URL: " + url);
        log.debug("Header: " + header);
        log.debug("===========Request End===========");

        JerseyClient client = RestAPIUtils.getJerseyClient(StringUtils.startsWithIgnoreCase(url, "HTTPS"));
        JerseyWebTarget target = client.target(url);

        buildQuery(target, query);

        Invocation.Builder inBuilder = target.request();

        buildHeader(inBuilder, header);

        File file = inBuilder.get(File.class);
        responseWrapper.setResponseBody(file);

        log.debug("=============Response=============");
        log.debug("File is loaded, ready for processing.");
        log.debug("===========Response End===========");
        return responseWrapper;
    }

    private void buildHeader(Invocation.Builder inBuilder, HeaderWrapper header) {
        if (null != header && !header.getHeaders().isEmpty()) {
            for (NameValuePair nameValuePair : header.getHeaders()) {
                inBuilder.header(nameValuePair.getName(), nameValuePair.getValue());
            }
        }
    }

    private void buildQuery(JerseyWebTarget target, QueryWrapper query) {
        if (null != query && !query.getQueries().isEmpty()) {
            for (NameValuePair nameValuePair : query.getQueries()) {
                target.queryParam(nameValuePair.getName(), nameValuePair.getValue());
            }
        }
    }
    
    public static void main(String[] args) {
    	/*
    	JerseyRestAPIInvoker rest_invoker = new JerseyRestAPIInvoker();
    	
    	HeaderWrapper header = new HeaderWrapper();
    	header.addHeader("Content-Type", "text/plain");
    	
    	ResponseWrapper response = rest_invoker.sendRequest(HTTPMethod.METHOD_GET, "https://hr.e-lead.cn:8443/eplat-pro/epUserController.do?loginCheck", header, null, null);
    	
    	System.out.println("------------------------------");
    	System.out.println("response status = " + response.getResponseStatus());
    	System.out.println("response = " + response);
    	*/
    	
    	/*
    	String url = PushPunchCardDatas.getPush_mach_data_url();
    	
    	System.out.println("url = " + url);
    	
    	url = "https://192.168.1.8:8443/eplat-08/eppp.do?helloWorld";
    	url = "https://localhost.:8443/eplat-08/epAttenController.do?pushMachPunchDataCHECKINOUT";
    	
    	boolean match_result = RestAPIUtils.match("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?", url);
    	
    	System.out.println("匹配URL字符串结果：" + match_result);
    	*/
    	
    	
    	JerseyRestAPIInvoker rest_invoker = new JerseyRestAPIInvoker();
    	
    	String url = PushPunchCardDatas.getPush_mach_data_url();
    	HeaderWrapper header = new HeaderWrapper();
    	header.addHeader("Content-Type", "text/plain");
    	
    	ResponseWrapper response = rest_invoker.sendRequest(HTTPMethod.METHOD_GET, url, header, null, null);
    	
    	System.out.println("------------------------------");
    	System.out.println("response status = " + response.getResponseStatus());
    	System.out.println("response = " + response);
    	
	}
    
}
