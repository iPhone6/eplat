package com.cn.eplat.model;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.cn.eplat.utils.DateUtil;

public class MachCheckInOut {
	
	private Integer id;	// 主键id（打卡机打卡数据推送记录表）
	private Integer userid;	// 打卡机用户id
	private String badge_number;	// 打卡机用户工号
	private Date check_time;	// 打卡时间
	private String check_type;	// 打卡机考勤状态
	private Integer verify_code;	// 验证方式
	private String sensor_id;	// 
	private String memo_info;	// 
	private String work_code;	// 工作代码
	private String sn;	// 打卡机序列号/设备外键
	private Integer user_ext_fmt;	// 
	private String push_status;	// 打卡机打卡数据推送状态（主要包括：push_success、push_failed和request_failed三种）
	private Date last_push_time;	// 最近一次推送时间
	private Integer push_count;	// 推送次数计数
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getBadge_number() {
		return badge_number;
	}
	public void setBadge_number(String badge_number) {
		this.badge_number = badge_number;
	}
	public Date getCheck_time() {
		return check_time;
	}
	public void setCheck_time(Date check_time) {
		this.check_time = check_time;
	}
	public String getCheck_type() {
		return check_type;
	}
	public void setCheck_type(String check_type) {
		this.check_type = check_type;
	}
	public Integer getVerify_code() {
		return verify_code;
	}
	public void setVerify_code(Integer verify_code) {
		this.verify_code = verify_code;
	}
	public String getSensor_id() {
		return sensor_id;
	}
	public void setSensor_id(String sensor_id) {
		this.sensor_id = sensor_id;
	}
	public String getMemo_info() {
		return memo_info;
	}
	public void setMemo_info(String memo_info) {
		this.memo_info = memo_info;
	}
	public String getWork_code() {
		return work_code;
	}
	public void setWork_code(String work_code) {
		this.work_code = work_code;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getUser_ext_fmt() {
		return user_ext_fmt;
	}
	public void setUser_ext_fmt(Integer user_ext_fmt) {
		this.user_ext_fmt = user_ext_fmt;
	}
	public String getPush_status() {
		return push_status;
	}
	public void setPush_status(String push_status) {
		this.push_status = push_status;
	}
	public Date getLast_push_time() {
		return last_push_time;
	}
	public void setLast_push_time(Date last_push_time) {
		this.last_push_time = last_push_time;
	}
	public Integer getPush_count() {
		return push_count;
	}
	public void setPush_count(Integer push_count) {
		this.push_count = push_count;
	}
	
	@Override
	public String toString() {
		return "MachCheckInOut [id=" + id + ", userid=" + userid + ", badge_number=" + badge_number + ", check_time=" + check_time + ", check_type=" + check_type
				+ ", verify_code=" + verify_code + ", sensor_id=" + sensor_id + ", memo_info=" + memo_info + ", work_code=" + work_code + ", sn=" + sn + ", user_ext_fmt="
				+ user_ext_fmt + ", push_status=" + push_status + ", last_push_time=" + last_push_time + ", push_count=" + push_count + "]";
	}
	
}
