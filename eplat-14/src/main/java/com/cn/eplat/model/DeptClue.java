package com.cn.eplat.model;

/**
 * 部门线索信息实体类
 * @author Administrator
 *
 */
public class DeptClue {
	private Integer id;	// 主键id
	private String company_name;	// 公司名（树形组织结构中最高层级的部门名）
	private String clue_str;	// 部门线索信息字符串
	private String dept_id_clue;	// 部门id线索字符串（直接通过部门id精准定位到该部门及其下级部门的id信息）
	private Integer level_count;	// 表示这条部门线索信息中包含了多少个部门层级
	private Integer last_dept_id;	// 表示这条部门线索信息中最后一个部门的部门id
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getClue_str() {
		return clue_str;
	}
	public void setClue_str(String clue_str) {
		this.clue_str = clue_str;
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
	public Integer getLast_dept_id() {
		return last_dept_id;
	}
	public void setLast_dept_id(Integer last_dept_id) {
		this.last_dept_id = last_dept_id;
	}
	
	@Override
	public String toString() {
		return "DeptClue [id=" + id + ", company_name=" + company_name + ", clue_str=" + clue_str + ", dept_id_clue=" + dept_id_clue + ", level_count=" + level_count
				+ ", last_dept_id=" + last_dept_id + "]";
	}
	
}
