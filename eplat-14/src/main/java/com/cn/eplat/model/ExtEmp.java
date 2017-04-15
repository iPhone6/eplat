package com.cn.eplat.model;

/**
 * 
 * @author Administrator
 * 
 * 外部联系人实体类
 * 
 */
public class ExtEmp {
	private Integer id;	// 主键ID
	private Integer contact_id;	// 联系人ID（钉钉企业通讯录导出的数据字段）
	private String name;	// 姓名
	private String mobile_phone;	// 手机号
	private Integer charger_uid;	// 负责人id（企业人员id），最多一个负责人
	private String tag_type;	// 类型标签（客户、渠道商、供应商、合作伙伴、其他类型等，只能选其一）
	private String tag_level;	// 级别标签（一般、重要、核心等，只能选其一）
	private String tag_status;	// 状态标签（潜在、意向、洽谈、成交、流失等，只能选其一）
	private String company;	// 公司
	private String job_title;	// 职位
	private String address;	// 地址
	private String notes;	// 备注
	private String shareto_uids;	// 共享给（钉钉企业通讯录外部联系人中的字段，取值为人员id），可以共享给多个人员
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getContact_id() {
		return contact_id;
	}
	public void setContact_id(Integer contact_id) {
		this.contact_id = contact_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile_phone() {
		return mobile_phone;
	}
	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}
	public Integer getCharger_uid() {
		return charger_uid;
	}
	public void setCharger_uid(Integer charger_uid) {
		this.charger_uid = charger_uid;
	}
	public String getTag_type() {
		return tag_type;
	}
	public void setTag_type(String tag_type) {
		this.tag_type = tag_type;
	}
	public String getTag_level() {
		return tag_level;
	}
	public void setTag_level(String tag_level) {
		this.tag_level = tag_level;
	}
	public String getTag_status() {
		return tag_status;
	}
	public void setTag_status(String tag_status) {
		this.tag_status = tag_status;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getJob_title() {
		return job_title;
	}
	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getShareto_uids() {
		return shareto_uids;
	}
	public void setShareto_uids(String shareto_uids) {
		this.shareto_uids = shareto_uids;
	}
	
	@Override
	public String toString() {
		return "ExtEmp [id=" + id + ", contact_id=" + contact_id + ", name=" + name + ", mobile_phone=" + mobile_phone + ", charger_uid=" + charger_uid + ", tag_type=" + tag_type
				+ ", tag_level=" + tag_level + ", tag_status=" + tag_status + ", company=" + company + ", job_title=" + job_title + ", address=" + address + ", notes=" + notes
				+ ", shareto_uids=" + shareto_uids + "]";
	}
	
}
