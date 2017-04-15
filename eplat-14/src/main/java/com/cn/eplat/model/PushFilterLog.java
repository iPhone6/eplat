package com.cn.eplat.model;

import java.util.Date;

/**
 * 筛选日志表 的实体类
 * 
 * @author Administrator
 *
 */
public class PushFilterLog {
	private Long id;	// 筛选日志表的主键id
	private Date dayof_date;	// 参与筛选的考勤数据日期
	private Integer ep_uid;	// 用户id
	private Date filter_time;	// 筛选操作的时间
	private String status;	// 筛选的结果状态
	private String describe;	// 对筛选结果状态的描述信息
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDayof_date() {
		return dayof_date;
	}
	public void setDayof_date(Date dayof_date) {
		this.dayof_date = dayof_date;
	}
	public Integer getEp_uid() {
		return ep_uid;
	}
	public void setEp_uid(Integer ep_uid) {
		this.ep_uid = ep_uid;
	}
	public Date getFilter_time() {
		return filter_time;
	}
	public void setFilter_time(Date filter_time) {
		this.filter_time = filter_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	@Override
	public String toString() {
		return "PushFilterLog [id=" + id + ", dayof_date=" + dayof_date + ", ep_uid=" + ep_uid + ", filter_time=" + filter_time + ", status=" + status + ", describe=" + describe
				+ "]";
	}
	
}
