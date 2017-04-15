package com.b510b.excel.vo;

/**
 * 从Excel表格数据导入员工信息对应的实体类
 * 
 * @author Administrator
 *
 */
public class Employee {
	private Integer row_no;	// 员工信息在Excel表格中所在行号
	private String userid;	// 员工UserID
	private String dept;	// 部门
	private String job_title;	// 职位
	private String name;	// 姓名
	private String gender;	// 性别
	private String work_no;	// 工号
	private String is_managers;	// 是否此部门主管
	private String mobile_phone;	// 手机号
	private String email;	// 邮箱
	private String fixed_phone;	// 分机号
	private String work_place;	// 办公地点
	private String notes;	// 备注
	
	public Integer getRow_no() {
		return row_no;
	}
	public void setRow_no(Integer row_no) {
		this.row_no = row_no;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getJob_title() {
		return job_title;
	}
	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getWork_no() {
		return work_no;
	}
	public void setWork_no(String work_no) {
		this.work_no = work_no;
	}
	public String getIs_managers() {
		return is_managers;
	}
	public void setIs_managers(String is_managers) {
		this.is_managers = is_managers;
	}
	public String getMobile_phone() {
		return mobile_phone;
	}
	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFixed_phone() {
		return fixed_phone;
	}
	public void setFixed_phone(String fixed_phone) {
		this.fixed_phone = fixed_phone;
	}
	public String getWork_place() {
		return work_place;
	}
	public void setWork_place(String work_place) {
		this.work_place = work_place;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	@Override
	public String toString() {
		return "Employee [row_no=" + row_no + ", userid=" + userid + ", dept=" + dept + ", job_title=" + job_title + ", name=" + name + ", gender=" + gender + ", work_no="
				+ work_no + ", is_managers=" + is_managers + ", mobile_phone=" + mobile_phone + ", email=" + email + ", fixed_phone=" + fixed_phone + ", work_place=" + work_place
				+ ", notes=" + notes + "]";
	}
	
}
