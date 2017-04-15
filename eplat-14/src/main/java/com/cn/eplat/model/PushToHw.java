package com.cn.eplat.model;

import java.util.Date;

/**
 * 推送华为考勤信息表 实体类
 * 
 * @author Administrator
 *
 */
public class PushToHw {
	private Long id;	// 推送华为考勤信息表的主键id
	private Integer ep_uid;	// 用户id
	private String name;	// 用户姓名
	private String id_no;	// 身份证号
	private Date dayof_date;	// 考勤日期
	private String dayof_week;	// 星期几
	private Date on_duty_time;	// 上班打卡时间
	private Date off_duty_time;	// 下班打卡时间
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getEp_uid() {
		return ep_uid;
	}
	public void setEp_uid(Integer ep_uid) {
		this.ep_uid = ep_uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId_no() {
		return id_no;
	}
	public void setId_no(String id_no) {
		this.id_no = id_no;
	}
	public Date getDayof_date() {
		return dayof_date;
	}
	public void setDayof_date(Date dayof_date) {
		this.dayof_date = dayof_date;
	}
	public String getDayof_week() {
		return dayof_week;
	}
	public void setDayof_week(String dayof_week) {
		this.dayof_week = dayof_week;
	}
	public Date getOn_duty_time() {
		return on_duty_time;
	}
	public void setOn_duty_time(Date on_duty_time) {
		this.on_duty_time = on_duty_time;
	}
	public Date getOff_duty_time() {
		return off_duty_time;
	}
	public void setOff_duty_time(Date off_duty_time) {
		this.off_duty_time = off_duty_time;
	}
	
	@Override
	public String toString() {
		return "PushToHw [id=" + id + ", ep_uid=" + ep_uid + ", name=" + name + ", id_no=" + id_no + ", dayof_date=" + dayof_date + ", dayof_week=" + dayof_week
				+ ", on_duty_time=" + on_duty_time + ", off_duty_time=" + off_duty_time + "]";
	}
	
}
