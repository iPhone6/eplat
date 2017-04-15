package com.easemob.server.comm.body;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.server.comm.wrapper.BodyWrapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class PunchDatasBody implements BodyWrapper {
	
	private String punch_datas;
	
	public String getPunch_datas() {
		return punch_datas;
	}
	public void setPunch_datas(String punch_datas) {
		this.punch_datas = punch_datas;
	}
	
	public PunchDatasBody(String data) {
		this.punch_datas = data;
	}
	
	@Override
	public ContainerNode<?> getBody() {
		return JsonNodeFactory.instance.objectNode().put("punch_datas", punch_datas);
	}

	@Override
	public Boolean validate() {
		
//		JSONObject.toJSON(punch_datas);
		
		if(StringUtils.isBlank(punch_datas)) {
			return false;
		}
		
		try {
			JSONArray.parseArray(punch_datas);
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("PunchDatasBody校验punch_datas时出现异常，punch_datas = " + punch_datas);
			System.out.println("异常信息：" + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		String datas = "[{'111':123}, {'aa':true}, 0, false, null, {}]";
		
		JSONArray parse_arr = null;
		try {
			parse_arr = JSONArray.parseArray(datas);
		} catch (Exception e) {
			System.out.println("转换JSON数组时出现异常");
			System.out.println("异常类型：" + e.getClass());
			e.printStackTrace();
			return;
		}
		
		System.out.println("转换JSON数组正常");
		System.out.println(parse_arr);
	}
	
}
