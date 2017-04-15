package com.cn.eplat.model;

import java.util.Date;

/**
 * 打卡机Access数据库打卡数据拷贝表 实体类
 * 
 * @author Administrator
 *
 */
public class MachChkioCopy {
	private Long id;	// 打卡机Access数据库打卡数据拷贝表的主键id
	private Integer userid;	// 打卡机用户id
	private String badgenumber;	// 打卡机用户工号
	private Date checktime;	// 打卡时间
	private String checktype;	// 打卡机考勤状态
	private Integer verifycode;	// 验证方式
	private String sensorid;
	private String memoinfo;
	private String workcode;	// 工作代码
	private String sn;	// 打卡机序列号/设备外键
	private Integer userextfmt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getBadgenumber() {
		return badgenumber;
	}
	public void setBadgenumber(String badgenumber) {
		this.badgenumber = badgenumber;
	}
	public Date getChecktime() {
		return checktime;
	}
	public void setChecktime(Date checktime) {
		this.checktime = checktime;
	}
	public String getChecktype() {
		return checktype;
	}
	public void setChecktype(String checktype) {
		this.checktype = checktype;
	}
	public Integer getVerifycode() {
		return verifycode;
	}
	public void setVerifycode(Integer verifycode) {
		this.verifycode = verifycode;
	}
	public String getSensorid() {
		return sensorid;
	}
	public void setSensorid(String sensorid) {
		this.sensorid = sensorid;
	}
	public String getMemoinfo() {
		return memoinfo;
	}
	public void setMemoinfo(String memoinfo) {
		this.memoinfo = memoinfo;
	}
	public String getWorkcode() {
		return workcode;
	}
	public void setWorkcode(String workcode) {
		this.workcode = workcode;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getUserextfmt() {
		return userextfmt;
	}
	public void setUserextfmt(Integer userextfmt) {
		this.userextfmt = userextfmt;
	}
	
	@Override
	public String toString() {
		return "MachChkioCopy [id=" + id + ", userid=" + userid + ", badgenumber=" + badgenumber + ", checktime=" + checktime + ", checktype=" + checktype + ", verifycode="
				+ verifycode + ", sensorid=" + sensorid + ", memoinfo=" + memoinfo + ", workcode=" + workcode + ", sn=" + sn + ", userextfmt=" + userextfmt + "]";
	}
	
}
