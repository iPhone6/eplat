package com.cn.eplat.model;

/**
 * 部门_用户中间表对应的实体类
 * @author Administrator
 *
 */
public class DeptUser {
	private Integer id;	// 主键ID
	private Integer dept_id;	// 部门id
	private Integer ep_uid;	// 人员id
	private Integer is_manager;	// 是否部门主管（1：是，0：否）
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDept_id() {
		return dept_id;
	}
	public void setDept_id(Integer dept_id) {
		this.dept_id = dept_id;
	}
	public Integer getEp_uid() {
		return ep_uid;
	}
	public void setEp_uid(Integer ep_uid) {
		this.ep_uid = ep_uid;
	}
	public Integer getIs_manager() {
		return is_manager;
	}
	public void setIs_manager(Integer is_manager) {
		this.is_manager = is_manager;
	}
	
	@Override
	public String toString() {
		return "DeptUser [id=" + id + ", dept_id=" + dept_id + ", ep_uid=" + ep_uid + ", is_manager=" + is_manager + "]";
	}
	
}
