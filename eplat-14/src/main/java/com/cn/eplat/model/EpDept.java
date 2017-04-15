package com.cn.eplat.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author Administrator
 * 
 * 部门实体类
 * 
 */
public class EpDept {
	private Integer id;	// 主键ID
	private String name;	// 部门名称（钉钉管理后台中，除了不能包含短横(“-”)这个字符外，其它字符都可以，包括各种特殊字符。要求同级部门名称不能重名，不区分大小写）
	private Integer level;	// 部门层级，用来表示当前部门在公司所有部门树形结构中处于第几个层级。编号从1开始，表示最高一层的根部门，下层部门的层级数按自然数序列依次增加。
	private Integer superior_id;	// 上级部门ID
	private Integer order;	// 同一上级部门下的子部门顺序编号（参照钉钉管理后台，可能需要对子部门进行排序展示）
	private List<EpDept> childs = new ArrayList<EpDept>();	// 直接子部门列表
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getSuperior_id() {
		return superior_id;
	}
	public void setSuperior_id(Integer superior_id) {
		this.superior_id = superior_id;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public List<EpDept> getChilds() {
		return childs;
	}
	public void setChilds(List<EpDept> childs) {
		this.childs = childs;
	}
	
	@Override
	public String toString() {
		String json_str = JSONObject.toJSONString(this);
		return json_str;
//		return "EpDept [id=" + id + ", name=" + name + ", level=" + level + ", superior_id=" + superior_id + ", order=" + order + ", childs=" + childs + "]";
	}
	
}
