package com.cn.eplat.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

/**
 * 
 * @author zhangyangshun
 * 
 * 卡片实体类，构造树形结构
 *
 */
public class EpCard {
	// 卡片id
	private Integer id;
	// 卡片标题
	private String title;
	// 卡片类型/种类（主要包括：）
	private String type;
	// 指向父卡片的引用（相当于当前节点的父节点）
	private EpCard parent;
	// 指向子卡片的引用（卡片组，相当于当前节点的子节点）
	private List<EpCard> childs;
	// 卡片图标（有些卡片需要有相对应的图标用于显示）
	private String icon_url;	// 这里只储存图标的url
	// 数据节点
	private String data_url;	// 通过请求接口URL来获取最终需要展示的数据
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public EpCard getParent() {
		return parent;
	}
	public void setParent(EpCard parent) {
		this.parent = parent;
	}
	public List<EpCard> getChilds() {
		return childs;
	}
	public void setChilds(List<EpCard> childs) {
		this.childs = childs;
	}
	public String getIcon_url() {
		return icon_url;
	}
	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}
	public String getData_url() {
		return data_url;
	}
	public void setData_url(String data_url) {
		this.data_url = data_url;
	}


	public JSONObject getData() {
		JSONObject json = new JSONObject();
		
		// 返回数据中加入卡片标题（卡片标题不用检查是否为空，也即卡片标题可以为空）
		json.put("card_title", title);
		// 如果卡片图标URL不为空，则说明当前卡片有对应的图标，在返回结果中加入该字段
		if(!StringUtils.isNullOrEmpty(icon_url)) {
			json.put("card_icon", icon_url);
		}
		
		// 如果子节点为空，说明当前节点为叶子节点
		if(childs == null || (childs != null && childs.size() == 0)) {
			if(!StringUtils.isNullOrEmpty(data_url)) {
				json.put("data", "根据 data_url 获取最终数据" + data_url);
			}
		} else { // 如果子节点不为空，说明当前节点为根节点或中间节点，循环遍历其子节点以获取最终数据
			JSONArray ja = new JSONArray();
			for(EpCard c:childs) {
				ja.add(c.getData());
			}
			// 将子节点遍历后的数据放入卡片组字段 card_group 中
			json.put("card_group", ja);
		}
		
		return json;
	}
	
	@Override
	public String toString() {
		return "EpCard [id=" + id + ", title=" + title + ", type=" + type
				+ ", parent=" + parent + ", childs=" + childs + ", icon_url="
				+ icon_url + ", data_url=" + data_url + "]";
	}
	
	public static void main(String[] args) {
		EpCard ec1 = new EpCard();
		EpCard ec2 = new EpCard();
		EpCard ec3 = new EpCard();
//		EpCard ec4 = new EpCard();
		List<EpCard> list1 = new ArrayList<EpCard>();
		list1.add(ec2);
		list1.add(ec3);
		ec1.setChilds(list1);
		
		ec2.setParent(ec1);
		ec3.setParent(ec1);
		
		ec2.setData_url("data2");
		ec3.setData_url("data3");
		
		JSONObject data1 = ec1.getData();
		System.out.println(data1);
		
		
	}
	
}
