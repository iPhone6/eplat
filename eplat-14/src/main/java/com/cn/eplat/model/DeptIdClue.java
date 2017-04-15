package com.cn.eplat.model;

import java.util.List;

/**
 * 部门id线索字符串 实体类
 * 
 * @author Administrator
 *
 */
public class DeptIdClue {
	private Integer id;	// 主键id
	private String dept_id_clue;	// 部门id线索字符串
	private Integer level_count;	// 这条部门id线索串的部门层级数（也即是串尾的那个部门的层级数）
	private String company_name;	// 公司名（即根部门的部门名），方便查看这条部门id线索是属于哪个公司的
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDept_id_clue() {
		return dept_id_clue;
	}
	public void setDept_id_clue(String dept_id_clue) {
		this.dept_id_clue = dept_id_clue;
	}
	public Integer getLevel_count() {
		return level_count;
	}
	public void setLevel_count(Integer level_count) {
		this.level_count = level_count;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	
	@Override
	public String toString() {
		return "DeptIdClue [id=" + id + ", dept_id_clue=" + dept_id_clue + ", level_count=" + level_count + ", company_name=" + company_name + "]";
	}
	
}
